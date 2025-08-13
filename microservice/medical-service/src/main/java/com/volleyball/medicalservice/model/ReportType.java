package com.volleyball.medicalservice.model;

/**
 * Énumération des types de rapports médicaux
 */
public enum ReportType {
    MEDICAL_EXAMINATION("Examen médical"),
    INJURY_REPORT("Rapport de blessure"),
    TREATMENT_REPORT("Rapport de traitement"),
    RECOVERY_REPORT("Rapport de récupération"),
    FITNESS_ASSESSMENT("Évaluation de condition physique"),
    CLEARANCE_REPORT("Rapport d'autorisation"),
    SPECIALIST_REPORT("Rapport de spécialiste"),
    EMERGENCY_REPORT("Rapport d'urgence"),
    FOLLOW_UP_REPORT("Rapport de suivi"),
    PRESCRIPTION("Prescription médicale");

    private final String displayName;

    ReportType(String displayName) {
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
