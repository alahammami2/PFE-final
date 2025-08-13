package com.volleyball.performanceservice.model;

/**
 * Énumération des statuts d'absence
 */
public enum StatutAbsence {
    EN_ATTENTE("En attente"),
    APPROUVEE("Approuvée"),
    REFUSEE("Refusée"),
    ANNULEE("Annulée");

    private final String description;

    StatutAbsence(String description) {
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
