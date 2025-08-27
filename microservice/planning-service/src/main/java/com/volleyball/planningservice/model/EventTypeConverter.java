package com.volleyball.planningservice.model;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.text.Normalizer;

@Converter(autoApply = true)
public class EventTypeConverter implements AttributeConverter<EventType, String> {

    @Override
    public String convertToDatabaseColumn(EventType attribute) {
        if (attribute == null) return null;
        // Store as enum name to keep consistency
        return attribute.name();
    }

    @Override
    public EventType convertToEntityAttribute(String dbData) {
        if (dbData == null) return null;
        // 1) Try by enum name directly
        try {
            return EventType.valueOf(dbData);
        } catch (IllegalArgumentException ignored) {}

        // 2) Try by displayName (legacy values) with accent/case insensitivity
        String normalized = normalize(dbData);
        for (EventType type : EventType.values()) {
            if (normalize(type.getDisplayName()).equals(normalized)) {
                return type;
            }
        }

        // 3) Try by upper-case of legacy without accents (e.g., "MATCH")
        for (EventType type : EventType.values()) {
            if (type.name().equalsIgnoreCase(normalized)) {
                return type;
            }
        }

        // If unknown, default to AUTRE to avoid crashes
        return EventType.AUTRE;
    }

    private static String normalize(String s) {
        String n = Normalizer.normalize(s, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", ""); // remove diacritics
        return n.trim().toLowerCase();
    }
}
