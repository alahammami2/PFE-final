package com.volleyball.performanceservice.model;

/**
 * Énumération des positions des joueurs de volleyball
 */
public enum Position {
    PASSEUR("Passeur"),
    ATTAQUANT("Attaquant"),
    CENTRAL("Central"),
    LIBERO("Libéro"),
    POINTU("Pointu"),
    RECEPTEUR_ATTAQUANT("Récepteur-Attaquant");

    private final String description;

    Position(String description) {
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
