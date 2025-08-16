package com.volleyball.medicalservice.controller;

import com.volleyball.medicalservice.model.HealthRecord;
import com.volleyball.medicalservice.service.HealthRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Contrôleur REST pour la gestion des dossiers médicaux
 */
@RestController
@RequestMapping("/api/medical/dossiers-sante")
@CrossOrigin(origins = "*")
public class HealthRecordController {

    @Autowired
    private HealthRecordService healthRecordService;

    /**
     * Crée un nouveau dossier médical
     */
    @PostMapping
    public ResponseEntity<HealthRecord> createHealthRecord(@Valid @RequestBody HealthRecord healthRecord) {
        try {
            HealthRecord createdRecord = healthRecordService.createHealthRecord(healthRecord);
            return new ResponseEntity<>(createdRecord, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Récupère tous les dossiers médicaux
     */
    @GetMapping
    public ResponseEntity<List<HealthRecord>> getAllHealthRecords() {
        try {
            List<HealthRecord> records = healthRecordService.getAllHealthRecords();
            return new ResponseEntity<>(records, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Récupère un dossier médical par ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<HealthRecord> getHealthRecordById(@PathVariable Long id) {
        try {
            Optional<HealthRecord> record = healthRecordService.getHealthRecordById(id);
            return record.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                         .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Récupère un dossier médical par ID de joueur
     */
    @GetMapping("/joueurs/{playerId}")
    public ResponseEntity<HealthRecord> getHealthRecordByPlayerId(@PathVariable Long playerId) {
        try {
            Optional<HealthRecord> record = healthRecordService.getHealthRecordByPlayerId(playerId);
            return record.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                         .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Met à jour un dossier médical
     */
    @PutMapping("/{id}")
    public ResponseEntity<HealthRecord> updateHealthRecord(@PathVariable Long id, @Valid @RequestBody HealthRecord healthRecord) {
        try {
            HealthRecord updatedRecord = healthRecordService.updateHealthRecord(id, healthRecord);
            return new ResponseEntity<>(updatedRecord, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Supprime un dossier médical
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteHealthRecord(@PathVariable Long id) {
        try {
            healthRecordService.deleteHealthRecord(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Récupère les dossiers par statut
     */
    @GetMapping("/statut/{status}")
    public ResponseEntity<List<HealthRecord>> getHealthRecordsByStatus(@PathVariable com.volleyball.medicalservice.model.MedicalStatus status) {
        try {
            List<HealthRecord> records = healthRecordService.getHealthRecordsByStatus(status);
            return new ResponseEntity<>(records, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    

    /**
     * Récupère les joueurs nécessitant un examen médical
     */
    @GetMapping("/a-controler")
    public ResponseEntity<List<HealthRecord>> getPlayersNeedingCheckup() {
        try {
            List<HealthRecord> records = healthRecordService.getPlayersNeedingCheckup();
            return new ResponseEntity<>(records, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Recherche par nom de joueur
     */
    @GetMapping("/recherche")
    public ResponseEntity<List<HealthRecord>> searchByPlayerName(@RequestParam String name) {
        try {
            List<HealthRecord> records = healthRecordService.searchByPlayerName(name);
            return new ResponseEntity<>(records, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    

    /**
     * Met à jour le statut de santé
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<HealthRecord> updateHealthStatus(@PathVariable Long id, @RequestParam com.volleyball.medicalservice.model.MedicalStatus status) {
        try {
            HealthRecord updatedRecord = healthRecordService.updateHealthStatus(id, status);
            return new ResponseEntity<>(updatedRecord, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Programme le prochain examen médical
     */
    @PutMapping("/{id}/programmer-controle")
    public ResponseEntity<HealthRecord> scheduleNextCheckup(@PathVariable Long id, @RequestParam String nextCheckupDate) {
        try {
            LocalDate checkupDate = LocalDate.parse(nextCheckupDate);
            HealthRecord updatedRecord = healthRecordService.scheduleNextCheckup(id, checkupDate);
            return new ResponseEntity<>(updatedRecord, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Récupère les statistiques des dossiers médicaux
     */
    @GetMapping("/statistiques")
    public ResponseEntity<HealthRecordService.HealthRecordStatistics> getHealthRecordStatistics() {
        try {
            HealthRecordService.HealthRecordStatistics stats = healthRecordService.getHealthRecordStatistics();
            return new ResponseEntity<>(stats, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
