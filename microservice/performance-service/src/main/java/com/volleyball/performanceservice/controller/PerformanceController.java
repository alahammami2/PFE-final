package com.volleyball.performanceservice.controller;

import com.volleyball.performanceservice.dto.CreateAbsenceRequest;
import com.volleyball.performanceservice.dto.CreatePerformanceRequest;
import com.volleyball.performanceservice.dto.CreatePlayerRequest;
import com.volleyball.performanceservice.model.*;
import com.volleyball.performanceservice.service.AbsenceService;
import com.volleyball.performanceservice.service.PerformanceService;
import com.volleyball.performanceservice.service.PlayerService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Contrôleur REST pour le service de performance et absences
 */
@RestController
@RequestMapping("/api/performance")
@CrossOrigin(origins = "*")
public class PerformanceController {

    @Autowired
    private PlayerService playerService;

    @Autowired
    private AbsenceService absenceService;

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

    // ==================== GESTION DES JOUEURS ====================

    /**
     * Créer un nouveau joueur
     */
    @PostMapping("/players")
    public ResponseEntity<Map<String, Object>> createPlayer(@Valid @RequestBody CreatePlayerRequest request) {
        try {
            Player player = playerService.createPlayer(request);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Joueur créé avec succès");
            response.put("data", player);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Erreur lors de la création du joueur: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    /**
     * Obtenir tous les joueurs actifs
     */
    @GetMapping("/players")
    public ResponseEntity<Map<String, Object>> getAllPlayers(@RequestParam(defaultValue = "true") boolean activeOnly) {
        try {
            List<Player> players = activeOnly ? playerService.getAllActivePlayers() : playerService.getAllPlayers();
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", players);
            response.put("count", players.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Erreur lors de la récupération des joueurs: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Obtenir un joueur par ID
     */
    @GetMapping("/players/{id}")
    public ResponseEntity<Map<String, Object>> getPlayerById(@PathVariable Long id) {
        try {
            Player player = playerService.getPlayerById(id);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", player);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    /**
     * Obtenir les joueurs par position
     */
    @GetMapping("/players/position/{position}")
    public ResponseEntity<Map<String, Object>> getPlayersByPosition(@PathVariable Position position) {
        try {
            List<Player> players = playerService.getPlayersByPosition(position);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", players);
            response.put("count", players.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Erreur lors de la récupération des joueurs: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Rechercher des joueurs par nom
     */
    @GetMapping("/players/search")
    public ResponseEntity<Map<String, Object>> searchPlayers(@RequestParam String query) {
        try {
            List<Player> players = playerService.searchPlayersByName(query);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", players);
            response.put("count", players.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Erreur lors de la recherche: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Mettre à jour un joueur
     */
    @PutMapping("/players/{id}")
    public ResponseEntity<Map<String, Object>> updatePlayer(@PathVariable Long id, @Valid @RequestBody CreatePlayerRequest request) {
        try {
            Player player = playerService.updatePlayer(id, request);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Joueur mis à jour avec succès");
            response.put("data", player);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Erreur lors de la mise à jour: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    /**
     * Changer le statut d'un joueur
     */
    @PatchMapping("/players/{id}/status")
    public ResponseEntity<Map<String, Object>> changePlayerStatus(@PathVariable Long id, @RequestParam StatutJoueur statut) {
        try {
            Player player = playerService.changePlayerStatus(id, statut);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Statut du joueur mis à jour avec succès");
            response.put("data", player);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Erreur lors du changement de statut: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    /**
     * Supprimer un joueur
     */
    @DeleteMapping("/players/{id}")
    public ResponseEntity<Map<String, Object>> deletePlayer(@PathVariable Long id) {
        try {
            playerService.deletePlayer(id);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Joueur supprimé avec succès");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Erreur lors de la suppression: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    /**
     * Obtenir les statistiques des joueurs
     */
    @GetMapping("/players/statistics")
    public ResponseEntity<Map<String, Object>> getPlayersStatistics() {
        try {
            Object statistics = playerService.getPlayersStatistics();
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", statistics);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Erreur lors de la récupération des statistiques: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // ==================== GESTION DES ABSENCES ====================

    /**
     * Créer une nouvelle absence
     */
    @PostMapping("/absences")
    public ResponseEntity<Map<String, Object>> createAbsence(@Valid @RequestBody CreateAbsenceRequest request) {
        try {
            Absence absence = absenceService.createAbsence(request);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Absence créée avec succès");
            response.put("data", absence);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Erreur lors de la création de l'absence: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }



    /**
     * Obtenir toutes les absences
     */
    @GetMapping("/absences")
    public ResponseEntity<Map<String, Object>> getAllAbsences() {
        try {
            List<Absence> absences = absenceService.getAllAbsences();
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", absences);
            response.put("count", absences.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Erreur lors de la récupération des absences: " + e.getMessage());
            response.put("error", e.getClass().getSimpleName());
            response.put("stackTrace", e.getStackTrace()[0].toString());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Obtenir une absence par ID
     */
    @GetMapping("/absences/{id}")
    public ResponseEntity<Map<String, Object>> getAbsenceById(@PathVariable Long id) {
        try {
            Absence absence = absenceService.getAbsenceById(id);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", absence);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    /**
     * Obtenir les absences d'un joueur
     */
    @GetMapping("/players/{playerId}/absences")
    public ResponseEntity<Map<String, Object>> getAbsencesByPlayer(@PathVariable Long playerId) {
        try {
            List<Absence> absences = absenceService.getAbsencesByPlayer(playerId);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", absences);
            response.put("count", absences.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Erreur lors de la récupération des absences: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Obtenir les absences en cours
     */
    @GetMapping("/absences/current")
    public ResponseEntity<Map<String, Object>> getCurrentAbsences() {
        try {
            List<Absence> absences = absenceService.getAbsencesEnCours();
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", absences);
            response.put("count", absences.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Erreur lors de la récupération des absences en cours: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Obtenir les absences par période
     */
    @GetMapping("/absences/period")
    public ResponseEntity<Map<String, Object>> getAbsencesByPeriod(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDebut,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFin) {
        try {
            List<Absence> absences = absenceService.getAbsencesByPeriode(dateDebut, dateFin);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", absences);
            response.put("count", absences.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Erreur lors de la récupération des absences: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Approuver une absence
     */
    @PatchMapping("/absences/{id}/approve")
    public ResponseEntity<Map<String, Object>> approveAbsence(@PathVariable Long id, @RequestParam(required = false) String commentaires) {
        try {
            Absence absence = absenceService.approuverAbsence(id, commentaires);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Absence approuvée avec succès");
            response.put("data", absence);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Erreur lors de l'approbation: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    /**
     * Refuser une absence
     */
    @PatchMapping("/absences/{id}/reject")
    public ResponseEntity<Map<String, Object>> rejectAbsence(@PathVariable Long id, @RequestParam(required = false) String commentaires) {
        try {
            Absence absence = absenceService.refuserAbsence(id, commentaires);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Absence refusée avec succès");
            response.put("data", absence);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Erreur lors du refus: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    /**
     * Supprimer une absence
     */
    @DeleteMapping("/absences/{id}")
    public ResponseEntity<Map<String, Object>> deleteAbsence(@PathVariable Long id) {
        try {
            absenceService.deleteAbsence(id);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Absence supprimée avec succès");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Erreur lors de la suppression: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

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

    /**
     * Obtenir les statistiques moyennes d'un joueur
     */
    @GetMapping("/players/{playerId}/stats/average")
    public ResponseEntity<Map<String, Object>> getPlayerAverageStats(@PathVariable Long playerId) {
        try {
            Object stats = performanceService.getAverageStatsByPlayer(playerId);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", stats);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    /**
     * Obtenir les statistiques totales d'un joueur
     */
    @GetMapping("/players/{playerId}/stats/total")
    public ResponseEntity<Map<String, Object>> getPlayerTotalStats(@PathVariable Long playerId) {
        try {
            Object stats = performanceService.getTotalStatsByPlayer(playerId);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", stats);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

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

    // ==================== STATISTIQUES GLOBALES ====================

    /**
     * Obtenir les statistiques globales du service
     */
    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getGlobalStatistics() {
        try {
            Object playersStats = playerService.getPlayersStatistics();
            Object absencesStats = absenceService.getAbsencesStatistics();
            Object performancesStats = performanceService.getPerformancesStatistics();

            Map<String, Object> globalStats = new HashMap<>();
            globalStats.put("joueurs", playersStats);
            globalStats.put("absences", absencesStats);
            globalStats.put("performances", performancesStats);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", globalStats);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Erreur lors de la récupération des statistiques: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
