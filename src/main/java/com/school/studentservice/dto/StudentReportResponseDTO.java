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
    private Long classId;        // Keep classId (Long)
    private Long streamId;       // Keep streamId (Long)
    private String className;     // Add className (String) for display
    private String streamName;    // Add streamName (String) for display
    private LocalDate reportDate;
    private List<ExamResponseDTO> results;
    private Double averageScore;
    private Integer totalPoints;
    private String overallGrade;

    public StudentReportResponseDTO(Long studentId, String studentName, String admissionNumber, Long classId, Long streamId, String className, String streamName, LocalDate reportDate, List<ExamResponseDTO> results, Double averageScore, Integer totalPoints, String overallGrade) {
        this.studentId = studentId;
        this.studentName = studentName;
        this.admissionNumber = admissionNumber;
        this.classId = classId;
        this.streamId = streamId;
        this.className = className;      // Initialize className
        this.streamName = streamName;     // Initialize streamName
        this.reportDate = reportDate;
        this.results = results;
        this.averageScore = averageScore;
        this.totalPoints = totalPoints;
        this.overallGrade = overallGrade;
    }
}