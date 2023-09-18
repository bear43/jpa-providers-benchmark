package org.example.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import lombok.RequiredArgsConstructor;
import org.example.model.Student;

import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import static org.example.dao.BaseDao.inTransaction;

@RequiredArgsConstructor
public class StudentDao {
    private static final String SELECT_ALL = "select student from Student student";

    public List<Student> findAll() {
        return inTransaction(em -> {
            return em.createQuery(SELECT_ALL, Student.class).getResultList();
        });
    }

    public void create(Student student) {
        inTransaction(em -> {
            em.persist(student);
        });
    }

}
