package com.school.studentservice.entity;


public enum Term {
    TERM_1("Term 1"),
    TERM_2("Term 2"),
    TERM_3("Term 3");

    private final String value;

    Term(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static Term fromValue(String value) {
        for (Term term : Term.values()) {
            if (term.value.equalsIgnoreCase(value)) {
                return term;
            }
        }
        throw new IllegalArgumentException("Invalid term: " + value);
    }
}