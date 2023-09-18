package org.example.generate;

import org.example.model.Student;
import org.instancio.Instancio;
import org.instancio.Select;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StudentGenerator implements Generator<Student> {

    public static final int FULLNAME_LENGTH_DEFAULT = 32;
    public static final int MAX_COURSE_DEFAULT = 4;

    private final int fullnameLen;
    private final int maxCourse;

    public StudentGenerator(int fullnameLen, int maxCourse) {
        this.fullnameLen = fullnameLen;
        this.maxCourse = maxCourse;
    }

    public StudentGenerator() {
        this(FULLNAME_LENGTH_DEFAULT, MAX_COURSE_DEFAULT);
    }

    @Override
    public Stream<Student> generate(int max) {
        return Stream.generate(this::generate)
                .limit(max);
    }

    @Override
    public Student generate() {
        return Instancio.of(Student.class)
                .generate(Select.field(Student::getFullName), gen -> gen.string().length(fullnameLen))
                .generate(Select.field(Student::getCourse), gen -> gen.ints().range(1, maxCourse))
                .ignore(Select.field(Student::getId))
                .create();
    }
}
