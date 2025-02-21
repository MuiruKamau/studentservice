package com.school.studentservice.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class GradeCriteriaResponseDTO {
    private Long id;
    private int minScore;
    private int maxScore;
    private String grade;
    private int points;

    public GradeCriteriaResponseDTO(Long id, int minScore, int maxScore, String grade, int points) {
        this.id = id;
        this.minScore = minScore;
        this.maxScore = maxScore;
        this.grade = grade;
        this.points = points;
    }
}
