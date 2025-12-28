package fr.upjvthomashromain.bibliouniv.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

@Component
public class StartupDatabaseInitializer implements ApplicationListener<ApplicationReadyEvent> {

    private static final Logger log = LoggerFactory.getLogger(StartupDatabaseInitializer.class);

    private Map<String, String> createStmts = new HashMap<>();
    private Map<String, List<String>> insertStmtsMap = new HashMap<>();
    private Map<String, List<String>> desiredColumnsMap = new HashMap<>();

    @Autowired
    private DataSource dataSource;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private Environment env;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        String url = env.getProperty("spring.datasource.url", "jdbc:mysql://localhost:3306/");
        String host = "localhost";
        int port = 3306; // default MySQL

        try {
            if (url != null && url.startsWith("jdbc:mysql://")) {
                String after = url.substring("jdbc:mysql://".length());
                String hostPortPart = after.split("/", 2)[0];
                if (hostPortPart.contains(":")) {
                    String[] hp = hostPortPart.split(":");
                    host = hp[0];
                    try { port = Integer.parseInt(hp[1]); } catch (NumberFormatException ignored) {}
                } else {
                    host = hostPortPart;
                }
            }
        } catch (Exception e) {
            log.warn("Could not parse datasource URL '{}', using defaults", url);
        }

        log.info("Expecting MySQL on {}:{} (default port 3306 if none provided)", host, port);

        // Check connectivity
        try (Connection c = dataSource.getConnection()) {
            log.info("Connected to database: {}", c.getMetaData().getURL());

            parseSeedSql();

            for (String tableName : createStmts.keySet()) {
                List<String> desiredColumns = desiredColumnsMap.get(tableName);
                List<String> currentColumns = jdbcTemplate.queryForList(
                        "SELECT column_name FROM information_schema.columns WHERE table_schema = DATABASE() AND table_name = ? ORDER BY ordinal_position",
                        String.class, tableName);

                boolean tableExists = !currentColumns.isEmpty();
                boolean matches = currentColumns.equals(desiredColumns);

                if (!tableExists) {
                    log.info("Table {} does not exist — creating and seeding", tableName);
                    runCreate(tableName);
                    runInserts(tableName);
                } else if (!matches) {
                    log.info("Table {} exists but schema mismatch — migrating", tableName);
                    migrate(tableName);
                    runInserts(tableName);
                } else {
                    log.info("Table {} exists and matches schema — seeding data", tableName);
                    runInserts(tableName);
                }
            }

        } catch (SQLException sqle) {
            log.error("Unable to connect to datasource — is MySQL running on {}:{} ?", host, port, sqle);
            throw new RuntimeException("Database connection failed", sqle);
        } catch (Exception e) {
            log.error("Unexpected error during startup DB check", e);
            throw new RuntimeException("Startup database initialization failed", e);
        }
    }

    private void parseSeedSql() {
        ClassPathResource r = new ClassPathResource("db/seed.sql");
        if (!r.exists()) {
            log.warn("Seed file db/seed.sql not found in classpath — skipping");
            return;
        }

        try {
            String sql = new String(r.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
            // naive split on semicolon; keep it simple for small seed file
            String[] statements = Stream.of(sql.split(";"))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .toArray(String[]::new);

            Pattern createPattern = Pattern.compile("CREATE TABLE(?: IF NOT EXISTS)?\\s+(\\w+)\\s*\\((.*)\\)", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
            Pattern insertPattern = Pattern.compile("INSERT INTO\\s+(\\w+)\\s+.*", Pattern.CASE_INSENSITIVE);

            for (String stmt : statements) {
                Matcher createMatcher = createPattern.matcher(stmt);
                if (createMatcher.find()) {
                    String tableName = createMatcher.group(1);
                    String columnsPart = createMatcher.group(2);
                    createStmts.put(tableName, stmt);
                    List<String> columns = parseColumns(columnsPart);
                    desiredColumnsMap.put(tableName, columns);
                } else {
                    Matcher insertMatcher = insertPattern.matcher(stmt);
                    if (insertMatcher.find()) {
                        String tableName = insertMatcher.group(1);
                        insertStmtsMap.computeIfAbsent(tableName, k -> new ArrayList<>()).add("INSERT IGNORE" + stmt.substring(6));
                    }
                }
            }
            log.info("Parsed seed SQL: {} tables", createStmts.size());
        } catch (IOException ioe) {
            log.error("Failed to read seed SQL file", ioe);
        } catch (Exception e) {
            log.error("Failed to parse seed SQL", e);
        }
    }

    private List<String> parseColumns(String columnsPart) {
        List<String> columns = new ArrayList<>();
        // Simple parsing: split by comma, take first word as column name
        String[] parts = columnsPart.split(",");
        for (String part : parts) {
            String trimmed = part.trim();
            if (!trimmed.isEmpty()) {
                String columnName = trimmed.split("\\s+")[0];
                columns.add(columnName);
            }
        }
        return columns;
    }

    private void runCreate(String tableName) {
        String stmt = createStmts.get(tableName);
        if (stmt != null) {
            log.debug("Executing CREATE statement for {}: {}", tableName, stmt);
            jdbcTemplate.execute(stmt);
        }
    }

    private void runInserts(String tableName) {
        List<String> stmts = insertStmtsMap.get(tableName);
        if (stmts != null) {
            for (String stmt : stmts) {
                log.debug("Executing INSERT statement for {}: {}", tableName, stmt);
                jdbcTemplate.execute(stmt);
            }
        }
        log.info("Seeding completed for {}", tableName);
    }

    private void migrate(String tableName) {
        log.info("Migrating table {}", tableName);
        jdbcTemplate.execute("CREATE TABLE " + tableName + "_temp AS SELECT * FROM " + tableName);
        jdbcTemplate.execute("DROP TABLE " + tableName);
        runCreate(tableName);
        jdbcTemplate.execute("INSERT INTO " + tableName + " SELECT * FROM " + tableName + "_temp");
        jdbcTemplate.execute("DROP TABLE " + tableName + "_temp");
        log.info("Migration completed for {}", tableName);
    }
}
