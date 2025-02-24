package com.school.studentservice.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class ExamRequestDTO {
    private Long studentId;
    private LocalDate examDate;
    private Long subjectId;
    private double score;
    private String term; // Add term to request DTO
}
