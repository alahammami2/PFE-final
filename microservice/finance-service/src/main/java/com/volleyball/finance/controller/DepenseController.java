package com.volleyball.finance.controller;

import com.volleyball.finance.model.Depense;
import com.volleyball.finance.service.DepenseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/finance/depenses")
@CrossOrigin(origins = "*")
public class DepenseController {
    
    @Autowired
    private DepenseService depenseService;
    
    // CRUD Endpoints
    @PostMapping
    public ResponseEntity<Depense> createDepense(@Valid @RequestBody Depense depense) {
        try {
            Depense savedDepense = depenseService.createDepense(depense);
            return new ResponseEntity<>(savedDepense, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @GetMapping
    public ResponseEntity<List<Depense>> getAllDepenses() {
        try {
            List<Depense> depenses = depenseService.getAllDepenses();
            if (depenses.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(depenses, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Depense> getDepenseById(@PathVariable Long id) {
        Optional<Depense> depense = depenseService.getDepenseById(id);
        return depense.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                     .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Depense> updateDepense(@PathVariable Long id, @Valid @RequestBody Depense depenseDetails) {
        try {
            Depense updatedDepense = depenseService.updateDepense(id, depenseDetails);
            return new ResponseEntity<>(updatedDepense, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteDepense(@PathVariable Long id) {
        try {
            depenseService.deleteDepense(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    // Business Logic Endpoints
    @GetMapping("/periode")
    public ResponseEntity<List<Depense>> getDepensesByPeriode(@RequestParam String dateDebut, @RequestParam String dateFin) {
        try {
            LocalDate debut = LocalDate.parse(dateDebut);
            LocalDate fin = LocalDate.parse(dateFin);
            List<Depense> depenses = depenseService.getDepensesByPeriode(debut, fin);
            return new ResponseEntity<>(depenses, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @GetMapping("/minimum-amount")
    public ResponseEntity<List<Depense>> getDepensesWithMinimumAmount(@RequestParam Double montant) {
        try {
            List<Depense> depenses = depenseService.getDepensesWithMinimumAmount(montant);
            return new ResponseEntity<>(depenses, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @GetMapping("/range")
    public ResponseEntity<List<Depense>> getDepensesByRange(@RequestParam Double montantMin, @RequestParam Double montantMax) {
        try {
            List<Depense> depenses = depenseService.getDepensesByRange(montantMin, montantMax);
            return new ResponseEntity<>(depenses, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @GetMapping("/search")
    public ResponseEntity<List<Depense>> searchDepensesByDescription(@RequestParam String description) {
        try {
            List<Depense> depenses = depenseService.searchDepensesByDescription(description);
            return new ResponseEntity<>(depenses, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @GetMapping("/annee/{annee}")
    public ResponseEntity<List<Depense>> getDepensesByAnnee(@PathVariable int annee) {
        try {
            List<Depense> depenses = depenseService.getDepensesByAnnee(annee);
            return new ResponseEntity<>(depenses, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @GetMapping("/mois/{mois}/annee/{annee}")
    public ResponseEntity<List<Depense>> getDepensesByMoisEtAnnee(@PathVariable int mois, @PathVariable int annee) {
        try {
            List<Depense> depenses = depenseService.getDepensesByMoisEtAnnee(mois, annee);
            return new ResponseEntity<>(depenses, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @GetMapping("/periode/total")
    public ResponseEntity<Double> getTotalMontantByPeriode(@RequestParam String dateDebut, @RequestParam String dateFin) {
        try {
            LocalDate debut = LocalDate.parse(dateDebut);
            LocalDate fin = LocalDate.parse(dateFin);
            Double total = depenseService.getTotalMontantByPeriode(debut, fin);
            return new ResponseEntity<>(total, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @GetMapping("/recent")
    public ResponseEntity<List<Depense>> getRecentDepensesOrderByMontant(@RequestParam String dateDebut) {
        try {
            LocalDate debut = LocalDate.parse(dateDebut);
            List<Depense> depenses = depenseService.getRecentDepensesOrderByMontant(debut);
            return new ResponseEntity<>(depenses, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @PutMapping("/{id}/modifier")
    public ResponseEntity<Depense> modifierDepense(@PathVariable Long id, @RequestParam Double nouveauMontant, @RequestParam String nouvelleDescription) {
        try {
            Depense depense = depenseService.modifierDepense(id, nouveauMontant, nouvelleDescription);
            return new ResponseEntity<>(depense, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @PostMapping("/{id}/consulter")
    public ResponseEntity<HttpStatus> consulterDepense(@PathVariable Long id) {
        try {
            depenseService.consulterDepense(id);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

