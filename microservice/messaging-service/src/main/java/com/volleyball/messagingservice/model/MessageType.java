package com.volleyball.messagingservice.model;

/**
 * Énumération des types de messages
 */
public enum MessageType {
    PERSONNEL("Message personnel"),
    EQUIPE("Message d'équipe"),
    ENTRAINEMENT("Message d'entraînement"),
    MATCH("Message de match"),
    ADMINISTRATIF("Message administratif"),
    URGENCE("Message d'urgence"),
    INFORMATION("Message d'information"),
    CONVOCATION("Convocation"),
    AUTRE("Autre");

    private final String description;

    MessageType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
