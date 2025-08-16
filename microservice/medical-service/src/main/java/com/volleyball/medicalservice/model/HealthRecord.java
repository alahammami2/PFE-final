package com.volleyball.medicalservice.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entité représentant un dossier médical d'un joueur
 */
@Entity
@Table(name = "health_records")
public class HealthRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "L'ID du joueur est obligatoire")
    @Column(name = "player_id", nullable = false)
    private Long playerId;

    @NotBlank(message = "Le nom du joueur est obligatoire")
    @Size(max = 100, message = "Le nom ne peut pas dépasser 100 caractères")
    @Column(name = "player_name", nullable = false, length = 100)
    private String playerName;

    @Size(max = 50, message = "Le type de blessure ne peut pas dépasser 50 caractères")
    @Column(name = "blessure_type", length = 50)
    private String blessureType;

    @Column(name = "blessure_date")
    private LocalDate blessureDate;

    @Column(name = "statut_physique", length = 100)
    private String statutPhysique;

    @NotNull(message = "Le statut est obligatoire")
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    private MedicalStatus status;

    @Column(name = "last_medical_checkup")
    private LocalDate lastMedicalCheckup;

    @Column(name = "next_checkup_due")
    private LocalDate nextCheckupDue;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Constructeurs
    public HealthRecord() {
        this.createdAt = LocalDateTime.now();
        this.status = MedicalStatus.EN_SUIVI;
    }

    public HealthRecord(Long playerId, String playerName) {
        this();
        this.playerId = playerId;
        this.playerName = playerName;
    }

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPlayerId() {
        return playerId;
    }

    public void setPlayerId(Long playerId) {
        this.playerId = playerId;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public String getBlessureType() {
        return blessureType;
    }

    public void setBlessureType(String blessureType) {
        this.blessureType = blessureType;
    }

    public LocalDate getBlessureDate() {
        return blessureDate;
    }

    public void setBlessureDate(LocalDate blessureDate) {
        this.blessureDate = blessureDate;
    }

    public String getStatutPhysique() {
        return statutPhysique;
    }

    public void setStatutPhysique(String statutPhysique) {
        this.statutPhysique = statutPhysique;
    }

    public MedicalStatus getStatus() {
        return status;
    }

    public void setStatus(MedicalStatus status) {
        this.status = status;
    }

    public LocalDate getLastMedicalCheckup() {
        return lastMedicalCheckup;
    }

    public void setLastMedicalCheckup(LocalDate lastMedicalCheckup) {
        this.lastMedicalCheckup = lastMedicalCheckup;
    }

    public LocalDate getNextCheckupDue() {
        return nextCheckupDue;
    }

    public void setNextCheckupDue(LocalDate nextCheckupDue) {
        this.nextCheckupDue = nextCheckupDue;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "HealthRecord{" +
                "id=" + id +
                ", playerId=" + playerId +
                ", playerName='" + playerName + '\'' +
                ", status=" + status +
                ", lastMedicalCheckup=" + lastMedicalCheckup +
                ", nextCheckupDue=" + nextCheckupDue +
                '}';
    }
}
