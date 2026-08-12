package tracker.persistence.schema;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class AppSchemaInitializer {

    private static final List<String> REQUIRED_TABLES = Arrays.asList(
            "raw_messages",
            "transactions"
    );

    private static final String SCHEMA_SCRIPT = "persistDBSchema.sql";
    private static final String TABLE_CHECK_SQL =
            "SELECT 1 FROM sqlite_schema WHERE type='table' AND name=?";

    private final DataSource persistDataSource;

    public AppSchemaInitializer(@Qualifier("persistDataSource") DataSource persistDataSource) {
        this.persistDataSource = Objects.requireNonNull(persistDataSource, "persistDataSource must not be null");
    }

    /**
     * Initializes the application database schema if tables don't exist.
     * Creates raw_messages and transactions tables from persistDBSchema.sql.
     *
     * @throws RuntimeException if schema initialization fails
     */
    public void initializeSchema() {
        try (Connection con = persistDataSource.getConnection()) {

            if (!con.isValid(2)) {
                log.error("PersistDB: Connection timeout - database unreachable");
                throw new RuntimeException("PersistDB: Connection timeout - database unreachable");
            }

            if (allTablesExist(con)) {
                log.info("App Database: All required tables already exist");
                return;
            }

            log.info("App Database: Initializing database schema");
            ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
            populator.addScript(new ClassPathResource(SCHEMA_SCRIPT));
            populator.populate(con);
            log.info("App Database: Schema initialized successfully");

        } catch (SQLException e) {
            log.error("App Database: Failed to initialize database schema - error={}", e.getMessage());
            throw new RuntimeException("App Database: Schema initialization failed", e);
        }
    }

    private boolean allTablesExist(Connection con) throws SQLException {
        for (String tableName : REQUIRED_TABLES) {
            if (!tableExists(con, tableName)) {
                return false;
            }
        }
        return true;
    }

    private boolean tableExists(Connection con, String tableName) throws SQLException {
        try (PreparedStatement stmt = con.prepareStatement(TABLE_CHECK_SQL)) {
            stmt.setString(1, tableName);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }
}