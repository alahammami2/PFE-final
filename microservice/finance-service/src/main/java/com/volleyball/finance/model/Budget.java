package com.volleyball.finance.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "budgets")
public class Budget {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "L'ID finance est obligatoire")
    @Column(name = "id_finance", unique = true, nullable = false)
    private String idFinance;
    
    @NotNull(message = "Le montant est obligatoire")
    @PositiveOrZero(message = "Le montant doit être positif ou zéro")
    @Column(name = "montant", nullable = false)
    private Double montant;
    
    @OneToMany(mappedBy = "budget", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<CategorieBudget> categories = new ArrayList<>();
    
    // Constructeurs
    public Budget() {}
    
    public Budget(String idFinance, Double montant) {
        this.idFinance = idFinance;
        this.montant = montant;
    }
    
    // Getters et Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getIdFinance() {
        return idFinance;
    }
    
    public void setIdFinance(String idFinance) {
        this.idFinance = idFinance;
    }
    
    public Double getMontant() {
        return montant;
    }
    
    public void setMontant(Double montant) {
        this.montant = montant;
    }
    
    public List<CategorieBudget> getCategories() {
        return categories;
    }
    
    public void setCategories(List<CategorieBudget> categories) {
        this.categories = categories;
    }
    
    // Méthodes métier
    public void consulter() {
        // Logique pour consulter le budget
        System.out.println("Consultation du budget: " + idFinance + " - Montant: " + montant);
    }
    
    public void mettreAJour(Double nouveauMontant) {
        this.montant = nouveauMontant;
        System.out.println("Budget mis à jour: " + idFinance + " - Nouveau montant: " + montant);
    }
    
    @Override
    public String toString() {
        return "Budget{" +
                "id=" + id +
                ", idFinance='" + idFinance + '\'' +
                ", montant=" + montant +
                '}';
    }
}
