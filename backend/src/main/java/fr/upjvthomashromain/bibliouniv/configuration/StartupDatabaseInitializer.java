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
import java.util.stream.Stream;

@Component
public class StartupDatabaseInitializer implements ApplicationListener<ApplicationReadyEvent> {

    private static final Logger log = LoggerFactory.getLogger(StartupDatabaseInitializer.class);

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

            Integer tableCount = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = DATABASE()",
                    Integer.class);

            if (tableCount == null) tableCount = 0;
            log.info("Number of tables in schema: {}", tableCount);

            if (tableCount == 0) {
                log.info("No tables found — running seed SQL (classpath:db/seed.sql)");
                runSeedSql();
            } else {
                log.info("Schema already contains tables; skipping seeding");
            }

        } catch (SQLException sqle) {
            log.error("Unable to connect to datasource — is MySQL running on {}:{} ?", host, port, sqle);
        } catch (Exception e) {
            log.error("Unexpected error during startup DB check", e);
        }
    }

    private void runSeedSql() {
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

            for (String stmt : statements) {
                log.debug("Executing SQL statement: {}", stmt);
                jdbcTemplate.execute(stmt);
            }
            log.info("Database seeding completed");
        } catch (IOException ioe) {
            log.error("Failed to read seed SQL file", ioe);
        } catch (Exception e) {
            log.error("Failed to execute seed SQL", e);
        }
    }
}
