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
    
    @NotNull(message = "L'ID dépense est obligatoire")
    @Column(name = "id_depense", nullable = false)
    private Integer idDepense;
    
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
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categorie_budget_id", nullable = false)
    private CategorieBudget categorieBudget;
    
    // Constructeurs
    public Depense() {}
    
    public Depense(Integer idDepense, Double montant, LocalDate date, String description) {
        this.idDepense = idDepense;
        this.montant = montant;
        this.date = date;
        this.description = description;
    }
    
    // Getters et Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Integer getIdDepense() {
        return idDepense;
    }
    
    public void setIdDepense(Integer idDepense) {
        this.idDepense = idDepense;
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
    
    public CategorieBudget getCategorieBudget() {
        return categorieBudget;
    }
    
    public void setCategorieBudget(CategorieBudget categorieBudget) {
        this.categorieBudget = categorieBudget;
    }
    
    // Méthodes métier
    public void consulter() {
        System.out.println("Consultation de la dépense: " + idDepense + " - " + description + " - Montant: " + montant);
    }
    
    public void mettreAJour(Double nouveauMontant, String nouvelleDescription) {
        this.montant = nouveauMontant;
        this.description = nouvelleDescription;
        System.out.println("Dépense mise à jour: " + idDepense);
    }
    
    @Override
    public String toString() {
        return "Depense{" +
                "id=" + id +
                ", idDepense=" + idDepense +
                ", montant=" + montant +
                ", date=" + date +
                ", description='" + description + '\'' +
                '}';
    }
}
