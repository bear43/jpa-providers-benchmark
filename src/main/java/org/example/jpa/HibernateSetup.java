package org.example.jpa;

import jakarta.persistence.EntityManagerFactory;
import org.example.model.Mark;
import org.example.model.Student;
import org.example.model.Subject;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

public class HibernateSetup implements Setup {
    @Override
    public EntityManagerFactory configure() {
        final StandardServiceRegistry registry =
                new StandardServiceRegistryBuilder()
                        .loadProperties("hibernate.properties")
                        .build();
        try {
            return new MetadataSources(registry)
                    .addAnnotatedClasses(Student.class, Subject.class, Mark.class)
                    .buildMetadata()
                    .buildSessionFactory();
        } catch (Exception e) {
            // The registry would be destroyed by the SessionFactory, but we
            // had trouble building the SessionFactory so destroy it manually.
            StandardServiceRegistryBuilder.destroy(registry);
            throw e;
        }
    }
}
