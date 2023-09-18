package org.example.config;

import com.zaxxer.hikari.HikariDataSource;
import jakarta.persistence.spi.PersistenceProvider;
import java.util.Properties;
import java.util.Set;
import javax.sql.DataSource;
import lombok.RequiredArgsConstructor;
import org.datanucleus.api.jakarta.PersistenceProviderImpl;
import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.platform.database.H2Platform;
import org.example.config.props.HikariProps;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.dialect.H2Dialect;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.AbstractJpaVendorAdapter;
import org.springframework.orm.jpa.vendor.EclipseLinkJpaVendorAdapter;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.support.TransactionTemplate;

@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(JpaProperties.class)
@EnableTransactionManagement
@EnableJpaRepositories(entityManagerFactoryRef = "entityManagerFactory",
        transactionManagerRef = "transactionManager",
        basePackages = {"org.example.dao"})
public class JPAConfig {

    private static final String HIBERNATE = "hibernate";
    private static final String ECLIPSELINK = "eclipselink";
    private static final String DATANUCLEUS = "datanucleus";
    private final HikariProps hikariProps;
    private final Environment environment;

    @Bean
    public DataSource dataSource() {
        return new HikariDataSource(hikariProps);
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(JpaProperties jpaProperties) {
        DataSource dataSource = dataSource();
        final Properties properties = new Properties();
        properties.putAll(jpaProperties.getProperties());

        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource);
        em.setJpaProperties(properties);
        em.setPackagesToScan("org.example.model");
        em.setJpaVendorAdapter(createJpaVendorAdapter(em));
        return em;
    }


    protected AbstractJpaVendorAdapter createJpaVendorAdapter(LocalContainerEntityManagerFactoryBean em) {
        Set<String> profiles = Set.of(environment.getActiveProfiles());
        if (profiles.contains(HIBERNATE)) {
            HibernateJpaVendorAdapter hibernateJpaVendorAdapter = new HibernateJpaVendorAdapter();
            hibernateJpaVendorAdapter.setDatabasePlatform(H2Dialect.class.getName());
            em.getJpaPropertyMap().put(AvailableSettings.HBM2DDL_AUTO, "create");
            em.setPersistenceUnitName(HIBERNATE);
            return hibernateJpaVendorAdapter;
        } else if (profiles.contains(ECLIPSELINK)) {
            EclipseLinkJpaVendorAdapter vendorAdapter = new EclipseLinkJpaVendorAdapter();
            vendorAdapter.setDatabasePlatform(H2Platform.class.getName());
            em.getJpaPropertyMap().put(PersistenceUnitProperties.DDL_GENERATION, PersistenceUnitProperties.CREATE_ONLY);
            em.setPersistenceUnitName(ECLIPSELINK);
            return vendorAdapter;
        } else if (profiles.contains(DATANUCLEUS)) {
            AbstractJpaVendorAdapter datanucleusJpaVendorAdapter = new AbstractJpaVendorAdapter() {
                @Override
                public PersistenceProvider getPersistenceProvider() {
                    return new PersistenceProviderImpl();
                }
            };
            datanucleusJpaVendorAdapter.setDatabasePlatform(H2Platform.class.getName());
            em.getJpaPropertyMap().put("datanucleus.schema.autoCreateAll", "true");
            em.setPersistenceUnitName(DATANUCLEUS);
            return datanucleusJpaVendorAdapter;
        } else {
            throw new IllegalStateException("You should set one of profile (hibernate/eclipselink)");
        }
    }

    @Bean
    public PlatformTransactionManager transactionManager(@Qualifier("entityManagerFactory") LocalContainerEntityManagerFactoryBean pgExtEntityManager) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(pgExtEntityManager.getObject());
        return transactionManager;
    }

    @Bean
    public TransactionTemplate transactionTemplate(PlatformTransactionManager transactionManager) {
        return new TransactionTemplate(transactionManager);
    }

}
