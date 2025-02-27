package com.school.studentservice.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
public class StudentReportResponseDTO {
    private Long id;
    private String name;
    private String admissionNumber;
    private Long classId;
    private Long streamId;
    private String className;
    private String streamName;
    private LocalDate reportDate;
    private List<ExamResponseDTO> examResults;

    // Modified to be maps of Term -> Map<ExamType, Value>
    private Map<String, Map<String, Double>> averageScorePerExamType; // Term -> (ExamType -> Average Score)
    private Map<String, Map<String, Double>> averagePointsPerTermAndExamType; // Term -> (ExamType -> Average Points)
    private Map<String, Map<String, String>> overallGradePerTermAndExamType;  // Term -> (ExamType -> Overall Grade)
    private Map<String, Map<String, Integer>> classRankPerExamType;
    private Map<String, Map<String, Integer>> streamRankPerExamType;


    public StudentReportResponseDTO(Long id, String name, String admissionNumber, Long classId, Long streamId, String className, String streamName, LocalDate reportDate, List<ExamResponseDTO> examResults, Map<String, Map<String, Double>> averageScorePerExamType, Map<String, Map<String, Double>> averagePointsPerTermAndExamType, Map<String, Map<String, String>> overallGradePerTermAndExamType, Map<String, Map<String, Integer>> classRankPerExamType, Map<String, Map<String, Integer>> streamRankPerExamType) {
        this.id = id;
        this.name = name;
        this.admissionNumber = admissionNumber;
        this.classId = classId;
        this.streamId = streamId;
        this.className = className;
        this.streamName = streamName;
        this.reportDate = reportDate;
        this.examResults = examResults;
        this.averageScorePerExamType = averageScorePerExamType;
        this.averagePointsPerTermAndExamType = averagePointsPerTermAndExamType;
        this.overallGradePerTermAndExamType = overallGradePerTermAndExamType;
        this.classRankPerExamType = classRankPerExamType;
        this.streamRankPerExamType = streamRankPerExamType;
    }
}