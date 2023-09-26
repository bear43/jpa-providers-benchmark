package org.example;

import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

public class BenchmarkRunner {
    public static final int TOTAL_SUBJECTS = 10;
    public static final int TOTAL_STUDENTS = 10_000;

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(ReadBenchmark.class.getSimpleName())
                .include(WriteBenchmark.class.getSimpleName())
                .forks(1)
                .build();
        new Runner(opt).run();
    }
}
