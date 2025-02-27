package com.school.studentservice.controller;

import com.school.studentservice.dto.ExamRequestDTO;
import com.school.studentservice.dto.ExamResponseDTO;
import com.school.studentservice.entity.ExamType;
import com.school.studentservice.entity.Term;
import com.school.studentservice.service.ExamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS})
@RestController
@RequestMapping("/api/exams")
public class ExamController {

    private final ExamService examService;

    @Autowired
    public ExamController(ExamService examService) {
        this.examService = examService;
    }

    @GetMapping("/terms")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMINISTRATOR')")
    public ResponseEntity<List<String>> getAllTerms() {
        List<String> terms = Arrays.stream(Term.values())
                .map(Term::getValue)
                .collect(Collectors.toList());
        return new ResponseEntity<>(terms, HttpStatus.OK);
    }

    @GetMapping("/exam-types")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMINISTRATOR')")
    public ResponseEntity<List<String>> getAllExamTypes() {
        List<String> examTypes = Arrays.stream(ExamType.values())
                .map(ExamType::getValue)
                .collect(Collectors.toList());
        return new ResponseEntity<>(examTypes, HttpStatus.OK);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMINISTRATOR')")
    public ResponseEntity<ExamResponseDTO> createExam(@RequestBody ExamRequestDTO examRequestDTO) {
        try {
            ExamResponseDTO createdExam = examService.createExam(examRequestDTO);
            return new ResponseEntity<>(createdExam, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMINISTRATOR')")
    public ResponseEntity<ExamResponseDTO> getExamById(@PathVariable Long id) {
        ExamResponseDTO exam = examService.getExamById(id);
        if (exam != null) {
            return new ResponseEntity<>(exam, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMINISTRATOR')")
    public ResponseEntity<List<ExamResponseDTO>> getAllExams() {
        List<ExamResponseDTO> exams = examService.getAllExams();
        return new ResponseEntity<>(exams, HttpStatus.OK);
    }

    @GetMapping("/student/{studentId}")
    @PreAuthorize("hasAnyRole('STUDENT', 'TEACHER', 'ADMINISTRATOR')")
    public ResponseEntity<List<ExamResponseDTO>> getExamsByStudentId(
            @PathVariable Long studentId,
            @RequestParam(value = "term", required = false) String term,
            @RequestParam(value = "subject", required = false) String subject
    ) {
        List<ExamResponseDTO> exams = examService.getExamsByStudentId(studentId, term, subject);
        return new ResponseEntity<>(exams, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMINISTRATOR')")
    public ResponseEntity<ExamResponseDTO> updateExam(@PathVariable Long id, @RequestBody ExamRequestDTO examRequestDTO) {
        try {
            ExamResponseDTO updatedExam = examService.updateExam(id, examRequestDTO);
            if (updatedExam != null) {
                return new ResponseEntity<>(updatedExam, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMINISTRATOR')")
    public ResponseEntity<Void> deleteExam(@PathVariable Long id) {
        if (examService.deleteExam(id)) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}