package tracker.config.persistence;

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
 * Initializes and validates persistence database (tracker.db)
 * Handles schema creation and connection validation for tracker.db
 */
@Slf4j
@Configuration
public class PersistenceDatabaseValidator {

    @Value("${app.db.persistent.path}")
    private String persistenceDbPath;

    @Value("${app.db.persistent.name}")
    private String persistenceDbName;

    private final DataSource persistenceDataSource;

    public PersistenceDatabaseValidator(@Qualifier("persistenceDataSource") DataSource persistenceDataSource) {
        this.persistenceDataSource = persistenceDataSource;
    }

    /**
     * Validates connection to persistence database
     * @throws RuntimeException if connection fails
     */
    public void validatePersistenceDatabaseConnection() {
        try (Connection con = persistenceDataSource.getConnection()) {
            if (con != null && !con.isClosed()) {
                log.info("✓ Persistence database connection established [db={}] [path={}]",
                        persistenceDbName, persistenceDbPath);
            }
        } catch (SQLException e) {
            log.error("✗ Failed to establish persistence database connection [db={}] [path={}] [error={}]",
                    persistenceDbName, persistenceDbPath, e.getMessage(), e);
            throw new RuntimeException("Persistence database connection failed", e);
        }
    }

    /**
     * Initializes persistence database schema (creates messages table if not exists)
     * @param tableName name of the table to create
     * @throws RuntimeException if schema initialization fails
     */
    public void initializePersistenceDatabaseSchema(String tableName) {
        try (Connection con = persistenceDataSource.getConnection()) {
            if (tableExists(con, tableName)) {
                log.info("✓ Table already exists [db={}] [table={}]", persistenceDbName, tableName);
                return;
            }

            String createTableSql =String.format( """
                    CREATE TABLE IF NOT EXISTS %s (
                        message_id INTEGER PRIMARY KEY AUTOINCREMENT,
                        from_address TEXT NOT NULL,
                        to_address TEXT NOT NULL,
                        amount INTEGER NOT NULL,
                        transaction_type TEXT NOT NULL,
                        timestamp INTEGER NOT NULL,
                        created_at DATETIME DEFAULT CURRENT_TIMESTAMP
                    );
                    """, tableName);

            try (var stmt = con.createStatement()) {
                stmt.execute(createTableSql);
                log.info("✓ Table created successfully [db={}] [table={}]", persistenceDbName, tableName);
            }

        } catch (SQLException e) {
            log.error("✗ Failed to initialize database schema [db={}] [table={}] [error={}]",
                    persistenceDbName, tableName, e.getMessage(), e);
            throw new RuntimeException("Schema initialization failed", e);
        }
    }

    /**
     * Checks if a table exists in the persistence database
     * @param con active database connection
     * @param tableName name of the table to check
     * @return true if table exists, false otherwise
     */
    private boolean tableExists(Connection con, String tableName) {
        try {
            DatabaseMetaData metaData = con.getMetaData();
            try (ResultSet rs = metaData.getTables(null, null, tableName, null)) {
                boolean exists = rs.next();

                if (exists) {
                    log.debug("Table existence check [db={}] [table={}] [exists=true]", 
                             persistenceDbName, tableName);
                } else {
                    log.debug("Table existence check [db={}] [table={}] [exists=false]", 
                             persistenceDbName, tableName);
                }

                return exists;
            }

        } catch (SQLException e) {
            log.warn("Failed to check table existence [db={}] [table={}] [error={}]",
                    persistenceDbName, tableName, e.getMessage());
            return false;
        }
    }
}
