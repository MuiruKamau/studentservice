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

        // Get all exams for student -  Filters are ignored based on the prompt
        List<ExamResponseDTO> examResults = examService.getExamsByStudentId(studentId, null, null); // Term and subject filters removed

        // Enhance ExamResponseDTOs in results to include subject names
        List<ExamResponseDTO> enhancedExamResults = examResults.stream()
                .map(this::enhanceExamResponseWithSubjectName)
                .collect(Collectors.toList());

        // Calculate average score per exam type
        Map<String, Double> averageScorePerExamType = enhancedExamResults.stream()
                .collect(Collectors.groupingBy(
                        exam -> exam.getExamType().getValue(), // Changed to get String value of ExamType
                        Collectors.averagingDouble(ExamResponseDTO::getScore)
                ));


        // Calculate total grade points
        int totalPoints = enhancedExamResults.stream()
                .mapToInt(ExamResponseDTO::getGradePoints)
                .sum();

        // Calculate average grade points
        int numberOfSubjects = enhancedExamResults.size();
        double totalAveragePoints = numberOfSubjects > 0
                ? (double) totalPoints / numberOfSubjects
                : 0.0;

        // Calculate overall grade using the average points
        String overallGrade = calculateOverallGrade(totalAveragePoints);

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
                averageScorePerExamType, // Using averageScorePerExamType
                totalPoints,
                overallGrade
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
        // Get exams with filters - Filters are ignored based on the prompt
        List<ExamResponseDTO> examResults = examService.getExamsByStudentId(student.getId(), null, null); // Term and subject filters removed

        // Enhance ExamResponseDTOs in results to include subject names
        List<ExamResponseDTO> enhancedExamResults = examResults.stream()
                .map(this::enhanceExamResponseWithSubjectName)
                .collect(Collectors.toList());

        // Calculate average score per exam type
        Map<String, Double> averageScorePerExamType = enhancedExamResults.stream()
                .collect(Collectors.groupingBy(
                        exam -> exam.getExamType().getValue(), // Changed to get String value of ExamType
                        Collectors.averagingDouble(ExamResponseDTO::getScore)
                ));

        // Calculate total grade points
        int totalPoints = enhancedExamResults.stream()
                .mapToInt(ExamResponseDTO::getGradePoints)
                .sum();

        // Calculate average grade points
        int numberOfSubjects = enhancedExamResults.size();
        double totalAveragePoints = numberOfSubjects > 0
                ? (double) totalPoints / numberOfSubjects
                : 0.0;

        // Calculate overall grade using the average points
        String overallGrade = calculateOverallGrade(totalAveragePoints);

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
                averageScorePerExamType, // Using averageScorePerExamType
                totalPoints,
                overallGrade
        );
    }

    private String calculateOverallGrade(double averagePoints) {
        List<GradeCriteriaResponseDTO> gradeCriteriaList = configurationServiceClient.getAllGrades();
        String overallGrade = "F"; // Default grade

        for (GradeCriteriaResponseDTO criteria : gradeCriteriaList) {
            if (averagePoints >= criteria.getPoints()) {
                overallGrade = criteria.getGrade();
                break;
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