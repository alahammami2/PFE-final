package com.volleyball.authservice.repository;

import com.volleyball.authservice.model.Role;
import com.volleyball.authservice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Trouve un utilisateur par son email
     */
    Optional<User> findByEmail(String email);

    /**
     * Vérifie si un email existe déjà
     */
    boolean existsByEmail(String email);

    /**
     * Trouve tous les utilisateurs actifs
     */
    List<User> findByActifTrue();

    /**
     * Trouve tous les utilisateurs par rôle
     */
    List<User> findByRole(Role role);

    /**
     * Trouve tous les utilisateurs actifs par rôle
     */
    List<User> findByRoleAndActifTrue(Role role);

    /**
     * Compte le nombre d'utilisateurs par rôle
     */
    @Query("SELECT COUNT(u) FROM User u WHERE u.role = :role AND u.actif = true")
    long countByRoleAndActifTrue(@Param("role") Role role);

    /**
     * Trouve les utilisateurs par nom ou prénom (recherche insensible à la casse)
     */
    @Query("SELECT u FROM User u WHERE " +
           "LOWER(u.nom) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(u.prenom) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<User> findByNomOrPrenomContainingIgnoreCase(@Param("searchTerm") String searchTerm);
}
