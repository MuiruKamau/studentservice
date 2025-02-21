package com.school.studentservice.client;

import com.school.studentservice.dto.GradeCriteriaResponseDTO; // Create this DTO in student-service
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "configuration-service") // Name of the Configuration Service (as in Eureka or application name)
public interface ConfigurationServiceClient {

    @GetMapping("/api/config/grade-criteria")
    List<GradeCriteriaResponseDTO> getAllGrades();

    @GetMapping("/api/config/grade-criteria/{id}")
    GradeCriteriaResponseDTO getGradeById(@PathVariable Long id);

    // You can add more endpoints from Configuration Service as needed
}
