package tracker.config.db;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.simple.JdbcClient;

import javax.sql.DataSource;

@Configuration
public class DataSourceConfig {

    @Bean(name="sourceDataSource")
    @ConfigurationProperties(prefix = "app.db.source")
    public DataSource sourceDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name="sourceJdbcClient")
    public JdbcClient sourceJdbcClient(@Qualifier("sourceDataSource") DataSource ds) {
        return JdbcClient.create(ds);
    }

    @Bean(name="persistDataSource")
    @ConfigurationProperties(prefix = "app.db.persist")
    public DataSource persistDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name="persistJdbcClient")
    public JdbcClient persistJdbcClient(@Qualifier("persistDataSource") DataSource ds) {
        return JdbcClient.create(ds);
    }

}
