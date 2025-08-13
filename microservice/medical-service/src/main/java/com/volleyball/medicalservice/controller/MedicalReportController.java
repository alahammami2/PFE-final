package com.volleyball.medicalservice.controller;

import com.volleyball.medicalservice.model.MedicalReport;
import com.volleyball.medicalservice.model.ReportStatus;
import com.volleyball.medicalservice.model.ReportType;
import com.volleyball.medicalservice.service.MedicalReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Contrôleur REST pour la gestion des rapports médicaux
 */
 
public class MedicalReportController {

    @Autowired
    private MedicalReportService reportService;

    /**
     * Crée un nouveau rapport médical
     */
    @PostMapping
    public ResponseEntity<MedicalReport> createReport(@Valid @RequestBody MedicalReport report) {
        try {
            MedicalReport createdReport = reportService.createReport(report);
            return new ResponseEntity<>(createdReport, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Récupère tous les rapports médicaux
     */
    @GetMapping
    public ResponseEntity<List<MedicalReport>> getAllReports() {
        try {
            List<MedicalReport> reports = reportService.getAllReports();
            return new ResponseEntity<>(reports, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Récupère un rapport médical par ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<MedicalReport> getReportById(@PathVariable Long id) {
        try {
            Optional<MedicalReport> report = reportService.getReportById(id);
            return report.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                         .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Met à jour un rapport médical
     */
    @PutMapping("/{id}")
    public ResponseEntity<MedicalReport> updateReport(@PathVariable Long id, @Valid @RequestBody MedicalReport report) {
        try {
            MedicalReport updatedReport = reportService.updateReport(id, report);
            return new ResponseEntity<>(updatedReport, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Supprime un rapport médical
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteReport(@PathVariable Long id) {
        try {
            reportService.deleteReport(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Récupère les rapports d'un joueur
     */
    @GetMapping("/player/{playerId}")
    public ResponseEntity<List<MedicalReport>> getReportsByPlayerId(@PathVariable Long playerId) {
        try {
            List<MedicalReport> reports = reportService.getReportsByPlayerId(playerId);
            return new ResponseEntity<>(reports, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Récupère les rapports par statut
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<MedicalReport>> getReportsByStatus(@PathVariable ReportStatus status) {
        try {
            List<MedicalReport> reports = reportService.getReportsByStatus(status);
            return new ResponseEntity<>(reports, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Récupère les rapports par type
     */
    @GetMapping("/type/{type}")
    public ResponseEntity<List<MedicalReport>> getReportsByType(@PathVariable ReportType type) {
        try {
            List<MedicalReport> reports = reportService.getReportsByType(type);
            return new ResponseEntity<>(reports, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Récupère les rapports par médecin
     */
    @GetMapping("/doctor")
    public ResponseEntity<List<MedicalReport>> getReportsByDoctor(@RequestParam String doctorName) {
        try {
            List<MedicalReport> reports = reportService.getReportsByDoctor(doctorName);
            return new ResponseEntity<>(reports, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Récupère les rapports liés à un rendez-vous
     */
    @GetMapping("/appointment/{appointmentId}")
    public ResponseEntity<List<MedicalReport>> getReportsByAppointmentId(@PathVariable Long appointmentId) {
        try {
            List<MedicalReport> reports = reportService.getReportsByAppointmentId(appointmentId);
            return new ResponseEntity<>(reports, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Récupère les rapports confidentiels
     */
    @GetMapping("/confidential")
    public ResponseEntity<List<MedicalReport>> getConfidentialReports() {
        try {
            List<MedicalReport> reports = reportService.getConfidentialReports();
            return new ResponseEntity<>(reports, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Recherche par titre
     */
    @GetMapping("/search/title")
    public ResponseEntity<List<MedicalReport>> searchByTitle(@RequestParam String title) {
        try {
            List<MedicalReport> reports = reportService.searchByTitle(title);
            return new ResponseEntity<>(reports, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Recherche par nom de joueur
     */
    @GetMapping("/search/player")
    public ResponseEntity<List<MedicalReport>> searchByPlayerName(@RequestParam String name) {
        try {
            List<MedicalReport> reports = reportService.searchByPlayerName(name);
            return new ResponseEntity<>(reports, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Recherche textuelle dans le contenu
     */
    @GetMapping("/search/content")
    public ResponseEntity<List<MedicalReport>> searchInContent(@RequestParam String searchTerm) {
        try {
            List<MedicalReport> reports = reportService.searchInContent(searchTerm);
            return new ResponseEntity<>(reports, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Récupère les rapports avec date de retour au jeu
     */
    @GetMapping("/with-return-to-play")
    public ResponseEntity<List<MedicalReport>> getReportsWithReturnToPlayDate() {
        try {
            List<MedicalReport> reports = reportService.getReportsWithReturnToPlayDate();
            return new ResponseEntity<>(reports, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Récupère les rapports avec restrictions
     */
    @GetMapping("/with-restrictions")
    public ResponseEntity<List<MedicalReport>> getReportsWithRestrictions() {
        try {
            List<MedicalReport> reports = reportService.getReportsWithRestrictions();
            return new ResponseEntity<>(reports, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Met à jour le statut d'un rapport
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<MedicalReport> updateReportStatus(@PathVariable Long id, @RequestParam ReportStatus status) {
        try {
            MedicalReport updatedReport = reportService.updateReportStatus(id, status);
            return new ResponseEntity<>(updatedReport, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Approuve un rapport
     */
    @PutMapping("/{id}/approve")
    public ResponseEntity<MedicalReport> approveReport(@PathVariable Long id) {
        try {
            MedicalReport approvedReport = reportService.approveReport(id);
            return new ResponseEntity<>(approvedReport, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Publie un rapport
     */
    @PutMapping("/{id}/publish")
    public ResponseEntity<MedicalReport> publishReport(@PathVariable Long id) {
        try {
            MedicalReport publishedReport = reportService.publishReport(id);
            return new ResponseEntity<>(publishedReport, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Archive un rapport
     */
    @PutMapping("/{id}/archive")
    public ResponseEntity<MedicalReport> archiveReport(@PathVariable Long id) {
        try {
            MedicalReport archivedReport = reportService.archiveReport(id);
            return new ResponseEntity<>(archivedReport, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Récupère les statistiques des rapports médicaux
     */
    @GetMapping("/statistics")
    public ResponseEntity<MedicalReportService.ReportStatistics> getReportStatistics() {
        try {
            MedicalReportService.ReportStatistics stats = reportService.getReportStatistics();
            return new ResponseEntity<>(stats, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
