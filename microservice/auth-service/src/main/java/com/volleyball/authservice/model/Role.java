package com.volleyball.authservice.model;

public enum Role {
    ADMIN("ADMIN"),
    COACH("COACH"),
    JOUEUR("JOUEUR"),
    RESPONSABLE_FINANCIER("RESPONSABLE_FINANCIER"),
    STAFF_MEDICAL("STAFF_MEDICAL"),
    INVITE("INVITE");

    private final String value;

    Role(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }
}
