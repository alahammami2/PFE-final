package com.volleyball.medicalservice.model;

/**
 * Énumération des statuts de rapports médicaux
 */
public enum ReportStatus {
    DRAFT("Brouillon"),
    PENDING_REVIEW("En attente de révision"),
    REVIEWED("Révisé"),
    APPROVED("Approuvé"),
    PUBLISHED("Publié"),
    ARCHIVED("Archivé"),
    CANCELLED("Annulé");

    private final String displayName;

    ReportStatus(String displayName) {
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
