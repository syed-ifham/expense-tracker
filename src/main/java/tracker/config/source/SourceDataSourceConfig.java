package tracker.config.source;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

/**
 * Configures DataSource for phone.db (source/temporary database)
 * Provides:
 * - sourceDataSource bean (DataSource for phone.db)
 * - sourceJdbcTemplate bean (JdbcTemplate for phone.db queries)
 */
@Slf4j
@Configuration
public class SourceDataSourceConfig {

    @Value("${app.db.temp.path}")
    private String sourceDbPath;

    @Value("${app.db.temp.driver}")
    private String sourceDbDriver;

    /**
     * Creates DataSource bean for source
     */
    @Bean(name = "sourceDataSource")
    public DataSource sourceDataSource() {
        log.info("[BEAN-CREATE] sourceDataSource starting...");
        log.info("[BEAN-INJECT] sourceDbPath = {}", sourceDbPath);
        log.info("[BEAN-INJECT] sourceDbDriver = {}", sourceDbDriver);
        
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(sourceDbDriver);
        
        String url = "jdbc:sqlite:" + sourceDbPath;
        dataSource.setUrl(url);
        
        log.info("✓ Source DataSource initialized");
        log.info("  [db-name=phone.db]");
        log.info("  [config-path={}]", sourceDbPath);
        log.info("  [jdbc-url={}]", url);
        log.info("  [driver={}]", sourceDbDriver);
        
        return dataSource;
    }

    /**
     * Creates JdbcTemplate bean for source
     */
    @Bean(name = "sourceJdbcTemplate")
    public JdbcTemplate sourceJdbcTemplate(@Qualifier("sourceDataSource") DataSource sourceDataSource) {
        JdbcTemplate template = new JdbcTemplate(sourceDataSource);
        log.debug("✓ Source JdbcTemplate bean created [db=phone.db]");
        return template;
    }
}
