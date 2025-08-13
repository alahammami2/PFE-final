package com.volleyball.finance.repository;

import com.volleyball.finance.model.Depense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface DepenseRepository extends JpaRepository<Depense, Long> {
    
    Optional<Depense> findByIdDepense(Integer idDepense);
    
    List<Depense> findByCategorieBudgetId(Long categorieBudgetId);
    
    List<Depense> findByDateBetween(LocalDate dateDebut, LocalDate dateFin);
    
    List<Depense> findByMontantGreaterThan(Double montant);
    
    List<Depense> findByMontantBetween(Double montantMin, Double montantMax);
    
    List<Depense> findByDescriptionContainingIgnoreCase(String description);
    
    @Query("SELECT d FROM Depense d WHERE d.categorieBudget.id = :categorieBudgetId ORDER BY d.date DESC")
    List<Depense> findByCategorieBudgetIdOrderByDateDesc(@Param("categorieBudgetId") Long categorieBudgetId);
    
    @Query("SELECT SUM(d.montant) FROM Depense d WHERE d.categorieBudget.id = :categorieBudgetId")
    Double getTotalMontantByCategorie(@Param("categorieBudgetId") Long categorieBudgetId);
    
    @Query("SELECT SUM(d.montant) FROM Depense d WHERE d.date BETWEEN :dateDebut AND :dateFin")
    Double getTotalMontantByPeriode(@Param("dateDebut") LocalDate dateDebut, @Param("dateFin") LocalDate dateFin);
    
    @Query("SELECT d FROM Depense d WHERE YEAR(d.date) = :annee")
    List<Depense> findByAnnee(@Param("annee") int annee);
    
    @Query("SELECT d FROM Depense d WHERE MONTH(d.date) = :mois AND YEAR(d.date) = :annee")
    List<Depense> findByMoisEtAnnee(@Param("mois") int mois, @Param("annee") int annee);
    
    @Query("SELECT COUNT(d) FROM Depense d WHERE d.categorieBudget.id = :categorieBudgetId")
    Long countDepensesByCategorie(@Param("categorieBudgetId") Long categorieBudgetId);
    
    @Query("SELECT d FROM Depense d WHERE d.date >= :dateDebut ORDER BY d.montant DESC")
    List<Depense> findRecentDepensesOrderByMontant(@Param("dateDebut") LocalDate dateDebut);
}
