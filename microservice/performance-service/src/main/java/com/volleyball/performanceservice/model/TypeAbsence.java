package com.volleyball.performanceservice.model;

/**
 * Énumération des types d'absence
 */
public enum TypeAbsence {
    MALADIE("Maladie"),
    BLESSURE("Blessure"),
    PERSONNEL("Personnel"),
    PROFESSIONNEL("Professionnel"),
    VACANCES("Vacances"),
    AUTRE("Autre");

    private final String description;

    TypeAbsence(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return description;
    }
}
