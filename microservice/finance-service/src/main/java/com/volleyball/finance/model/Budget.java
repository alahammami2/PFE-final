package com.volleyball.finance.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
 

@Entity
@Table(name = "budgets")
public class Budget {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotNull(message = "Le montant est obligatoire")
    @Column(name = "montant", nullable = false)
    private Double montant;
    
    // Constructeurs
    public Budget() {}
    
    public Budget(Double montant) {
        this.montant = montant;
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
    
    // Méthode métier simplifiée
    public void mettreAJour(Double nouveauMontant) {
        this.montant = nouveauMontant;
    }
    
    @Override
    public String toString() {
        return "Budget{" +
                "id=" + id +
                ", montant=" + montant +
                '}';
    }



    public void consulter() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'consulter'");
    }

   
}
