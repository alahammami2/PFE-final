package com.volleyball.medicalservice.repository;

import com.volleyball.medicalservice.model.MedicalAppointment;
import com.volleyball.medicalservice.model.AppointmentStatus;
import com.volleyball.medicalservice.model.AppointmentType;
import com.volleyball.medicalservice.model.AppointmentPriority;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository pour la gestion des rendez-vous médicaux
 */
// Ancien repository rendez-vous (neutralisé, plus de mapping Spring Data)
public interface MedicalAppointmentRepository {

    /**
     * Trouve tous les rendez-vous d'un joueur
     */
    List<MedicalAppointment> findByPlayerIdOrderByAppointmentDateTimeDesc(Long playerId);

    /**
     * Trouve tous les rendez-vous par statut
     */
    List<MedicalAppointment> findByStatusOrderByAppointmentDateTimeAsc(AppointmentStatus status);

    /**
     * Trouve tous les rendez-vous par type
     */
    List<MedicalAppointment> findByAppointmentTypeOrderByAppointmentDateTimeAsc(AppointmentType appointmentType);

    /**
     * Trouve tous les rendez-vous par priorité
     */
    List<MedicalAppointment> findByPriorityOrderByAppointmentDateTimeAsc(AppointmentPriority priority);

    /**
     * Trouve les rendez-vous d'aujourd'hui
     */
    @Query("SELECT ma FROM MedicalAppointment ma WHERE CAST(ma.appointmentDateTime AS date) = CURRENT_DATE ORDER BY ma.appointmentDateTime ASC")
    List<MedicalAppointment> findTodaysAppointments();

    /**
     * Trouve les rendez-vous de la semaine
     */
    @Query("SELECT ma FROM MedicalAppointment ma WHERE ma.appointmentDateTime BETWEEN :startDate AND :endDate ORDER BY ma.appointmentDateTime ASC")
    List<MedicalAppointment> findAppointmentsBetweenDates(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    /**
     * Trouve les rendez-vous à venir
     */
    @Query("SELECT ma FROM MedicalAppointment ma WHERE ma.appointmentDateTime > CURRENT_TIMESTAMP ORDER BY ma.appointmentDateTime ASC")
    List<MedicalAppointment> findUpcomingAppointments();

    /**
     * Trouve les rendez-vous passés
     */
    @Query("SELECT ma FROM MedicalAppointment ma WHERE ma.appointmentDateTime < CURRENT_TIMESTAMP ORDER BY ma.appointmentDateTime DESC")
    List<MedicalAppointment> findPastAppointments();

    /**
     * Trouve les rendez-vous par médecin
     */
    List<MedicalAppointment> findByDoctorNameOrderByAppointmentDateTimeAsc(String doctorName);

    /**
     * Trouve les rendez-vous nécessitant un suivi
     */
    @Query("SELECT ma FROM MedicalAppointment ma WHERE ma.followUpRequired = true AND ma.followUpDate IS NOT NULL ORDER BY ma.followUpDate ASC")
    List<MedicalAppointment> findAppointmentsNeedingFollowUp();

    /**
     * Trouve les rendez-vous urgents
     */
    @Query("SELECT ma FROM MedicalAppointment ma WHERE ma.priority IN ('URGENT', 'EMERGENCY') ORDER BY ma.appointmentDateTime ASC")
    List<MedicalAppointment> findUrgentAppointments();

    /**
     * Recherche par nom de joueur
     */
    @Query("SELECT ma FROM MedicalAppointment ma WHERE LOWER(ma.playerName) LIKE LOWER(CONCAT('%', :name, '%')) ORDER BY ma.appointmentDateTime DESC")
    List<MedicalAppointment> findByPlayerNameContainingIgnoreCase(@Param("name") String name);

    /**
     * Compte les rendez-vous par statut
     */
    long countByStatus(AppointmentStatus status);

    /**
     * Compte les rendez-vous par type
     */
    long countByAppointmentType(AppointmentType appointmentType);

    /**
     * Trouve les rendez-vous d'un joueur avec un statut spécifique
     */
    List<MedicalAppointment> findByPlayerIdAndStatusOrderByAppointmentDateTimeDesc(Long playerId, AppointmentStatus status);

    /**
     * Trouve les rendez-vous dans une plage de dates pour un joueur
     */
    @Query("SELECT ma FROM MedicalAppointment ma WHERE ma.playerId = :playerId AND ma.appointmentDateTime BETWEEN :startDate AND :endDate ORDER BY ma.appointmentDateTime ASC")
    List<MedicalAppointment> findPlayerAppointmentsBetweenDates(@Param("playerId") Long playerId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
}
