package com.school.studentservice.controller;

import com.school.studentservice.dto.StudentReportResponseDTO;
import com.school.studentservice.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS})
@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportService reportService;

    @Autowired
    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    // Endpoint to get student report for ALL exam types and ALL terms
    @GetMapping("/students/{studentId}")
    @PreAuthorize("hasAnyRole('STUDENT', 'TEACHER', 'ADMINISTRATOR')")
    public ResponseEntity<StudentReportResponseDTO> getStudentReport(
            @PathVariable Long studentId
    ) {
        try {
            StudentReportResponseDTO report = reportService.generateStudentReport(studentId, null, null, null); // No term or examType filter
            return new ResponseEntity<>(report, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Endpoint to get student report for a SPECIFIC exam type and ALL terms
    @GetMapping("/students/{studentId}/examType/{examType}")
    @PreAuthorize("hasAnyRole('STUDENT', 'TEACHER', 'ADMINISTRATOR')")
    public ResponseEntity<StudentReportResponseDTO> getStudentReportByExamType(
            @PathVariable Long studentId,
            @PathVariable String examType
    ) {
        try {
            StudentReportResponseDTO report = reportService.generateStudentReport(studentId, null, examType, null); // ExamType filter, no term filter
            return new ResponseEntity<>(report, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // New endpoint to get student report for a SPECIFIC term and ALL exam types
    @GetMapping("/students/{studentId}/term/{term}")
    @PreAuthorize("hasAnyRole('STUDENT', 'TEACHER', 'ADMINISTRATOR')")
    public ResponseEntity<StudentReportResponseDTO> getStudentReportByTerm(
            @PathVariable Long studentId,
            @PathVariable String term
    ) {
        try {
            StudentReportResponseDTO report = reportService.generateStudentReport(studentId, term, null, null); // Term filter, no examType filter
            return new ResponseEntity<>(report, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // New endpoint to get student report for a SPECIFIC term and SPECIFIC exam type
    @GetMapping("/students/{studentId}/term/{term}/examType/{examType}")
    @PreAuthorize("hasAnyRole('STUDENT', 'TEACHER', 'ADMINISTRATOR')")
    public ResponseEntity<StudentReportResponseDTO> getStudentReportByTermAndExamType(
            @PathVariable Long studentId,
            @PathVariable String term,
            @PathVariable String examType
    ) {
        try {
            StudentReportResponseDTO report = reportService.generateStudentReport(studentId, term, examType, null); // Term and examType filter
            return new ResponseEntity<>(report, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }


    // Endpoint to get class report for ALL exam types and ALL terms
    @GetMapping("/classes/{classId}")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMINISTRATOR')")
    public ResponseEntity<List<StudentReportResponseDTO>> getClassReport(
            @PathVariable Long classId,
            @RequestParam(value = "stream", required = false) String stream
    ) {
        try {
            List<StudentReportResponseDTO> reports = reportService.generateClassReport(classId, stream, null, null, null); // No term or examType filter
            return new ResponseEntity<>(reports, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Endpoint to get class report for a SPECIFIC exam type and ALL terms
    @GetMapping("/classes/{classId}/examType/{examType}")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMINISTRATOR')")
    public ResponseEntity<List<StudentReportResponseDTO>> getClassReportByExamType(
            @PathVariable Long classId,
            @PathVariable String examType,
            @RequestParam(value = "stream", required = false) String stream
    ) {
        try {
            List<StudentReportResponseDTO> reports = reportService.generateClassReport(classId, stream, null, examType, null); // ExamType filter, no term filter
            return new ResponseEntity<>(reports, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // New endpoint to get class report for a SPECIFIC term and ALL exam types
    @GetMapping("/classes/{classId}/term/{term}")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMINISTRATOR')")
    public ResponseEntity<List<StudentReportResponseDTO>> getClassReportByTerm(
            @PathVariable Long classId,
            @PathVariable String term,
            @RequestParam(value = "stream", required = false) String stream
    ) {
        try {
            List<StudentReportResponseDTO> reports = reportService.generateClassReport(classId, stream, term, null, null); // Term filter, no examType filter
            return new ResponseEntity<>(reports, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // New endpoint to get class report for a SPECIFIC term and SPECIFIC exam type
    @GetMapping("/classes/{classId}/term/{term}/examType/{examType}")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMINISTRATOR')")
    public ResponseEntity<List<StudentReportResponseDTO>> getClassReportByTermAndExamType(
            @PathVariable Long classId,
            @PathVariable String term,
            @PathVariable String examType,
            @RequestParam(value = "stream", required = false) String stream
    ) {
        try {
            List<StudentReportResponseDTO> reports = reportService.generateClassReport(classId, stream, term, examType, null); // Term and examType filter
            return new ResponseEntity<>(reports, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}