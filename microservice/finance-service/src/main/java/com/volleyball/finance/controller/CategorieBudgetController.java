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
    
    @GetMapping("/by-id-categorie")
    public ResponseEntity<CategorieBudget> getCategorieBudgetByIdCategorie(@RequestParam Integer idCategorie) {
        Optional<CategorieBudget> categorie = categorieBudgetService.getCategorieBudgetByIdCategorie(idCategorie);
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
    
    @GetMapping("/budget/{budgetId}")
    public ResponseEntity<List<CategorieBudget>> getCategoriesByBudget(@PathVariable Long budgetId) {
        try {
            List<CategorieBudget> categories = categorieBudgetService.getCategoriesByBudget(budgetId);
            return new ResponseEntity<>(categories, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @GetMapping("/budget/{budgetId}/ordered")
    public ResponseEntity<List<CategorieBudget>> getCategoriesByBudgetOrderByMontant(@PathVariable Long budgetId) {
        try {
            List<CategorieBudget> categories = categorieBudgetService.getCategoriesByBudgetOrderByMontant(budgetId);
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
    
    @GetMapping("/budget/{budgetId}/total")
    public ResponseEntity<Double> getTotalMontantAlloueByBudget(@PathVariable Long budgetId) {
        try {
            Double total = categorieBudgetService.getTotalMontantAlloueByBudget(budgetId);
            return new ResponseEntity<>(total, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @GetMapping("/{id}/with-depenses")
    public ResponseEntity<CategorieBudget> getCategorieWithDepenses(@PathVariable Long id) {
        Optional<CategorieBudget> categorie = categorieBudgetService.getCategorieWithDepenses(id);
        return categorie.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                       .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
    
    @GetMapping("/with-remaining-budget")
    public ResponseEntity<List<CategorieBudget>> getCategoriesWithRemainingBudget() {
        try {
            List<CategorieBudget> categories = categorieBudgetService.getCategoriesWithRemainingBudget();
            return new ResponseEntity<>(categories, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @GetMapping("/budget/{budgetId}/count")
    public ResponseEntity<Long> countCategoriesByBudget(@PathVariable Long budgetId) {
        try {
            Long count = categorieBudgetService.countCategoriesByBudget(budgetId);
            return new ResponseEntity<>(count, HttpStatus.OK);
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
