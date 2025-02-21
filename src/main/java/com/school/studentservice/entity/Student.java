package com.school.studentservice.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.Set;

@Entity
@Table(name = "students")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String admissionNumber;

    @Column(nullable = false)
    private LocalDate dateOfBirth;

    @Column(nullable = false)
    private LocalDate enrollmentDate;

    @Column(nullable = false, name = "student_address") // Rename Student's address column
    private String address;

    @Column(nullable = false)
    private String contactNumber;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "parentName", column = @Column(name = "parent_name")), // Rename embedded parentName
            @AttributeOverride(name = "contact", column = @Column(name = "parent_contact")),   // Rename embedded contact
            @AttributeOverride(name = "address", column = @Column(name = "parent_address"))    // Rename embedded address
    })
    private ParentDetails parentDetails;

    @Column(nullable = false)
    private String className;

    @Column(nullable = false)
    private String streamName;

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Exam> exams;
}
