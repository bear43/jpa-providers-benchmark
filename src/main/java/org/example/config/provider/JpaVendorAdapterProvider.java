package org.example.config.provider;

import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.AbstractJpaVendorAdapter;

public interface JpaVendorAdapterProvider {
    String getName();
    AbstractJpaVendorAdapter getAdapter(LocalContainerEntityManagerFactoryBean emf);
}
