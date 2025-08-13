package com.volleyball.messagingservice.model;

/**
 * Énumération des priorités de messages
 */
public enum MessagePriority {
    BASSE("Priorité basse"),
    NORMALE("Priorité normale"),
    HAUTE("Priorité haute"),
    URGENTE("Priorité urgente");

    private final String description;

    MessagePriority(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
