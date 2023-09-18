package org.example;

import jakarta.persistence.EntityManagerFactory;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.dao.BaseDao;
import org.example.dao.EntityManagerTracker;
import org.example.dao.StudentDao;
import org.example.jpa.Setup;
import org.example.model.Student;
import org.instancio.Instancio;
import org.instancio.Select;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.util.List;
import java.util.Random;
import java.util.stream.LongStream;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PROTECTED)
public abstract class AbstractBenchmark {

    static long TOTAL_STUDENTS = 10000;
    static int FULLNAME_LENGTH = 32;

    @org.openjdk.jmh.annotations.Setup
    public void setup() {
        EntityManagerFactory emf = getSetup().configure();
        setEmf(emf);
        EntityManagerTracker.getEntityManagerFactory().set(emf);
        StudentDao studentDao = new StudentDao();
        setStudentDao(studentDao);
        BaseDao.inTransaction(em -> {
            Random random = new Random();
            LongStream.range(1, TOTAL_STUDENTS + 1)
                    .mapToObj(id -> createStudent(random, id))
                    .forEach(studentDao::create);
        });
    }

    @TearDown
    public void tearDown() {
        EntityManagerFactory emf = getEmf();
        if (emf == null || !emf.isOpen()) throw new IllegalStateException("emf in the end of benchmark should be not null and open!");
        emf.close();
    }

    @Benchmark
    public void readAllFromFlatTable(Blackhole blackhole) {
        List<Student> students = getStudentDao().findAll();
        blackhole.consume(students);
    }

    protected abstract Setup getSetup();
    protected abstract void setStudentDao(StudentDao studentDao);
    protected abstract StudentDao getStudentDao();
    protected abstract void setEmf(EntityManagerFactory emf);
    protected abstract EntityManagerFactory getEmf();

    private Student createStudent(Random random, long id) {
        Student student = new Student();
        student.setCourse(random.nextInt() % 4);
        student.setFullName(generateFullName(id));
        return student;
    }

    private String generateFullName(Long id) {
        return Instancio.of(String.class)
                .withSeed(id)
                .generate(Select.root(), gen -> gen.string().length(FULLNAME_LENGTH))
                .create();
    }
}
