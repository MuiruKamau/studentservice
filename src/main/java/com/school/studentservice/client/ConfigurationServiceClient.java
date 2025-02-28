package com.school.studentservice.client;

import com.school.studentservice.dto.GradeCriteriaResponseDTO;
import com.school.studentservice.dto.SchoolClassResponseDTO;
import com.school.studentservice.dto.StreamResponseDTO;
import com.school.studentservice.dto.LearningSubjectResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "configuration-service",configuration = FeignClientConfig.class)
public interface ConfigurationServiceClient {

    @GetMapping("/api/config/grade-criteria")
    List<GradeCriteriaResponseDTO> getAllGrades();

    @GetMapping("/api/config/grade-criteria/{id}")
    GradeCriteriaResponseDTO getGradeById(@PathVariable Long id);

    // New methods to fetch Class, Stream, and Subject details by ID:

    @GetMapping("/api/config/school-classes/{id}")
    SchoolClassResponseDTO getClassById(@PathVariable Long id);

    @GetMapping("/api/config/streams/{id}")
    StreamResponseDTO getStreamById(@PathVariable Long id);

    @GetMapping("/api/config/learning-subjects/{id}")
    LearningSubjectResponseDTO getLearningSubjectById(@PathVariable Long id);

    @GetMapping("/api/config/learning-subjects/by-name")
    LearningSubjectResponseDTO getLearningSubjectByName(@RequestParam("name") String name);

    @GetMapping("/api/config/class-subjects/class/{classId}") // Get subjects for a class
    List<LearningSubjectResponseDTO> getSubjectsForClass(@PathVariable Long classId);

    @GetMapping("/api/config/stream-subjects/stream/{streamId}") // Get subjects for a stream
    List<LearningSubjectResponseDTO> getSubjectsForStream(@PathVariable Long streamId);
}