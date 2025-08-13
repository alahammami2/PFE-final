package com.volleyball.finance.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "categories_budget")
public class CategorieBudget {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotNull(message = "L'ID catégorie est obligatoire")
    @Column(name = "id_categorie", nullable = false)
    private Integer idCategorie;
    
    @NotBlank(message = "Le nom de la catégorie est obligatoire")
    @Column(name = "nom_categorie", nullable = false)
    private String nomCategorie;
    
    @NotNull(message = "Le montant alloué est obligatoire")
    @PositiveOrZero(message = "Le montant alloué doit être positif ou zéro")
    @Column(name = "montant_alloue", nullable = false)
    private Double montantAlloue;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "budget_id", nullable = false)
    private Budget budget;
    
    @OneToMany(mappedBy = "categorieBudget", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Depense> depenses = new ArrayList<>();
    
    // Constructeurs
    public CategorieBudget() {}
    
    public CategorieBudget(Integer idCategorie, String nomCategorie, Double montantAlloue) {
        this.idCategorie = idCategorie;
        this.nomCategorie = nomCategorie;
        this.montantAlloue = montantAlloue;
    }
    
    // Getters et Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Integer getIdCategorie() {
        return idCategorie;
    }
    
    public void setIdCategorie(Integer idCategorie) {
        this.idCategorie = idCategorie;
    }
    
    public String getNomCategorie() {
        return nomCategorie;
    }
    
    public void setNomCategorie(String nomCategorie) {
        this.nomCategorie = nomCategorie;
    }
    
    public Double getMontantAlloue() {
        return montantAlloue;
    }
    
    public void setMontantAlloue(Double montantAlloue) {
        this.montantAlloue = montantAlloue;
    }
    
    public Budget getBudget() {
        return budget;
    }
    
    public void setBudget(Budget budget) {
        this.budget = budget;
    }
    
    public List<Depense> getDepenses() {
        return depenses;
    }
    
    public void setDepenses(List<Depense> depenses) {
        this.depenses = depenses;
    }
    
    // Méthodes métier
    public void mettreAJour(Double nouveauMontant) {
        this.montantAlloue = nouveauMontant;
        System.out.println("Catégorie budget mise à jour: " + nomCategorie + " - Nouveau montant: " + montantAlloue);
    }
    
    public Double getMontantDepense() {
        return depenses.stream()
                .mapToDouble(Depense::getMontant)
                .sum();
    }
    
    public Double getMontantRestant() {
        return montantAlloue - getMontantDepense();
    }
    
    @Override
    public String toString() {
        return "CategorieBudget{" +
                "id=" + id +
                ", idCategorie=" + idCategorie +
                ", nomCategorie='" + nomCategorie + '\'' +
                ", montantAlloue=" + montantAlloue +
                '}';
    }
}
