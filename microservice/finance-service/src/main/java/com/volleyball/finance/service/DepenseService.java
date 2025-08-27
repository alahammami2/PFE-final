package com.volleyball.finance.service;

import com.volleyball.finance.model.Depense;
import com.volleyball.finance.repository.DepenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class DepenseService {
    
    @Autowired
    private DepenseRepository depenseRepository;
    
    // CRUD Operations
    public Depense createDepense(Depense depense) {
        return depenseRepository.save(depense);
    }
    
    public List<Depense> getAllDepenses() {
        return depenseRepository.findAll();
    }
    
    public Optional<Depense> getDepenseById(Long id) {
        return depenseRepository.findById(id);
    }
    
    public Depense updateDepense(Long id, Depense depenseDetails) {
        Depense depense = depenseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Dépense non trouvée avec l'ID: " + id));
        
        depense.setMontant(depenseDetails.getMontant());
        depense.setDate(depenseDetails.getDate());
        depense.setDescription(depenseDetails.getDescription());
        depense.setStatut(depenseDetails.getStatut());
        depense.setCategorie(depenseDetails.getCategorie());
        
        return depenseRepository.save(depense);
    }
    
    public void deleteDepense(Long id) {
        Depense depense = depenseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Dépense non trouvée avec l'ID: " + id));
        depenseRepository.delete(depense);
    }
    
    // Business Logic Methods
    public List<Depense> getDepensesByPeriode(LocalDate dateDebut, LocalDate dateFin) {
        return depenseRepository.findByDateBetween(dateDebut, dateFin);
    }
    
    public List<Depense> getDepensesWithMinimumAmount(Double montant) {
        return depenseRepository.findByMontantGreaterThan(montant);
    }
    
    public List<Depense> getDepensesByRange(Double montantMin, Double montantMax) {
        return depenseRepository.findByMontantBetween(montantMin, montantMax);
    }
    
    public List<Depense> searchDepensesByDescription(String description) {
        return depenseRepository.findByDescriptionContainingIgnoreCase(description);
    }
    
    public List<Depense> getDepensesByAnnee(int annee) {
        return depenseRepository.findByAnnee(annee);
    }
    
    public List<Depense> getDepensesByMoisEtAnnee(int mois, int annee) {
        return depenseRepository.findByMoisEtAnnee(mois, annee);
    }
    
    public Double getTotalMontantByPeriode(LocalDate dateDebut, LocalDate dateFin) {
        return depenseRepository.getTotalMontantByPeriode(dateDebut, dateFin);
    }
    
    public List<Depense> getRecentDepensesOrderByMontant(LocalDate dateDebut) {
        return depenseRepository.findRecentDepensesOrderByMontant(dateDebut);
    }
    
    public Depense modifierDepense(Long id, Double nouveauMontant, String nouvelleDescription) {
        Depense depense = depenseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Dépense non trouvée avec l'ID: " + id));
        
        depense.mettreAJour(nouveauMontant, nouvelleDescription);
        return depenseRepository.save(depense);
    }
    
    public void consulterDepense(Long id) {
        Depense depense = depenseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Dépense non trouvée avec l'ID: " + id));
        depense.consulter();
    }
}

