package com.volleyball.finance.controller;

import com.volleyball.finance.model.CategorieBudget;
import com.volleyball.finance.service.CategorieBudgetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/finance/categories-budget")
@CrossOrigin(origins = "*")
public class CategorieBudgetController {
    
    @Autowired
    private CategorieBudgetService categorieBudgetService;
    
    // CRUD Endpoints
    @PostMapping
    public ResponseEntity<CategorieBudget> createCategorieBudget(@Valid @RequestBody CategorieBudget categorieBudget) {
        try {
            CategorieBudget savedCategorie = categorieBudgetService.createCategorieBudget(categorieBudget);
            return new ResponseEntity<>(savedCategorie, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @GetMapping
    public ResponseEntity<List<CategorieBudget>> getAllCategoriesBudget() {
        try {
            List<CategorieBudget> categories = categorieBudgetService.getAllCategoriesBudget();
            if (categories.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(categories, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<CategorieBudget> getCategorieBudgetById(@PathVariable Long id) {
        Optional<CategorieBudget> categorie = categorieBudgetService.getCategorieBudgetById(id);
        return categorie.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                       .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<CategorieBudget> updateCategorieBudget(@PathVariable Long id, @Valid @RequestBody CategorieBudget categorieDetails) {
        try {
            CategorieBudget updatedCategorie = categorieBudgetService.updateCategorieBudget(id, categorieDetails);
            return new ResponseEntity<>(updatedCategorie, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteCategorieBudget(@PathVariable Long id) {
        try {
            categorieBudgetService.deleteCategorieBudget(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    // Business Logic Endpoints
    @GetMapping("/search")
    public ResponseEntity<List<CategorieBudget>> getCategoriesByNom(@RequestParam String nomCategorie) {
        try {
            List<CategorieBudget> categories = categorieBudgetService.getCategoriesByNom(nomCategorie);
            return new ResponseEntity<>(categories, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @GetMapping("/minimum-amount")
    public ResponseEntity<List<CategorieBudget>> getCategoriesWithMinimumAmount(@RequestParam Double montant) {
        try {
            List<CategorieBudget> categories = categorieBudgetService.getCategoriesWithMinimumAmount(montant);
            return new ResponseEntity<>(categories, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @GetMapping("/range")
    public ResponseEntity<List<CategorieBudget>> getCategoriesByRange(@RequestParam Double montantMin, @RequestParam Double montantMax) {
        try {
            List<CategorieBudget> categories = categorieBudgetService.getCategoriesByRange(montantMin, montantMax);
            return new ResponseEntity<>(categories, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @PutMapping("/{id}/ajuster")
    public ResponseEntity<CategorieBudget> ajusterMontantAlloue(@PathVariable Long id, @RequestParam Double nouveauMontant) {
        try {
            CategorieBudget categorie = categorieBudgetService.ajusterMontantAlloue(id, nouveauMontant);
            return new ResponseEntity<>(categorie, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

