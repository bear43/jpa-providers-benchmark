package org.example;

import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import jakarta.persistence.EntityManager;
import org.example.dao.MarkDao;
import org.example.dao.StudentDao;
import org.example.dao.SubjectDao;
import org.example.generate.Generator;
import org.example.generate.MarkGenerator;
import org.example.generate.StudentGenerator;
import org.example.generate.SubjectGenerator;
import org.example.model.Mark;
import org.example.model.Student;
import org.example.model.Subject;
import org.instancio.Instancio;
import org.instancio.Select;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.support.TransactionTemplate;


@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
@Warmup(iterations = 5)
@Measurement(iterations = 10)
@SpringBootApplication
public class ReadBenchmark {
    public static final int TOTAL_SUBJECTS = 1;
    public static final int TOTAL_STUDENTS = 50_000;

    private ConfigurableApplicationContext context;
    private StudentDao studentDao;
    private SubjectDao subjectDao;
    private MarkDao markDao;
    private TransactionTemplate transactionTemplate;
    private static final AtomicReference<CountDownLatch> countDownLatch = new AtomicReference<>(new CountDownLatch(0));

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(ReadBenchmark.class.getSimpleName())
                .forks(1)
                .build();

        new Runner(opt).run();
    }

    @Setup
    public void setup() throws InterruptedException {
        countDownLatch.get().await();
        try {
            context = SpringApplication.run(ReadBenchmark.class, "");
            studentDao = context.getBean(StudentDao.class);
            subjectDao = context.getBean(SubjectDao.class);
            markDao = context.getBean(MarkDao.class);
            transactionTemplate = context.getBean(TransactionTemplate.class);
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
        Set<Student> students = create(studentGenerator, TOTAL_STUDENTS, studentDao);
        Set<Subject> subjects = create(subjectGenerator, TOTAL_SUBJECTS, subjectDao);

        Generator<Mark> markGenerator = new MarkGenerator(students, subjects);
        Set<Mark> marks = create(markGenerator, 0, markDao);
    }

    private <T> Set<T> create(Generator<T> generator, int size, JpaRepository<T, Long> repository) {
        return transactionTemplate.execute(status -> generator.generate(size)
                .map(repository::save)
                .collect(Collectors.toSet()));
    }

    @TearDown
    public void tearDown() {
        context.close();
        countDownLatch.get().countDown();
    }

    @Benchmark
    public void readAllFromFlatTable(Blackhole blackhole) {
        List<Student> students = studentDao.findAll();
        blackhole.consume(students);
    }

    @Benchmark
    public void readAllFromJoinTable(Blackhole blackhole) {
        List<Mark> marks = markDao.findAll();
        blackhole.consume(marks);
    }
}
