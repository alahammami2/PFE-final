package com.volleyball.planningservice.model;

public enum EventType {
    ENTRAINEMENT("Entraînement"),
    MATCH("Match"),
    REUNION("Réunion"),
    COMPETITION("Compétition"),
    EVENEMENT("Événement"),
    AUTRE("Autre");

    private final String displayName;

    EventType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
} 