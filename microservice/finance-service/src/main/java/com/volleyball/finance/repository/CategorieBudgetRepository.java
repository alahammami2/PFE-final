package com.volleyball.finance.repository;

import com.volleyball.finance.model.CategorieBudget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategorieBudgetRepository extends JpaRepository<CategorieBudget, Long> {
    
    Optional<CategorieBudget> findByIdCategorie(Integer idCategorie);
    
    List<CategorieBudget> findByNomCategorieContainingIgnoreCase(String nomCategorie);
    
    List<CategorieBudget> findByBudgetId(Long budgetId);
    
    List<CategorieBudget> findByMontantAlloueGreaterThan(Double montant);
    
    List<CategorieBudget> findByMontantAlloueBetween(Double montantMin, Double montantMax);
    
    @Query("SELECT cb FROM CategorieBudget cb WHERE cb.budget.id = :budgetId ORDER BY cb.montantAlloue DESC")
    List<CategorieBudget> findByBudgetIdOrderByMontantDesc(@Param("budgetId") Long budgetId);
    
    @Query("SELECT SUM(cb.montantAlloue) FROM CategorieBudget cb WHERE cb.budget.id = :budgetId")
    Double getTotalMontantAlloueByBudget(@Param("budgetId") Long budgetId);
    
    @Query("SELECT cb FROM CategorieBudget cb JOIN FETCH cb.depenses WHERE cb.id = :id")
    Optional<CategorieBudget> findByIdWithDepenses(@Param("id") Long id);
    
    @Query("SELECT cb FROM CategorieBudget cb WHERE cb.montantAlloue > " +
           "(SELECT COALESCE(SUM(d.montant), 0) FROM Depense d WHERE d.categorieBudget.id = cb.id)")
    List<CategorieBudget> findCategoriesWithRemainingBudget();
    
    @Query("SELECT COUNT(cb) FROM CategorieBudget cb WHERE cb.budget.id = :budgetId")
    Long countCategoriesByBudget(@Param("budgetId") Long budgetId);
}
