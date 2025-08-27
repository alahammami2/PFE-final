package com.volleyball.finance.repository;

import com.volleyball.finance.model.Budget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BudgetRepository extends JpaRepository<Budget, Long> {
    List<Budget> findByMontantGreaterThan(Double montant);
    
    List<Budget> findByMontantBetween(Double montantMin, Double montantMax);

    // Custom queries using montant only
    @org.springframework.data.jpa.repository.Query("SELECT b FROM Budget b WHERE b.montant >= :montantMin ORDER BY b.montant DESC")
    List<Budget> findBudgetsWithMinimumAmount(@org.springframework.data.repository.query.Param("montantMin") Double montantMin);

    @org.springframework.data.jpa.repository.Query("SELECT SUM(b.montant) FROM Budget b")
    Double getTotalBudget();

    @org.springframework.data.jpa.repository.Query("SELECT COUNT(b) FROM Budget b")
    Long countAllBudgets();
}

