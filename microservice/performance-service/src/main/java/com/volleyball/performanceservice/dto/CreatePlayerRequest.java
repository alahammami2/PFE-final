package com.volleyball.performanceservice.dto;

import com.volleyball.performanceservice.model.Position;
import com.volleyball.performanceservice.model.StatutJoueur;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

/**
 * DTO pour la création d'un joueur
 */
public class CreatePlayerRequest {

    @NotBlank(message = "Le nom est obligatoire")
    @Size(max = 100, message = "Le nom ne peut pas dépasser 100 caractères")
    private String nom;

    @NotBlank(message = "Le prénom est obligatoire")
    @Size(max = 100, message = "Le prénom ne peut pas dépasser 100 caractères")
    private String prenom;

    @Email(message = "Format d'email invalide")
    @Size(max = 150, message = "L'email ne peut pas dépasser 150 caractères")
    private String email;

    @Size(max = 20, message = "Le téléphone ne peut pas dépasser 20 caractères")
    private String telephone;

    @NotNull(message = "La date de naissance est obligatoire")
    @Past(message = "La date de naissance doit être dans le passé")
    private LocalDate dateNaissance;

    @NotNull(message = "La position est obligatoire")
    private Position position;

    @Min(value = 1, message = "Le numéro de maillot doit être positif")
    @Max(value = 99, message = "Le numéro de maillot ne peut pas dépasser 99")
    private Integer numeroMaillot;

    @Min(value = 100, message = "La taille doit être au moins 100 cm")
    @Max(value = 250, message = "La taille ne peut pas dépasser 250 cm")
    private Integer tailleCm;

    @DecimalMin(value = "30.0", message = "Le poids doit être au moins 30 kg")
    @DecimalMax(value = "200.0", message = "Le poids ne peut pas dépasser 200 kg")
    private Double poidsKg;

    private StatutJoueur statut = StatutJoueur.ACTIF;

    private LocalDate dateDebutEquipe;

    // Constructeurs
    public CreatePlayerRequest() {
    }

    public CreatePlayerRequest(String nom, String prenom, String email, LocalDate dateNaissance, Position position) {
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.dateNaissance = dateNaissance;
        this.position = position;
    }

    // Getters et Setters
    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public LocalDate getDateNaissance() {
        return dateNaissance;
    }

    public void setDateNaissance(LocalDate dateNaissance) {
        this.dateNaissance = dateNaissance;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public Integer getNumeroMaillot() {
        return numeroMaillot;
    }

    public void setNumeroMaillot(Integer numeroMaillot) {
        this.numeroMaillot = numeroMaillot;
    }

    public Integer getTailleCm() {
        return tailleCm;
    }

    public void setTailleCm(Integer tailleCm) {
        this.tailleCm = tailleCm;
    }

    public Double getPoidsKg() {
        return poidsKg;
    }

    public void setPoidsKg(Double poidsKg) {
        this.poidsKg = poidsKg;
    }

    public StatutJoueur getStatut() {
        return statut;
    }

    public void setStatut(StatutJoueur statut) {
        this.statut = statut;
    }

    public LocalDate getDateDebutEquipe() {
        return dateDebutEquipe;
    }

    public void setDateDebutEquipe(LocalDate dateDebutEquipe) {
        this.dateDebutEquipe = dateDebutEquipe;
    }

    @Override
    public String toString() {
        return "CreatePlayerRequest{" +
                "nom='" + nom + '\'' +
                ", prenom='" + prenom + '\'' +
                ", email='" + email + '\'' +
                ", position=" + position +
                ", numeroMaillot=" + numeroMaillot +
                '}';
    }
}
