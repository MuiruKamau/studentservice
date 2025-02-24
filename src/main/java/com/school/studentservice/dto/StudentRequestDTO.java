package com.school.studentservice.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class StudentRequestDTO {
    private String name;
    private LocalDate dateOfBirth;
    private LocalDate enrollmentDate;
    private String address;
    private String contactNumber;
    private ParentDetailsDTO parentDetails;
    private Long classId;
    private Long streamId;
}