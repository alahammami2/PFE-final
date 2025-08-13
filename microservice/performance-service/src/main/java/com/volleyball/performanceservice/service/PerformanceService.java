package com.volleyball.performanceservice.service;

import com.volleyball.performanceservice.dto.CreatePerformanceRequest;
import com.volleyball.performanceservice.model.Performance;
import com.volleyball.performanceservice.model.Player;
import com.volleyball.performanceservice.model.TypePerformance;
import com.volleyball.performanceservice.repository.PerformanceRepository;
import com.volleyball.performanceservice.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
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

        // Vérifier qu'une performance n'existe pas déjà pour ce joueur, cette date et ce type
        if (performanceRepository.existsByPlayerIdAndDatePerformanceAndTypePerformance(
                request.getPlayerId(), request.getDatePerformance(), request.getTypePerformance())) {
            throw new RuntimeException("Une performance existe déjà pour ce joueur à cette date et pour ce type d'activité");
        }

        // Validation des statistiques
        validatePerformanceStats(request);

        // Création de la performance
        Performance performance = new Performance();
        performance.setPlayer(player);
        performance.setDatePerformance(request.getDatePerformance());
        performance.setTypePerformance(request.getTypePerformance());
        
        // Statistiques offensives
        performance.setAttaquesTotales(request.getAttaquesTotales());
        performance.setAttaquesReussies(request.getAttaquesReussies());
        performance.setAces(request.getAces());
        
        // Statistiques défensives
        performance.setBlocs(request.getBlocs());
        performance.setReceptionsTotales(request.getReceptionsTotales());
        performance.setReceptionsReussies(request.getReceptionsReussies());
        performance.setDefenses(request.getDefenses());
        
        // Statistiques de service
        performance.setServicesTotaux(request.getServicesTotaux());
        performance.setServicesReussis(request.getServicesReussis());
        
        // Erreurs
        performance.setErreursAttaque(request.getErreursAttaque());
        performance.setErreursService(request.getErreursService());
        performance.setErreursReception(request.getErreursReception());
        
        // Temps et note
        performance.setTempsJeuMinutes(request.getTempsJeuMinutes());
        performance.setNoteGlobale(request.getNoteGlobale());
        performance.setCommentaires(request.getCommentaires());

        return performanceRepository.save(performance);
    }

    /**
     * Valider la cohérence des statistiques
     */
    private void validatePerformanceStats(CreatePerformanceRequest request) {
        // Les attaques réussies ne peuvent pas dépasser les attaques totales
        if (request.getAttaquesReussies() > request.getAttaquesTotales()) {
            throw new RuntimeException("Le nombre d'attaques réussies ne peut pas dépasser le nombre d'attaques totales");
        }

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

    /**
     * Obtenir les performances par type
     */
    @Transactional(readOnly = true)
    public List<Performance> getPerformancesByType(TypePerformance typePerformance) {
        return performanceRepository.findByTypePerformance(typePerformance);
    }

    /**
     * Obtenir les performances par période
     */
    @Transactional(readOnly = true)
    public List<Performance> getPerformancesByPeriode(LocalDate dateDebut, LocalDate dateFin) {
        return performanceRepository.findByPeriode(dateDebut, dateFin);
    }

    /**
     * Obtenir les performances d'un joueur par période
     */
    @Transactional(readOnly = true)
    public List<Performance> getPerformancesByPlayerAndPeriode(Long playerId, LocalDate dateDebut, LocalDate dateFin) {
        return performanceRepository.findByPlayerAndPeriode(playerId, dateDebut, dateFin);
    }

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
     * Obtenir les statistiques moyennes d'un joueur
     */
    @Transactional(readOnly = true)
    public Object getAverageStatsByPlayer(Long playerId) {
        Object[] stats = performanceRepository.getAverageStatsByPlayer(playerId);
        if (stats == null || stats[0] == null) {
            throw new RuntimeException("Aucune statistique trouvée pour le joueur avec l'ID: " + playerId);
        }

        return new Object() {
            public final Double moyenneAttaquesTotales = (Double) stats[0];
            public final Double moyenneAttaquesReussies = (Double) stats[1];
            public final Double moyenneAces = (Double) stats[2];
            public final Double moyenneBlocs = (Double) stats[3];
            public final Double moyenneNoteGlobale = (Double) stats[4];
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
            public final Long totalAttaques = (Long) stats[0];
            public final Long totalAttaquesReussies = (Long) stats[1];
            public final Long totalAces = (Long) stats[2];
            public final Long totalBlocs = (Long) stats[3];
            public final Long totalDefenses = (Long) stats[4];
        };
    }

    /**
     * Comparer les performances entre joueurs
     */
    @Transactional(readOnly = true)
    public List<Object[]> comparePlayersPerformance(LocalDate dateDebut) {
        return performanceRepository.comparePlayersPerformance(dateDebut);
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
        performance.setDatePerformance(request.getDatePerformance());
        performance.setTypePerformance(request.getTypePerformance());
        
        // Statistiques offensives
        performance.setAttaquesTotales(request.getAttaquesTotales());
        performance.setAttaquesReussies(request.getAttaquesReussies());
        performance.setAces(request.getAces());
        
        // Statistiques défensives
        performance.setBlocs(request.getBlocs());
        performance.setReceptionsTotales(request.getReceptionsTotales());
        performance.setReceptionsReussies(request.getReceptionsReussies());
        performance.setDefenses(request.getDefenses());
        
        // Statistiques de service
        performance.setServicesTotaux(request.getServicesTotaux());
        performance.setServicesReussis(request.getServicesReussis());
        
        // Erreurs
        performance.setErreursAttaque(request.getErreursAttaque());
        performance.setErreursService(request.getErreursService());
        performance.setErreursReception(request.getErreursReception());
        
        // Temps et note
        performance.setTempsJeuMinutes(request.getTempsJeuMinutes());
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
        List<Object[]> repartitionTypes = performanceRepository.countByTypePerformance();
        List<Object[]> topPlayers = performanceRepository.findTopPerformingPlayers();

        Map<String, Object> statistics = new HashMap<>();
        statistics.put("totalPerformances", totalPerformancesCount);
        statistics.put("repartitionParType", repartitionTypes);
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
