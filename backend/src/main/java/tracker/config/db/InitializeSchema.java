package tracker.config.db;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Service
@Slf4j
public class InitializeSchema {

    @Value("${app.db.persist.metadata.schema}")
    private String persistSchemaPath;

    private final DataSource persistDataSource;

    public InitializeSchema(@Qualifier("persistDataSource") DataSource persistDataSource) {
        this.persistDataSource = persistDataSource;
    }

    public void persistSchema() {
        try (Connection con = persistDataSource.getConnection()) {

            if (!con.isValid(2)) {
                log.error("PersistDB: Connection Timeout DB unreachable");
                throw new RuntimeException("PersistDB: Connection Timeout DB unreachable");
            }

            if (tableExists(con, "raw_messages")) {
                log.info("✓ PersistDB: Table already exists  [raw_messages]");
                return;
            }

            if (tableExists(con, "transactions")) {
                log.info("✓ PersistDB: Table already exists  [transactions]");
                return;
            }

            log.info("✓ PersistDB: Initializing DB Schema");
            ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
            populator.addScript(new ClassPathResource("persistDBSchema.sql"));
            populator.populate(con);
            log.info("✓ PersistDB: Schema initialized successfully");

        } catch (SQLException e) {
            log.error("✗ PersistDB: Failed to initialize database schema  [error={}]", e.getMessage());
            throw new RuntimeException("PersistDB : Schema initialization failed", e);
        }
    }

    private boolean tableExists(Connection con, String tableName) throws SQLException {
        String sql = "SELECT 1 FROM sqlite_schema WHERE type='table' AND name=?";
        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, tableName);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

}
