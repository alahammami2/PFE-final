package com.volleyball.performanceservice.repository;

import com.volleyball.performanceservice.model.Player;
import com.volleyball.performanceservice.model.Position;
import com.volleyball.performanceservice.model.StatutJoueur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository pour la gestion des joueurs
 */
@Repository
public interface PlayerRepository extends JpaRepository<Player, Long> {

    // Recherche par email
    Optional<Player> findByEmail(String email);

    // Recherche par numéro de maillot
    Optional<Player> findByNumeroMaillot(Integer numeroMaillot);

    // Recherche par nom et prénom
    List<Player> findByNomContainingIgnoreCaseOrPrenomContainingIgnoreCase(String nom, String prenom);

    // Joueurs actifs
    List<Player> findByActifTrue();

    // Joueurs par statut
    List<Player> findByStatut(StatutJoueur statut);

    // Joueurs par position
    List<Player> findByPosition(Position position);

    // Joueurs actifs par position
    List<Player> findByPositionAndActifTrue(Position position);

    // Joueurs par tranche d'âge
    @Query("SELECT p FROM Player p WHERE p.dateNaissance BETWEEN :dateDebut AND :dateFin AND p.actif = true")
    List<Player> findByAgeRange(@Param("dateDebut") LocalDate dateDebut, @Param("dateFin") LocalDate dateFin);

    // Joueurs avec numéro de maillot disponible
    @Query("SELECT p FROM Player p WHERE p.numeroMaillot IS NOT NULL AND p.actif = true ORDER BY p.numeroMaillot")
    List<Player> findPlayersWithJerseyNumber();

    // Statistiques des joueurs
    @Query("SELECT COUNT(p) FROM Player p WHERE p.actif = true")
    long countActivePlayer();

    @Query("SELECT COUNT(p) FROM Player p WHERE p.statut = :statut")
    long countByStatut(@Param("statut") StatutJoueur statut);

    @Query("SELECT p.position, COUNT(p) FROM Player p WHERE p.actif = true GROUP BY p.position")
    List<Object[]> countPlayersByPosition();

    // Vérification d'unicité
    boolean existsByEmail(String email);
    boolean existsByNumeroMaillot(Integer numeroMaillot);
    boolean existsByEmailAndIdNot(String email, Long id);
    boolean existsByNumeroMaillotAndIdNot(Integer numeroMaillot, Long id);
}
