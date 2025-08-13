package com.volleyball.performanceservice.dto;

import com.volleyball.performanceservice.model.TypeAbsence;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

/**
 * DTO pour la création d'une absence
 */
public class CreateAbsenceRequest {

    @NotNull(message = "L'ID du joueur est obligatoire")
    private Long playerId;

    @NotNull(message = "La date de début est obligatoire")
    private LocalDate dateDebut;

    private LocalDate dateFin;

    @NotNull(message = "Le type d'absence est obligatoire")
    private TypeAbsence typeAbsence;

    @Size(max = 500, message = "La raison ne peut pas dépasser 500 caractères")
    private String raison;

    private Boolean justifiee = false;

    @Size(max = 1000, message = "Les commentaires ne peuvent pas dépasser 1000 caractères")
    private String commentaires;

    // Constructeurs
    public CreateAbsenceRequest() {
    }

    public CreateAbsenceRequest(Long playerId, LocalDate dateDebut, TypeAbsence typeAbsence, String raison) {
        this.playerId = playerId;
        this.dateDebut = dateDebut;
        this.typeAbsence = typeAbsence;
        this.raison = raison;
    }

    // Getters et Setters
    public Long getPlayerId() {
        return playerId;
    }

    public void setPlayerId(Long playerId) {
        this.playerId = playerId;
    }

    public LocalDate getDateDebut() {
        return dateDebut;
    }

    public void setDateDebut(LocalDate dateDebut) {
        this.dateDebut = dateDebut;
    }

    public LocalDate getDateFin() {
        return dateFin;
    }

    public void setDateFin(LocalDate dateFin) {
        this.dateFin = dateFin;
    }

    public TypeAbsence getTypeAbsence() {
        return typeAbsence;
    }

    public void setTypeAbsence(TypeAbsence typeAbsence) {
        this.typeAbsence = typeAbsence;
    }

    public String getRaison() {
        return raison;
    }

    public void setRaison(String raison) {
        this.raison = raison;
    }

    public Boolean getJustifiee() {
        return justifiee;
    }

    public void setJustifiee(Boolean justifiee) {
        this.justifiee = justifiee;
    }

    public String getCommentaires() {
        return commentaires;
    }

    public void setCommentaires(String commentaires) {
        this.commentaires = commentaires;
    }

    @Override
    public String toString() {
        return "CreateAbsenceRequest{" +
                "playerId=" + playerId +
                ", dateDebut=" + dateDebut +
                ", dateFin=" + dateFin +
                ", typeAbsence=" + typeAbsence +
                ", raison='" + raison + '\'' +
                '}';
    }
}
