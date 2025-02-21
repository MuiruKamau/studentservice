package com.school.studentservice.entity;

import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ParentDetails {
    private String parentName;
    private String contact;
    private String address;
}

