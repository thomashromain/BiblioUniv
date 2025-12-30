package fr.upjvthomashromain.bibliouniv.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class StartupDatabaseInitializer implements ApplicationListener<ApplicationReadyEvent> {

    private static final Logger log = LoggerFactory.getLogger(StartupDatabaseInitializer.class);

    private final LinkedHashMap<String, String> createStmts = new LinkedHashMap<>();
    private final Map<String, List<String>> insertStmtsMap = new LinkedHashMap<>();
    private final Map<String, List<String>> desiredColumnsMap = new LinkedHashMap<>();

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        // Clear maps to ensure fresh state
        createStmts.clear();
        insertStmtsMap.clear();
        desiredColumnsMap.clear();

        parseSeedSql();

        if (createStmts.isEmpty()) {
            log.info("No schema found in seed.sql to verify.");
            return;
        }

        // Check each table and warn if mismatch found
        for (String tableName : createStmts.keySet()) {
            if (!checkTableSchema(tableName)) {
                log.warn("### SCHEMA MISMATCH DETECTED ### Table '{}' does not match seed.sql definition!", tableName);
                log.warn("Expected columns: {}", desiredColumnsMap.get(tableName));
                log.warn("To synchronize, a database reset is required.");
                // We do NOT call resetDatabase() here anymore.
            }
        }
    }

    /**
     * Compares the live MySQL schema against the parsed desiredColumnsMap.
     */
    private boolean checkTableSchema(String tableName) {
        try {
            List<String> actualColumns = jdbcTemplate.queryForList(
                "SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = ? ORDER BY ORDINAL_POSITION",
                String.class, tableName);

            if (actualColumns.isEmpty()) return false;

            List<String> expected = desiredColumnsMap.get(tableName);
            
            if (actualColumns.size() != expected.size()) return false;

            for (int i = 0; i < expected.size(); i++) {
                if (!expected.get(i).equalsIgnoreCase(actualColumns.get(i))) return false;
            }
            return true;
        } catch (Exception e) {
            log.error("Error checking schema for table: {}", tableName, e);
            return false;
        }
    }

    /**
     * Nukes the database and runs the full seed.sql script.
     */
    public void resetDatabase() {
        log.warn("### MANUAL RESET TRIGGERED: NUKING AND RE-SEEDING ###");
        
        // Ensure maps are populated before resetting
        if (createStmts.isEmpty()) parseSeedSql();

        try {
            jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 0");

            List<String> currentTables = jdbcTemplate.queryForList(
                "SELECT table_name FROM information_schema.tables WHERE table_schema = DATABASE()", String.class);

            for (String table : currentTables) {
                jdbcTemplate.execute("DROP TABLE IF EXISTS `" + table + "`");
            }
            
            jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 1");

            // Recreate Tables
            for (String tableName : createStmts.keySet()) {
                jdbcTemplate.execute(createStmts.get(tableName));
            }

            // Insert Data
            for (Map.Entry<String, List<String>> entry : insertStmtsMap.entrySet()) {
                for (String sql : entry.getValue()) {
                    jdbcTemplate.execute(sql);
                }
            }
            log.info("### DATABASE RESET COMPLETED SUCCESSFULLY ###");
        } catch (Exception e) {
            log.error("Database reset failed!", e);
        }
    }

    private void parseSeedSql() {
        ClassPathResource r = new ClassPathResource("db/seed.sql");
        try {
            String content = new String(r.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
            String[] statements = content.split(";\\s*(\\r?\\n|$)");

            Pattern createPattern = Pattern.compile("CREATE TABLE(?: IF NOT EXISTS)?\\s+`?(\\w+)`?\\s*\\((.*)\\)", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
            Pattern insertPattern = Pattern.compile("INSERT INTO\\s+`?(\\w+)`?", Pattern.CASE_INSENSITIVE);

            for (String stmt : statements) {
                String cleanStmt = stmt.trim();
                if (cleanStmt.isEmpty()) continue;

                Matcher createMatcher = createPattern.matcher(cleanStmt);
                if (createMatcher.find()) {
                    String tableName = createMatcher.group(1).toLowerCase();
                    createStmts.put(tableName, cleanStmt);
                    desiredColumnsMap.put(tableName, extractColumnNames(createMatcher.group(2)));
                } else {
                    Matcher insertMatcher = insertPattern.matcher(cleanStmt);
                    if (insertMatcher.find()) {
                        String tableName = insertMatcher.group(1).toLowerCase();
                        insertStmtsMap.computeIfAbsent(tableName, k -> new ArrayList<>()).add(cleanStmt);
                    }
                }
            }
        } catch (IOException e) {
            log.error("Failed to parse seed.sql", e);
        }
    }

    private List<String> extractColumnNames(String body) {
        List<String> columns = new ArrayList<>();
        String[] lines = body.split(",(?![^\\(]*\\))");
        for (String line : lines) {
            String trimmed = line.trim().replaceAll("`", "");
            String upper = trimmed.toUpperCase();
            if (upper.startsWith("PRIMARY") || upper.startsWith("FOREIGN") || 
                upper.startsWith("KEY") || upper.startsWith("CONSTRAINT") || 
                upper.startsWith("UNIQUE") || upper.startsWith("CHECK")) {
                continue;
            }
            String[] parts = trimmed.split("\\s+");
            if (parts.length > 0 && !parts[0].isEmpty()) {
                columns.add(parts[0]);
            }
        }
        return columns;
    }
}