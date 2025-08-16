package com.volleyball.adminrequestservice.model;

import jakarta.persistence.*;
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

    @Column(name = "budget_requested")
    private Double budgetRequested;

    // Champs supprimés: assignedTo, dateNeeded, adminComments, rejectionReason, updatedAt, processedAt, approvedAt, approvedBy, title

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // Champs de suivi supprimés conformément aux exigences

    // Constructeurs
    public AdminRequest() {
        this.createdAt = LocalDateTime.now();
    }

    public AdminRequest(Long requesterId, String description, RequestType type, RequestPriority priority) {
        this();
        this.requesterId = requesterId;
        this.description = description;
        this.type = type;
        this.priority = priority;
    }

    // Méthodes utilitaires supprimées: preUpdate, approve, reject, assign

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

    public Double getBudgetRequested() {
        return budgetRequested;
    }

    public void setBudgetRequested(Double budgetRequested) {
        this.budgetRequested = budgetRequested;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    // Getters/Setters pour les champs supprimés ont été retirés
}
