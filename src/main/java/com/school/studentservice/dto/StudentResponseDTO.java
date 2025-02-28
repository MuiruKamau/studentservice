package com.school.studentservice.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
public class StudentResponseDTO {
    private Long id;
    private String name;
    private String admissionNumber;
    private LocalDate dateOfBirth;
    private LocalDate enrollmentDate;
    private String address; // **Re-add address to DTO**
    private String contactNumber;
    private ParentDetailsDTO parentDetails;
    private Long classId;
    private Long streamId;
    private String className;
    private String streamName;
    private List<LearningSubjectResponseDTO> learningSubjects;

    public StudentResponseDTO(Long id, String name, String admissionNumber, LocalDate dateOfBirth, LocalDate enrollmentDate, String address, String contactNumber, ParentDetailsDTO parentDetails, Long classId, Long streamId, String className, String streamName, List<LearningSubjectResponseDTO> learningSubjects) {
        this.id = id;
        this.name = name;
        this.admissionNumber = admissionNumber;
        this.dateOfBirth = dateOfBirth;
        this.enrollmentDate = enrollmentDate;
        this.address = address; // **Re-add address initialization**
        this.contactNumber = contactNumber;
        this.parentDetails = parentDetails;
        this.classId = classId;
        this.streamId = streamId;
        this.className = className;
        this.streamName = streamName;
        this.learningSubjects = learningSubjects;
    }
}