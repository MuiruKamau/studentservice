package com.school.studentservice.service;

import com.school.studentservice.client.ConfigurationServiceClient;
import com.school.studentservice.dto.ExamRequestDTO;
import com.school.studentservice.dto.ExamResponseDTO;
import com.school.studentservice.dto.GradeCriteriaResponseDTO;
import com.school.studentservice.entity.Exam;
import com.school.studentservice.entity.Student;
import com.school.studentservice.repository.ExamRepository;
import com.school.studentservice.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.time.LocalDate;
import java.util.stream.Collectors;

@Service
public class ExamService {

    private final ExamRepository examRepository;
    private final StudentRepository studentRepository;
    private final ConfigurationServiceClient configurationServiceClient; // Inject Feign Client

    @Autowired
    public ExamService(ExamRepository examRepository, StudentRepository studentRepository, ConfigurationServiceClient configurationServiceClient) {
        this.examRepository = examRepository;
        this.studentRepository = studentRepository;
        this.configurationServiceClient = configurationServiceClient;
    }

    public ExamResponseDTO createExam(ExamRequestDTO examRequestDTO) {
        Student student = studentRepository.findById(examRequestDTO.getStudentId())
                .orElseThrow(() -> new IllegalArgumentException("Student not found with id: " + examRequestDTO.getStudentId()));

        Exam exam = Exam.builder()
                .student(student)
                .examDate(examRequestDTO.getExamDate())
                .subject(examRequestDTO.getSubject())
                .score(examRequestDTO.getScore())
                .build();

        Exam savedExam = examRepository.save(exam);
        calculateAndSetGrade(savedExam); // Calculate grade immediately after saving
        examRepository.save(savedExam); // Save again with grade and points
        return mapToDTO(savedExam);
    }

    public ExamResponseDTO getExamById(Long id) {
        return examRepository.findById(id)
                .map(this::mapToDTO)
                .orElse(null);
    }

    public List<ExamResponseDTO> getAllExams() {
        return examRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<ExamResponseDTO> getExamsByStudentId(Long studentId) {
        return examRepository.findByStudentId(studentId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public ExamResponseDTO updateExam(Long id, ExamRequestDTO examRequestDTO) {
        return examRepository.findById(id)
                .map(existingExam -> {
                    // Student student = studentRepository.findById(examRequestDTO.getStudentId()) - Student cannot be changed for existing exam
                    //        .orElseThrow(() -> new IllegalArgumentException("Student not found with id: " + examRequestDTO.getStudentId()));
                    existingExam.setExamDate(examRequestDTO.getExamDate());
                    existingExam.setSubject(examRequestDTO.getSubject());
                    existingExam.setScore(examRequestDTO.getScore());

                    calculateAndSetGrade(existingExam); // Recalculate grade if score is updated
                    Exam updatedExam = examRepository.save(existingExam);
                    return mapToDTO(updatedExam);
                })
                .orElse(null);
    }

    public boolean deleteExam(Long id) {
        if (examRepository.existsById(id)) {
            examRepository.deleteById(id);
            return true;
        }
        return false;
    }

    private void calculateAndSetGrade(Exam exam) {
        List<GradeCriteriaResponseDTO> gradeCriteriaList = configurationServiceClient.getAllGrades();
        String grade = "F"; // Default grade if no criteria matches
        int gradePoints = 0;

        for (GradeCriteriaResponseDTO criteria : gradeCriteriaList) {
            if (exam.getScore() >= criteria.getMinScore() && exam.getScore() <= criteria.getMaxScore()) {
                grade = criteria.getGrade();
                gradePoints = criteria.getPoints();
                break; // Exit loop once a matching criteria is found
            }
        }
        exam.setGrade(grade);
        exam.setGradePoints(gradePoints);
    }

    private ExamResponseDTO mapToDTO(Exam exam) {
        return new ExamResponseDTO(
                exam.getId(),
                exam.getExamDate(),
                exam.getSubject(),
                exam.getScore(),
                exam.getStudent().getId(),
                exam.getGrade(),
                exam.getGradePoints()
        );
    }
}