package com.school.studentservice.service;

import com.school.studentservice.client.ConfigurationServiceClient;
import com.school.studentservice.dto.ExamResponseDTO;
import com.school.studentservice.dto.GradeCriteriaResponseDTO;
import com.school.studentservice.dto.StudentReportResponseDTO;
import com.school.studentservice.dto.SchoolClassResponseDTO;
import com.school.studentservice.dto.StreamResponseDTO;
import com.school.studentservice.dto.LearningSubjectResponseDTO;
import com.school.studentservice.entity.ExamType;
import com.school.studentservice.entity.Student;
import com.school.studentservice.entity.Term;
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

        // Get exams with filters
        List<ExamResponseDTO> examResults = examService.getExamsByStudentIdWithFilters(studentId, term, examType, subject);

        // Enhance ExamResponseDTOs in results to include subject names
        List<ExamResponseDTO> enhancedExamResults = examResults.stream()
                .map(this::enhanceExamResponseWithSubjectName)
                .collect(Collectors.toList());

        // Calculate average score
        double averageScore = enhancedExamResults.stream()
                .mapToDouble(ExamResponseDTO::getScore)
                .average()
                .orElse(0.0);

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
                averageScore,
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
        // Get exams with filters
        List<ExamResponseDTO> examResults = examService.getExamsByStudentIdWithFilters(student.getId(), term, examType, subject);

        // Calculate average score
        double averageScore = examResults.stream()
                .mapToDouble(ExamResponseDTO::getScore)
                .average()
                .orElse(0.0);

        // Calculate total grade points
        int totalPoints = examResults.stream()
                .mapToInt(ExamResponseDTO::getGradePoints)
                .sum();

        // Calculate average grade points
        int numberOfSubjects = examResults.size();
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
                examResults,
                averageScore,
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