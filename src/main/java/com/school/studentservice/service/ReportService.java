package com.school.studentservice.service;

import com.school.studentservice.client.ConfigurationServiceClient;
import com.school.studentservice.dto.ExamResponseDTO;
import com.school.studentservice.dto.GradeCriteriaResponseDTO;
import com.school.studentservice.dto.StudentReportResponseDTO;
import com.school.studentservice.dto.SchoolClassResponseDTO; // Import SchoolClassResponseDTO
import com.school.studentservice.dto.StreamResponseDTO;      // Import StreamResponseDTO
import com.school.studentservice.dto.LearningSubjectResponseDTO; // Import LearningSubjectResponseDTO
import com.school.studentservice.entity.Student;
import com.school.studentservice.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;
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

    public StudentReportResponseDTO generateStudentReport(Long studentId, String term, String subject) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Student not found with id: " + studentId));

        // Fetch Class Name from Configuration Service using classId
        SchoolClassResponseDTO classDto = configurationServiceClient.getClassById(student.getClassId());
        String className = (classDto != null) ? classDto.getName() : "N/A";

        // Fetch Stream Name from Configuration Service using streamId
        StreamResponseDTO streamDto = configurationServiceClient.getStreamById(student.getStreamId());
        String streamName = (streamDto != null) ? streamDto.getName() : "N/A";


        List<ExamResponseDTO> examResults = examService.getExamsByStudentId(studentId, term, subject);

        // Enhance ExamResponseDTOs in results to include subject names
        List<ExamResponseDTO> enhancedExamResults = examResults.stream()
                .map(this::enhanceExamResponseWithSubjectName) // Call helper method to add subject name
                .collect(Collectors.toList());


        double averageScore = enhancedExamResults.stream() // Use enhanced exam results
                .mapToDouble(ExamResponseDTO::getScore)
                .average()
                .orElse(0.0);

        int totalPoints = enhancedExamResults.stream() // Use enhanced exam results
                .mapToInt(ExamResponseDTO::getGradePoints)
                .sum();

        String overallGrade = calculateOverallGrade(totalPoints);

        return new StudentReportResponseDTO(
                student.getId(),
                student.getName(),
                student.getAdmissionNumber(),
                student.getClassId(),
                student.getStreamId(),
                className,           // Use fetched className
                streamName,          // Use fetched streamName
                LocalDate.now(),
                enhancedExamResults, // Use enhanced exam results with subject names
                averageScore,
                totalPoints,
                overallGrade
        );
    }

    private ExamResponseDTO enhanceExamResponseWithSubjectName(ExamResponseDTO examResponseDTO) {
        // Fetch Subject Name from Configuration Service using subjectId
        LearningSubjectResponseDTO subjectDto = configurationServiceClient.getLearningSubjectById(examResponseDTO.getSubjectId());
        String subjectName = (subjectDto != null) ? subjectDto.getName() : "N/A";

        // Create a new ExamResponseDTO with subjectName
        return new ExamResponseDTO(
                examResponseDTO.getId(),
                examResponseDTO.getExamDate(),
                examResponseDTO.getSubjectId(), // Keep subjectId
                examResponseDTO.getScore(),
                examResponseDTO.getStudentId(),
                examResponseDTO.getGrade(),
                examResponseDTO.getGradePoints(),
                examResponseDTO.getTerm(),
                subjectName // Add subjectName to the DTO
        );
    }


    private String calculateOverallGrade(int totalPoints) {
        List<GradeCriteriaResponseDTO> gradeCriteriaList = configurationServiceClient.getAllGrades();
        String overallGrade = "F";
        for (GradeCriteriaResponseDTO criteria : gradeCriteriaList) {
            if (totalPoints >= criteria.getPoints()) {
                overallGrade = criteria.getGrade();
                break;
            }
        }
        return overallGrade;
    }
}





//package com.school.studentservice.service;
//
//import com.school.studentservice.dto.ExamResponseDTO;
//import com.school.studentservice.dto.StudentReportResponseDTO;
//import com.school.studentservice.entity.Student;
//import com.school.studentservice.repository.StudentRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import java.time.LocalDate;
//import java.util.List;
//import java.util.stream.Collectors;
//
//@Service
//public class ReportService {
//
//    private final StudentRepository studentRepository;
//    private final ExamService examService; // Inject ExamService
//
//    @Autowired
//    public ReportService(StudentRepository studentRepository, ExamService examService) {
//        this.studentRepository = studentRepository;
//        this.examService = examService;
//    }
//
//    public StudentReportResponseDTO generateStudentReport(Long studentId) {
//        Student student = studentRepository.findById(studentId)
//                .orElseThrow(() -> new IllegalArgumentException("Student not found with id: " + studentId));
//
//        List<ExamResponseDTO> examResults = examService.getExamsByStudentId(studentId);
//
//        double averageScore = examResults.stream()
//                .mapToDouble(ExamResponseDTO::getScore) // Use mapToDouble as score is double
//                .average()
//                .orElse(0.0);
//
//        int totalPoints = examResults.stream()
//                .mapToInt(exam -> (int) exam.getGradePoints()) // Cast to int if getGradePoints is double or needs explicit casting
//                .sum();
//
//        return new StudentReportResponseDTO(
//                student.getId(),
//                student.getName(),
//                student.getAdmissionNumber(),
//                student.getClassName(), // Use className directly as String
//                student.getStreamName(), // Use streamName directly as String
//                LocalDate.now(), // Report Date
//                examResults,
//                averageScore,
//                totalPoints
//        );
//    }
//}