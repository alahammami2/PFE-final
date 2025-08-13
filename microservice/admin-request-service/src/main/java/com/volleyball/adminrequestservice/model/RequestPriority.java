package com.volleyball.adminrequestservice.model;

/**
 * Énumération des priorités de demandes administratives
 */
public enum RequestPriority {
    BASSE("Priorité basse"),
    NORMALE("Priorité normale"),
    HAUTE("Priorité haute"),
    URGENTE("Priorité urgente"),
    CRITIQUE("Priorité critique");

    private final String description;

    RequestPriority(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
