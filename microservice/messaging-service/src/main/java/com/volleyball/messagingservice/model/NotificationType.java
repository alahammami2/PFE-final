package com.volleyball.messagingservice.model;

/**
 * Énumération des types de notifications
 */
public enum NotificationType {
    SYSTEME("Notification système"),
    ENTRAINEMENT("Notification d'entraînement"),
    MATCH("Notification de match"),
    CONVOCATION("Convocation"),
    ABSENCE("Notification d'absence"),
    PERFORMANCE("Notification de performance"),
    PLANNING("Notification de planning"),
    RAPPEL("Rappel"),
    ALERTE("Alerte"),
    INFORMATION("Information générale");

    private final String description;

    NotificationType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
