package org.example;

import jakarta.persistence.EntityManagerFactory;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;
import org.example.dao.StudentDao;
import org.example.model.Student;
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
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;


@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
@Warmup(iterations = 5)
@Measurement(iterations = 10)
@SpringBootApplication
public class SpringBenchmark {

    private static final int FULLNAME_LENGTH = 32;
    private static final int TOTAL_STUDENTS = 10_000;

    private ConfigurableApplicationContext context;
    private StudentDao studentDao;

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(SpringBenchmark.class.getSimpleName())
                .forks(0)
                .build();

        new Runner(opt).run();
    }

    @Setup
    public synchronized void setup() {
        try {
            String args = "";
            if(context == null) {
                context = SpringApplication.run(SpringBenchmark.class, args );
            }
            studentDao = context.getBean(StudentDao.class);
            PlatformTransactionManager transactionManager = context.getBean(PlatformTransactionManager.class);
            TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
            Random random = new Random();
            transactionTemplate.executeWithoutResult(status -> {
                IntStream.range(1, TOTAL_STUDENTS + 1)
                        .mapToObj(id -> createStudent(random, id))
                        .forEach(studentDao::save);
            });
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    @TearDown
    public void tearDown() {
        context.close();
    }

    @Benchmark
    public void readAllFromFlatTable(Blackhole blackhole) {
        List<Student> students = studentDao.findAll();
        blackhole.consume(students);
    }
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
