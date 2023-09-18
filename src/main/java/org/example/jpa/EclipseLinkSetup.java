package org.example.jpa;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class EclipseLinkSetup implements Setup {
    @Override
    public EntityManagerFactory configure() {
        return Persistence.createEntityManagerFactory("MyUnit");
    }
}
