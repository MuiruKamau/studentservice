
package com.school.studentservice.entity;

import com.school.studentservice.entity.ExamType;
import com.school.studentservice.entity.Term;
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

    private String grade;
    private int gradePoints;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Term term;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ExamType examType;
}