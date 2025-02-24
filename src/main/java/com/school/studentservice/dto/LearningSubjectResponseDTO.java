package com.school.studentservice.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LearningSubjectResponseDTO {
    private Long id;
    private String name;

    public LearningSubjectResponseDTO(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}
