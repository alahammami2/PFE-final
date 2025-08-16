package com.volleyball.medicalservice.service;

import com.volleyball.medicalservice.model.HealthRecord;
import com.volleyball.medicalservice.model.MedicalStatus;
import com.volleyball.medicalservice.repository.HealthRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Service pour la gestion des dossiers médicaux
 */
@Service
public class HealthRecordService {

    @Autowired
    private HealthRecordRepository healthRecordRepository;

    /**
     * Crée un nouveau dossier médical
     */
    public HealthRecord createHealthRecord(HealthRecord healthRecord) {
        return healthRecordRepository.save(healthRecord);
    }

    /**
     * Met à jour un dossier médical
     */
    public HealthRecord updateHealthRecord(Long id, HealthRecord healthRecord) {
        Optional<HealthRecord> existingRecord = healthRecordRepository.findById(id);
        if (existingRecord.isPresent()) {
            HealthRecord record = existingRecord.get();
            record.setPlayerName(healthRecord.getPlayerName());
            record.setBlessureType(healthRecord.getBlessureType());
            record.setBlessureDate(healthRecord.getBlessureDate());
            record.setStatutPhysique(healthRecord.getStatutPhysique());
            record.setStatus(healthRecord.getStatus());
            record.setLastMedicalCheckup(healthRecord.getLastMedicalCheckup());
            record.setNextCheckupDue(healthRecord.getNextCheckupDue());
            return healthRecordRepository.save(record);
        }
        throw new RuntimeException("Dossier médical non trouvé avec l'ID: " + id);
    }

    /**
     * Trouve tous les dossiers médicaux
     */
    public List<HealthRecord> getAllHealthRecords() {
        return healthRecordRepository.findAll();
    }

    /**
     * Trouve un dossier médical par ID
     */
    public Optional<HealthRecord> getHealthRecordById(Long id) {
        return healthRecordRepository.findById(id);
    }

    /**
     * Trouve un dossier médical par ID de joueur
     */
    public Optional<HealthRecord> getHealthRecordByPlayerId(Long playerId) {
        return healthRecordRepository.findByPlayerId(playerId);
    }

    /**
     * Trouve tous les dossiers médicaux par statut
     */
    public List<HealthRecord> getHealthRecordsByStatus(MedicalStatus status) {
        return healthRecordRepository.findByStatusOrderByPlayerNameAsc(status);
    }

    /**
     * Trouve les joueurs nécessitant un examen médical
     */
    public List<HealthRecord> getPlayersNeedingCheckup() {
        return healthRecordRepository.findByNextCheckupDueLessThanEqual(LocalDate.now());
    }

    /**
     * Recherche par nom de joueur
     */
    public List<HealthRecord> searchByPlayerName(String name) {
        return healthRecordRepository.findByPlayerNameContainingIgnoreCase(name);
    }

    /**
     * Met à jour le statut de santé
     */
    public HealthRecord updateHealthStatus(Long id, MedicalStatus status) {
        Optional<HealthRecord> existingRecord = healthRecordRepository.findById(id);
        if (existingRecord.isPresent()) {
            HealthRecord record = existingRecord.get();
            record.setStatus(status);
            return healthRecordRepository.save(record);
        }
        throw new RuntimeException("Dossier médical non trouvé avec l'ID: " + id);
    }

    /**
     * Programme le prochain examen médical
     */
    public HealthRecord scheduleNextCheckup(Long id, LocalDate nextCheckupDate) {
        Optional<HealthRecord> existingRecord = healthRecordRepository.findById(id);
        if (existingRecord.isPresent()) {
            HealthRecord record = existingRecord.get();
            record.setNextCheckupDue(nextCheckupDate);
            return healthRecordRepository.save(record);
        }
        throw new RuntimeException("Dossier médical non trouvé avec l'ID: " + id);
    }

    /**
     * Supprime un dossier médical
     */
    public void deleteHealthRecord(Long id) {
        healthRecordRepository.deleteById(id);
    }

    /**
     * Compte les dossiers par statut
     */
    public long countByStatus(MedicalStatus status) {
        return healthRecordRepository.countByStatus(status);
    }

    /**
     * Obtient les statistiques des dossiers médicaux
     */
    public HealthRecordStatistics getHealthRecordStatistics() {
        HealthRecordStatistics stats = new HealthRecordStatistics();
        stats.setTotalRecords(healthRecordRepository.count());
        stats.setActiveRecords(countByStatus(MedicalStatus.EN_SUIVI));
        stats.setInjuredRecords(countByStatus(MedicalStatus.REPOS));
        stats.setRecoveringRecords(countByStatus(MedicalStatus.RETABLI));
        stats.setRestrictedRecords(0);
        stats.setPlayersNeedingCheckup(getPlayersNeedingCheckup().size());
        return stats;
    }

    /**
     * Classe interne pour les statistiques
     */
    public static class HealthRecordStatistics {
        private long totalRecords;
        private long activeRecords;
        private long injuredRecords;
        private long recoveringRecords;
        private long restrictedRecords;
        private long playersNeedingCheckup;

        // Getters et Setters
        public long getTotalRecords() { return totalRecords; }
        public void setTotalRecords(long totalRecords) { this.totalRecords = totalRecords; }
        public long getActiveRecords() { return activeRecords; }
        public void setActiveRecords(long activeRecords) { this.activeRecords = activeRecords; }
        public long getInjuredRecords() { return injuredRecords; }
        public void setInjuredRecords(long injuredRecords) { this.injuredRecords = injuredRecords; }
        public long getRecoveringRecords() { return recoveringRecords; }
        public void setRecoveringRecords(long recoveringRecords) { this.recoveringRecords = recoveringRecords; }
        public long getRestrictedRecords() { return restrictedRecords; }
        public void setRestrictedRecords(long restrictedRecords) { this.restrictedRecords = restrictedRecords; }
        public long getPlayersNeedingCheckup() { return playersNeedingCheckup; }
        public void setPlayersNeedingCheckup(long playersNeedingCheckup) { this.playersNeedingCheckup = playersNeedingCheckup; }
    }
}
