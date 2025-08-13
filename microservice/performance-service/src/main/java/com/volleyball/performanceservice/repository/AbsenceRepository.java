package com.volleyball.performanceservice.repository;

import com.volleyball.performanceservice.model.Absence;
import com.volleyball.performanceservice.model.Player;
import com.volleyball.performanceservice.model.StatutAbsence;
import com.volleyball.performanceservice.model.TypeAbsence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Repository pour la gestion des absences
 */
@Repository
public interface AbsenceRepository extends JpaRepository<Absence, Long> {

    // Absences par joueur
    List<Absence> findByPlayer(Player player);
    List<Absence> findByPlayerId(Long playerId);

    // Absences par statut
    List<Absence> findByStatut(StatutAbsence statut);

    // Absences par type
    List<Absence> findByTypeAbsence(TypeAbsence typeAbsence);

    // Absences justifiées ou non
    List<Absence> findByJustifiee(Boolean justifiee);

    // Absences en cours
    @Query("SELECT a FROM Absence a WHERE a.dateDebut <= :aujourdhui AND (a.dateFin IS NULL OR a.dateFin >= :aujourdhui)")
    List<Absence> findAbsencesEnCours(@Param("aujourdhui") LocalDate aujourdhui);

    // Absences en cours par joueur
    @Query("SELECT a FROM Absence a WHERE a.player.id = :playerId AND a.dateDebut <= :aujourdhui AND (a.dateFin IS NULL OR a.dateFin >= :aujourdhui)")
    List<Absence> findAbsencesEnCoursByPlayer(@Param("playerId") Long playerId, @Param("aujourdhui") LocalDate aujourdhui);

    // Absences par période
    @Query("SELECT a FROM Absence a WHERE a.dateDebut >= :dateDebut AND a.dateDebut <= :dateFin")
    List<Absence> findByPeriode(@Param("dateDebut") LocalDate dateDebut, @Param("dateFin") LocalDate dateFin);

    // Absences par joueur et période
    @Query("SELECT a FROM Absence a WHERE a.player.id = :playerId AND a.dateDebut >= :dateDebut AND a.dateDebut <= :dateFin")
    List<Absence> findByPlayerAndPeriode(@Param("playerId") Long playerId, @Param("dateDebut") LocalDate dateDebut, @Param("dateFin") LocalDate dateFin);

    // Absences longues (plus de X jours)
    @Query("SELECT a FROM Absence a WHERE (a.dateFin IS NULL AND a.dateDebut <= :dateLimite) OR (a.dateFin IS NOT NULL AND FUNCTION('DATE_PART', 'day', a.dateFin - a.dateDebut) >= :joursMinimum)")
    List<Absence> findAbsencesLongues(@Param("joursMinimum") int joursMinimum, @Param("dateLimite") LocalDate dateLimite);

    // Statistiques des absences
    @Query("SELECT COUNT(a) FROM Absence a WHERE a.player.id = :playerId")
    long countByPlayerId(@Param("playerId") Long playerId);

    @Query("SELECT COUNT(a) FROM Absence a WHERE a.statut = :statut")
    long countByStatut(@Param("statut") StatutAbsence statut);

    @Query("SELECT a.typeAbsence, COUNT(a) FROM Absence a GROUP BY a.typeAbsence")
    List<Object[]> countByTypeAbsence();

    @Query("SELECT COUNT(a) FROM Absence a WHERE a.dateDebut >= :dateDebut AND a.dateDebut <= :dateFin")
    long countByPeriode(@Param("dateDebut") LocalDate dateDebut, @Param("dateFin") LocalDate dateFin);

    // Absences récurrentes (même joueur, même type, dans une période)
    @Query("SELECT a FROM Absence a WHERE a.player.id = :playerId AND a.typeAbsence = :typeAbsence AND a.dateDebut >= :dateDebut AND a.dateDebut <= :dateFin")
    List<Absence> findAbsencesRecurrentes(@Param("playerId") Long playerId, @Param("typeAbsence") TypeAbsence typeAbsence, @Param("dateDebut") LocalDate dateDebut, @Param("dateFin") LocalDate dateFin);

    // Joueurs avec le plus d'absences
    @Query("SELECT a.player, COUNT(a) as nbAbsences FROM Absence a GROUP BY a.player ORDER BY nbAbsences DESC")
    List<Object[]> findPlayersWithMostAbsences();

    // Vérification de chevauchement d'absences
    @Query("SELECT COUNT(a) > 0 FROM Absence a WHERE a.player.id = :playerId AND a.id != :absenceId AND ((a.dateDebut <= :dateFin AND a.dateFin >= :dateDebut) OR (a.dateDebut <= :dateFin AND a.dateFin IS NULL))")
    boolean existsOverlappingAbsence(@Param("playerId") Long playerId, @Param("absenceId") Long absenceId, @Param("dateDebut") LocalDate dateDebut, @Param("dateFin") LocalDate dateFin);
}
