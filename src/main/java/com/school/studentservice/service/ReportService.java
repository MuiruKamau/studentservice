package com.school.studentservice.service;

import com.school.studentservice.client.ConfigurationServiceClient;
import com.school.studentservice.dto.ExamResponseDTO;
import com.school.studentservice.dto.GradeCriteriaResponseDTO;
import com.school.studentservice.dto.StudentReportResponseDTO;
import com.school.studentservice.dto.SchoolClassResponseDTO;
import com.school.studentservice.dto.StreamResponseDTO;
import com.school.studentservice.dto.LearningSubjectResponseDTO;
import com.school.studentservice.entity.Student;
import com.school.studentservice.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReportService {

    private final StudentRepository studentRepository;
    private final ExamService examService;
    private final ConfigurationServiceClient configurationServiceClient;

    @Autowired
    public ReportService(StudentRepository studentRepository, ExamService examService, ConfigurationServiceClient configurationServiceClient) {
        this.studentRepository = studentRepository;
        this.examService = examService;
        this.configurationServiceClient = configurationServiceClient;
    }

    public StudentReportResponseDTO generateStudentReport(Long studentId, String term, String examType, String subject) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Student not found with id: " + studentId));

        // Fetch Class Name from Configuration Service using classId
        SchoolClassResponseDTO classDto = configurationServiceClient.getClassById(student.getClassId());
        String className = (classDto != null) ? classDto.getName() : "N/A";

        // Fetch Stream Name from Configuration Service using streamId
        StreamResponseDTO streamDto = configurationServiceClient.getStreamById(student.getStreamId());
        String streamName = (streamDto != null) ? streamDto.getName() : "N/A";

        // Get exams for student with filters now being applied
        List<ExamResponseDTO> examResults = examService.getExamsByStudentIdWithFilters(studentId, term, examType, subject);

        // Enhance ExamResponseDTOs in results to include subject names
        List<ExamResponseDTO> enhancedExamResults = examResults.stream()
                .map(this::enhanceExamResponseWithSubjectName)
                .collect(Collectors.toList());

        // Group exams by Term first, then by ExamType
        Map<String, List<ExamResponseDTO>> examsGroupedByTerm = enhancedExamResults.stream()
                .collect(Collectors.groupingBy(exam -> exam.getTerm().getValue()));

        Map<String, Map<String, Double>> averageScorePerTermAndExamType = calculateAverageScorePerTermAndExamType(examsGroupedByTerm);
        Map<String, Map<String, Double>> averagePointsPerTermAndExamType = calculateAveragePointsPerTermAndExamType(examsGroupedByTerm);
        Map<String, Map<String, String>> overallGradePerTermAndExamType = calculateOverallGradePerTermAndExamType(averagePointsPerTermAndExamType); // Corrected line

        return new StudentReportResponseDTO(
                student.getId(),
                student.getName(),
                student.getAdmissionNumber(),
                student.getClassId(),
                student.getStreamId(),
                className,
                streamName,
                LocalDate.now(),
                enhancedExamResults,
                averageScorePerTermAndExamType,
                averagePointsPerTermAndExamType,
                overallGradePerTermAndExamType
        );
    }

    public List<StudentReportResponseDTO> generateClassReport(Long classId, String stream, String term, String examType, String subject) {
        List<Student> studentsInClass = studentRepository.findByClassId(classId);

        // Filter by streamId if provided
        if (stream != null && !stream.isEmpty()) {
            try {
                Long streamId = Long.parseLong(stream);
                studentsInClass = studentsInClass.stream()
                        .filter(student -> student.getStreamId().equals(streamId))
                        .collect(Collectors.toList());
            } catch (NumberFormatException e) {
                // If stream is not a valid Long, ignore the filter
            }
        }

        return studentsInClass.stream()
                .map(student -> generateStudentReportForClassReport(student, term, examType, subject))
                .collect(Collectors.toList());
    }

    private StudentReportResponseDTO generateStudentReportForClassReport(Student student, String term, String examType, String subject) {
        // Get exams with filters - Filters are now applied here
        List<ExamResponseDTO> examResults = examService.getExamsByStudentIdWithFilters(student.getId(), term, examType, subject);

        // Enhance ExamResponseDTOs in results to include subject names
        List<ExamResponseDTO> enhancedExamResults = examResults.stream()
                .map(this::enhanceExamResponseWithSubjectName)
                .collect(Collectors.toList());

        // Group exams by Term first, then by ExamType
        Map<String, List<ExamResponseDTO>> examsGroupedByTerm = enhancedExamResults.stream()
                .collect(Collectors.groupingBy(exam -> exam.getTerm().getValue()));

        Map<String, Map<String, Double>> averageScorePerTermAndExamType = calculateAverageScorePerTermAndExamType(examsGroupedByTerm);
        Map<String, Map<String, Double>> averagePointsPerTermAndExamType = calculateAveragePointsPerTermAndExamType(examsGroupedByTerm);
        Map<String, Map<String, String>> overallGradePerTermAndExamType = calculateOverallGradePerTermAndExamType(averagePointsPerTermAndExamType); // Corrected line


        // Fetch Class Name from Configuration Service using classId
        SchoolClassResponseDTO classDto = configurationServiceClient.getClassById(student.getClassId());
        String className = (classDto != null) ? classDto.getName() : "N/A";

        // Fetch Stream Name from Configuration Service using streamId
        StreamResponseDTO streamDto = configurationServiceClient.getStreamById(student.getStreamId());
        String streamName = (streamDto != null) ? streamDto.getName() : "N/A";

        return new StudentReportResponseDTO(
                student.getId(),
                student.getName(),
                student.getAdmissionNumber(),
                student.getClassId(),
                student.getStreamId(),
                className,
                streamName,
                LocalDate.now(),
                enhancedExamResults,
                averageScorePerTermAndExamType,
                averagePointsPerTermAndExamType,
                overallGradePerTermAndExamType
        );
    }

    private Map<String, Map<String, Double>> calculateAverageScorePerTermAndExamType(Map<String, List<ExamResponseDTO>> examsByTerm) {
        Map<String, Map<String, Double>> termExamTypeAverageScoreMap = new HashMap<>();
        for (Map.Entry<String, List<ExamResponseDTO>> termEntry : examsByTerm.entrySet()) {
            String term = termEntry.getKey();
            Map<String, Double> examTypeAverageScoreMap = termEntry.getValue().stream()
                    .collect(Collectors.groupingBy(
                            exam -> exam.getExamType().getValue(),
                            Collectors.averagingDouble(ExamResponseDTO::getScore)
                    ));
            termExamTypeAverageScoreMap.put(term, examTypeAverageScoreMap);
        }
        return termExamTypeAverageScoreMap;
    }

    private Map<String, Map<String, Double>> calculateAveragePointsPerTermAndExamType(Map<String, List<ExamResponseDTO>> examsByTerm) {
        Map<String, Map<String, Double>> termExamTypeAveragePointsMap = new HashMap<>();
        for (Map.Entry<String, List<ExamResponseDTO>> termEntry : examsByTerm.entrySet()) {
            String term = termEntry.getKey();
            Map<String, Double> examTypeAveragePointsMap = termEntry.getValue().stream()
                    .collect(Collectors.groupingBy(
                            exam -> exam.getExamType().getValue(),
                            Collectors.averagingDouble(ExamResponseDTO::getGradePoints)
                    ));
            termExamTypeAveragePointsMap.put(term, examTypeAveragePointsMap);
        }
        return termExamTypeAveragePointsMap;
    }

    private Map<String, Map<String, String>> calculateOverallGradePerTermAndExamType(Map<String, Map<String, Double>> averagePointsPerTermAndExamType) {
        Map<String, Map<String, String>> termExamTypeOverallGradeMap = new HashMap<>();
        for (Map.Entry<String, Map<String, Double>> termEntry : averagePointsPerTermAndExamType.entrySet()) {
            String term = termEntry.getKey();
            Map<String, String> examTypeOverallGradeMap = new HashMap<>(); // Create a new inner map for each term
            for (Map.Entry<String, Double> examTypeEntry : termEntry.getValue().entrySet()) {
                String examType = examTypeEntry.getKey();
                String overallGrade = calculateOverallGradeForExamType(examTypeEntry.getValue());
                examTypeOverallGradeMap.put(examType, overallGrade); // Correct put for examType and overallGrade
            }
            termExamTypeOverallGradeMap.put(term, examTypeOverallGradeMap); // Correct put for term and examTypeOverallGradeMap
        }
        return termExamTypeOverallGradeMap;
    }


    private String calculateOverallGradeForExamType(double averagePoints) {
        List<GradeCriteriaResponseDTO> gradeCriteriaList = configurationServiceClient.getAllGrades();
        String overallGrade = "F"; // Default grade

        for (GradeCriteriaResponseDTO criteria : gradeCriteriaList) {
            if (averagePoints >= criteria.getPoints()) {
                overallGrade = criteria.getGrade();
                return overallGrade; // Return as soon as a grade is found
            }
        }
        return overallGrade;
    }

    private ExamResponseDTO enhanceExamResponseWithSubjectName(ExamResponseDTO examResponseDTO) {
        // Fetch Subject Name from Configuration Service using subjectId
        LearningSubjectResponseDTO subjectDto = configurationServiceClient.getLearningSubjectById(examResponseDTO.getSubjectId());
        String subjectName = (subjectDto != null) ? subjectDto.getName() : "N/A";

        // Create a new ExamResponseDTO with subjectName
        return new ExamResponseDTO(
                examResponseDTO.getId(),
                examResponseDTO.getExamDate(),
                examResponseDTO.getSubjectId(),
                examResponseDTO.getScore(),
                examResponseDTO.getStudentId(),
                examResponseDTO.getGrade(),
                examResponseDTO.getGradePoints(),
                examResponseDTO.getTerm(),
                examResponseDTO.getExamType(),
                subjectName
        );
    }
}