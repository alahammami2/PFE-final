package com.volleyball.performanceservice.repository;

import com.volleyball.performanceservice.model.Performance;
import com.volleyball.performanceservice.model.Player;
import com.volleyball.performanceservice.model.TypePerformance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Repository pour la gestion des performances
 */
@Repository
public interface PerformanceRepository extends JpaRepository<Performance, Long> {

    // Performances par joueur
    List<Performance> findByPlayer(Player player);
    List<Performance> findByPlayerId(Long playerId);

    // Performances par type
    List<Performance> findByTypePerformance(TypePerformance typePerformance);

    // Performances par date
    List<Performance> findByDatePerformance(LocalDate datePerformance);

    // Performances par période
    @Query("SELECT p FROM Performance p WHERE p.datePerformance >= :dateDebut AND p.datePerformance <= :dateFin")
    List<Performance> findByPeriode(@Param("dateDebut") LocalDate dateDebut, @Param("dateFin") LocalDate dateFin);

    // Performances par joueur et période
    @Query("SELECT p FROM Performance p WHERE p.player.id = :playerId AND p.datePerformance >= :dateDebut AND p.datePerformance <= :dateFin")
    List<Performance> findByPlayerAndPeriode(@Param("playerId") Long playerId, @Param("dateDebut") LocalDate dateDebut, @Param("dateFin") LocalDate dateFin);

    // Performances par joueur et type
    List<Performance> findByPlayerIdAndTypePerformance(Long playerId, TypePerformance typePerformance);

    // Dernières performances d'un joueur
    @Query("SELECT p FROM Performance p WHERE p.player.id = :playerId ORDER BY p.datePerformance DESC")
    List<Performance> findLatestByPlayer(@Param("playerId") Long playerId);

    // Meilleures performances (par note)
    @Query("SELECT p FROM Performance p WHERE p.noteGlobale IS NOT NULL ORDER BY p.noteGlobale DESC")
    List<Performance> findTopPerformances();

    // Meilleures performances d'un joueur
    @Query("SELECT p FROM Performance p WHERE p.player.id = :playerId AND p.noteGlobale IS NOT NULL ORDER BY p.noteGlobale DESC")
    List<Performance> findTopPerformancesByPlayer(@Param("playerId") Long playerId);

    // Statistiques moyennes par joueur
    @Query("SELECT AVG(p.attaquesTotales), AVG(p.attaquesReussies), AVG(p.aces), AVG(p.blocs), AVG(p.noteGlobale) FROM Performance p WHERE p.player.id = :playerId")
    Object[] getAverageStatsByPlayer(@Param("playerId") Long playerId);

    // Statistiques totales par joueur
    @Query("SELECT SUM(p.attaquesTotales), SUM(p.attaquesReussies), SUM(p.aces), SUM(p.blocs), SUM(p.defenses) FROM Performance p WHERE p.player.id = :playerId")
    Object[] getTotalStatsByPlayer(@Param("playerId") Long playerId);

    // Évolution des performances (derniers N matchs)
    @Query("SELECT p FROM Performance p WHERE p.player.id = :playerId ORDER BY p.datePerformance DESC")
    List<Performance> findRecentPerformancesByPlayer(@Param("playerId") Long playerId);

    // Comparaison de performances entre joueurs
    @Query("SELECT p.player.id, AVG(p.noteGlobale) as moyenneNote FROM Performance p WHERE p.datePerformance >= :dateDebut GROUP BY p.player.id ORDER BY moyenneNote DESC")
    List<Object[]> comparePlayersPerformance(@Param("dateDebut") LocalDate dateDebut);

    // Performances par type et période
    @Query("SELECT p FROM Performance p WHERE p.typePerformance = :typePerformance AND p.datePerformance >= :dateDebut AND p.datePerformance <= :dateFin")
    List<Performance> findByTypeAndPeriode(@Param("typePerformance") TypePerformance typePerformance, @Param("dateDebut") LocalDate dateDebut, @Param("dateFin") LocalDate dateFin);

    // Statistiques globales
    @Query("SELECT COUNT(p) FROM Performance p")
    long countTotalPerformances();

    @Query("SELECT COUNT(p) FROM Performance p WHERE p.player.id = :playerId")
    long countByPlayerId(@Param("playerId") Long playerId);

    @Query("SELECT COUNT(p) FROM Performance p WHERE p.typePerformance = :typePerformance")
    long countByType(@Param("typePerformance") TypePerformance typePerformance);

    @Query("SELECT p.typePerformance, COUNT(p) FROM Performance p GROUP BY p.typePerformance")
    List<Object[]> countByTypePerformance();

    // Joueurs les plus performants
    @Query("SELECT p.player, AVG(p.noteGlobale) as moyenne FROM Performance p WHERE p.noteGlobale IS NOT NULL GROUP BY p.player ORDER BY moyenne DESC")
    List<Object[]> findTopPerformingPlayers();

    // Performances avec note supérieure à un seuil
    @Query("SELECT p FROM Performance p WHERE p.noteGlobale >= :noteMinimum ORDER BY p.noteGlobale DESC")
    List<Performance> findPerformancesAboveThreshold(@Param("noteMinimum") Double noteMinimum);

    // Performances d'un joueur avec note supérieure à un seuil
    @Query("SELECT p FROM Performance p WHERE p.player.id = :playerId AND p.noteGlobale >= :noteMinimum ORDER BY p.noteGlobale DESC")
    List<Performance> findPlayerPerformancesAboveThreshold(@Param("playerId") Long playerId, @Param("noteMinimum") Double noteMinimum);

    // Vérification d'existence
    boolean existsByPlayerIdAndDatePerformanceAndTypePerformance(Long playerId, LocalDate datePerformance, TypePerformance typePerformance);
}
