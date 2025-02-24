package com.school.studentservice.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class StreamResponseDTO {
    private Long id;
    private String name;
    private Long schoolClassId; // Returning schoolClassId in response

    public StreamResponseDTO(Long id, String name, Long schoolClassId) {
        this.id = id;
        this.name = name;
        this.schoolClassId = schoolClassId;
    }
}
