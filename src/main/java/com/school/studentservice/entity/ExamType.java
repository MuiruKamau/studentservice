package com.school.studentservice.entity;

public enum ExamType {
    OPENER("Opener"),
    MID_TERM("Mid-Term"),
    END_TERM("End-Term");

    private final String value;

    ExamType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static ExamType fromValue(String value) {
        for (ExamType examType : ExamType.values()) {
            if (examType.value.equalsIgnoreCase(value)) {
                return examType;
            }
        }
        throw new IllegalArgumentException("Invalid exam type: " + value);
    }
}