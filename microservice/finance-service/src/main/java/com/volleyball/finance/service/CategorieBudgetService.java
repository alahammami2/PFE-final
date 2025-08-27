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
    
    public CategorieBudget updateCategorieBudget(Long id, CategorieBudget categorieDetails) {
        CategorieBudget categorie = categorieBudgetRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Catégorie budget non trouvée avec l'ID: " + id));
        
        // Update simple fields
        categorie.setDate(categorieDetails.getDate());
        categorie.setDescription(categorieDetails.getDescription());
        categorie.setMontant(categorieDetails.getMontant());
        categorie.setCategorie(categorieDetails.getCategorie());
        
        return categorieBudgetRepository.save(categorie);
    }
    
    public void deleteCategorieBudget(Long id) {
        CategorieBudget categorie = categorieBudgetRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Catégorie budget non trouvée avec l'ID: " + id));
        categorieBudgetRepository.delete(categorie);
    }
    
    // Business Logic Methods
    public List<CategorieBudget> getCategoriesByNom(String nomCategorie) {
        return categorieBudgetRepository.findByCategorieContainingIgnoreCase(nomCategorie);
    }
    
    public List<CategorieBudget> getCategoriesWithMinimumAmount(Double montant) {
        return categorieBudgetRepository.findByMontantGreaterThan(montant);
    }
    
    public List<CategorieBudget> getCategoriesByRange(Double montantMin, Double montantMax) {
        return categorieBudgetRepository.findByMontantBetween(montantMin, montantMax);
    }
    
    public CategorieBudget ajusterMontantAlloue(Long id, Double nouveauMontant) {
        CategorieBudget categorie = categorieBudgetRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Catégorie budget non trouvée avec l'ID: " + id));
        
        categorie.mettreAJour(nouveauMontant);
        return categorieBudgetRepository.save(categorie);
    }
}

