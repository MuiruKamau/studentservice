package com.school.studentservice.controller;

import com.school.studentservice.dto.StudentReportResponseDTO;
import com.school.studentservice.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportService reportService;

    @Autowired
    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/students/{studentId}")
    @PreAuthorize("hasAnyRole('STUDENT', 'TEACHER', 'ADMINISTRATOR')")
    public ResponseEntity<StudentReportResponseDTO> getStudentReport(
            @PathVariable Long studentId,
            @RequestParam(value = "term", required = false) String term,
            @RequestParam(value = "examType", required = false) String examType,
            @RequestParam(value = "subject", required = false) String subject
    ) {
        try {
            StudentReportResponseDTO report = reportService.generateStudentReport(studentId, term, examType, subject);
            return new ResponseEntity<>(report, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/classes/{classId}")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMINISTRATOR')")
    public ResponseEntity<List<StudentReportResponseDTO>> getClassReport(
            @PathVariable Long classId,
            @RequestParam(value = "stream", required = false) String stream,
            @RequestParam(value = "term", required = false) String term,
            @RequestParam(value = "examType", required = false) String examType,
            @RequestParam(value = "subject", required = false) String subject
    ) {
        try {
            List<StudentReportResponseDTO> reports = reportService.generateClassReport(classId, stream, term, examType, subject);
            return new ResponseEntity<>(reports, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}