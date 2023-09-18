package org.example.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "student")
@ToString
@EqualsAndHashCode
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Student implements Serializable {

    @GeneratedValue
    @Id
    @Column(name = "id", nullable = false)
    Long id;

    @Column(name = "fullname", nullable = false)
    String fullName;

    @Column(name = "course", nullable = false)
    Integer course;
}
