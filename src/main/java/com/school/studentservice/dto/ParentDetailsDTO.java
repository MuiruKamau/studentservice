package com.school.studentservice.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder; // Import Builder

@Data
@NoArgsConstructor
public class ParentDetailsDTO {
    private String parentName;
    private String contact;
    private String address;

    public ParentDetailsDTO(String parentName, String contact, String address) {
        this.parentName = parentName;
        this.contact = contact;
        this.address = address;
    }
}