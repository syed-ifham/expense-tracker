package tracker.repository.phone;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import tracker.model.RawMessage;

import java.util.List;

/**
 * Repository for accessing raw message data from source database (phone.db).
 * Queries the phone.db for raw SMS messages containing transaction keywords.
 */
@Slf4j
@Repository
public class PhoneMessageRepository {

    @Value("${app.db.temp.path}")
    private String sourceDbPath;

    @Value("${app.db.temp.name}")
    private String sourceDbName;

    @Value("${app.db.temp.table-name}")
    private String sourceTableName;

    private final JdbcTemplate sourceJdbcTemplate;

    public PhoneMessageRepository(@Qualifier("sourceJdbcTemplate") JdbcTemplate sourceJdbcTemplate) {
        this.sourceJdbcTemplate = sourceJdbcTemplate;
    }

    /**
     * Fetches messages from source database that contain transaction-related keywords
     * Filters: WHERE body LIKE '%A/C%' OR body LIKE '%INR%'
     *
     * @return List of RawMessage objects, or empty list if no messages found
     * @throws RuntimeException if database query fails
     */
    public List<RawMessage> fetchMessages() {
        long startTime = System.currentTimeMillis();
        log.debug("Executing query on source database [db={}] [path={}] [table={}]", 
                  sourceDbName, sourceDbPath, sourceTableName);
        
        log.debug("[DEBUG] JdbcTemplate class: {}", sourceJdbcTemplate.getClass().getName());
        log.debug("[DEBUG] DataSource: {}", sourceJdbcTemplate.getDataSource());

        try {
            String query = String.format("""
                    SELECT
                        message_id,
                        from_address,
                        body,
                        timestamp
                    FROM %s
                    WHERE body LIKE '%%A/C%%' OR body LIKE '%%INR%%'
                    ORDER BY timestamp DESC;
                    """, sourceTableName);

            log.debug("Executing SQL: {}", query);

            List<RawMessage> results = sourceJdbcTemplate.query(query, (result, rowNum) -> new RawMessage(
                    result.getInt("message_id"),
                    result.getString("from_address"),
                    result.getString("body"),
                    result.getLong("timestamp")
            ));

            long duration = System.currentTimeMillis() - startTime;
            log.info("✓ Query executed successfully [db={}] [rowCount={}] [duration={}ms]",
                    sourceDbName, results.size(), duration);

            if (results.isEmpty()) {
                log.debug("Query returned empty result set [db={}] [filters=A/C,INR]", sourceDbName);
            }

            return results;

        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            log.error("✗ Database query failed on source database [db={}] [path={}] [table={}] [duration={}ms] [error={}]",
                    sourceDbName, sourceDbPath, sourceTableName, duration, e.getMessage(), e);
            throw new RuntimeException("Failed to fetch messages from source database: " + e.getMessage(), e);
        }
    }
}
