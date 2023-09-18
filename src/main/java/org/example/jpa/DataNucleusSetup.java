package org.example.jpa;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class DataNucleusSetup implements Setup {
    @Override
    public EntityManagerFactory configure() {
        return Persistence.createEntityManagerFactory("MyUnit");
    }
}
