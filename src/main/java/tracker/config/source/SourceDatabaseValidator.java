package tracker.config.source;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Validates connection to source database (phone.db)
 * Performs health checks for phone.db connectivity
 */
@Slf4j
@Configuration
public class SourceDatabaseValidator {

    @Value("${app.db.temp.path}")
    private String sourceDbPath;

    @Value("${app.db.temp.name}")
    private String sourceDbName;

    private final DataSource sourceDataSource;

    public SourceDatabaseValidator(@Qualifier("sourceDataSource") DataSource sourceDataSource) {
        this.sourceDataSource = sourceDataSource;
    }

    /**
     * Validates connection to source database
     * @throws RuntimeException if connection fails
     */
    public void validateSourceDatabaseConnection() {
        try (Connection con = sourceDataSource.getConnection()) {
            if (con != null && !con.isClosed()) {
                log.info("✓ Source database connection established [db={}] [path={}]",
                        sourceDbName, sourceDbPath);
            }
        } catch (SQLException e) {
            log.error("✗ Failed to establish source database connection [db={}] [path={}] [error={}]",
                    sourceDbName, sourceDbPath, e.getMessage(), e);
            throw new RuntimeException("Source database connection failed", e);
        }
    }

    /**
     * Initializes source database schema (creates message table if not exists)
     * @param tableName name of the table to create
     * @throws RuntimeException if schema initialization fails
     */
    public void initializeSourceDatabaseSchema(String tableName) {
        try (Connection con = sourceDataSource.getConnection()) {
            if (tableExists(con, tableName)) {
                log.info("✓ Table already exists [db={}] [table={}]", sourceDbName, tableName);
                return;
            }

            String createTableSql = """
                    CREATE TABLE IF NOT EXISTS %s (
                        message_id INTEGER PRIMARY KEY AUTOINCREMENT,
                        from_address TEXT NOT NULL,
                        body TEXT NOT NULL,
                        timestamp INTEGER NOT NULL
                    );
                    """.formatted(tableName);

            try (var stmt = con.createStatement()) {
                stmt.execute(createTableSql);
                log.info("✓ Table created successfully [db={}] [table={}]", sourceDbName, tableName);
            }

        } catch (SQLException e) {
            log.error("✗ Failed to initialize database schema [db={}] [table={}] [error={}]",
                    sourceDbName, tableName, e.getMessage(), e);
            throw new RuntimeException("Schema initialization failed", e);
        }
    }

    /**
     * Checks if a table exists in the source database
     * @param con active database connection
     * @param tableName name of the table to check
     * @return true if table exists, false otherwise
     */
    private boolean tableExists(Connection con, String tableName) {
        try {
            DatabaseMetaData metaData = con.getMetaData();
            try (ResultSet rs = metaData.getTables(null, null, tableName, null)) {
                boolean exists = rs.next();
                log.debug("Table existence check [db={}] [table={}] [exists={}]", 
                         sourceDbName, tableName, exists);
                return exists;
            }
        } catch (SQLException e) {
            log.warn("Failed to check table existence [db={}] [table={}] [error={}]",
                    sourceDbName, tableName, e.getMessage());
            return false;
        }
    }
}
