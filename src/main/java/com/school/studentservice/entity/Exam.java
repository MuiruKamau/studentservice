package com.school.studentservice.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "exams")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Exam {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate examDate;

    @Column(nullable = false)
    private Long subjectId;

    @Column(nullable = false)
    private double score;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    private String grade; // Added Grade
    private int gradePoints; // Added Grade Points

    @Column(nullable = false) // Add term field - Ensure not nullable
    private String term; // e.g., "Term 1", "Term 2", "Term 3"
}