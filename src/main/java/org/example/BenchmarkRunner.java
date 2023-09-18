package org.example;

import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

public class BenchmarkRunner {
    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
//                .include(HibernateBenchmark.class.getSimpleName())
                //.include(DataNucleusBenchmark.class.getSimpleName())
                .include(EclipseLinkBenchmark.class.getSimpleName())
                .forks(0)
                .build();

        new Runner(opt).run();
    }
}
