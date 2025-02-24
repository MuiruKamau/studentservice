package com.school.studentservice.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SchoolClassResponseDTO {
    private Long id;
    private String name;

    public SchoolClassResponseDTO(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}
