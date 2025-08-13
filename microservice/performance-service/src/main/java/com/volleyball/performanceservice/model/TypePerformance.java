package com.volleyball.performanceservice.model;

/**
 * Énumération des types de performance
 */
public enum TypePerformance {
    MATCH("Match"),
    ENTRAINEMENT("Entraînement"),
    COMPETITION("Compétition"),
    TOURNOI("Tournoi"),
    AMICAL("Match amical");

    private final String description;

    TypePerformance(String description) {
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
