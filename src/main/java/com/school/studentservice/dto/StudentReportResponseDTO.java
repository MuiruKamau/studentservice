package com.school.studentservice.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

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

    // Modified to be maps of Term -> Map<ExamType, Value>
    private Map<String, Map<String, Double>> averageScorePerExamType; // Term -> (ExamType -> Average Score)
    private Map<String, Map<String, Double>> averagePointsPerExamType; // Term -> (ExamType -> Average Points)
    private Map<String, Map<String, String>> overallGradePerExamType;  // Term -> (ExamType -> Overall Grade)


    public StudentReportResponseDTO(Long studentId, String studentName, String admissionNumber, Long classId, Long streamId, String className, String streamName, LocalDate reportDate, List<ExamResponseDTO> results, Map<String, Map<String, Double>> averageScorePerExamType, Map<String, Map<String, Double>> averagePointsPerExamType, Map<String, Map<String, String>> overallGradePerExamType) {
        this.studentId = studentId;
        this.studentName = studentName;
        this.admissionNumber = admissionNumber;
        this.classId = classId;
        this.streamId = streamId;
        this.className = className;
        this.streamName = streamName;
        this.reportDate = reportDate;
        this.results = results;
        this.averageScorePerExamType = averageScorePerExamType;
        this.averagePointsPerExamType = averagePointsPerExamType;
        this.overallGradePerExamType = overallGradePerExamType;
    }
}