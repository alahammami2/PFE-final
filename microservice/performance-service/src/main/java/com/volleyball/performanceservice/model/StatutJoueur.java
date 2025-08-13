package com.volleyball.performanceservice.model;

/**
 * Énumération des statuts des joueurs
 */
public enum StatutJoueur {
    ACTIF("Actif"),
    BLESSE("Blessé"),
    SUSPENDU("Suspendu"),
    INACTIF("Inactif"),
    TRANSFERE("Transféré");

    private final String description;

    StatutJoueur(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return description;
    }
}
