package org.example.jpa;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.SharedCacheMode;
import jakarta.persistence.ValidationMode;
import jakarta.persistence.spi.*;
import org.example.AbstractBenchmark;
import org.example.model.Mark;
import org.example.model.Student;
import org.example.model.Subject;

import javax.sql.DataSource;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

public class EclipseLinkSetup implements Setup {
    @Override
    public EntityManagerFactory configure() {
        PersistenceProviderResolver resolver = PersistenceProviderResolverHolder.getPersistenceProviderResolver();
        List<PersistenceProvider> providers = resolver.getPersistenceProviders();
        PersistenceProvider eclipseLinkPersistenceProvider = providers.stream()
                .filter(provider -> provider instanceof org.eclipse.persistence.jpa.PersistenceProvider)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Cannot found EclipseLink persistence provider"));
        Map<String, String> vendorProps = prepareVendorProps();
        return eclipseLinkPersistenceProvider.createContainerEntityManagerFactory(createPersistenceUserInfo(), vendorProps);
    }

    private Map<String, String> prepareVendorProps() {
        return Map.of("eclipselink.ddl-generation", "drop-and-create-tables");
    }

    private PersistenceUnitInfo createPersistenceUserInfo() {
        return new PersistenceUnitInfo() {
            @Override
            public String getPersistenceUnitName() {
                return "EclipseLinkUnit";
            }

            @Override
            public String getPersistenceProviderClassName() {
                return org.eclipse.persistence.jpa.PersistenceProvider.class.getSimpleName();
            }

            @Override
            public PersistenceUnitTransactionType getTransactionType() {
                return PersistenceUnitTransactionType.RESOURCE_LOCAL;
            }

            @Override
            public DataSource getJtaDataSource() {
                return null;
            }

            @Override
            public DataSource getNonJtaDataSource() {
                return buildDataSource();
            }

            @Override
            public List<String> getMappingFileNames() {
                return List.of();
            }

            @Override
            public List<URL> getJarFileUrls() {
                return List.of();
            }

            @Override
            public URL getPersistenceUnitRootUrl() {
                return null;
            }

            @Override
            public List<String> getManagedClassNames() {
                return Set.of(Student.class, Subject.class, Mark.class)
                        .stream()
                        .map(Class::getSimpleName)
                        .toList();
            }

            @Override
            public boolean excludeUnlistedClasses() {
                return true;
            }

            @Override
            public SharedCacheMode getSharedCacheMode() {
                return SharedCacheMode.UNSPECIFIED;
            }

            @Override
            public ValidationMode getValidationMode() {
                return ValidationMode.AUTO;
            }

            @Override
            public Properties getProperties() {
                Properties props = new Properties();
                props.putAll(prepareVendorProps());
                return props;
            }

            @Override
            public String getPersistenceXMLSchemaVersion() {
                return null;
            }

            @Override
            public ClassLoader getClassLoader() {
                return AbstractBenchmark.class.getClassLoader();
            }

            @Override
            public void addTransformer(ClassTransformer transformer) {

            }

            @Override
            public ClassLoader getNewTempClassLoader() {
                return AbstractBenchmark.class.getClassLoader();
            }
        };
    }
}
