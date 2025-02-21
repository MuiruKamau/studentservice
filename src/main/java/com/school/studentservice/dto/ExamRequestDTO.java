package com.school.studentservice.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class ExamRequestDTO {
    private Long studentId;
    private LocalDate examDate;
    private String subject;
    private double score;
}