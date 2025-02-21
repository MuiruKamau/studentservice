package com.school.studentservice.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
public class StudentReportResponseDTO {
    private Long studentId;
    private String studentName;
    private String admissionNumber;
    private String className; // Now String, not ID
    private String streamName; // Now String, not ID
    private LocalDate reportDate;
    private List<ExamResponseDTO> results;
    private Double averageScore;
    private Integer totalPoints;

    public StudentReportResponseDTO(Long studentId, String studentName, String admissionNumber, String className, String streamName, LocalDate reportDate, List<ExamResponseDTO> results, Double averageScore, Integer totalPoints) {
        this.studentId = studentId;
        this.studentName = studentName;
        this.admissionNumber = admissionNumber;
        this.className = className;
        this.streamName = streamName;
        this.reportDate = reportDate;
        this.results = results;
        this.averageScore = averageScore;
        this.totalPoints = totalPoints;
    }
}