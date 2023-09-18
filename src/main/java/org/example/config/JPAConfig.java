package org.example.config;

import com.zaxxer.hikari.HikariDataSource;
import jakarta.annotation.PreDestroy;
import jakarta.persistence.spi.PersistenceProvider;

import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;
import javax.sql.DataSource;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.datanucleus.api.jakarta.PersistenceProviderImpl;
import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.platform.database.H2Platform;
import org.example.config.props.HikariProps;
import org.example.config.provider.JpaVendorAdapterProvider;
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
import org.springframework.transaction.TransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.support.TransactionTemplate;

@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(JpaProperties.class)
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = "org.example.dao")
public class JPAConfig {
    private final HikariProps hikariProps;
    private final Environment environment;
    private final List<JpaVendorAdapterProvider> jpaProviders;

    @Bean
    public DataSource dataSource() {
        return new HikariDataSource(hikariProps);
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(JpaProperties jpaProperties) {
        DataSource dataSource = dataSource();
        final Properties properties = new Properties();
        properties.putAll(jpaProperties.getProperties());

        LocalContainerEntityManagerFactoryBean emf = new LocalContainerEntityManagerFactoryBean();
        emf.setDataSource(dataSource);
        emf.setJpaProperties(properties);
        emf.setPackagesToScan("org.example.model");
        emf.setJpaVendorAdapter(createJpaVendorAdapter(emf));
        return emf;
    }


    protected AbstractJpaVendorAdapter createJpaVendorAdapter(LocalContainerEntityManagerFactoryBean emf) {
        Set<String> profiles = Set.of(environment.getActiveProfiles());
        if (profiles.isEmpty()) {
            String providerNames = jpaProviders.stream()
                    .map(JpaVendorAdapterProvider::getName)
                    .collect(Collectors.joining(", "));
            throw new IllegalStateException("Select one of the profiles: " + providerNames + ".");
        }
        if (profiles.size() > 1) {
            String providerNames = jpaProviders.stream()
                    .map(JpaVendorAdapterProvider::getName)
                    .collect(Collectors.joining(", "));
            throw new IllegalStateException("Only one profile can be active! Current profiles: " + providerNames + ".");
        }
        JpaVendorAdapterProvider jpaVendorAdapterProvider = jpaProviders.stream()
                .filter(provider -> profiles.contains(provider.getName()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Unknown profile: " + jpaProviders.stream().findFirst().get() + "."));
        return jpaVendorAdapterProvider.getAdapter(emf);
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
