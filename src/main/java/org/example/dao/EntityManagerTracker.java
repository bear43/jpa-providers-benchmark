package org.example.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import lombok.Getter;
import lombok.Setter;

import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantLock;

public final class EntityManagerTracker {
    private static final ThreadLocal<EntityManager> entityManagerThreadLocal = new ThreadLocal<>();
    @Getter
    private static final AtomicReference<EntityManagerFactory> entityManagerFactory = new AtomicReference<>();

    public static EntityManager get() {
        EntityManager entityManager = entityManagerThreadLocal.get();
        if (entityManager == null || !entityManager.isOpen()) {
            entityManagerThreadLocal.remove();
            entityManager = entityManagerFactory.get().createEntityManager();
            entityManagerThreadLocal.set(entityManager);
        }
        return entityManager;
    }

    public static boolean isInstantiated() {
        return entityManagerThreadLocal.get() != null;
    }

    public static void evict() {
        EntityManager entityManager = entityManagerThreadLocal.get();
        if (entityManager == null) return;
        if (entityManager.isOpen()) {
            entityManager.close();
        }
        entityManagerThreadLocal.remove();
    }
}
