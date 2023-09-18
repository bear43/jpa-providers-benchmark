package org.example.config.provider;

import org.hibernate.cfg.AvailableSettings;
import org.hibernate.dialect.H2Dialect;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.AbstractJpaVendorAdapter;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

@Configuration
public class HibernateVendorAdapterProvider implements JpaVendorAdapterProvider {
    @Override
    public String getName() {
        return "hibernate";
    }

    @Override
    public AbstractJpaVendorAdapter getAdapter(LocalContainerEntityManagerFactoryBean emf) {
        HibernateJpaVendorAdapter hibernateJpaVendorAdapter = new HibernateJpaVendorAdapter();
        hibernateJpaVendorAdapter.setDatabasePlatform(H2Dialect.class.getName());
        emf.getJpaPropertyMap().put(AvailableSettings.HBM2DDL_AUTO, "create");
        emf.setPersistenceUnitName(getName());
        return hibernateJpaVendorAdapter;
    }
}
