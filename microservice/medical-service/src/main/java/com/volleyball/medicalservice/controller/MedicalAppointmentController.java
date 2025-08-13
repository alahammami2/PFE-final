package com.volleyball.medicalservice.controller;

import com.volleyball.medicalservice.model.MedicalAppointment;
import com.volleyball.medicalservice.model.AppointmentStatus;
import com.volleyball.medicalservice.model.AppointmentType;
import com.volleyball.medicalservice.model.AppointmentPriority;
import com.volleyball.medicalservice.service.MedicalAppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Contrôleur REST pour la gestion des rendez-vous médicaux
 */
 
public class MedicalAppointmentController {

    @Autowired
    private MedicalAppointmentService appointmentService;

    /**
     * Crée un nouveau rendez-vous médical
     */
    @PostMapping
    public ResponseEntity<MedicalAppointment> createAppointment(@Valid @RequestBody MedicalAppointment appointment) {
        try {
            MedicalAppointment createdAppointment = appointmentService.createAppointment(appointment);
            return new ResponseEntity<>(createdAppointment, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Récupère tous les rendez-vous
     */
    @GetMapping
    public ResponseEntity<List<MedicalAppointment>> getAllAppointments() {
        try {
            List<MedicalAppointment> appointments = appointmentService.getAllAppointments();
            return new ResponseEntity<>(appointments, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Récupère un rendez-vous par ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<MedicalAppointment> getAppointmentById(@PathVariable Long id) {
        try {
            Optional<MedicalAppointment> appointment = appointmentService.getAppointmentById(id);
            return appointment.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                             .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Met à jour un rendez-vous
     */
    @PutMapping("/{id}")
    public ResponseEntity<MedicalAppointment> updateAppointment(@PathVariable Long id, @Valid @RequestBody MedicalAppointment appointment) {
        try {
            MedicalAppointment updatedAppointment = appointmentService.updateAppointment(id, appointment);
            return new ResponseEntity<>(updatedAppointment, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Supprime un rendez-vous
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteAppointment(@PathVariable Long id) {
        try {
            appointmentService.deleteAppointment(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Récupère les rendez-vous d'un joueur
     */
    @GetMapping("/player/{playerId}")
    public ResponseEntity<List<MedicalAppointment>> getAppointmentsByPlayerId(@PathVariable Long playerId) {
        try {
            List<MedicalAppointment> appointments = appointmentService.getAppointmentsByPlayerId(playerId);
            return new ResponseEntity<>(appointments, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Récupère les rendez-vous par statut
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<MedicalAppointment>> getAppointmentsByStatus(@PathVariable AppointmentStatus status) {
        try {
            List<MedicalAppointment> appointments = appointmentService.getAppointmentsByStatus(status);
            return new ResponseEntity<>(appointments, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Récupère les rendez-vous par type
     */
    @GetMapping("/type/{type}")
    public ResponseEntity<List<MedicalAppointment>> getAppointmentsByType(@PathVariable AppointmentType type) {
        try {
            List<MedicalAppointment> appointments = appointmentService.getAppointmentsByType(type);
            return new ResponseEntity<>(appointments, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Récupère les rendez-vous par priorité
     */
    @GetMapping("/priority/{priority}")
    public ResponseEntity<List<MedicalAppointment>> getAppointmentsByPriority(@PathVariable AppointmentPriority priority) {
        try {
            List<MedicalAppointment> appointments = appointmentService.getAppointmentsByPriority(priority);
            return new ResponseEntity<>(appointments, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Récupère les rendez-vous d'aujourd'hui
     */
    @GetMapping("/today")
    public ResponseEntity<List<MedicalAppointment>> getTodaysAppointments() {
        try {
            List<MedicalAppointment> appointments = appointmentService.getTodaysAppointments();
            return new ResponseEntity<>(appointments, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Récupère les rendez-vous à venir
     */
    @GetMapping("/upcoming")
    public ResponseEntity<List<MedicalAppointment>> getUpcomingAppointments() {
        try {
            List<MedicalAppointment> appointments = appointmentService.getUpcomingAppointments();
            return new ResponseEntity<>(appointments, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Récupère les rendez-vous passés
     */
    @GetMapping("/past")
    public ResponseEntity<List<MedicalAppointment>> getPastAppointments() {
        try {
            List<MedicalAppointment> appointments = appointmentService.getPastAppointments();
            return new ResponseEntity<>(appointments, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Récupère les rendez-vous par médecin
     */
    @GetMapping("/doctor")
    public ResponseEntity<List<MedicalAppointment>> getAppointmentsByDoctor(@RequestParam String doctorName) {
        try {
            List<MedicalAppointment> appointments = appointmentService.getAppointmentsByDoctor(doctorName);
            return new ResponseEntity<>(appointments, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Récupère les rendez-vous nécessitant un suivi
     */
    @GetMapping("/follow-up")
    public ResponseEntity<List<MedicalAppointment>> getAppointmentsNeedingFollowUp() {
        try {
            List<MedicalAppointment> appointments = appointmentService.getAppointmentsNeedingFollowUp();
            return new ResponseEntity<>(appointments, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Récupère les rendez-vous urgents
     */
    @GetMapping("/urgent")
    public ResponseEntity<List<MedicalAppointment>> getUrgentAppointments() {
        try {
            List<MedicalAppointment> appointments = appointmentService.getUrgentAppointments();
            return new ResponseEntity<>(appointments, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Recherche par nom de joueur
     */
    @GetMapping("/search")
    public ResponseEntity<List<MedicalAppointment>> searchByPlayerName(@RequestParam String name) {
        try {
            List<MedicalAppointment> appointments = appointmentService.searchByPlayerName(name);
            return new ResponseEntity<>(appointments, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Met à jour le statut d'un rendez-vous
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<MedicalAppointment> updateAppointmentStatus(@PathVariable Long id, @RequestParam AppointmentStatus status) {
        try {
            MedicalAppointment updatedAppointment = appointmentService.updateAppointmentStatus(id, status);
            return new ResponseEntity<>(updatedAppointment, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Confirme un rendez-vous
     */
    @PutMapping("/{id}/confirm")
    public ResponseEntity<MedicalAppointment> confirmAppointment(@PathVariable Long id) {
        try {
            MedicalAppointment confirmedAppointment = appointmentService.confirmAppointment(id);
            return new ResponseEntity<>(confirmedAppointment, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Annule un rendez-vous
     */
    @PutMapping("/{id}/cancel")
    public ResponseEntity<MedicalAppointment> cancelAppointment(@PathVariable Long id) {
        try {
            MedicalAppointment cancelledAppointment = appointmentService.cancelAppointment(id);
            return new ResponseEntity<>(cancelledAppointment, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Marque un rendez-vous comme terminé
     */
    @PutMapping("/{id}/complete")
    public ResponseEntity<MedicalAppointment> completeAppointment(@PathVariable Long id) {
        try {
            MedicalAppointment completedAppointment = appointmentService.completeAppointment(id);
            return new ResponseEntity<>(completedAppointment, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Reprogramme un rendez-vous
     */
    @PutMapping("/{id}/reschedule")
    public ResponseEntity<MedicalAppointment> rescheduleAppointment(@PathVariable Long id, @RequestParam String newDateTime) {
        try {
            LocalDateTime dateTime = LocalDateTime.parse(newDateTime);
            MedicalAppointment rescheduledAppointment = appointmentService.rescheduleAppointment(id, dateTime);
            return new ResponseEntity<>(rescheduledAppointment, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Récupère les statistiques des rendez-vous
     */
    @GetMapping("/statistics")
    public ResponseEntity<MedicalAppointmentService.AppointmentStatistics> getAppointmentStatistics() {
        try {
            MedicalAppointmentService.AppointmentStatistics stats = appointmentService.getAppointmentStatistics();
            return new ResponseEntity<>(stats, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
