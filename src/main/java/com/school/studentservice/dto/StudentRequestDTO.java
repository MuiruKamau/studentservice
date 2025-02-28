package com.school.studentservice.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@NoArgsConstructor
public class StudentRequestDTO {
    private String name;
    private LocalDate dateOfBirth;
    private LocalDate enrollmentDate;
    private String address; // **Re-add address to DTO**
    private String contactNumber;
    private ParentDetailsDTO parentDetails;
    private Long classId;
    private Long streamId;
}