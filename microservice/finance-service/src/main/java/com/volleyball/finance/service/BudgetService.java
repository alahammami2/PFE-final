package com.volleyball.finance.service;

import com.volleyball.finance.model.Budget;
import com.volleyball.finance.repository.BudgetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class BudgetService {
    
    @Autowired
    private BudgetRepository budgetRepository;
    
    // CRUD Operations
    public Budget createBudget(Budget budget) {
        return budgetRepository.save(budget);
    }
    
    public List<Budget> getAllBudgets() {
        return budgetRepository.findAll();
    }
    
    public Optional<Budget> getBudgetById(Long id) {
        return budgetRepository.findById(id);
    }
    
    public Optional<Budget> getBudgetByIdFinance(String idFinance) {
        return budgetRepository.findByIdFinance(idFinance);
    }
    
    public Budget updateBudget(Long id, Budget budgetDetails) {
        Budget budget = budgetRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Budget non trouvé avec l'ID: " + id));
        
        budget.setIdFinance(budgetDetails.getIdFinance());
        budget.setMontant(budgetDetails.getMontant());
        
        return budgetRepository.save(budget);
    }
    
    public void deleteBudget(Long id) {
        Budget budget = budgetRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Budget non trouvé avec l'ID: " + id));
        budgetRepository.delete(budget);
    }
    
    // Business Logic Methods
    public List<Budget> getBudgetsWithMinimumAmount(Double montantMin) {
        return budgetRepository.findBudgetsWithMinimumAmount(montantMin);
    }
    
    public List<Budget> getBudgetsByRange(Double montantMin, Double montantMax) {
        return budgetRepository.findByMontantBetween(montantMin, montantMax);
    }
    
    public Double getTotalBudget() {
        return budgetRepository.getTotalBudget();
    }
    
    public Long getTotalBudgetCount() {
        return budgetRepository.countAllBudgets();
    }
    
    public Optional<Budget> getBudgetWithCategories(Long id) {
        return budgetRepository.findByIdWithCategories(id);
    }
    
    public List<Budget> getBudgetsByCategorieName(String nomCategorie) {
        return budgetRepository.findBudgetsByCategorieName(nomCategorie);
    }
    
    public Budget ajusterBudget(Long id, Double nouveauMontant) {
        Budget budget = budgetRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Budget non trouvé avec l'ID: " + id));
        
        budget.mettreAJour(nouveauMontant);
        return budgetRepository.save(budget);
    }
    
    public void consulterBudget(Long id) {
        Budget budget = budgetRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Budget non trouvé avec l'ID: " + id));
        budget.consulter();
    }
}
