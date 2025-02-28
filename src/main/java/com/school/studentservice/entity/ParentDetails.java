package com.school.studentservice.entity;

import jakarta.persistence.Column; // Import Column annotation
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParentDetails {
    private String parentName;
    private String parentContact;
    @Column(name = "parent_address") // Map to parent_address column
    private String address; // Address for ParentDetails, mapped to parent_address
}