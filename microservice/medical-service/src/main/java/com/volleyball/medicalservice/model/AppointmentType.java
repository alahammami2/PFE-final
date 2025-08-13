package com.volleyball.medicalservice.model;

/**
 * Énumération des types de rendez-vous médicaux
 */
public enum AppointmentType {
    CHECKUP("Examen de routine"),
    CONSULTATION("Consultation"),
    EMERGENCY("Urgence"),
    FOLLOW_UP("Suivi"),
    PHYSICAL_THERAPY("Kinésithérapie"),
    SPORTS_MEDICINE("Médecine du sport"),
    INJURY_ASSESSMENT("Évaluation de blessure"),
    VACCINATION("Vaccination"),
    MEDICAL_CLEARANCE("Autorisation médicale"),
    SPECIALIST_CONSULTATION("Consultation spécialisée");

    private final String displayName;

    AppointmentType(String displayName) {
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
