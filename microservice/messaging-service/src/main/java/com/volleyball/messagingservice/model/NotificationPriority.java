package com.volleyball.messagingservice.model;

/**
 * Énumération des priorités de notifications
 */
public enum NotificationPriority {
    BASSE("Priorité basse"),
    NORMALE("Priorité normale"),
    HAUTE("Priorité haute"),
    URGENTE("Priorité urgente"),
    CRITIQUE("Priorité critique");

    private final String description;

    NotificationPriority(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
