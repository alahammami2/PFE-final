package com.volleyball.medicalservice.model;

/**
 * Énumération des statuts de rendez-vous médicaux
 */
public enum AppointmentStatus {
    SCHEDULED("Programmé"),
    CONFIRMED("Confirmé"),
    IN_PROGRESS("En cours"),
    COMPLETED("Terminé"),
    CANCELLED("Annulé"),
    NO_SHOW("Absence"),
    RESCHEDULED("Reprogrammé"),
    PENDING("En attente");

    private final String displayName;

    AppointmentStatus(String displayName) {
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
