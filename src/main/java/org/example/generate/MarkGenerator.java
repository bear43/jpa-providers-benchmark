package org.example.generate;

import org.example.model.Mark;
import org.example.model.MarkEnum;
import org.example.model.Student;
import org.example.model.Subject;
import org.instancio.Instancio;
import org.instancio.Select;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Stream;

public class MarkGenerator implements Generator<Mark> {

    public static final LocalDateTime MIN_DATETIME_DEFAULT = LocalDateTime.of(2023, 1, 1, 8, 0, 0);
    public static final LocalDateTime MAX_DATETIME_DEFAULT = LocalDateTime.of(2024, 1, 1, 14, 0, 0);

    private final LocalDateTime minDatetime;
    private final LocalDateTime maxDatetime;
    private final Set<Student> students;
    private final Set<Subject> subjects;

    public MarkGenerator(LocalDateTime minDatetime, LocalDateTime maxDatetime, Set<Student> students, Set<Subject> subjects) {
        this.minDatetime = minDatetime;
        this.maxDatetime = maxDatetime;
        this.students = students;
        this.subjects = subjects;
    }

    public MarkGenerator(Set<Student> students, Set<Subject> subjects) {
        this(MIN_DATETIME_DEFAULT, MAX_DATETIME_DEFAULT, students, subjects);
    }

    @Override
    public Stream<Mark> generate(int max) {
        if (max != 0) {
            throw new IllegalStateException("Given generator creates data from `subjects` && `students`. max should be always equal to 0");
        }
        return students.stream()
                .flatMap(student -> subjects.stream()
                        .map(subject -> {
                            Mark mark = generate();
                            mark.setStudent(student);
                            mark.setSubject(subject);
                            return mark;
                        }))
                .flatMap(mark -> Stream.of(MarkEnum.values())
                        .map(markEnum -> mark.toBuilder()
                                .value(markEnum)
                                .build()));
    }

    @Override
    public Mark generate() {
        return Instancio.of(Mark.class)
                .generate(Select.field(Mark::getCreatedAt), gen -> gen.temporal().localDateTime().range(minDatetime, maxDatetime))
                .ignore(Select.field(Mark::getStudent))
                .ignore(Select.field(Mark::getSubject))
                .create();
    }
}
