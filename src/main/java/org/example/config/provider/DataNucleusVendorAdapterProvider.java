package org.example.config.provider;

import jakarta.persistence.spi.PersistenceProvider;
import org.datanucleus.api.jakarta.PersistenceProviderImpl;
import org.eclipse.persistence.platform.database.H2Platform;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.AbstractJpaVendorAdapter;

@Configuration
public class DataNucleusVendorAdapterProvider implements JpaVendorAdapterProvider {
    @Override
    public String getName() {
        return "datanucleus";
    }

    @Override
    public AbstractJpaVendorAdapter getAdapter(LocalContainerEntityManagerFactoryBean emf) {
        AbstractJpaVendorAdapter datanucleusJpaVendorAdapter = new AbstractJpaVendorAdapter() {
            @Override
            public PersistenceProvider getPersistenceProvider() {
                return new PersistenceProviderImpl();
            }
        };
        datanucleusJpaVendorAdapter.setDatabasePlatform(H2Platform.class.getName());
        emf.getJpaPropertyMap().put("datanucleus.schema.autoCreateAll", "true");
        emf.setPersistenceUnitName(getName());
        return datanucleusJpaVendorAdapter;
    }
}
