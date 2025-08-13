package com.volleyball.planningservice.service;

import com.volleyball.planningservice.dto.CreateEventRequest;
import com.volleyball.planningservice.dto.EventResponse;
import com.volleyball.planningservice.dto.UpdateEventRequest;
import com.volleyball.planningservice.model.Event;
import com.volleyball.planningservice.model.EventType;
import com.volleyball.planningservice.repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PlanningService {

    @Autowired
    private EventRepository eventRepository;

    // Créer un nouvel événement
    public EventResponse createEvent(CreateEventRequest request) {
        Event event = new Event();
        event.setTitre(request.getTitre());
        event.setDescription(request.getDescription());
        event.setDateDebut(request.getDateDebut());
        event.setDateFin(request.getDateFin());
        event.setType(request.getType());
        event.setLieu(request.getLieu());
        event.setActif(true);
        event.setDateCreation(LocalDateTime.now());

        Event savedEvent = eventRepository.save(event);
        return convertToResponse(savedEvent);
    }

    // Récupérer tous les événements actifs
    public List<EventResponse> getAllEvents() {
        List<Event> events = eventRepository.findByActifTrue();
        return events.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    // Récupérer un événement par ID
    public EventResponse getEventById(Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Événement non trouvé avec l'ID: " + id));
        
        if (!event.getActif()) {
            throw new RuntimeException("Événement inactif avec l'ID: " + id);
        }
        
        return convertToResponse(event);
    }

    // Récupérer les événements par type
    public List<EventResponse> getEventsByType(EventType type) {
        List<Event> events = eventRepository.findByTypeAndActifTrue(type);
        return events.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    // Récupérer les événements à venir
    public List<EventResponse> getUpcomingEvents() {
        List<Event> events = eventRepository.findUpcomingEvents(LocalDateTime.now());
        return events.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    // Récupérer les événements du jour
    public List<EventResponse> getTodayEvents() {
        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime nextDay = today.plusDays(1).atStartOfDay();
        List<Event> events = eventRepository.findEventsOverlapping(startOfDay, nextDay);
        return events.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    // Mettre à jour un événement existant
    public EventResponse updateEvent(Long id, UpdateEventRequest request) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Événement non trouvé avec l'ID: " + id));

        if (!Boolean.TRUE.equals(event.getActif())) {
            throw new RuntimeException("Événement inactif avec l'ID: " + id);
        }

        event.setTitre(request.getTitre());
        event.setDescription(request.getDescription());
        event.setDateDebut(request.getDateDebut());
        event.setDateFin(request.getDateFin());
        event.setType(request.getType());
        event.setLieu(request.getLieu());
        event.setDateModification(LocalDateTime.now());

        Event saved = eventRepository.save(event);
        return convertToResponse(saved);
    }

    // Supprimer un événement (soft delete)
    public void deleteEvent(Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Événement non trouvé avec l'ID: " + id));
        
        event.setActif(false);
        event.setDateModification(LocalDateTime.now());
        eventRepository.save(event);
    }

    // Convertir Event en EventResponse
    private EventResponse convertToResponse(Event event) {
        return new EventResponse(
                event.getId(),
                event.getTitre(),
                event.getDescription(),
                event.getDateDebut(),
                event.getDateFin(),
                event.getType(),
                event.getLieu(),
                event.getActif(),
                event.getDateCreation(),
                event.getDateModification()
        );
    }
} 