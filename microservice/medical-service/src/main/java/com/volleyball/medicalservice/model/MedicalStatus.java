package com.volleyball.medicalservice.model;

/**
 * Statuts médicaux d'un joueur
 */
public enum MedicalStatus {
    EN_SUIVI("En suivi"),
    REPOS("Repos"),
    RETABLI("Rétabli");

    private final String displayName;

    MedicalStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}


