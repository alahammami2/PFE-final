package com.volleyball.adminrequestservice.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

/**
 * Entité représentant une demande administrative dans le système
 */
@Entity
@Table(name = "admin_requests")
public class AdminRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "L'ID du demandeur est obligatoire")
    @Column(name = "requester_id", nullable = false)
    private Long requesterId;

    @NotBlank(message = "Le titre est obligatoire")
    @Size(max = 200, message = "Le titre ne peut pas dépasser 200 caractères")
    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @NotBlank(message = "La description est obligatoire")
    @Size(max = 2000, message = "La description ne peut pas dépasser 2000 caractères")
    @Column(name = "description", nullable = false, length = 2000)
    private String description;

    @NotNull(message = "Le type de demande est obligatoire")
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private RequestType type;

    @NotNull(message = "La priorité est obligatoire")
    @Enumerated(EnumType.STRING)
    @Column(name = "priority", nullable = false)
    private RequestPriority priority = RequestPriority.NORMALE;

    @NotNull(message = "Le statut est obligatoire")
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private RequestStatus status = RequestStatus.BROUILLON;

    @Column(name = "assigned_to")
    private Long assignedTo;

    @Column(name = "budget_requested")
    private Double budgetRequested;

    @Column(name = "date_needed")
    private LocalDateTime dateNeeded;

    @Size(max = 1000, message = "Les commentaires ne peuvent pas dépasser 1000 caractères")
    @Column(name = "admin_comments", length = 1000)
    private String adminComments;

    @Size(max = 500, message = "La raison du rejet ne peut pas dépasser 500 caractères")
    @Column(name = "rejection_reason", length = 500)
    private String rejectionReason;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "processed_at")
    private LocalDateTime processedAt;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    @Column(name = "approved_by")
    private Long approvedBy;

    // Constructeurs
    public AdminRequest() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public AdminRequest(Long requesterId, String title, String description, RequestType type, RequestPriority priority) {
        this();
        this.requesterId = requesterId;
        this.title = title;
        this.description = description;
        this.type = type;
        this.priority = priority;
    }

    // Méthodes utilitaires
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public void approve(Long approvedBy) {
        this.status = RequestStatus.APPROUVEE;
        this.approvedBy = approvedBy;
        this.approvedAt = LocalDateTime.now();
        this.processedAt = LocalDateTime.now();
    }

    public void reject(String reason, Long rejectedBy) {
        this.status = RequestStatus.REJETEE;
        this.rejectionReason = reason;
        this.approvedBy = rejectedBy;
        this.processedAt = LocalDateTime.now();
    }

    public void assign(Long assignedTo) {
        this.assignedTo = assignedTo;
        this.status = RequestStatus.EN_COURS;
    }

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getRequesterId() {
        return requesterId;
    }

    public void setRequesterId(Long requesterId) {
        this.requesterId = requesterId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public RequestType getType() {
        return type;
    }

    public void setType(RequestType type) {
        this.type = type;
    }

    public RequestPriority getPriority() {
        return priority;
    }

    public void setPriority(RequestPriority priority) {
        this.priority = priority;
    }

    public RequestStatus getStatus() {
        return status;
    }

    public void setStatus(RequestStatus status) {
        this.status = status;
    }

    public Long getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(Long assignedTo) {
        this.assignedTo = assignedTo;
    }

    public Double getBudgetRequested() {
        return budgetRequested;
    }

    public void setBudgetRequested(Double budgetRequested) {
        this.budgetRequested = budgetRequested;
    }

    public LocalDateTime getDateNeeded() {
        return dateNeeded;
    }

    public void setDateNeeded(LocalDateTime dateNeeded) {
        this.dateNeeded = dateNeeded;
    }

    public String getAdminComments() {
        return adminComments;
    }

    public void setAdminComments(String adminComments) {
        this.adminComments = adminComments;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
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

    public LocalDateTime getProcessedAt() {
        return processedAt;
    }

    public void setProcessedAt(LocalDateTime processedAt) {
        this.processedAt = processedAt;
    }

    public LocalDateTime getApprovedAt() {
        return approvedAt;
    }

    public void setApprovedAt(LocalDateTime approvedAt) {
        this.approvedAt = approvedAt;
    }

    public Long getApprovedBy() {
        return approvedBy;
    }

    public void setApprovedBy(Long approvedBy) {
        this.approvedBy = approvedBy;
    }
}
