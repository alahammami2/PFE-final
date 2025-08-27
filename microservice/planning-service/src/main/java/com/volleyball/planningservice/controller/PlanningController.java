package com.volleyball.planningservice.controller;

import com.volleyball.planningservice.dto.CreateEventRequest;
import com.volleyball.planningservice.dto.EventResponse;
import com.volleyball.planningservice.dto.UpdateEventRequest;
import com.volleyball.planningservice.model.EventType;
import com.volleyball.planningservice.service.PlanningService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/planning")
public class PlanningController {

    @Autowired
    private PlanningService planningService;

    // Endpoint de santé
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Service de planification opérationnel");
        response.put("data", "Planning Service v1.0.0");
        return ResponseEntity.ok(response);
    }

    // Mettre à jour un événement existant
    @PutMapping("/events/{id}")
    public ResponseEntity<Map<String, Object>> updateEvent(@PathVariable Long id,
                                                           @Valid @RequestBody UpdateEventRequest request) {
        try {
            EventResponse updated = planningService.updateEvent(id, request);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Événement modifié avec succès");
            response.put("data", updated);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Erreur lors de la modification de l'événement: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // Créer un nouvel événement
    @PostMapping("/events")
    public ResponseEntity<Map<String, Object>> createEvent(@Valid @RequestBody CreateEventRequest request) {
        try {
            EventResponse event = planningService.createEvent(request);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Événement créé avec succès");
            response.put("data", event);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Erreur lors de la création de l'événement: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // Récupérer tous les événements
    @GetMapping("/events")
    public ResponseEntity<Map<String, Object>> getAllEvents() {
        try {
            List<EventResponse> events = planningService.getAllEvents();
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Événements récupérés avec succès");
            response.put("data", events);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Erreur lors de la récupération des événements: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // Récupérer un événement par ID
    @GetMapping("/events/{id}")
    public ResponseEntity<Map<String, Object>> getEventById(@PathVariable Long id) {
        try {
            EventResponse event = planningService.getEventById(id);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Événement récupéré avec succès");
            response.put("data", event);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Erreur lors de la récupération de l'événement: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // Récupérer les événements par type
    @GetMapping("/events/type/{type}")
    public ResponseEntity<Map<String, Object>> getEventsByType(@PathVariable EventType type) {
        try {
            List<EventResponse> events = planningService.getEventsByType(type);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Événements récupérés avec succès");
            response.put("data", events);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Erreur lors de la récupération des événements: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // Récupérer les événements à venir
    @GetMapping("/events/upcoming")
    public ResponseEntity<Map<String, Object>> getUpcomingEvents() {
        try {
            List<EventResponse> events = planningService.getUpcomingEvents();
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Événements à venir récupérés avec succès");
            response.put("data", events);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Erreur lors de la récupération des événements: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // Récupérer les événements du jour
    @GetMapping("/events/today")
    public ResponseEntity<Map<String, Object>> getTodayEvents() {
        try {
            List<EventResponse> events = planningService.getTodayEvents();
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Événements du jour récupérés avec succès");
            response.put("data", events);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Erreur lors de la récupération des événements: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // Compter les matchs passés par type (CHAMPIONNAT et COUPE)
    @GetMapping("/events/matches/past/count")
    public ResponseEntity<Map<String, Object>> countPastMatchesByType() {
        try {
            Map<String, Long> counts = planningService.countPastMatchesByType();
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Comptage des matchs passés par type");
            response.put("data", counts);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Erreur lors du comptage des matchs: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // Supprimer un événement
    @DeleteMapping("/events/{id}")
    public ResponseEntity<Map<String, Object>> deleteEvent(@PathVariable Long id) {
        try {
            planningService.deleteEvent(id);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Événement supprimé avec succès");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Erreur lors de la suppression de l'événement: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
} 