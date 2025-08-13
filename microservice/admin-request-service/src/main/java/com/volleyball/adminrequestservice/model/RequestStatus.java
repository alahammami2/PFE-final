package com.volleyball.adminrequestservice.model;

/**
 * Énumération des statuts de demandes administratives
 */
public enum RequestStatus {
    BROUILLON("Brouillon"),
    SOUMISE("Soumise"),
    EN_COURS("En cours de traitement"),
    EN_ATTENTE("En attente d'informations"),
    APPROUVEE("Approuvée"),
    REJETEE("Rejetée"),
    ANNULEE("Annulée"),
    TERMINEE("Terminée");

    private final String description;

    RequestStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
