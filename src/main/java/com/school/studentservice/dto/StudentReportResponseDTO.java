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
    private Long classId;
    private Long streamId;
    private String className;
    private String streamName;
    private LocalDate reportDate;
    private List<ExamResponseDTO> results;
    private Double averageScore;
    private Integer totalPoints;
    private String overallGrade;

    // Calculated fields that could be added (optional)
    private Double averagePoints;  // Average points per subject

    public StudentReportResponseDTO(Long studentId, String studentName, String admissionNumber, Long classId, Long streamId, String className, String streamName, LocalDate reportDate, List<ExamResponseDTO> results, Double averageScore, Integer totalPoints, String overallGrade) {
        this.studentId = studentId;
        this.studentName = studentName;
        this.admissionNumber = admissionNumber;
        this.classId = classId;
        this.streamId = streamId;
        this.className = className;
        this.streamName = streamName;
        this.reportDate = reportDate;
        this.results = results;
        this.averageScore = averageScore;
        this.totalPoints = totalPoints;
        this.overallGrade = overallGrade;

        // Calculate and set the average points
        this.averagePoints = results.isEmpty() ? 0.0 : (double) totalPoints / results.size();
    }
}