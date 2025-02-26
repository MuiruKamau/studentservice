package com.school.studentservice.dto;

import com.school.studentservice.entity.ExamType;
import com.school.studentservice.entity.Term;
import lombok.Data;
import java.time.LocalDate;

@Data
public class ExamRequestDTO {
    private Long studentId;
    private LocalDate examDate;
    private Long subjectId;
    private double score;
    private Term term;
    private ExamType examType;
}
