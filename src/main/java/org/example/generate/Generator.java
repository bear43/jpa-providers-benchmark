package org.example.generate;

import java.util.stream.Stream;

public interface Generator<T> {
    Stream<T> generate(int max);
    T generate();
}
