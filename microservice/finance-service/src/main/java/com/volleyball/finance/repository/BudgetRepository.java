package com.volleyball.finance.repository;

import com.volleyball.finance.model.Budget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BudgetRepository extends JpaRepository<Budget, Long> {
    
    Optional<Budget> findByIdFinance(String idFinance);
    
    List<Budget> findByMontantGreaterThan(Double montant);
    
    List<Budget> findByMontantBetween(Double montantMin, Double montantMax);
    
    @Query("SELECT b FROM Budget b WHERE b.montant >= :montantMin ORDER BY b.montant DESC")
    List<Budget> findBudgetsWithMinimumAmount(@Param("montantMin") Double montantMin);
    
    @Query("SELECT SUM(b.montant) FROM Budget b")
    Double getTotalBudget();
    
    @Query("SELECT COUNT(b) FROM Budget b")
    Long countAllBudgets();
    
    @Query("SELECT b FROM Budget b JOIN FETCH b.categories WHERE b.id = :id")
    Optional<Budget> findByIdWithCategories(@Param("id") Long id);
    
    @Query("SELECT DISTINCT b FROM Budget b JOIN FETCH b.categories c WHERE c.nomCategorie LIKE %:nomCategorie%")
    List<Budget> findBudgetsByCategorieName(@Param("nomCategorie") String nomCategorie);
}
