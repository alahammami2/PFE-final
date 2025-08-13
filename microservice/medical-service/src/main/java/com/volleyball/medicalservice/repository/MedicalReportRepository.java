package com.volleyball.medicalservice.repository;

import com.volleyball.medicalservice.model.MedicalReport;
import com.volleyball.medicalservice.model.ReportStatus;
import com.volleyball.medicalservice.model.ReportType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
 

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository pour la gestion des rapports médicaux
 */
// Ancien repository rapports (neutralisé, plus de mapping Spring Data)
public interface MedicalReportRepository {

    /**
     * Trouve tous les rapports d'un joueur
     */
    List<MedicalReport> findByPlayerIdOrderByCreatedAtDesc(Long playerId);

    /**
     * Trouve tous les rapports par statut
     */
    List<MedicalReport> findByStatusOrderByCreatedAtDesc(ReportStatus status);

    /**
     * Trouve tous les rapports par type
     */
    List<MedicalReport> findByReportTypeOrderByCreatedAtDesc(ReportType reportType);

    /**
     * Trouve tous les rapports par médecin
     */
    List<MedicalReport> findByDoctorNameOrderByCreatedAtDesc(String doctorName);

    /**
     * Trouve les rapports liés à un rendez-vous
     */
    List<MedicalReport> findByAppointmentIdOrderByCreatedAtDesc(Long appointmentId);

    /**
     * Trouve les rapports confidentiels
     */
    List<MedicalReport> findByIsConfidentialOrderByCreatedAtDesc(Boolean isConfidential);

    /**
     * Recherche par titre
     */
    @Query("SELECT mr FROM MedicalReport mr WHERE LOWER(mr.title) LIKE LOWER(CONCAT('%', :title, '%')) ORDER BY mr.createdAt DESC")
    List<MedicalReport> findByTitleContainingIgnoreCase(@Param("title") String title);

    /**
     * Recherche par nom de joueur
     */
    @Query("SELECT mr FROM MedicalReport mr WHERE LOWER(mr.playerName) LIKE LOWER(CONCAT('%', :name, '%')) ORDER BY mr.createdAt DESC")
    List<MedicalReport> findByPlayerNameContainingIgnoreCase(@Param("name") String name);

    /**
     * Trouve les rapports dans une plage de dates
     */
    @Query("SELECT mr FROM MedicalReport mr WHERE mr.createdAt BETWEEN :startDate AND :endDate ORDER BY mr.createdAt DESC")
    List<MedicalReport> findReportsBetweenDates(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    /**
     * Trouve les rapports avec date de retour au jeu
     */
    @Query("SELECT mr FROM MedicalReport mr WHERE mr.returnToPlayDate IS NOT NULL ORDER BY mr.returnToPlayDate ASC")
    List<MedicalReport> findReportsWithReturnToPlayDate();

    /**
     * Trouve les rapports avec restrictions
     */
    @Query("SELECT mr FROM MedicalReport mr WHERE mr.restrictions IS NOT NULL AND mr.restrictions != '' ORDER BY mr.createdAt DESC")
    List<MedicalReport> findReportsWithRestrictions();

    /**
     * Compte les rapports par statut
     */
    long countByStatus(ReportStatus status);

    /**
     * Compte les rapports par type
     */
    long countByReportType(ReportType reportType);

    /**
     * Trouve les rapports d'un joueur par type
     */
    List<MedicalReport> findByPlayerIdAndReportTypeOrderByCreatedAtDesc(Long playerId, ReportType reportType);

    /**
     * Trouve les rapports d'un joueur par statut
     */
    List<MedicalReport> findByPlayerIdAndStatusOrderByCreatedAtDesc(Long playerId, ReportStatus status);

    /**
     * Recherche textuelle dans le contenu
     */
    @Query("SELECT mr FROM MedicalReport mr WHERE LOWER(mr.content) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR LOWER(mr.diagnosis) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR LOWER(mr.treatment) LIKE LOWER(CONCAT('%', :searchTerm, '%')) ORDER BY mr.createdAt DESC")
    List<MedicalReport> searchInContent(@Param("searchTerm") String searchTerm);
}
