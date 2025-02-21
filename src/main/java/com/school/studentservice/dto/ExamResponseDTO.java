package com.school.studentservice.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@NoArgsConstructor
public class ExamResponseDTO {
    private Long id;
    private LocalDate examDate;
    private String subject;
    private double score;
    private Long studentId;
    private String grade;
    private int gradePoints;
    private String term;

    public ExamResponseDTO(Long id, LocalDate examDate, String subject, double score, Long studentId, String grade, int gradePoints,String term) {
        this.id = id;
        this.examDate = examDate;
        this.subject = subject;
        this.score = score;
        this.studentId = studentId;
        this.grade = grade;
        this.gradePoints = gradePoints;
        this.term = term;
    }
}
