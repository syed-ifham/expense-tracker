package tracker.config.persistence;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

/**
 * Configures DataSource for tracker.db (persistence/permanent database)
 * Provides:
 * - persistenceDataSource bean (DataSource for tracker.db)
 * - persistenceJdbcTemplate bean (JdbcTemplate for tracker.db queries)
 */
@Slf4j
@Primary
@Configuration
public class PersistenceDataSourceConfig {

    @Value("${app.db.persistent.path}")
    private String persistenceDbPath;

    @Value("${app.db.persistent.driver}")
    private String persistenceDbDriver;

    /**
     * Creates DataSource bean for persistence (tracker.db)
     */
    @Bean(name = "persistenceDataSource")
    public DataSource persistenceDataSource() {
        log.info("[BEAN-CREATE] persistenceDataSource starting...");
        log.info("[BEAN-INJECT] persistenceDbPath = {}", persistenceDbPath);
        log.info("[BEAN-INJECT] persistenceDbDriver = {}", persistenceDbDriver);
        
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(persistenceDbDriver);
        
        String url = "jdbc:sqlite:" + persistenceDbPath;
        dataSource.setUrl(url);
        
        log.info("✓ Persistence DataSource initialized");
        log.info("  [db-name=tracker.db]");
        log.info("  [config-path={}]", persistenceDbPath);
        log.info("  [jdbc-url={}]", url);
        log.info("  [driver={}]", persistenceDbDriver);
        
        return dataSource;
    }

    /**
     * Creates JdbcTemplate bean for persistence (tracker.db) queries
     */
    @Bean(name = "persistenceJdbcTemplate")
    public JdbcTemplate persistenceJdbcTemplate(@Qualifier("persistenceDataSource") DataSource persistenceDataSource) {
        JdbcTemplate template = new JdbcTemplate(persistenceDataSource);
        log.debug("✓ Persistence JdbcTemplate bean created [db=tracker.db]");
        return template;
    }
}
