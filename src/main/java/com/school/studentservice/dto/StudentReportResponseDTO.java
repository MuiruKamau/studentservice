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
    private LocalDate reportDate;
    private List<ExamResponseDTO> results;
    private Double averageScore;
    private Integer totalPoints;
    private String overallGrade;

    public StudentReportResponseDTO(Long studentId, String studentName, String admissionNumber, Long classId, Long streamId, LocalDate reportDate, List<ExamResponseDTO> results, Double averageScore, Integer totalPoints,String overallGrade) {
        this.studentId = studentId;
        this.studentName = studentName;
        this.admissionNumber = admissionNumber;
        this.classId = classId;
        this.streamId = streamId;
        this.reportDate = reportDate;
        this.results = results;
        this.averageScore = averageScore;
        this.totalPoints = totalPoints;
        this.overallGrade = overallGrade;
    }
}