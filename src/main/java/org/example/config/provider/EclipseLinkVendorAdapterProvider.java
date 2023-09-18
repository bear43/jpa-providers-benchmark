package org.example.config.provider;

import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.platform.database.H2Platform;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.AbstractJpaVendorAdapter;
import org.springframework.orm.jpa.vendor.EclipseLinkJpaVendorAdapter;

@Configuration
public class EclipseLinkVendorAdapterProvider implements JpaVendorAdapterProvider {
    @Override
    public String getName() {
        return "eclipselink";
    }

    @Override
    public AbstractJpaVendorAdapter getAdapter(LocalContainerEntityManagerFactoryBean emf) {
        EclipseLinkJpaVendorAdapter vendorAdapter = new EclipseLinkJpaVendorAdapter();
        vendorAdapter.setDatabasePlatform(H2Platform.class.getName());
        emf.getJpaPropertyMap().put(PersistenceUnitProperties.DDL_GENERATION, PersistenceUnitProperties.CREATE_ONLY);
        emf.setPersistenceUnitName(getName());
        return vendorAdapter;
    }
}
