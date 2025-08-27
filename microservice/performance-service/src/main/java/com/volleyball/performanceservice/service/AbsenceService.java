package com.volleyball.performanceservice.service;

import com.volleyball.performanceservice.dto.CreateAbsenceRequest;
import com.volleyball.performanceservice.model.Absence;
import com.volleyball.performanceservice.model.Player;
import com.volleyball.performanceservice.model.StatutAbsence;
import com.volleyball.performanceservice.model.TypeAbsence;
import com.volleyball.performanceservice.repository.AbsenceRepository;
import com.volleyball.performanceservice.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service pour la gestion des absences
 */
@Service
@Transactional
public class AbsenceService {

    @Autowired
    private AbsenceRepository absenceRepository;

    @Autowired
    private PlayerRepository playerRepository;

    /**
     * Créer une nouvelle absence
     */
    public Absence createAbsence(CreateAbsenceRequest request) {
        // Résoudre le joueur soit par ID, soit par email
        Player player = null;
        if (request.getPlayerId() != null) {
            player = playerRepository.findById(request.getPlayerId()).orElse(null);
        }
        if (player == null && request.getEmail() != null && !request.getEmail().isEmpty()) {
            player = playerRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new RuntimeException("Joueur non trouvé avec l'email: " + request.getEmail()));
        }
        if (player == null) {
            throw new RuntimeException("Aucun joueur fourni: playerId et email sont manquants ou invalides");
        }

        // Vérifier la cohérence des dates
        if (request.getDateFin() != null && request.getDateFin().isBefore(request.getDateDebut())) {
            throw new RuntimeException("La date de fin ne peut pas être antérieure à la date de début");
        }

        // Vérifier les chevauchements d'absences
        if (request.getDateFin() != null && 
            absenceRepository.existsOverlappingAbsence(player.getId(), 0L, request.getDateDebut(), request.getDateFin())) {
            throw new RuntimeException("Cette absence chevauche avec une absence existante pour ce joueur");
        }

        // Création de l'absence
        Absence absence = new Absence();
        absence.setPlayer(player);
        absence.setDateDebut(request.getDateDebut());
        absence.setDateFin(request.getDateFin());
        absence.setTypeAbsence(request.getTypeAbsence());
        absence.setRaison(request.getRaison());
        absence.setJustifiee(request.getJustifiee() != null ? request.getJustifiee() : false);
        absence.setCommentaires(request.getCommentaires());
        absence.setStatut(StatutAbsence.EN_ATTENTE);

        return absenceRepository.save(absence);
    }

    /**
     * Obtenir toutes les absences
     */
    @Transactional(readOnly = true)
    public List<Absence> getAllAbsences() {
        return absenceRepository.findAll();
    }

    /**
     * Obtenir une absence par ID
     */
    @Transactional(readOnly = true)
    public Absence getAbsenceById(Long id) {
        return absenceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Absence non trouvée avec l'ID: " + id));
    }

    /**
     * Obtenir les absences d'un joueur
     */
    @Transactional(readOnly = true)
    public List<Absence> getAbsencesByPlayer(Long playerId) {
        return absenceRepository.findByPlayerId(playerId);
    }

    /**
     * Obtenir les absences par type
     */
    @Transactional(readOnly = true)
    public List<Absence> getAbsencesByType(TypeAbsence typeAbsence) {
        return absenceRepository.findByTypeAbsence(typeAbsence);
    }

    /**
     * Obtenir les absences par statut
     */
    @Transactional(readOnly = true)
    public List<Absence> getAbsencesByStatut(StatutAbsence statut) {
        return absenceRepository.findByStatut(statut);
    }

    /**
     * Obtenir les absences en cours
     */
    @Transactional(readOnly = true)
    public List<Absence> getAbsencesEnCours() {
        return absenceRepository.findAbsencesEnCours(LocalDate.now());
    }

    /**
     * Obtenir les absences en cours d'un joueur
     */
    @Transactional(readOnly = true)
    public List<Absence> getAbsencesEnCoursByPlayer(Long playerId) {
        return absenceRepository.findAbsencesEnCoursByPlayer(playerId, LocalDate.now());
    }

    /**
     * Obtenir les absences par période
     */
    @Transactional(readOnly = true)
    public List<Absence> getAbsencesByPeriode(LocalDate dateDebut, LocalDate dateFin) {
        return absenceRepository.findByPeriode(dateDebut, dateFin);
    }

    /**
     * Obtenir les absences d'un joueur par période
     */
    @Transactional(readOnly = true)
    public List<Absence> getAbsencesByPlayerAndPeriode(Long playerId, LocalDate dateDebut, LocalDate dateFin) {
        return absenceRepository.findByPlayerAndPeriode(playerId, dateDebut, dateFin);
    }

    /**
     * Obtenir les absences longues (plus de X jours)
     */
    @Transactional(readOnly = true)
    public List<Absence> getAbsencesLongues(int joursMinimum) {
        LocalDate dateLimite = LocalDate.now().minusDays(joursMinimum);
        return absenceRepository.findAbsencesLongues(joursMinimum, dateLimite);
    }

    /**
     * Mettre à jour une absence
     */
    public Absence updateAbsence(Long id, CreateAbsenceRequest request) {
        Absence absence = getAbsenceById(id);

        // Vérifier la cohérence des dates
        if (request.getDateFin() != null && request.getDateFin().isBefore(request.getDateDebut())) {
            throw new RuntimeException("La date de fin ne peut pas être antérieure à la date de début");
        }

        // Vérifier les chevauchements d'absences (exclure l'absence actuelle)
        if (request.getDateFin() != null && 
            absenceRepository.existsOverlappingAbsence(request.getPlayerId(), id, request.getDateDebut(), request.getDateFin())) {
            throw new RuntimeException("Cette absence chevauche avec une autre absence existante pour ce joueur");
        }

        // Mise à jour des champs
        absence.setDateDebut(request.getDateDebut());
        absence.setDateFin(request.getDateFin());
        absence.setTypeAbsence(request.getTypeAbsence());
        absence.setRaison(request.getRaison());
        if (request.getJustifiee() != null) {
            absence.setJustifiee(request.getJustifiee());
        }
        absence.setCommentaires(request.getCommentaires());

        return absenceRepository.save(absence);
    }

    /**
     * Approuver une absence
     */
    public Absence approuverAbsence(Long id, String commentaires) {
        Absence absence = getAbsenceById(id);
        absence.setStatut(StatutAbsence.APPROUVEE);
        if (commentaires != null) {
            absence.setCommentaires(commentaires);
        }
        return absenceRepository.save(absence);
    }

    /**
     * Refuser une absence
     */
    public Absence refuserAbsence(Long id, String commentaires) {
        Absence absence = getAbsenceById(id);
        absence.setStatut(StatutAbsence.REFUSEE);
        if (commentaires != null) {
            absence.setCommentaires(commentaires);
        }
        return absenceRepository.save(absence);
    }

    /**
     * Annuler une absence
     */
    public Absence annulerAbsence(Long id, String commentaires) {
        Absence absence = getAbsenceById(id);
        absence.setStatut(StatutAbsence.ANNULEE);
        if (commentaires != null) {
            absence.setCommentaires(commentaires);
        }
        return absenceRepository.save(absence);
    }

    /**
     * Terminer une absence (définir la date de fin)
     */
    public Absence terminerAbsence(Long id, LocalDate dateFin) {
        Absence absence = getAbsenceById(id);
        if (dateFin.isBefore(absence.getDateDebut())) {
            throw new RuntimeException("La date de fin ne peut pas être antérieure à la date de début");
        }
        absence.setDateFin(dateFin);
        return absenceRepository.save(absence);
    }

    /**
     * Supprimer une absence
     */
    public void deleteAbsence(Long id) {
        Absence absence = getAbsenceById(id);
        absenceRepository.delete(absence);
    }

    /**
     * Obtenir les statistiques des absences
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getAbsencesStatistics() {
        long totalCount = absenceRepository.count();
        long enAttenteCount = absenceRepository.countByStatut(StatutAbsence.EN_ATTENTE);
        long approuveesCount = absenceRepository.countByStatut(StatutAbsence.APPROUVEE);
        long refuseesCount = absenceRepository.countByStatut(StatutAbsence.REFUSEE);
        List<Object[]> repartitionTypes = absenceRepository.countByTypeAbsence();
        List<Object[]> joueursAvecPlusAbsences = absenceRepository.findPlayersWithMostAbsences();

        Map<String, Object> statistics = new HashMap<>();
        statistics.put("totalAbsences", totalCount);
        statistics.put("absencesEnAttente", enAttenteCount);
        statistics.put("absencesApprouvees", approuveesCount);
        statistics.put("absencesRefusees", refuseesCount);
        statistics.put("repartitionParType", repartitionTypes);
        statistics.put("joueursAvecPlusAbsences", joueursAvecPlusAbsences);
        
        return statistics;
    }

    /**
     * Obtenir les absences récurrentes d'un joueur
     */
    @Transactional(readOnly = true)
    public List<Absence> getAbsencesRecurrentes(Long playerId, TypeAbsence typeAbsence, int derniersMois) {
        LocalDate dateDebut = LocalDate.now().minusMonths(derniersMois);
        LocalDate dateFin = LocalDate.now();
        return absenceRepository.findAbsencesRecurrentes(playerId, typeAbsence, dateDebut, dateFin);
    }
}
