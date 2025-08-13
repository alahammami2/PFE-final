package com.volleyball.finance.service;

import com.volleyball.finance.model.CategorieBudget;
import com.volleyball.finance.repository.CategorieBudgetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CategorieBudgetService {
    
    @Autowired
    private CategorieBudgetRepository categorieBudgetRepository;
    
    // CRUD Operations
    public CategorieBudget createCategorieBudget(CategorieBudget categorieBudget) {
        return categorieBudgetRepository.save(categorieBudget);
    }
    
    public List<CategorieBudget> getAllCategoriesBudget() {
        return categorieBudgetRepository.findAll();
    }
    
    public Optional<CategorieBudget> getCategorieBudgetById(Long id) {
        return categorieBudgetRepository.findById(id);
    }
    
    public Optional<CategorieBudget> getCategorieBudgetByIdCategorie(Integer idCategorie) {
        return categorieBudgetRepository.findByIdCategorie(idCategorie);
    }
    
    public CategorieBudget updateCategorieBudget(Long id, CategorieBudget categorieDetails) {
        CategorieBudget categorie = categorieBudgetRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Catégorie budget non trouvée avec l'ID: " + id));
        
        categorie.setIdCategorie(categorieDetails.getIdCategorie());
        categorie.setNomCategorie(categorieDetails.getNomCategorie());
        categorie.setMontantAlloue(categorieDetails.getMontantAlloue());
        
        return categorieBudgetRepository.save(categorie);
    }
    
    public void deleteCategorieBudget(Long id) {
        CategorieBudget categorie = categorieBudgetRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Catégorie budget non trouvée avec l'ID: " + id));
        categorieBudgetRepository.delete(categorie);
    }
    
    // Business Logic Methods
    public List<CategorieBudget> getCategoriesByNom(String nomCategorie) {
        return categorieBudgetRepository.findByNomCategorieContainingIgnoreCase(nomCategorie);
    }
    
    public List<CategorieBudget> getCategoriesByBudget(Long budgetId) {
        return categorieBudgetRepository.findByBudgetId(budgetId);
    }
    
    public List<CategorieBudget> getCategoriesByBudgetOrderByMontant(Long budgetId) {
        return categorieBudgetRepository.findByBudgetIdOrderByMontantDesc(budgetId);
    }
    
    public List<CategorieBudget> getCategoriesWithMinimumAmount(Double montant) {
        return categorieBudgetRepository.findByMontantAlloueGreaterThan(montant);
    }
    
    public List<CategorieBudget> getCategoriesByRange(Double montantMin, Double montantMax) {
        return categorieBudgetRepository.findByMontantAlloueBetween(montantMin, montantMax);
    }
    
    public Double getTotalMontantAlloueByBudget(Long budgetId) {
        return categorieBudgetRepository.getTotalMontantAlloueByBudget(budgetId);
    }
    
    public Optional<CategorieBudget> getCategorieWithDepenses(Long id) {
        return categorieBudgetRepository.findByIdWithDepenses(id);
    }
    
    public List<CategorieBudget> getCategoriesWithRemainingBudget() {
        return categorieBudgetRepository.findCategoriesWithRemainingBudget();
    }
    
    public Long countCategoriesByBudget(Long budgetId) {
        return categorieBudgetRepository.countCategoriesByBudget(budgetId);
    }
    
    public CategorieBudget ajusterMontantAlloue(Long id, Double nouveauMontant) {
        CategorieBudget categorie = categorieBudgetRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Catégorie budget non trouvée avec l'ID: " + id));
        
        categorie.mettreAJour(nouveauMontant);
        return categorieBudgetRepository.save(categorie);
    }
}
