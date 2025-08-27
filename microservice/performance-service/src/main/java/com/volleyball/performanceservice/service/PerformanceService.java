package com.volleyball.performanceservice.service;

import com.volleyball.performanceservice.dto.CreatePerformanceRequest;
import com.volleyball.performanceservice.model.Performance;
import com.volleyball.performanceservice.model.Player;
import com.volleyball.performanceservice.repository.PerformanceRepository;
import com.volleyball.performanceservice.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service pour la gestion des performances
 */
@Service
@Transactional
public class PerformanceService {

    @Autowired
    private PerformanceRepository performanceRepository;

    @Autowired
    private PlayerRepository playerRepository;

    /**
     * Créer une nouvelle performance
     */
    public Performance createPerformance(CreatePerformanceRequest request) {
        // Vérifier que le joueur existe
        Player player = playerRepository.findById(request.getPlayerId())
                .orElseThrow(() -> new RuntimeException("Joueur non trouvé avec l'ID: " + request.getPlayerId()));

        // Validation des statistiques
        validatePerformanceStats(request);

        // Création de la performance
        Performance performance = new Performance();
        performance.setPlayer(player);
        
        // Statistiques offensives
        performance.setAces(request.getAces());
        
        // Statistiques défensives
        performance.setReceptionsTotales(request.getReceptionsTotales());
        performance.setReceptionsReussies(request.getReceptionsReussies());
        performance.setBloc(request.getBloc());
        
        // Statistiques de service
        performance.setServicesTotaux(request.getServicesTotaux());
        performance.setServicesReussis(request.getServicesReussis());
        
        // Erreurs
        performance.setErreursService(request.getErreursService());
        performance.setErreursReception(request.getErreursReception());
        
        // Note
        performance.setNoteGlobale(request.getNoteGlobale());
        performance.setCommentaires(request.getCommentaires());

        return performanceRepository.save(performance);
    }

    /**
     * Valider la cohérence des statistiques
     */
    private void validatePerformanceStats(CreatePerformanceRequest request) {
        // Règle d'attaques retirée (colonnes supprimées)

        // Les réceptions réussies ne peuvent pas dépasser les réceptions totales
        if (request.getReceptionsReussies() > request.getReceptionsTotales()) {
            throw new RuntimeException("Le nombre de réceptions réussies ne peut pas dépasser le nombre de réceptions totales");
        }

        // Les services réussis ne peuvent pas dépasser les services totaux
        if (request.getServicesReussis() > request.getServicesTotaux()) {
            throw new RuntimeException("Le nombre de services réussis ne peut pas dépasser le nombre de services totaux");
        }
    }

    /**
     * Obtenir toutes les performances
     */
    @Transactional(readOnly = true)
    public List<Performance> getAllPerformances() {
        return performanceRepository.findAll();
    }

    /**
     * Obtenir une performance par ID
     */
    @Transactional(readOnly = true)
    public Performance getPerformanceById(Long id) {
        return performanceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Performance non trouvée avec l'ID: " + id));
    }

    /**
     * Obtenir les performances d'un joueur
     */
    @Transactional(readOnly = true)
    public List<Performance> getPerformancesByPlayer(Long playerId) {
        return performanceRepository.findByPlayerId(playerId);
    }

    // Méthodes par type/période supprimées (colonnes supprimées)

    /**
     * Obtenir les dernières performances d'un joueur
     */
    @Transactional(readOnly = true)
    public List<Performance> getLatestPerformancesByPlayer(Long playerId, int limit) {
        List<Performance> allPerformances = performanceRepository.findRecentPerformancesByPlayer(playerId);
        return allPerformances.stream().limit(limit).toList();
    }

    /**
     * Obtenir les meilleures performances
     */
    @Transactional(readOnly = true)
    public List<Performance> getTopPerformances() {
        return performanceRepository.findTopPerformances();
    }

    /**
     * Obtenir les meilleures performances d'un joueur
     */
    @Transactional(readOnly = true)
    public List<Performance> getTopPerformancesByPlayer(Long playerId) {
        return performanceRepository.findTopPerformancesByPlayer(playerId);
    }

    /**
     * Obtenir la moyenne globale de la note sur toutes les performances
     */
    @Transactional(readOnly = true)
    public Double getAverageGlobalNote() {
        Double avg = performanceRepository.getAverageGlobalNote();
        return avg != null ? avg : 0.0;
    }

    /**
     * Obtenir les statistiques moyennes d'un joueur
     */
    @Transactional(readOnly = true)
    public Object getAverageStatsByPlayer(Long playerId) {
        Object[] stats = performanceRepository.getAverageStatsByPlayer(playerId);
        if (stats == null || stats[0] == null) {
            throw new RuntimeException("Aucune statistique trouvée pour le joueur avec l'ID: " + playerId);
        }

        return new Object() {
            public final Double moyenneAces = (Double) stats[0];
            public final Double moyenneNoteGlobale = (Double) stats[1];
        };
    }

    /**
     * Obtenir les statistiques totales d'un joueur
     */
    @Transactional(readOnly = true)
    public Object getTotalStatsByPlayer(Long playerId) {
        Object[] stats = performanceRepository.getTotalStatsByPlayer(playerId);
        if (stats == null || stats[0] == null) {
            throw new RuntimeException("Aucune statistique trouvée pour le joueur avec l'ID: " + playerId);
        }

        return new Object() {
            public final Long totalAces = (Long) stats[0];
        };
    }

    // Comparaison des performances entre joueurs (moyenne globale)
    @Transactional(readOnly = true)
    public List<Object[]> comparePlayersPerformance() {
        return performanceRepository.comparePlayersPerformance();
    }

    /**
     * Obtenir les joueurs les plus performants
     */
    @Transactional(readOnly = true)
    public List<Object[]> getTopPerformingPlayers() {
        return performanceRepository.findTopPerformingPlayers();
    }

    /**
     * Mettre à jour une performance
     */
    public Performance updatePerformance(Long id, CreatePerformanceRequest request) {
        Performance performance = getPerformanceById(id);

        // Validation des statistiques
        validatePerformanceStats(request);

        // Mise à jour des champs
        
        // Statistiques offensives
        performance.setAces(request.getAces());
        
        // Statistiques défensives
        performance.setReceptionsTotales(request.getReceptionsTotales());
        performance.setReceptionsReussies(request.getReceptionsReussies());
        performance.setBloc(request.getBloc());
        
        // Statistiques de service
        performance.setServicesTotaux(request.getServicesTotaux());
        performance.setServicesReussis(request.getServicesReussis());
        
        // Erreurs
        performance.setErreursService(request.getErreursService());
        performance.setErreursReception(request.getErreursReception());
        
        // Note
        performance.setNoteGlobale(request.getNoteGlobale());
        performance.setCommentaires(request.getCommentaires());

        return performanceRepository.save(performance);
    }

    /**
     * Supprimer une performance
     */
    public void deletePerformance(Long id) {
        Performance performance = getPerformanceById(id);
        performanceRepository.delete(performance);
    }

    /**
     * Obtenir les statistiques globales des performances
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getPerformancesStatistics() {
        long totalPerformancesCount = performanceRepository.countTotalPerformances();
        List<Object[]> topPlayers = performanceRepository.findTopPerformingPlayers();

        Map<String, Object> statistics = new HashMap<>();
        statistics.put("totalPerformances", totalPerformancesCount);
        statistics.put("joueursLesPlusPerformants", topPlayers);
        
        return statistics;
    }

    /**
     * Obtenir les performances avec note supérieure à un seuil
     */
    @Transactional(readOnly = true)
    public List<Performance> getPerformancesAboveThreshold(Double noteMinimum) {
        return performanceRepository.findPerformancesAboveThreshold(noteMinimum);
    }

    /**
     * Obtenir les performances d'un joueur avec note supérieure à un seuil
     */
    @Transactional(readOnly = true)
    public List<Performance> getPlayerPerformancesAboveThreshold(Long playerId, Double noteMinimum) {
        return performanceRepository.findPlayerPerformancesAboveThreshold(playerId, noteMinimum);
    }
}
