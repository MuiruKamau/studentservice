package com.school.studentservice.controller;

import com.school.studentservice.dto.ExamRequestDTO;
import com.school.studentservice.dto.ExamResponseDTO;
import com.school.studentservice.service.ExamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/exams")
public class ExamController {

    private final ExamService examService;

    @Autowired
    public ExamController(ExamService examService) {
        this.examService = examService;
    }

    @PostMapping
    public ResponseEntity<ExamResponseDTO> createExam(@RequestBody ExamRequestDTO examRequestDTO) {
        try {
            ExamResponseDTO createdExam = examService.createExam(examRequestDTO);
            return new ResponseEntity<>(createdExam, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExamResponseDTO> getExamById(@PathVariable Long id) {
        ExamResponseDTO exam = examService.getExamById(id);
        if (exam != null) {
            return new ResponseEntity<>(exam, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping
    public ResponseEntity<List<ExamResponseDTO>> getAllExams() {
        List<ExamResponseDTO> exams = examService.getAllExams();
        return new ResponseEntity<>(exams, HttpStatus.OK);
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<ExamResponseDTO>> getExamsByStudentId(
            @PathVariable Long studentId,
            @RequestParam(value = "term", required = false) String term,
            @RequestParam(value = "subject", required = false) String subject
    ) {
        List<ExamResponseDTO> exams = examService.getExamsByStudentId(studentId, term, subject);
        return new ResponseEntity<>(exams, HttpStatus.OK);
    }


    @PutMapping("/{id}")
    public ResponseEntity<ExamResponseDTO> updateExam(@PathVariable Long id, @RequestBody ExamRequestDTO examRequestDTO) {
        try {
            ExamResponseDTO updatedExam = examService.updateExam(id, examRequestDTO); // CORRECTED - Instance call
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
    public ResponseEntity<Void> deleteExam(@PathVariable Long id) {
        if (examService.deleteExam(id)) { // CORRECTED - Instance call
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
