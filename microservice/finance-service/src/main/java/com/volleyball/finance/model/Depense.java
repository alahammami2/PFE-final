package com.volleyball.finance.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDate;

@Entity
@Table(name = "depenses")
public class Depense {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // id_depense supprimé
    
    @NotNull(message = "Le montant est obligatoire")
    @Positive(message = "Le montant doit être positif")
    @Column(name = "montant", nullable = false)
    private Double montant;
    
    @NotNull(message = "La date est obligatoire")
    @Column(name = "date", nullable = false)
    private LocalDate date;
    
    @NotBlank(message = "La description est obligatoire")
    @Column(name = "description", nullable = false, length = 500)
    private String description;
    
    // Relation avec categorie_budget supprimée
    
    @NotBlank(message = "Le statut est obligatoire")
    @Column(name = "statut", nullable = false, length = 50)
    private String statut;

    @NotBlank(message = "La catégorie est obligatoire")
    @Column(name = "categorie", nullable = false, length = 100)
    private String categorie;
    
    // Constructeurs
    public Depense() {}
    
    public Depense(Double montant, LocalDate date, String description, String statut, String categorie) {
        this.montant = montant;
        this.date = date;
        this.description = description;
        this.statut = statut;
        this.categorie = categorie;
    }
    
    // Getters et Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    
    public Double getMontant() {
        return montant;
    }
    
    public void setMontant(Double montant) {
        this.montant = montant;
    }
    
    public LocalDate getDate() {
        return date;
    }
    
    public void setDate(LocalDate date) {
        this.date = date;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getStatut() { return statut; }

    public void setStatut(String statut) { this.statut = statut; }

    public String getCategorie() { return categorie; }

    public void setCategorie(String categorie) { this.categorie = categorie; }
    
    // Méthodes métier
    public void consulter() {
        System.out.println("Consultation de la dépense: " + id + " - " + description + " - Montant: " + montant);
    }
    
    public void mettreAJour(Double nouveauMontant, String nouvelleDescription) {
        this.montant = nouveauMontant;
        this.description = nouvelleDescription;
        System.out.println("Dépense mise à jour: " + id);
    }
    
    @Override
    public String toString() {
        return "Depense{" +
                "id=" + id +
                
                ", montant=" + montant +
                ", date=" + date +
                ", description='" + description + '\'' +
                ", statut='" + statut + '\'' +
                ", categorie='" + categorie + '\'' +
                '}';
    }
}
