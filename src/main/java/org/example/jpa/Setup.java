package org.example.jpa;

import jakarta.persistence.EntityManagerFactory;

public interface Setup {
    EntityManagerFactory configure();
}
