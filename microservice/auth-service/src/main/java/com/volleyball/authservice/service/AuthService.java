package com.volleyball.authservice.service;

import com.volleyball.authservice.dto.AuthResponse;
import com.volleyball.authservice.dto.CreateUserRequest;
import com.volleyball.authservice.dto.LoginRequest;
import com.volleyball.authservice.dto.UpdateUserRequest;
import com.volleyball.authservice.model.User;
import com.volleyball.authservice.repository.UserRepository;
import com.volleyball.authservice.security.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * Authentifie un utilisateur et génère un token JWT
     */
    public AuthResponse login(LoginRequest loginRequest) {
        try {
            logger.debug("Tentative de connexion pour l'email: {}", loginRequest.getEmail());

            // Vérification si l'utilisateur existe
            if (!userRepository.existsByEmail(loginRequest.getEmail())) {
                logger.warn("Tentative de connexion avec un email inexistant: {}", loginRequest.getEmail());
                throw new RuntimeException("Email ou mot de passe incorrect");
            }

            // Récupération de l'utilisateur pour vérification
            User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

            logger.debug("Utilisateur trouvé: {} - Actif: {} - Rôle: {}",
                user.getEmail(), user.getActif(), user.getRole());

            if (!user.getActif()) {
                logger.warn("Tentative de connexion avec un compte inactif: {}", loginRequest.getEmail());
                throw new RuntimeException("Compte utilisateur inactif");
            }

            // Authentification avec Spring Security
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    loginRequest.getEmail(),
                    loginRequest.getMotDePasse()
                )
            );

            // Récupération de l'utilisateur authentifié
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            // L'utilisateur a déjà été récupéré plus haut, on le réutilise

            // Génération du token JWT avec des claims personnalisés
            Map<String, Object> extraClaims = new HashMap<>();
            extraClaims.put("userId", user.getId());
            extraClaims.put("role", user.getRole().getValue());
            extraClaims.put("nom", user.getNom());
            extraClaims.put("prenom", user.getPrenom());

            String token = jwtUtil.generateToken(userDetails, extraClaims);

            logger.info("Connexion réussie pour l'utilisateur: {}", user.getEmail());

            return new AuthResponse(
                token,
                user.getId(),
                user.getNom(),
                user.getPrenom(),
                user.getEmail(),
                user.getRole()
            );

        } catch (BadCredentialsException e) {
            logger.error("Échec de l'authentification pour l'email: {}", loginRequest.getEmail());
            throw new RuntimeException("Email ou mot de passe incorrect");
        } catch (Exception e) {
            logger.error("Erreur lors de la connexion: {}", e.getMessage());
            throw new RuntimeException("Erreur lors de la connexion: " + e.getMessage());
        }
    }

    /**
     * Crée un nouvel utilisateur
     */
    public User createUser(CreateUserRequest createUserRequest) {
        try {
            logger.debug("Création d'un nouvel utilisateur avec l'email: {}", createUserRequest.getEmail());

            // Vérification si l'email existe déjà
            if (userRepository.existsByEmail(createUserRequest.getEmail())) {
                throw new RuntimeException("Un utilisateur avec cet email existe déjà");
            }

            // Création du nouvel utilisateur
            User user = new User();
            user.setNom(createUserRequest.getNom());
            user.setPrenom(createUserRequest.getPrenom());
            user.setEmail(createUserRequest.getEmail());
            user.setMotDePasse(passwordEncoder.encode(createUserRequest.getMotDePasse()));
            user.setRole(createUserRequest.getRole());
            user.setActif(true);

            User savedUser = userRepository.save(user);

            logger.info("Utilisateur créé avec succès: {}", savedUser.getEmail());

            return savedUser;

        } catch (Exception e) {
            logger.error("Erreur lors de la création de l'utilisateur: {}", e.getMessage());
            throw new RuntimeException("Erreur lors de la création de l'utilisateur: " + e.getMessage());
        }
    }

    /**
     * Modifie un utilisateur existant
     */
    public User updateUser(Long userId, UpdateUserRequest updateUserRequest) {
        try {
            logger.debug("Modification de l'utilisateur avec l'ID: {}", userId);

            // Récupération de l'utilisateur existant
            User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec l'ID: " + userId));

            // Vérification si le nouvel email existe déjà (sauf pour l'utilisateur actuel)
            if (!existingUser.getEmail().equals(updateUserRequest.getEmail()) &&
                userRepository.existsByEmail(updateUserRequest.getEmail())) {
                throw new RuntimeException("Un utilisateur avec cet email existe déjà");
            }

            // Mise à jour des champs
            existingUser.setNom(updateUserRequest.getNom());
            existingUser.setPrenom(updateUserRequest.getPrenom());
            existingUser.setEmail(updateUserRequest.getEmail());
            existingUser.setRole(updateUserRequest.getRole());
            existingUser.setActif(updateUserRequest.getActif());

            User updatedUser = userRepository.save(existingUser);

            logger.info("Utilisateur modifié avec succès: {}", updatedUser.getEmail());

            return updatedUser;

        } catch (Exception e) {
            logger.error("Erreur lors de la modification de l'utilisateur: {}", e.getMessage());
            throw new RuntimeException("Erreur lors de la modification de l'utilisateur: " + e.getMessage());
        }
    }

    /**
     * Supprime un utilisateur (désactive le compte)
     */
    public void deleteUser(Long userId) {
        try {
            logger.debug("Suppression de l'utilisateur avec l'ID: {}", userId);

            // Récupération de l'utilisateur existant
            User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec l'ID: " + userId));

            // Désactivation du compte au lieu de suppression physique
            existingUser.setActif(false);
            userRepository.save(existingUser);

            logger.info("Utilisateur supprimé (désactivé) avec succès: {}", existingUser.getEmail());

        } catch (Exception e) {
            logger.error("Erreur lors de la suppression de l'utilisateur: {}", e.getMessage());
            throw new RuntimeException("Erreur lors de la suppression de l'utilisateur: " + e.getMessage());
        }
    }

    /**
     * Récupère un utilisateur par son ID
     */
    public User getUserById(Long userId) {
        try {
            logger.debug("Récupération de l'utilisateur avec l'ID: {}", userId);

            User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec l'ID: " + userId));

            logger.debug("Utilisateur trouvé: {}", user.getEmail());

            return user;

        } catch (Exception e) {
            logger.error("Erreur lors de la récupération de l'utilisateur: {}", e.getMessage());
            throw new RuntimeException("Erreur lors de la récupération de l'utilisateur: " + e.getMessage());
        }
    }

    /**
     * Récupère tous les utilisateurs actifs
     */
    public List<User> getAllActiveUsers() {
        try {
            logger.debug("Récupération de tous les utilisateurs actifs");

            List<User> users = userRepository.findByActifTrue();

            logger.debug("Nombre d'utilisateurs actifs trouvés: {}", users.size());

            return users;

        } catch (Exception e) {
            logger.error("Erreur lors de la récupération des utilisateurs: {}", e.getMessage());
            throw new RuntimeException("Erreur lors de la récupération des utilisateurs: " + e.getMessage());
        }
    }

    /**
     * Vérifie si un email existe déjà
     */
    public boolean emailExists(String email) {
        return userRepository.existsByEmail(email);
    }
}
