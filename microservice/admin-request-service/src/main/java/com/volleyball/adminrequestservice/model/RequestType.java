package com.volleyball.adminrequestservice.model;

/**
 * Énumération des types de demandes administratives
 */
public enum RequestType {
    CONGE("Demande de congé"),
    ABSENCE("Justification d'absence"),
    MATERIEL("Demande de matériel"),
    TRANSPORT("Demande de transport"),
    HEBERGEMENT("Demande d'hébergement"),
    BUDGET("Demande budgétaire"),
    FORMATION("Demande de formation"),
    EVENEMENT("Organisation d'événement"),
    PARTENARIAT("Demande de partenariat"),
    AUTRE("Autre demande");

    private final String description;

    RequestType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
