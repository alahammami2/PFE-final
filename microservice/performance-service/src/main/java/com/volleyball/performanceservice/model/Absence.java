package com.volleyball.performanceservice.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entité représentant une absence d'un joueur
 */
@Entity
@Table(name = "absences")
public class Absence {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Le joueur est obligatoire")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player_id", nullable = false)
    @JsonIgnoreProperties({"absences", "performances", "hibernateLazyInitializer", "handler"})
    private Player player;

    @NotNull(message = "La date de début est obligatoire")
    @Column(name = "date_debut", nullable = false)
    private LocalDate dateDebut;

    @Column(name = "date_fin")
    private LocalDate dateFin;

    @NotNull(message = "Le type d'absence est obligatoire")
    @Enumerated(EnumType.STRING)
    @Column(name = "type_absence", nullable = false)
    private TypeAbsence typeAbsence;

    @Size(max = 500, message = "La raison ne peut pas dépasser 500 caractères")
    @Column(name = "raison", length = 500)
    private String raison;

    @NotNull(message = "Le statut est obligatoire")
    @Enumerated(EnumType.STRING)
    @Column(name = "statut", nullable = false)
    private StatutAbsence statut = StatutAbsence.EN_ATTENTE;

    @Column(name = "justifiee", nullable = false)
    private Boolean justifiee = false;

    @Size(max = 1000, message = "Les commentaires ne peuvent pas dépasser 1000 caractères")
    @Column(name = "commentaires", length = 1000)
    private String commentaires;

    @Column(name = "date_creation", nullable = false, updatable = false)
    private LocalDateTime dateCreation;

    @Column(name = "date_modification")
    private LocalDateTime dateModification;

    // Constructeurs
    public Absence() {
    }

    public Absence(Player player, LocalDate dateDebut, TypeAbsence typeAbsence, String raison) {
        this.player = player;
        this.dateDebut = dateDebut;
        this.typeAbsence = typeAbsence;
        this.raison = raison;
    }

    // Méthodes de cycle de vie JPA
    @PrePersist
    protected void onCreate() {
        dateCreation = LocalDateTime.now();
        dateModification = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        dateModification = LocalDateTime.now();
    }

    // Méthodes utilitaires
    public boolean isEnCours() {
        LocalDate aujourdhui = LocalDate.now();
        return dateDebut.isBefore(aujourdhui.plusDays(1)) && 
               (dateFin == null || dateFin.isAfter(aujourdhui.minusDays(1)));
    }

    public long getDureeEnJours() {
        if (dateFin == null) {
            return java.time.temporal.ChronoUnit.DAYS.between(dateDebut, LocalDate.now());
        }
        return java.time.temporal.ChronoUnit.DAYS.between(dateDebut, dateFin);
    }

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
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

    public StatutAbsence getStatut() {
        return statut;
    }

    public void setStatut(StatutAbsence statut) {
        this.statut = statut;
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

    public LocalDateTime getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(LocalDateTime dateCreation) {
        this.dateCreation = dateCreation;
    }

    public LocalDateTime getDateModification() {
        return dateModification;
    }

    public void setDateModification(LocalDateTime dateModification) {
        this.dateModification = dateModification;
    }

    @Override
    public String toString() {
        return "Absence{" +
                "id=" + id +
                ", player=" + (player != null ? player.getNom() + " " + player.getPrenom() : null) +
                ", dateDebut=" + dateDebut +
                ", dateFin=" + dateFin +
                ", typeAbsence=" + typeAbsence +
                ", statut=" + statut +
                ", justifiee=" + justifiee +
                '}';
    }
}
