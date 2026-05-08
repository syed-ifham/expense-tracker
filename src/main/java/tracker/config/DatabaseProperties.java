package tracker.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Centralized configuration properties for database connections
 * Provides type-safe access to all database configuration values
 * from application.yaml
 */
@Data
@Component
@ConfigurationProperties(prefix = "app.db")
public class DatabaseProperties {
    
    private Temp temp;
    private Persistent persistent;
    private App app;

    @Data
    public static class Temp {
        private String path;
        private String driver;
        private String name;
        private String tableName;
    }

    @Data
    public static class Persistent {
        private String path;
        private String driver;
        private String name;
        private String tableName;
    }

    @Data
    public static class App {
        private String name;
        private String version;
    }

    @Override
    public String toString() {
        return "DatabaseProperties{" +
                "temp=" + temp +
                ", persistent=" + persistent +
                ", app=" + app +
                '}';
    }
}
