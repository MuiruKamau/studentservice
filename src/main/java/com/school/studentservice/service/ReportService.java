package com.school.studentservice.service;

import com.school.studentservice.dto.ExamResponseDTO;
import com.school.studentservice.dto.StudentReportResponseDTO;
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
    private final ExamService examService; // Inject ExamService

    @Autowired
    public ReportService(StudentRepository studentRepository, ExamService examService) {
        this.studentRepository = studentRepository;
        this.examService = examService;
    }

    public StudentReportResponseDTO generateStudentReport(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Student not found with id: " + studentId));

        List<ExamResponseDTO> examResults = examService.getExamsByStudentId(studentId);

        double averageScore = examResults.stream()
                .mapToDouble(ExamResponseDTO::getScore) // Use mapToDouble as score is double
                .average()
                .orElse(0.0);

        int totalPoints = examResults.stream()
                .mapToInt(exam -> (int) exam.getGradePoints()) // Cast to int if getGradePoints is double or needs explicit casting
                .sum();

        return new StudentReportResponseDTO(
                student.getId(),
                student.getName(),
                student.getAdmissionNumber(),
                student.getClassName(), // Use className directly as String
                student.getStreamName(), // Use streamName directly as String
                LocalDate.now(), // Report Date
                examResults,
                averageScore,
                totalPoints
        );
    }
}