package com.volleyball.planningservice.model;

public enum EventType {
    ENTRAINEMENT("Entraînement"),
    MATCH_AMICAL("Match amical"),
    CHAMPIONNAT("Championnat"),
    COUPE("Coupe"),
    REUNION("Réunion"),
    VISONNAGE("Visonnage"),
    AUTRE("Autre");

    private final String displayName;

    EventType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
} 