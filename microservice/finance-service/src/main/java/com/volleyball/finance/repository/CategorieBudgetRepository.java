package com.volleyball.finance.repository;

import com.volleyball.finance.model.CategorieBudget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategorieBudgetRepository extends JpaRepository<CategorieBudget, Long> {
    // Matching fields: categorie (String), montant (Double), date (LocalDate), description (String)
    List<CategorieBudget> findByCategorieContainingIgnoreCase(String categorie);
    List<CategorieBudget> findByMontantGreaterThan(Double montant);
    List<CategorieBudget> findByMontantBetween(Double montantMin, Double montantMax);
}

