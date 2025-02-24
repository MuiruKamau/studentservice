package com.school.studentservice.client;

import com.school.studentservice.dto.GradeCriteriaResponseDTO;
import com.school.studentservice.dto.SchoolClassResponseDTO; // Add import for SchoolClass DTO
import com.school.studentservice.dto.StreamResponseDTO;      // Add import for Stream DTO
import com.school.studentservice.dto.LearningSubjectResponseDTO; // Add import for LearningSubject DTO
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "configuration-service") // Name of the Configuration Service
public interface ConfigurationServiceClient {

    @GetMapping("/api/config/grade-criteria")
    List<GradeCriteriaResponseDTO> getAllGrades();

    @GetMapping("/api/config/grade-criteria/{id}")
    GradeCriteriaResponseDTO getGradeById(@PathVariable Long id);

    // New methods to fetch Class, Stream, and Subject details by ID:

    @GetMapping("/api/config/school-classes/{id}") // Endpoint in ConfigurationService for Class by ID
    SchoolClassResponseDTO getClassById(@PathVariable Long id);

    @GetMapping("/api/config/streams/{id}")      // Endpoint in ConfigurationService for Stream by ID
    StreamResponseDTO getStreamById(@PathVariable Long id);

    @GetMapping("/api/config/learning-subjects/{id}") // Endpoint in ConfigurationService for Subject by ID
    LearningSubjectResponseDTO getLearningSubjectById(@PathVariable Long id);
}
