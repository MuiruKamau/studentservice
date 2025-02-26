
package com.school.studentservice.dto;

import com.school.studentservice.entity.ExamType;
import com.school.studentservice.entity.Term;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@NoArgsConstructor
public class ExamResponseDTO {
    private Long id;
    private LocalDate examDate;
    private Long subjectId;
    private double score;
    private Long studentId;
    private String grade;
    private int gradePoints;
    private Term term;
    private ExamType examType;
    private String subjectName;

    public ExamResponseDTO(Long id, LocalDate examDate, Long subjectId, double score, Long studentId, String grade, int gradePoints, Term term, ExamType examType, String subjectName) {
        this.id = id;
        this.examDate = examDate;
        this.subjectId = subjectId;
        this.score = score;
        this.studentId = studentId;
        this.grade = grade;
        this.gradePoints = gradePoints;
        this.term = term;
        this.examType = examType;
        this.subjectName = subjectName;
    }
}