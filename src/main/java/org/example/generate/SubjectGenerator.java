package org.example.generate;

import org.example.model.Subject;
import org.instancio.Instancio;
import org.instancio.Select;

import java.util.stream.Stream;

public class SubjectGenerator implements Generator<Subject> {

    public static final int NAME_MAX_LENGTH = 20;
    private final int nameMaxLen;

    public SubjectGenerator(int nameMaxLen) {
        this.nameMaxLen = nameMaxLen;
    }

    public SubjectGenerator() {
        this(NAME_MAX_LENGTH);
    }

    @Override
    public Stream<Subject> generate(int max) {
        return Stream.generate(this::generate)
                .limit(max);
    }

    @Override
    public Subject generate() {
        return Instancio.of(Subject.class)
                .ignore(Select.field(Subject::getId))
                .generate(Select.field(Subject::getName), gen -> gen.string().length(nameMaxLen))
                .create();
    }
}
