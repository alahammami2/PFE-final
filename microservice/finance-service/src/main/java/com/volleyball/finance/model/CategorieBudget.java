package com.volleyball.finance.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "recettes")
public class CategorieBudget {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotNull(message = "La date est obligatoire")
    @Column(name = "date", nullable = false)
    private java.time.LocalDate date;

    @NotBlank(message = "La description est obligatoire")
    @Column(name = "description", nullable = false)
    private String description;

    @NotNull(message = "Le montant est obligatoire")
    @PositiveOrZero(message = "Le montant doit être positif ou zéro")
    @Column(name = "montant", nullable = false)
    private Double montant;

    @NotBlank(message = "La catégorie est obligatoire")
    @Column(name = "categorie", nullable = false)
    private String categorie;
    
    // Lien vers Budget supprimé (table budgets indépendante)
    
    @OneToMany(mappedBy = "categorieBudget", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Depense> depenses = new ArrayList<>();
    
    // Constructeurs
    public CategorieBudget() {}
    
    public CategorieBudget(java.time.LocalDate date, String description, Double montant, String categorie) {
        this.date = date;
        this.description = description;
        this.montant = montant;
        this.categorie = categorie;
    }
    
    // Getters et Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public java.time.LocalDate getDate() { return date; }

    public void setDate(java.time.LocalDate date) { this.date = date; }

    public String getDescription() { return description; }

    public void setDescription(String description) { this.description = description; }

    public Double getMontant() { return montant; }

    public void setMontant(Double montant) { this.montant = montant; }

    public String getCategorie() { return categorie; }

    public void setCategorie(String categorie) { this.categorie = categorie; }
    
    // Champ budget supprimé: getters/setters retirés
    
    public List<Depense> getDepenses() {
        return depenses;
    }
    
    public void setDepenses(List<Depense> depenses) {
        this.depenses = depenses;
    }
    
    // Méthodes métier
    public void mettreAJour(Double nouveauMontant) {
        this.montant = nouveauMontant;
    }
    
    public Double getMontantDepense() {
        return depenses.stream().mapToDouble(Depense::getMontant).sum();
    }
    
    public Double getMontantRestant() {
        return (montant != null ? montant : 0.0) - getMontantDepense();
    }
    
    @Override
    public String toString() {
        return "CategorieBudget{" +
                "id=" + id +
                ", date=" + date +
                ", description='" + description + '\'' +
                ", montant=" + montant +
                ", categorie='" + categorie + '\'' +
                '}';
    }
}
