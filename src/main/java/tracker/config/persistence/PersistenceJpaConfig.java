package tracker.config.persistence;

import jakarta.persistence.EntityManagerFactory;
import org.hibernate.cfg.AvailableSettings;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        basePackages = "tracker.repository.tracker",
        entityManagerFactoryRef = "persistenceEntityManagerFactory",
        transactionManagerRef = "persistenceTransactionManager"
)
public class PersistenceJpaConfig {

    @Value("${spring.jpa.database-platform}")
    private String hibernateDialect;

    @Bean(name = "persistenceEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean persistenceEntityManagerFactory(
            @Qualifier("persistenceDataSource") DataSource persistenceDataSource) {

        LocalContainerEntityManagerFactoryBean emf = new LocalContainerEntityManagerFactoryBean();
        emf.setDataSource(persistenceDataSource);
        emf.setPackagesToScan("tracker.entity");

        JpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        emf.setJpaVendorAdapter(vendorAdapter);
        emf.setJpaPropertyMap(jpaProperties());
        return emf;
    }

    @Bean(name = "persistenceTransactionManager")
    public PlatformTransactionManager persistenceTransactionManager(
            @Qualifier("persistenceEntityManagerFactory") EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }

    @Bean
    public PersistenceExceptionTranslationPostProcessor persistenceExceptionTranslationPostProcessor() {
        return new PersistenceExceptionTranslationPostProcessor();
    }

    private Map<String, Object> jpaProperties() {
        Map<String, Object> props = new HashMap<>();
        props.put(AvailableSettings.DIALECT, hibernateDialect);
        props.put(AvailableSettings.HBM2DDL_AUTO, "none");
        props.put(AvailableSettings.SHOW_SQL, false);
        props.put(AvailableSettings.FORMAT_SQL, false);
        return props;
    }
}
