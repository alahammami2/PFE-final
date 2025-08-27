package com.volleyball.performanceservice.service;

import com.volleyball.performanceservice.dto.CreatePlayerRequest;
import com.volleyball.performanceservice.model.Player;
import com.volleyball.performanceservice.model.Position;
import com.volleyball.performanceservice.model.StatutJoueur;
import com.volleyball.performanceservice.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service pour la gestion des joueurs
 */
@Service
@Transactional
public class PlayerService {

    @Autowired
    private PlayerRepository playerRepository;

    /**
     * Créer un nouveau joueur
     */
    public Player createPlayer(CreatePlayerRequest request) {
        // Vérifications d'unicité
        if (request.getEmail() != null && playerRepository.existsByEmail(request.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Un joueur avec cet email existe déjà: " + request.getEmail());
        }

        // numéro de maillot peut être dupliqué désormais (validation supprimée)

        // Création du joueur
        Player player = new Player();
        player.setNom(request.getNom());
        player.setPrenom(request.getPrenom());
        player.setEmail(request.getEmail());
        player.setDateNaissance(request.getDateNaissance());
        player.setPosition(request.getPosition());
        player.setNumeroMaillot(request.getNumeroMaillot());
        player.setTailleCm(request.getTailleCm());
        player.setPoidsKg(request.getPoidsKg());
        player.setSalaire(request.getSalaire());
        player.setStatut(request.getStatut() != null ? request.getStatut() : StatutJoueur.ACTIF);
        // dateDebutEquipe et actif supprimés

        try {
            return playerRepository.save(player);
        } catch (DataIntegrityViolationException ex) {
            // En cas de contrainte d'unicité au niveau DB
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Contrainte d'unicité violée pour l'email ou le numéro de maillot", ex);
        }
    }

    /**
     * Obtenir tous les joueurs actifs
     */
    @Transactional(readOnly = true)
    public List<Player> getAllActivePlayers() {
        return playerRepository.findByStatut(StatutJoueur.ACTIF);
    }

    /**
     * Obtenir tous les joueurs
     */
    @Transactional(readOnly = true)
    public List<Player> getAllPlayers() {
        return playerRepository.findAll();
    }

    /**
     * Obtenir un joueur par ID
     */
    @Transactional(readOnly = true)
    public Player getPlayerById(Long id) {
        return playerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Joueur non trouvé avec l'ID: " + id));
    }

    /**
     * Obtenir les joueurs par position
     */
    @Transactional(readOnly = true)
    public List<Player> getPlayersByPosition(Position position) {
        return playerRepository.findByPosition(position);
    }

    /**
     * Obtenir les joueurs par statut
     */
    @Transactional(readOnly = true)
    public List<Player> getPlayersByStatut(StatutJoueur statut) {
        return playerRepository.findByStatut(statut);
    }

    /**
     * Rechercher des joueurs par nom ou prénom
     */
    @Transactional(readOnly = true)
    public List<Player> searchPlayersByName(String searchTerm) {
        return playerRepository.findByNomContainingIgnoreCaseOrPrenomContainingIgnoreCase(searchTerm, searchTerm);
    }

    /**
     * Mettre à jour un joueur
     */
    public Player updatePlayer(Long id, CreatePlayerRequest request) {
        Player player = getPlayerById(id);

        // Vérifications d'unicité (exclure le joueur actuel)
        if (request.getEmail() != null && !request.getEmail().equals(player.getEmail()) 
            && playerRepository.existsByEmailAndIdNot(request.getEmail(), id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Un autre joueur avec cet email existe déjà: " + request.getEmail());
        }

        // numéro de maillot peut être dupliqué désormais (validation supprimée)

        // Mise à jour des champs
        player.setNom(request.getNom());
        player.setPrenom(request.getPrenom());
        player.setEmail(request.getEmail());
        player.setDateNaissance(request.getDateNaissance());
        player.setPosition(request.getPosition());
        player.setNumeroMaillot(request.getNumeroMaillot());
        player.setTailleCm(request.getTailleCm());
        player.setPoidsKg(request.getPoidsKg());
        player.setSalaire(request.getSalaire());
        if (request.getStatut() != null) {
            player.setStatut(request.getStatut());
        }
        // dateDebutEquipe supprimée

        return playerRepository.save(player);
    }

    /**
     * Mettre à jour un joueur par email (pratique pour intégration front)
     */
    public Player updateByEmail(String email, CreatePlayerRequest request) {
        if (email == null || email.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email requis");
        }
        Player player = playerRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Joueur non trouvé avec l'email: " + email));

        // Vérifier unicité si email changé
        if (request.getEmail() != null && !request.getEmail().equals(player.getEmail())
                && playerRepository.existsByEmail(request.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Un autre joueur avec cet email existe déjà: " + request.getEmail());
        }

        // Mise à jour des champs
        player.setNom(request.getNom());
        player.setPrenom(request.getPrenom());
        player.setEmail(request.getEmail());
        player.setDateNaissance(request.getDateNaissance());
        player.setPosition(request.getPosition());
        player.setNumeroMaillot(request.getNumeroMaillot());
        player.setTailleCm(request.getTailleCm());
        player.setPoidsKg(request.getPoidsKg());
        player.setSalaire(request.getSalaire());
        if (request.getStatut() != null) {
            player.setStatut(request.getStatut());
        }

        return playerRepository.save(player);
    }

    /**
     * Changer le statut d'un joueur
     */
    public Player changePlayerStatus(Long id, StatutJoueur nouveauStatut) {
        Player player = getPlayerById(id);
        player.setStatut(nouveauStatut);
        return playerRepository.save(player);
    }

    /**
     * Désactiver un joueur (soft delete)
     */
    public void deactivatePlayer(Long id) {
        Player player = getPlayerById(id);
        player.setStatut(StatutJoueur.INACTIF);
        playerRepository.save(player);
    }

    /**
     * Réactiver un joueur
     */
    public Player reactivatePlayer(Long id) {
        Player player = getPlayerById(id);
        player.setStatut(StatutJoueur.ACTIF);
        return playerRepository.save(player);
    }

    /**
     * Supprimer définitivement un joueur
     */
    public void deletePlayer(Long id) {
        Player player = getPlayerById(id);
        playerRepository.delete(player);
    }

    /**
     * Supprimer un joueur par email (si existe)
     */
    public void deleteByEmail(String email) {
        if (email == null || email.isBlank()) return;
        playerRepository.findByEmail(email).ifPresent(playerRepository::delete);
    }

    /**
     * Obtenir les statistiques des joueurs
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getPlayersStatistics() {
        long totalActifs = playerRepository.countByStatut(StatutJoueur.ACTIF);
        long totalBlesses = playerRepository.countByStatut(StatutJoueur.BLESSE);
        long totalSuspendus = playerRepository.countByStatut(StatutJoueur.SUSPENDU);
        List<Object[]> repartitionPositions = playerRepository.countPlayersByPosition();

        Map<String, Object> statistics = new HashMap<>();
        statistics.put("totalJoueursActifs", totalActifs);
        statistics.put("totalJoueursBlesses", totalBlesses);
        statistics.put("totalJoueursSuspendus", totalSuspendus);
        statistics.put("repartitionParPosition", repartitionPositions);
        
        return statistics;
    }

    /**
     * Obtenir les joueurs par tranche d'âge
     */
    @Transactional(readOnly = true)
    public List<Player> getPlayersByAgeRange(int ageMin, int ageMax) {
        LocalDate dateMax = LocalDate.now().minusYears(ageMin);
        LocalDate dateMin = LocalDate.now().minusYears(ageMax + 1);
        return playerRepository.findByAgeRange(dateMin, dateMax);
    }

    /**
     * Obtenir les joueurs avec numéro de maillot
     */
    @Transactional(readOnly = true)
    public List<Player> getPlayersWithJerseyNumber() {
        return playerRepository.findPlayersWithJerseyNumber();
    }
}
