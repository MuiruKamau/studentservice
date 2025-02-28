package com.school.studentservice.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@Table(name = "students")
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @Column(unique = true)
    private String admissionNumber;
    private LocalDate dateOfBirth;
    private LocalDate enrollmentDate;
    @Column(name = "student_address") // Map to student_address column
    private String address; // **Re-add address field to Student entity**
    private String contactNumber;
    @Embedded
    private ParentDetails parentDetails;
    private Long classId;
    private Long streamId;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "student_learning_subjects",
            joinColumns = @JoinColumn(name = "student_id"),
            inverseJoinColumns = @JoinColumn(name = "learning_subject_id")
    )
    private Set<LearningSubject> learningSubjects = new HashSet<>();

    public Student(Long id, String name, String admissionNumber, LocalDate dateOfBirth, LocalDate enrollmentDate, String address, String contactNumber, ParentDetails parentDetails, Long classId, Long streamId, Set<LearningSubject> learningSubjects) {
        this.id = id;
        this.name = name;
        this.admissionNumber = admissionNumber;
        this.dateOfBirth = dateOfBirth;
        this.enrollmentDate = enrollmentDate;
        this.address = address; // **Re-add address initialization**
        this.contactNumber = contactNumber;
        this.parentDetails = parentDetails;
        this.classId = classId;
        this.streamId = streamId;
        this.learningSubjects = learningSubjects;
    }
}