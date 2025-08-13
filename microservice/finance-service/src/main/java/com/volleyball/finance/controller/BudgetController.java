package com.volleyball.finance.controller;

import com.volleyball.finance.model.Budget;
import com.volleyball.finance.service.BudgetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/finance/budgets")
@CrossOrigin(origins = "*")
public class BudgetController {
    
    @Autowired
    private BudgetService budgetService;
    
    // CRUD Endpoints
    @PostMapping
    public ResponseEntity<Budget> createBudget(@Valid @RequestBody Budget budget) {
        try {
            Budget savedBudget = budgetService.createBudget(budget);
            return new ResponseEntity<>(savedBudget, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @GetMapping
    public ResponseEntity<List<Budget>> getAllBudgets() {
        try {
            List<Budget> budgets = budgetService.getAllBudgets();
            if (budgets.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(budgets, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Budget> getBudgetById(@PathVariable Long id) {
        Optional<Budget> budget = budgetService.getBudgetById(id);
        return budget.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                     .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
    
    @GetMapping("/by-id-finance")
    public ResponseEntity<Budget> getBudgetByIdFinance(@RequestParam String idFinance) {
        Optional<Budget> budget = budgetService.getBudgetByIdFinance(idFinance);
        return budget.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                     .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Budget> updateBudget(@PathVariable Long id, @Valid @RequestBody Budget budgetDetails) {
        try {
            Budget updatedBudget = budgetService.updateBudget(id, budgetDetails);
            return new ResponseEntity<>(updatedBudget, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteBudget(@PathVariable Long id) {
        try {
            budgetService.deleteBudget(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    // Business Logic Endpoints
    @GetMapping("/minimum-amount")
    public ResponseEntity<List<Budget>> getBudgetsWithMinimumAmount(@RequestParam Double montantMin) {
        try {
            List<Budget> budgets = budgetService.getBudgetsWithMinimumAmount(montantMin);
            return new ResponseEntity<>(budgets, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @GetMapping("/range")
    public ResponseEntity<List<Budget>> getBudgetsByRange(@RequestParam Double montantMin, @RequestParam Double montantMax) {
        try {
            List<Budget> budgets = budgetService.getBudgetsByRange(montantMin, montantMax);
            return new ResponseEntity<>(budgets, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @GetMapping("/total")
    public ResponseEntity<Double> getTotalBudget() {
        try {
            Double total = budgetService.getTotalBudget();
            return new ResponseEntity<>(total, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @GetMapping("/count")
    public ResponseEntity<Long> getTotalBudgetCount() {
        try {
            Long count = budgetService.getTotalBudgetCount();
            return new ResponseEntity<>(count, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @GetMapping("/{id}/with-categories")
    public ResponseEntity<Budget> getBudgetWithCategories(@PathVariable Long id) {
        Optional<Budget> budget = budgetService.getBudgetWithCategories(id);
        return budget.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                     .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
    
    @GetMapping("/by-category")
    public ResponseEntity<List<Budget>> getBudgetsByCategorieName(@RequestParam String nomCategorie) {
        try {
            List<Budget> budgets = budgetService.getBudgetsByCategorieName(nomCategorie);
            return new ResponseEntity<>(budgets, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @PutMapping("/{id}/ajuster")
    public ResponseEntity<Budget> ajusterBudget(@PathVariable Long id, @RequestParam Double nouveauMontant) {
        try {
            Budget budget = budgetService.ajusterBudget(id, nouveauMontant);
            return new ResponseEntity<>(budget, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @PostMapping("/{id}/consulter")
    public ResponseEntity<HttpStatus> consulterBudget(@PathVariable Long id) {
        try {
            budgetService.consulterBudget(id);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
