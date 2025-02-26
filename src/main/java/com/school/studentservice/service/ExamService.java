package com.school.studentservice.service;

import com.school.studentservice.client.ConfigurationServiceClient;
import com.school.studentservice.dto.ExamRequestDTO;
import com.school.studentservice.dto.ExamResponseDTO;
import com.school.studentservice.dto.GradeCriteriaResponseDTO;
import com.school.studentservice.dto.LearningSubjectResponseDTO;
import com.school.studentservice.entity.Exam;
import com.school.studentservice.entity.ExamType;
import com.school.studentservice.entity.Student;
import com.school.studentservice.entity.Term;
import com.school.studentservice.repository.ExamRepository;
import com.school.studentservice.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ExamService {

    private final ExamRepository examRepository;
    private final StudentRepository studentRepository;
    private final ConfigurationServiceClient configurationServiceClient;

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
                .subjectId(examRequestDTO.getSubjectId())
                .score(examRequestDTO.getScore())
                .term(examRequestDTO.getTerm())
                .examType(examRequestDTO.getExamType())
                .build();

        Exam savedExam = examRepository.save(exam);
        calculateAndSetGrade(savedExam);
        examRepository.save(savedExam);
        return mapToDTO(savedExam);
    }

    public List<ExamResponseDTO> getExamsByStudentId(Long studentId, String termStr, String subject) {
        List<Exam> exams;

        if (termStr != null) {
            try {
                Term term = Term.fromValue(termStr);
                exams = examRepository.findByStudentIdAndTerm(studentId, term);
            } catch (IllegalArgumentException e) {
                // If invalid term is provided, return all exams
                exams = examRepository.findByStudentId(studentId);
            }
        } else {
            exams = examRepository.findByStudentId(studentId);
        }

        return exams.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<ExamResponseDTO> getExamsByStudentIdWithFilters(Long studentId, String termStr, String examTypeStr, String subject) {
        List<Exam> exams = examRepository.findByStudentId(studentId);

        // Filter by term if provided
        if (termStr != null && !termStr.isEmpty()) {
            try {
                Term term = Term.fromValue(termStr);
                exams = exams.stream()
                        .filter(exam -> exam.getTerm() == term)
                        .collect(Collectors.toList());
            } catch (IllegalArgumentException e) {
                // If invalid term, don't filter by term
            }
        }

        // Filter by exam type if provided
        if (examTypeStr != null && !examTypeStr.isEmpty()) {
            try {
                ExamType examType = ExamType.fromValue(examTypeStr);
                exams = exams.stream()
                        .filter(exam -> exam.getExamType() == examType)
                        .collect(Collectors.toList());
            } catch (IllegalArgumentException e) {
                // If invalid exam type, don't filter by exam type
            }
        }

        // Filter by subject if provided
        if (subject != null && !subject.isEmpty()) {
            try {
                Long subjectId = Long.parseLong(subject);
                exams = exams.stream()
                        .filter(exam -> exam.getSubjectId().equals(subjectId))
                        .collect(Collectors.toList());
            } catch (NumberFormatException e) {
                // If subject is not a valid Long, ignore the filter
            }
        }

        return exams.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<ExamResponseDTO> getAllExams() {
        return examRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public ExamResponseDTO getExamById(Long id) {
        return examRepository.findById(id)
                .map(this::mapToDTO)
                .orElse(null);
    }

    public ExamResponseDTO updateExam(Long id, ExamRequestDTO examRequestDTO) {
        return examRepository.findById(id)
                .map(existingExam -> {
                    existingExam.setExamDate(examRequestDTO.getExamDate());
                    existingExam.setSubjectId(examRequestDTO.getSubjectId());
                    existingExam.setScore(examRequestDTO.getScore());
                    existingExam.setTerm(examRequestDTO.getTerm());
                    existingExam.setExamType(examRequestDTO.getExamType());

                    calculateAndSetGrade(existingExam);
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
        String grade = "F";
        int gradePoints = 0;

        for (GradeCriteriaResponseDTO criteria : gradeCriteriaList) {
            if (exam.getScore() >= criteria.getMinScore() && exam.getScore() <= criteria.getMaxScore()) {
                grade = criteria.getGrade();
                gradePoints = criteria.getPoints();
                break;
            }
        }
        exam.setGrade(grade);
        exam.setGradePoints(gradePoints);
    }

    private ExamResponseDTO mapToDTO(Exam exam) {
        LearningSubjectResponseDTO subjectDto = configurationServiceClient.getLearningSubjectById(exam.getSubjectId());
        String subjectName = (subjectDto != null) ? subjectDto.getName() : "N/A";

        return new ExamResponseDTO(
                exam.getId(),
                exam.getExamDate(),
                exam.getSubjectId(),
                exam.getScore(),
                exam.getStudent().getId(),
                exam.getGrade(),
                exam.getGradePoints(),
                exam.getTerm(),
                exam.getExamType(),
                subjectName
        );
    }
}