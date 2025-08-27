package com.volleyball.performanceservice.controller;

import com.volleyball.performanceservice.dto.CreatePerformanceRequest;
import com.volleyball.performanceservice.model.Performance;
import com.volleyball.performanceservice.service.PerformanceService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Contrôleur REST pour le service de performance et absences
 */
@RestController
@RequestMapping("/api/performance")
public class PerformanceController {

    @Autowired
    private PerformanceService performanceService;

    // ==================== ENDPOINTS GÉNÉRAUX ====================

    /**
     * Health check du service
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "performance-service");
        response.put("timestamp", java.time.LocalDateTime.now());
        response.put("message", "Service de gestion des performances et absences opérationnel");
        return ResponseEntity.ok(response);
    }

    // ==================== GESTION DES PERFORMANCES UNIQUEMENT ====================

    // ==================== GESTION DES PERFORMANCES ====================

    /**
     * Créer une nouvelle performance
     */
    @PostMapping("/performances")
    public ResponseEntity<Map<String, Object>> createPerformance(@Valid @RequestBody CreatePerformanceRequest request) {
        try {
            Performance performance = performanceService.createPerformance(request);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Performance créée avec succès");
            response.put("data", performance);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Erreur lors de la création de la performance: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    /**
     * Obtenir toutes les performances
     */
    @GetMapping("/performances")
    public ResponseEntity<Map<String, Object>> getAllPerformances() {
        try {
            List<Performance> performances = performanceService.getAllPerformances();
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", performances);
            response.put("count", performances.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Erreur lors de la récupération des performances: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Obtenir une performance par ID
     */
    @GetMapping("/performances/{id}")
    public ResponseEntity<Map<String, Object>> getPerformanceById(@PathVariable Long id) {
        try {
            Performance performance = performanceService.getPerformanceById(id);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", performance);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    /**
     * Obtenir les performances d'un joueur
     */
    @GetMapping("/players/{playerId}/performances")
    public ResponseEntity<Map<String, Object>> getPerformancesByPlayer(@PathVariable Long playerId) {
        try {
            List<Performance> performances = performanceService.getPerformancesByPlayer(playerId);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", performances);
            response.put("count", performances.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Erreur lors de la récupération des performances: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // Optional player stats endpoints can be re-enabled if PlayerService is available in this service

    /**
     * Obtenir les meilleures performances
     */
    @GetMapping("/performances/top")
    public ResponseEntity<Map<String, Object>> getTopPerformances() {
        try {
            List<Performance> performances = performanceService.getTopPerformances();
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", performances);
            response.put("count", performances.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Erreur lors de la récupération des meilleures performances: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Supprimer une performance
     */
    @DeleteMapping("/performances/{id}")
    public ResponseEntity<Map<String, Object>> deletePerformance(@PathVariable Long id) {
        try {
            performanceService.deletePerformance(id);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Performance supprimée avec succès");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Erreur lors de la suppression: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    // Moyenne globale de la note sur toutes les performances
    @GetMapping("/performances/average-note")
    public ResponseEntity<Map<String, Object>> getAverageGlobalNote() {
        try {
            Double avg = performanceService.getAverageGlobalNote(); // 0..10
            double percentage = avg != null ? Math.min(100.0, Math.max(0.0, (avg / 10.0) * 100.0)) : 0.0;

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("averageNote", avg);
            response.put("averagePercentage", percentage);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Erreur lors du calcul de la moyenne: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
