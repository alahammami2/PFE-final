package com.volleyball.medicalservice.repository;

import com.volleyball.medicalservice.model.HealthRecord;
import com.volleyball.medicalservice.model.HealthStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository pour la gestion des dossiers médicaux
 */
@Repository
public interface HealthRecordRepository extends JpaRepository<HealthRecord, Long> {

    /**
     * Trouve un dossier médical par ID de joueur
     */
    Optional<HealthRecord> findByPlayerId(Long playerId);

    /**
     * Trouve tous les dossiers médicaux par statut
     */
    List<HealthRecord> findByStatus(HealthStatus status);

    /**
     * Trouve tous les dossiers médicaux actifs
     */
    List<HealthRecord> findByStatusOrderByPlayerNameAsc(HealthStatus status);

    /**
     * Trouve les joueurs nécessitant un examen médical
     */
    List<HealthRecord> findByNextCheckupDueLessThanEqual(LocalDate date);

    /**
     * Trouve les joueurs par nom (recherche partielle)
     */
    List<HealthRecord> findByPlayerNameContainingIgnoreCase(String name);

    /**
     * Compte les dossiers par statut
     */
    long countByStatus(HealthStatus status);
}
