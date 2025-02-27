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

    // Endpoint to get student report for ALL exam types
    @GetMapping("/students/{studentId}")
    @PreAuthorize("hasAnyRole('STUDENT', 'TEACHER', 'ADMINISTRATOR')")
    public ResponseEntity<StudentReportResponseDTO> getStudentReport(
            @PathVariable Long studentId
    ) {
        try {
            StudentReportResponseDTO report = reportService.generateStudentReport(studentId, null, null, null); // Pass nulls to indicate no filter
            return new ResponseEntity<>(report, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Endpoint to get student report for a SPECIFIC exam type
    @GetMapping("/students/{studentId}/{examType}")
    @PreAuthorize("hasAnyRole('STUDENT', 'TEACHER', 'ADMINISTRATOR')")
    public ResponseEntity<StudentReportResponseDTO> getStudentReportByExamType(
            @PathVariable Long studentId,
            @PathVariable String examType // Exam type is now part of the path
    ) {
        try {
            StudentReportResponseDTO report = reportService.generateStudentReport(studentId, null, examType, null); // Pass examType to filter
            return new ResponseEntity<>(report, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }


    // Endpoint to get class report for ALL exam types
    @GetMapping("/classes/{classId}")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMINISTRATOR')")
    public ResponseEntity<List<StudentReportResponseDTO>> getClassReport(
            @PathVariable Long classId,
            @RequestParam(value = "stream", required = false) String stream
    ) {
        try {
            List<StudentReportResponseDTO> reports = reportService.generateClassReport(classId, stream, null, null, null); // Pass nulls to indicate no filter
            return new ResponseEntity<>(reports, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Endpoint to get class report for a SPECIFIC exam type
    @GetMapping("/classes/{classId}/{examType}")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMINISTRATOR')")
    public ResponseEntity<List<StudentReportResponseDTO>> getClassReportByExamType(
            @PathVariable Long classId,
            @PathVariable String examType, // Exam type is now part of the path
            @RequestParam(value = "stream", required = false) String stream
    ) {
        try {
            List<StudentReportResponseDTO> reports = reportService.generateClassReport(classId, stream, null, examType, null); // Pass examType to filter
            return new ResponseEntity<>(reports, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}