package org.example.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import lombok.RequiredArgsConstructor;

import java.util.concurrent.CountDownLatch;
import java.util.function.Consumer;
import java.util.function.Function;

public class BaseDao {

    public static  <T> T inTransaction(Function<EntityManager, T> action) {
        boolean shouldCloseEntityManager = !EntityManagerTracker.isInstantiated();
        EntityManager entityManager = EntityManagerTracker.get();
        try {
            if (entityManager.isJoinedToTransaction()) {
                return action.apply(entityManager);
            }
            EntityTransaction transaction = entityManager.getTransaction();
            try {
                transaction.begin();
                T result = action.apply(entityManager);
                transaction.commit();
                return result;
            } catch (Exception ex) {
                transaction.rollback();
                throw ex;
            }
        } finally {
            if (shouldCloseEntityManager) {
                entityManager.close();
                EntityManagerTracker.evict();
            }
        }
    }

    public static void inTransaction(Consumer<EntityManager> action) {
        boolean shouldCloseEntityManager = !EntityManagerTracker.isInstantiated();
        EntityManager entityManager = EntityManagerTracker.get();
        try {
            if (entityManager.isJoinedToTransaction()) {
                action.accept(entityManager);
                return;
            }
            EntityTransaction transaction = entityManager.getTransaction();
            try {
                transaction.begin();
                action.accept(entityManager);
                transaction.commit();
            } catch (Exception ex) {
                transaction.rollback();
                throw ex;
            }
        } finally {
            if (shouldCloseEntityManager) {
                entityManager.close();
                EntityManagerTracker.evict();
            }
        }
    }

    public static <T> T inNewEm(Function<EntityManager, T> action) {
        try (EntityManager entityManager = EntityManagerTracker.getEntityManagerFactory().get().createEntityManager()) {
            if (entityManager.isJoinedToTransaction()) {
                return action.apply(entityManager);
            }
            EntityTransaction transaction = entityManager.getTransaction();
            try {
                transaction.begin();
                T result = action.apply(entityManager);
                transaction.commit();
                return result;
            } catch (Exception ex) {
                transaction.rollback();
                throw ex;
            }
        }
    }
}
