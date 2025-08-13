package com.volleyball.medicalservice.model;

/**
 * Énumération des statuts de santé
 */
public enum HealthStatus {
    ACTIVE("Actif"),
    INJURED("Blessé"),
    RECOVERING("En récupération"),
    CLEARED("Autorisé"),
    RESTRICTED("Restrictions médicales"),
    INACTIVE("Inactif");

    private final String displayName;

    HealthStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
