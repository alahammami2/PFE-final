package com.volleyball.performanceservice.repository;

import com.volleyball.performanceservice.model.Performance;
import com.volleyball.performanceservice.model.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository pour la gestion des performances
 */
@Repository
public interface PerformanceRepository extends JpaRepository<Performance, Long> {

    // Performances par joueur
    List<Performance> findByPlayer(Player player);

    // Performances par joueur (par ID)
    List<Performance> findByPlayerId(Long playerId);

    // Dernières performances d'un joueur
    @Query("SELECT p FROM Performance p WHERE p.player.id = :playerId ORDER BY p.dateCreation DESC")
    List<Performance> findLatestByPlayer(@Param("playerId") Long playerId);

    // Meilleures performances (par note)
    @Query("SELECT p FROM Performance p WHERE p.noteGlobale IS NOT NULL ORDER BY p.noteGlobale DESC")
    List<Performance> findTopPerformances();

    // Meilleures performances d'un joueur
    @Query("SELECT p FROM Performance p WHERE p.player.id = :playerId AND p.noteGlobale IS NOT NULL ORDER BY p.noteGlobale DESC")
    List<Performance> findTopPerformancesByPlayer(@Param("playerId") Long playerId);

    // Statistiques moyennes par joueur (attaques/blocs retirés)
    @Query("SELECT AVG(p.aces), AVG(p.noteGlobale) FROM Performance p WHERE p.player.id = :playerId")
    Object[] getAverageStatsByPlayer(@Param("playerId") Long playerId);

    // Statistiques totales par joueur (attaques/blocs retirés)
    @Query("SELECT SUM(p.aces) FROM Performance p WHERE p.player.id = :playerId")
    Object[] getTotalStatsByPlayer(@Param("playerId") Long playerId);

    // Évolution des performances (dernières N entrées par date de création)
    @Query("SELECT p FROM Performance p WHERE p.player.id = :playerId ORDER BY p.dateCreation DESC")
    List<Performance> findRecentPerformancesByPlayer(@Param("playerId") Long playerId);

    // Comparaison de performances entre joueurs (moyenne de note globale)
    @Query("SELECT p.player.id, AVG(p.noteGlobale) as moyenneNote FROM Performance p GROUP BY p.player.id ORDER BY moyenneNote DESC")
    List<Object[]> comparePlayersPerformance();

    // Moyenne globale de la note sur toutes les performances
    @Query("SELECT AVG(p.noteGlobale) FROM Performance p WHERE p.noteGlobale IS NOT NULL")
    Double getAverageGlobalNote();

    // Statistiques globales
    @Query("SELECT COUNT(p) FROM Performance p")
    long countTotalPerformances();

    @Query("SELECT COUNT(p) FROM Performance p WHERE p.player.id = :playerId")
    long countByPlayerId(@Param("playerId") Long playerId);

    // Répartition par type supprimée (type_performance supprimé)

    // Joueurs les plus performants
    @Query("SELECT p.player, AVG(p.noteGlobale) as moyenne FROM Performance p WHERE p.noteGlobale IS NOT NULL GROUP BY p.player ORDER BY moyenne DESC")
    List<Object[]> findTopPerformingPlayers();

    // Performances avec note supérieure à un seuil
    @Query("SELECT p FROM Performance p WHERE p.noteGlobale >= :noteMinimum ORDER BY p.noteGlobale DESC")
    List<Performance> findPerformancesAboveThreshold(@Param("noteMinimum") Double noteMinimum);

    // Performances d'un joueur avec note supérieure à un seuil
    @Query("SELECT p FROM Performance p WHERE p.player.id = :playerId AND p.noteGlobale >= :noteMinimum ORDER BY p.noteGlobale DESC")
    List<Performance> findPlayerPerformancesAboveThreshold(@Param("playerId") Long playerId, @Param("noteMinimum") Double noteMinimum);

    // Vérification d'existence spécifique supprimée (colonnes supprimées)
}
