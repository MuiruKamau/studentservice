package com.school.studentservice.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@NoArgsConstructor
public class StudentResponseDTO {
    private Long id;
    private String name;
    private String admissionNumber;
    private LocalDate dateOfBirth;
    private LocalDate enrollmentDate;
    private String address;
    private String contactNumber;
    private ParentDetailsDTO parentDetails;
    private String className;
    private String streamName;

    public StudentResponseDTO(Long id, String name, String admissionNumber, LocalDate dateOfBirth, LocalDate enrollmentDate, String address, String contactNumber, ParentDetailsDTO parentDetails, String className, String streamName) {
        this.id = id;
        this.name = name;
        this.admissionNumber = admissionNumber;
        this.dateOfBirth = dateOfBirth;
        this.enrollmentDate = enrollmentDate;
        this.address = address;
        this.contactNumber = contactNumber;
        this.parentDetails = parentDetails;
        this.className = className;
        this.streamName = streamName;
    }
}
