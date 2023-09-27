package org.example;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import org.example.dao.Dao;
import org.example.generate.Generator;
import org.example.generate.MarkGenerator;
import org.example.generate.StudentGenerator;
import org.example.generate.SubjectGenerator;
import org.example.model.Mark;
import org.example.model.Student;
import org.example.model.Subject;
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


@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
@Warmup(iterations = 5)
@Measurement(iterations = 10)
@SpringBootApplication
public class ReadBenchmark {
    private static final AtomicReference<CountDownLatch> countDownLatch = new AtomicReference<>(new CountDownLatch(0));
    private ConfigurableApplicationContext context;
    private Dao dao;

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
        List<Student> students = create(studentGenerator, BenchmarkRunner.TOTAL_STUDENTS, dao.getStudentDao());
        List<Subject> subjects = create(subjectGenerator, BenchmarkRunner.TOTAL_SUBJECTS, dao.getSubjectDao());

        Generator<Mark> markGenerator = new MarkGenerator(students, subjects);
        create(markGenerator, 0, dao.getMarkDao());
    }

    private <T> List<T> create(Generator<T> generator, int size, JpaRepository<T, Long> repository) {
        return dao.getTransactionTemplate().execute(status -> generator.generate(size)
                .map(repository::save)
                .collect(Collectors.toList()));
    }

    @TearDown
    public void tearDown() {
        context.close();
        countDownLatch.get().countDown();
    }

    @Benchmark
    public void readAllFromFlatTable(Blackhole blackhole) {
        List<Student> students = dao.getStudentDao().findAll();
        blackhole.consume(students);
    }

    @Benchmark
    public void readAllFromFlatTableInTx(Blackhole blackhole) {
        dao.getTransactionTemplate().executeWithoutResult(status -> {
            List<Student> students = dao.getStudentDao().findAll();
            blackhole.consume(students);
        });
    }

    @Benchmark
    public void readAllFromJoinTable(Blackhole blackhole) {
        List<Mark> marks = dao.getMarkDao().findAll();
        blackhole.consume(marks);
    }

    @Benchmark
    public void readAllFromJoinTableInTx(Blackhole blackhole) {
        dao.getTransactionTemplate().executeWithoutResult(status -> {
            List<Mark> marks = dao.getMarkDao().findAll();
            blackhole.consume(marks);
        });
    }

    @Benchmark
    public void readAllFromJoinTableInTxAccessStudentGetter(Blackhole blackhole) {
        dao.getTransactionTemplate().executeWithoutResult(status -> {
            List<Mark> marks = dao.getMarkDao().findAll();
            marks.forEach(mark -> blackhole.consume(mark.getStudent()));
            blackhole.consume(marks);
        });
    }

    @Benchmark
    public void readAllFromJoinTableInTxAccessSubjectGetter(Blackhole blackhole) {
        dao.getTransactionTemplate().executeWithoutResult(status -> {
            List<Mark> marks = dao.getMarkDao().findAll();
            marks.forEach(mark -> blackhole.consume(mark.getSubject()));
            blackhole.consume(marks);
        });
    }

    @Benchmark
    public void readAllFromJoinTableInTxAccessLazyGetters(Blackhole blackhole) {
        dao.getTransactionTemplate().executeWithoutResult(status -> {
            List<Mark> marks = dao.getMarkDao().findAll();
            marks.forEach(mark -> {
                blackhole.consume(mark.getStudent());
                blackhole.consume(mark.getSubject());
            });
            blackhole.consume(marks);
        });
    }
}
