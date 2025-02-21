package com.school.studentservice.controller;

import com.school.studentservice.dto.StudentReportResponseDTO;
import com.school.studentservice.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportService reportService;

    @Autowired
    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/students/{studentId}")
    public ResponseEntity<StudentReportResponseDTO> getStudentReport(
            @PathVariable Long studentId,
            @RequestParam(value = "term", required = false) String term, // Optional term parameter
            @RequestParam(value = "subject", required = false) String subject // Optional subject parameter
    ) {
        try {
            StudentReportResponseDTO report = reportService.generateStudentReport(studentId, term, subject); // Pass filters
            return new ResponseEntity<>(report, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
