package com.volleyball.medicalservice.service;

import com.volleyball.medicalservice.model.MedicalReport;
import com.volleyball.medicalservice.model.ReportStatus;
import com.volleyball.medicalservice.model.ReportType;
 

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service pour la gestion des rapports médicaux
 */
public class MedicalReportService {

    // Legacy medical reports layer disabled after schema refactor.
    private UnsupportedOperationException legacyDisabled() {
        return new UnsupportedOperationException("Legacy MedicalReport layer is disabled after schema refactor");
    }

    /**
     * Crée un nouveau rapport médical
     */
    public MedicalReport createReport(MedicalReport report) { throw legacyDisabled(); }

    /**
     * Met à jour un rapport médical
     */
    public MedicalReport updateReport(Long id, MedicalReport report) { throw legacyDisabled(); }

    /**
     * Trouve tous les rapports médicaux
     */
    public List<MedicalReport> getAllReports() { throw legacyDisabled(); }

    /**
     * Trouve un rapport médical par ID
     */
    public Optional<MedicalReport> getReportById(Long id) { throw legacyDisabled(); }

    /**
     * Trouve tous les rapports d'un joueur
     */
    public List<MedicalReport> getReportsByPlayerId(Long playerId) { throw legacyDisabled(); }

    /**
     * Trouve tous les rapports par statut
     */
    public List<MedicalReport> getReportsByStatus(ReportStatus status) { throw legacyDisabled(); }

    /**
     * Trouve tous les rapports par type
     */
    public List<MedicalReport> getReportsByType(ReportType type) { throw legacyDisabled(); }

    /**
     * Trouve tous les rapports par médecin
     */
    public List<MedicalReport> getReportsByDoctor(String doctorName) { throw legacyDisabled(); }

    /**
     * Trouve les rapports liés à un rendez-vous
     */
    public List<MedicalReport> getReportsByAppointmentId(Long appointmentId) { throw legacyDisabled(); }

    /**
     * Trouve les rapports confidentiels
     */
    public List<MedicalReport> getConfidentialReports() { throw legacyDisabled(); }

    /**
     * Recherche par titre
     */
    public List<MedicalReport> searchByTitle(String title) { throw legacyDisabled(); }

    /**
     * Recherche par nom de joueur
     */
    public List<MedicalReport> searchByPlayerName(String name) { throw legacyDisabled(); }

    /**
     * Trouve les rapports dans une plage de dates
     */
    public List<MedicalReport> getReportsBetweenDates(LocalDateTime startDate, LocalDateTime endDate) { throw legacyDisabled(); }

    /**
     * Trouve les rapports avec date de retour au jeu
     */
    public List<MedicalReport> getReportsWithReturnToPlayDate() { throw legacyDisabled(); }

    /**
     * Trouve les rapports avec restrictions
     */
    public List<MedicalReport> getReportsWithRestrictions() { throw legacyDisabled(); }

    /**
     * Recherche textuelle dans le contenu
     */
    public List<MedicalReport> searchInContent(String searchTerm) { throw legacyDisabled(); }

    /**
     * Met à jour le statut d'un rapport
     */
    public MedicalReport updateReportStatus(Long id, ReportStatus status) { throw legacyDisabled(); }

    /**
     * Approuve un rapport
     */
    public MedicalReport approveReport(Long id) { throw legacyDisabled(); }

    /**
     * Publie un rapport
     */
    public MedicalReport publishReport(Long id) { throw legacyDisabled(); }

    /**
     * Archive un rapport
     */
    public MedicalReport archiveReport(Long id) { throw legacyDisabled(); }

    /**
     * Supprime un rapport médical
     */
    public void deleteReport(Long id) { throw legacyDisabled(); }

    /**
     * Obtient les statistiques des rapports médicaux
     */
    public ReportStatistics getReportStatistics() { throw legacyDisabled(); }

    /**
     * Classe interne pour les statistiques
     */
    public static class ReportStatistics {
        private long totalReports;
        private long draftReports;
        private long pendingReports;
        private long approvedReports;
        private long publishedReports;
        private long archivedReports;
        private long confidentialReports;
        private long reportsWithRestrictions;
        private long reportsWithReturnToPlay;

        // Getters et Setters
        public long getTotalReports() { return totalReports; }
        public void setTotalReports(long totalReports) { this.totalReports = totalReports; }
        public long getDraftReports() { return draftReports; }
        public void setDraftReports(long draftReports) { this.draftReports = draftReports; }
        public long getPendingReports() { return pendingReports; }
        public void setPendingReports(long pendingReports) { this.pendingReports = pendingReports; }
        public long getApprovedReports() { return approvedReports; }
        public void setApprovedReports(long approvedReports) { this.approvedReports = approvedReports; }
        public long getPublishedReports() { return publishedReports; }
        public void setPublishedReports(long publishedReports) { this.publishedReports = publishedReports; }
        public long getArchivedReports() { return archivedReports; }
        public void setArchivedReports(long archivedReports) { this.archivedReports = archivedReports; }
        public long getConfidentialReports() { return confidentialReports; }
        public void setConfidentialReports(long confidentialReports) { this.confidentialReports = confidentialReports; }
        public long getReportsWithRestrictions() { return reportsWithRestrictions; }
        public void setReportsWithRestrictions(long reportsWithRestrictions) { this.reportsWithRestrictions = reportsWithRestrictions; }
        public long getReportsWithReturnToPlay() { return reportsWithReturnToPlay; }
        public void setReportsWithReturnToPlay(long reportsWithReturnToPlay) { this.reportsWithReturnToPlay = reportsWithReturnToPlay; }
    }
}
