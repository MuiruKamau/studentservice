package com.school.studentservice.entity;

import jakarta.persistence.Entity; // Jakarta Persistence
import jakarta.persistence.Id;     // Jakarta Persistence
import jakarta.persistence.Table;  // Jakarta Persistence
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@Table(name = "learning_subjects")
public class LearningSubject {

    @Id
    private Long id;
    private String name;

    public LearningSubject(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}

