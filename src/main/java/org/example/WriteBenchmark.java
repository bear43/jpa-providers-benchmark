package org.example;

import org.example.dao.Dao;
import org.example.generate.Generator;
import org.example.generate.MarkGenerator;
import org.example.generate.StudentGenerator;
import org.example.generate.SubjectGenerator;
import org.example.model.Mark;
import org.example.model.Student;
import org.example.model.Subject;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static org.example.BenchmarkRunner.TOTAL_STUDENTS;
import static org.example.BenchmarkRunner.TOTAL_SUBJECTS;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
@Warmup(iterations = 5)
@Measurement(iterations = 10)
@SpringBootApplication
public class WriteBenchmark {
    private static final AtomicReference<CountDownLatch> countDownLatch = new AtomicReference<>(new CountDownLatch(0));
    private ConfigurableApplicationContext context;
    private Dao dao;
    private final List<Student> students = new ArrayList<>();
    private final List<Subject> subjects = new ArrayList<>();
    private final List<Mark> marks = new ArrayList<>();

    @Setup
    public void setup() throws InterruptedException {
        countDownLatch.get().await();
        try {
            context = SpringApplication.run(ReadBenchmark.class, "");
            dao = new Dao(context);
            fillData();
            if (countDownLatch.get().getCount() == 0) {
                countDownLatch.set(new CountDownLatch(1));
            }
        } catch(Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    private void fillData() {
        Generator<Student> studentGenerator = new StudentGenerator();
        Generator<Subject> subjectGenerator = new SubjectGenerator();
        students.addAll(studentGenerator.generate(TOTAL_STUDENTS).toList());
        subjects.addAll(subjectGenerator.generate(TOTAL_SUBJECTS).toList());

        Generator<Mark> markGenerator = new MarkGenerator(students, subjects);
        marks.addAll(markGenerator.generate(0).toList());
    }

    @TearDown
    public void tearDown() {
        context.close();
        countDownLatch.get().countDown();
    }

    @Benchmark
    public void insertStudents(Blackhole blackhole) {
        List<Student> result = dao.getStudentDao().saveAllAndFlush(students);
        blackhole.consume(result);
    }

    @Benchmark
    public void insertSubjects(Blackhole blackhole) {
        List<Subject> result = dao.getSubjectDao().saveAllAndFlush(subjects);
        blackhole.consume(result);
    }

    @Benchmark
    public void insertMarks(Blackhole blackhole) {
        List<Student> studentsResult = dao.getStudentDao().saveAllAndFlush(students);
        List<Subject> subjectsResult = dao.getSubjectDao().saveAllAndFlush(subjects);
        List<Mark> marksResult = dao.getMarkDao().saveAllAndFlush(marks);
        blackhole.consume(studentsResult);
        blackhole.consume(subjectsResult);
        blackhole.consume(marksResult);
    }
}
