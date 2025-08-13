package com.volleyball.authservice.controller;

import com.volleyball.authservice.dto.*;
import com.volleyball.authservice.model.User;
import com.volleyball.authservice.service.AuthService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private AuthService authService;

    /**
     * Endpoint de connexion
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse> login(@Valid @RequestBody LoginRequest loginRequest, 
                                           BindingResult bindingResult) {
        try {
            logger.info("Tentative de connexion pour l'email: {}", loginRequest.getEmail());

            // Validation des erreurs de binding
            if (bindingResult.hasErrors()) {
                String errors = bindingResult.getFieldErrors().stream()
                    .map(error -> error.getField() + ": " + error.getDefaultMessage())
                    .collect(Collectors.joining(", "));
                
                logger.warn("Erreurs de validation lors de la connexion: {}", errors);
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Erreurs de validation: " + errors));
            }

            AuthResponse authResponse = authService.login(loginRequest);
            
            logger.info("Connexion réussie pour l'utilisateur: {}", loginRequest.getEmail());
            return ResponseEntity.ok(ApiResponse.success("Connexion réussie", authResponse));

        } catch (RuntimeException e) {
            logger.error("Erreur lors de la connexion: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            logger.error("Erreur interne lors de la connexion: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Erreur interne du serveur"));
        }
    }

    /**
     * Endpoint de création d'utilisateur
     */
    @PostMapping("/create-user")
    public ResponseEntity<ApiResponse> createUser(@Valid @RequestBody CreateUserRequest createUserRequest,
                                                BindingResult bindingResult) {
        try {
            logger.info("Tentative de création d'utilisateur avec l'email: {}", createUserRequest.getEmail());

            // Validation des erreurs de binding
            if (bindingResult.hasErrors()) {
                String errors = bindingResult.getFieldErrors().stream()
                    .map(error -> error.getField() + ": " + error.getDefaultMessage())
                    .collect(Collectors.joining(", "));
                
                logger.warn("Erreurs de validation lors de la création d'utilisateur: {}", errors);
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Erreurs de validation: " + errors));
            }

            User user = authService.createUser(createUserRequest);

            // Création de la réponse sans le mot de passe
            UserResponse userResponse = new UserResponse(
                user.getId(),
                user.getNom(),
                user.getPrenom(),
                user.getEmail(),
                user.getRole(),
                user.getActif(),
                user.getDateCreation()
            );

            logger.info("Utilisateur créé avec succès: {}", user.getEmail());
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Utilisateur créé avec succès", userResponse));

        } catch (RuntimeException e) {
            logger.error("Erreur lors de la création d'utilisateur: {}", e.getMessage());
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            logger.error("Erreur interne lors de la création d'utilisateur: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Erreur interne du serveur"));
        }
    }

    /**
     * Endpoint de modification d'utilisateur
     */
    @PutMapping("/users/{userId}")
    public ResponseEntity<ApiResponse> updateUser(@PathVariable Long userId,
                                                @Valid @RequestBody UpdateUserRequest updateUserRequest,
                                                BindingResult bindingResult) {
        try {
            logger.info("Tentative de modification d'utilisateur avec l'ID: {}", userId);

            // Validation des erreurs de binding
            if (bindingResult.hasErrors()) {
                String errors = bindingResult.getFieldErrors().stream()
                    .map(error -> error.getField() + ": " + error.getDefaultMessage())
                    .collect(Collectors.joining(", "));
                
                logger.warn("Erreurs de validation lors de la modification d'utilisateur: {}", errors);
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Erreurs de validation: " + errors));
            }

            User user = authService.updateUser(userId, updateUserRequest);

            // Création de la réponse sans le mot de passe
            UserResponse userResponse = new UserResponse(
                user.getId(),
                user.getNom(),
                user.getPrenom(),
                user.getEmail(),
                user.getRole(),
                user.getActif(),
                user.getDateCreation()
            );

            logger.info("Utilisateur modifié avec succès: {}", user.getEmail());
            return ResponseEntity.ok(ApiResponse.success("Utilisateur modifié avec succès", userResponse));

        } catch (RuntimeException e) {
            logger.error("Erreur lors de la modification d'utilisateur: {}", e.getMessage());
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            logger.error("Erreur interne lors de la modification d'utilisateur: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Erreur interne du serveur"));
        }
    }

    /**
     * Endpoint de suppression d'utilisateur
     */
    @DeleteMapping("/users/{userId}")
    public ResponseEntity<ApiResponse> deleteUser(@PathVariable Long userId) {
        try {
            logger.info("Tentative de suppression d'utilisateur avec l'ID: {}", userId);

            authService.deleteUser(userId);

            logger.info("Utilisateur supprimé avec succès: {}", userId);
            return ResponseEntity.ok(ApiResponse.success("Utilisateur supprimé avec succès"));

        } catch (RuntimeException e) {
            logger.error("Erreur lors de la suppression d'utilisateur: {}", e.getMessage());
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            logger.error("Erreur interne lors de la suppression d'utilisateur: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Erreur interne du serveur"));
        }
    }

    /**
     * Endpoint de récupération d'un utilisateur par ID
     */
    @GetMapping("/users/{userId}")
    public ResponseEntity<ApiResponse> getUserById(@PathVariable Long userId) {
        try {
            logger.info("Récupération d'utilisateur avec l'ID: {}", userId);

            User user = authService.getUserById(userId);

            // Création de la réponse sans le mot de passe
            UserResponse userResponse = new UserResponse(
                user.getId(),
                user.getNom(),
                user.getPrenom(),
                user.getEmail(),
                user.getRole(),
                user.getActif(),
                user.getDateCreation()
            );

            logger.info("Utilisateur récupéré avec succès: {}", user.getEmail());
            return ResponseEntity.ok(ApiResponse.success("Utilisateur récupéré avec succès", userResponse));

        } catch (RuntimeException e) {
            logger.error("Erreur lors de la récupération d'utilisateur: {}", e.getMessage());
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            logger.error("Erreur interne lors de la récupération d'utilisateur: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Erreur interne du serveur"));
        }
    }

    /**
     * Endpoint de récupération de tous les utilisateurs actifs
     */
    @GetMapping("/users")
    public ResponseEntity<ApiResponse> getAllUsers() {
        try {
            logger.info("Récupération de tous les utilisateurs actifs");

            List<User> users = authService.getAllActiveUsers();

            // Création de la réponse sans les mots de passe
            List<UserResponse> userResponses = users.stream()
                .map(user -> new UserResponse(
                    user.getId(),
                    user.getNom(),
                    user.getPrenom(),
                    user.getEmail(),
                    user.getRole(),
                    user.getActif(),
                    user.getDateCreation()
                ))
                .collect(Collectors.toList());

            logger.info("Utilisateurs récupérés avec succès: {}", userResponses.size());
            return ResponseEntity.ok(ApiResponse.success("Utilisateurs récupérés avec succès", userResponses));

        } catch (RuntimeException e) {
            logger.error("Erreur lors de la récupération des utilisateurs: {}", e.getMessage());
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            logger.error("Erreur interne lors de la récupération des utilisateurs: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Erreur interne du serveur"));
        }
    }

    /**
     * Endpoint de vérification de l'existence d'un email
     */
    @GetMapping("/check-email")
    public ResponseEntity<ApiResponse> checkEmail(@RequestParam String email) {
        try {
            boolean exists = authService.emailExists(email);
            return ResponseEntity.ok(ApiResponse.success("Vérification effectuée", exists));
        } catch (Exception e) {
            logger.error("Erreur lors de la vérification de l'email: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Erreur interne du serveur"));
        }
    }

    /**
     * Endpoint de test pour vérifier que le service fonctionne
     */
    @GetMapping("/health")
    public ResponseEntity<ApiResponse> health() {
        return ResponseEntity.ok(ApiResponse.success("Service d'authentification opérationnel"));
    }

    /**
     * Endpoint de debug pour lister les utilisateurs (à supprimer en production)
     */
    @GetMapping("/debug/users")
    public ResponseEntity<ApiResponse> debugUsers() {
        try {
            // Cet endpoint ne devrait être utilisé qu'en développement
            return ResponseEntity.ok(ApiResponse.success("Debug endpoint - voir les logs"));
        } catch (Exception e) {
            logger.error("Erreur lors du debug: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Erreur interne du serveur"));
        }
    }
}
