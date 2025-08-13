package com.volleyball.medicalservice.model;

/**
 * Énumération des priorités de rendez-vous médicaux
 */
public enum AppointmentPriority {
    LOW("Faible"),
    NORMAL("Normale"),
    HIGH("Élevée"),
    URGENT("Urgente"),
    EMERGENCY("Urgence");

    private final String displayName;

    AppointmentPriority(String displayName) {
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
