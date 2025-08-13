package com.volleyball.medicalservice.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

/**
 * Ancienne entité rapport (neutralisée, non mappée JPA)
 */
public class MedicalReport {

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

    @Column(name = "appointment_id")
    private Long appointmentId;

    @NotNull(message = "Le type de rapport est obligatoire")
    @Enumerated(EnumType.STRING)
    @Column(name = "report_type", nullable = false)
    private ReportType reportType;

    @NotBlank(message = "Le titre est obligatoire")
    @Size(max = 200, message = "Le titre ne peut pas dépasser 200 caractères")
    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @NotBlank(message = "Le contenu est obligatoire")
    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @NotBlank(message = "Le médecin est obligatoire")
    @Size(max = 100, message = "Le nom du médecin ne peut pas dépasser 100 caractères")
    @Column(name = "doctor_name", nullable = false, length = 100)
    private String doctorName;

    @Size(max = 100, message = "La spécialité ne peut pas dépasser 100 caractères")
    @Column(name = "doctor_specialty", length = 100)
    private String doctorSpecialty;

    @Column(name = "diagnosis", columnDefinition = "TEXT")
    private String diagnosis;

    @Column(name = "treatment", columnDefinition = "TEXT")
    private String treatment;

    @Column(name = "recommendations", columnDefinition = "TEXT")
    private String recommendations;

    @Column(name = "restrictions", columnDefinition = "TEXT")
    private String restrictions;

    @Column(name = "return_to_play_date")
    private LocalDateTime returnToPlayDate;

    @NotNull(message = "Le statut est obligatoire")
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ReportStatus status;

    @Column(name = "is_confidential")
    private Boolean isConfidential;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Constructeurs
    public MedicalReport() {
        this.createdAt = LocalDateTime.now();
        this.status = ReportStatus.DRAFT;
        this.isConfidential = false;
    }

    public MedicalReport(Long playerId, String playerName, ReportType reportType, 
                        String title, String content, String doctorName) {
        this();
        this.playerId = playerId;
        this.playerName = playerName;
        this.reportType = reportType;
        this.title = title;
        this.content = content;
        this.doctorName = doctorName;
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

    public Long getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(Long appointmentId) {
        this.appointmentId = appointmentId;
    }

    public ReportType getReportType() {
        return reportType;
    }

    public void setReportType(ReportType reportType) {
        this.reportType = reportType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public String getDoctorSpecialty() {
        return doctorSpecialty;
    }

    public void setDoctorSpecialty(String doctorSpecialty) {
        this.doctorSpecialty = doctorSpecialty;
    }

    public String getDiagnosis() {
        return diagnosis;
    }

    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
    }

    public String getTreatment() {
        return treatment;
    }

    public void setTreatment(String treatment) {
        this.treatment = treatment;
    }

    public String getRecommendations() {
        return recommendations;
    }

    public void setRecommendations(String recommendations) {
        this.recommendations = recommendations;
    }

    public String getRestrictions() {
        return restrictions;
    }

    public void setRestrictions(String restrictions) {
        this.restrictions = restrictions;
    }

    public LocalDateTime getReturnToPlayDate() {
        return returnToPlayDate;
    }

    public void setReturnToPlayDate(LocalDateTime returnToPlayDate) {
        this.returnToPlayDate = returnToPlayDate;
    }

    public ReportStatus getStatus() {
        return status;
    }

    public void setStatus(ReportStatus status) {
        this.status = status;
    }

    public Boolean getIsConfidential() {
        return isConfidential;
    }

    public void setIsConfidential(Boolean isConfidential) {
        this.isConfidential = isConfidential;
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
        return "MedicalReport{" +
                "id=" + id +
                ", playerId=" + playerId +
                ", playerName='" + playerName + '\'' +
                ", reportType=" + reportType +
                ", title='" + title + '\'' +
                ", doctorName='" + doctorName + '\'' +
                ", status=" + status +
                ", isConfidential=" + isConfidential +
                '}';
    }
}
