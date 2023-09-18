package org.example;

import jakarta.persistence.EntityManagerFactory;
import lombok.Getter;
import lombok.Setter;
import org.example.dao.StudentDao;
import org.example.jpa.DataNucleusSetup;
import org.example.jpa.Setup;
import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
@Warmup(iterations = 5)
@Measurement(iterations = 10)
public class DataNucleusBenchmark extends AbstractBenchmark{
    @Getter
    private final Setup setup = new DataNucleusSetup();
    @Getter
    @Setter
    private StudentDao studentDao;
    @Getter
    @Setter
    private EntityManagerFactory emf;
}