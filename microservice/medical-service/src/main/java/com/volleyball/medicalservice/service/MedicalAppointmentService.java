package com.volleyball.medicalservice.service;

import com.volleyball.medicalservice.model.MedicalAppointment;
import com.volleyball.medicalservice.model.AppointmentStatus;
import com.volleyball.medicalservice.model.AppointmentType;
import com.volleyball.medicalservice.model.AppointmentPriority;
 
 

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service pour la gestion des rendez-vous médicaux
 */
public class MedicalAppointmentService {

    // Legacy rendez-vous (appointments) layer disabled.
    private UnsupportedOperationException legacyDisabled() {
        return new UnsupportedOperationException("Legacy MedicalAppointment layer is disabled after schema refactor");
    }

    /**
     * Crée un nouveau rendez-vous médical
     */
    public MedicalAppointment createAppointment(MedicalAppointment appointment) { throw legacyDisabled(); }

    /**
     * Met à jour un rendez-vous médical
     */
    public MedicalAppointment updateAppointment(Long id, MedicalAppointment appointment) { throw legacyDisabled(); }

    /**
     * Trouve tous les rendez-vous
     */
    public List<MedicalAppointment> getAllAppointments() { throw legacyDisabled(); }

    /**
     * Trouve un rendez-vous par ID
     */
    public Optional<MedicalAppointment> getAppointmentById(Long id) { throw legacyDisabled(); }

    /**
     * Trouve tous les rendez-vous d'un joueur
     */
    public List<MedicalAppointment> getAppointmentsByPlayerId(Long playerId) { throw legacyDisabled(); }

    /**
     * Trouve tous les rendez-vous par statut
     */
    public List<MedicalAppointment> getAppointmentsByStatus(AppointmentStatus status) { throw legacyDisabled(); }

    /**
     * Trouve tous les rendez-vous par type
     */
    public List<MedicalAppointment> getAppointmentsByType(AppointmentType type) { throw legacyDisabled(); }

    /**
     * Trouve tous les rendez-vous par priorité
     */
    public List<MedicalAppointment> getAppointmentsByPriority(AppointmentPriority priority) { throw legacyDisabled(); }

    /**
     * Trouve les rendez-vous d'aujourd'hui
     */
    public List<MedicalAppointment> getTodaysAppointments() { throw legacyDisabled(); }

    /**
     * Trouve les rendez-vous à venir
     */
    public List<MedicalAppointment> getUpcomingAppointments() { throw legacyDisabled(); }

    /**
     * Trouve les rendez-vous passés
     */
    public List<MedicalAppointment> getPastAppointments() { throw legacyDisabled(); }

    /**
     * Trouve les rendez-vous par médecin
     */
    public List<MedicalAppointment> getAppointmentsByDoctor(String doctorName) { throw legacyDisabled(); }

    /**
     * Trouve les rendez-vous nécessitant un suivi
     */
    public List<MedicalAppointment> getAppointmentsNeedingFollowUp() { throw legacyDisabled(); }

    /**
     * Trouve les rendez-vous urgents
     */
    public List<MedicalAppointment> getUrgentAppointments() { throw legacyDisabled(); }

    /**
     * Recherche par nom de joueur
     */
    public List<MedicalAppointment> searchByPlayerName(String name) { throw legacyDisabled(); }

    /**
     * Trouve les rendez-vous dans une plage de dates
     */
    public List<MedicalAppointment> getAppointmentsBetweenDates(LocalDateTime startDate, LocalDateTime endDate) { throw legacyDisabled(); }

    /**
     * Met à jour le statut d'un rendez-vous
     */
    public MedicalAppointment updateAppointmentStatus(Long id, AppointmentStatus status) { throw legacyDisabled(); }

    /**
     * Confirme un rendez-vous
     */
    public MedicalAppointment confirmAppointment(Long id) { throw legacyDisabled(); }

    /**
     * Annule un rendez-vous
     */
    public MedicalAppointment cancelAppointment(Long id) { throw legacyDisabled(); }

    /**
     * Marque un rendez-vous comme terminé
     */
    public MedicalAppointment completeAppointment(Long id) { throw legacyDisabled(); }

    /**
     * Reprogramme un rendez-vous
     */
    public MedicalAppointment rescheduleAppointment(Long id, LocalDateTime newDateTime) { throw legacyDisabled(); }

    /**
     * Supprime un rendez-vous
     */
    public void deleteAppointment(Long id) { throw legacyDisabled(); }

    /**
     * Obtient les statistiques des rendez-vous
     */
    public AppointmentStatistics getAppointmentStatistics() { throw legacyDisabled(); }

    /**
     * Classe interne pour les statistiques
     */
    public static class AppointmentStatistics {
        private long totalAppointments;
        private long scheduledAppointments;
        private long confirmedAppointments;
        private long completedAppointments;
        private long cancelledAppointments;
        private long todaysAppointments;
        private long upcomingAppointments;
        private long urgentAppointments;
        private long followUpAppointments;

        // Getters et Setters
        public long getTotalAppointments() { return totalAppointments; }
        public void setTotalAppointments(long totalAppointments) { this.totalAppointments = totalAppointments; }
        public long getScheduledAppointments() { return scheduledAppointments; }
        public void setScheduledAppointments(long scheduledAppointments) { this.scheduledAppointments = scheduledAppointments; }
        public long getConfirmedAppointments() { return confirmedAppointments; }
        public void setConfirmedAppointments(long confirmedAppointments) { this.confirmedAppointments = confirmedAppointments; }
        public long getCompletedAppointments() { return completedAppointments; }
        public void setCompletedAppointments(long completedAppointments) { this.completedAppointments = completedAppointments; }
        public long getCancelledAppointments() { return cancelledAppointments; }
        public void setCancelledAppointments(long cancelledAppointments) { this.cancelledAppointments = cancelledAppointments; }
        public long getTodaysAppointments() { return todaysAppointments; }
        public void setTodaysAppointments(long todaysAppointments) { this.todaysAppointments = todaysAppointments; }
        public long getUpcomingAppointments() { return upcomingAppointments; }
        public void setUpcomingAppointments(long upcomingAppointments) { this.upcomingAppointments = upcomingAppointments; }
        public long getUrgentAppointments() { return urgentAppointments; }
        public void setUrgentAppointments(long urgentAppointments) { this.urgentAppointments = urgentAppointments; }
        public long getFollowUpAppointments() { return followUpAppointments; }
        public void setFollowUpAppointments(long followUpAppointments) { this.followUpAppointments = followUpAppointments; }
    }
}
