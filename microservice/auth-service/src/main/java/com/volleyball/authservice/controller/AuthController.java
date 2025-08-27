package com.volleyball.authservice.controller;

import com.volleyball.authservice.dto.*;
import com.volleyball.authservice.model.User;
import com.volleyball.authservice.service.AuthService;
import jakarta.validation.Valid;
import com.volleyball.authservice.model.Role;
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
     * Endpoint de récupération de tous les utilisateurs avec leurs mots de passe (usage admin uniquement)
     */
    @GetMapping("/users/with-passwords")
    public ResponseEntity<ApiResponse> getAllUsersWithPasswords() {
        try {
            logger.warn("[SECURITY] Récupération de tous les utilisateurs avec mot de passe — à utiliser uniquement en DEV/ADMIN");

            List<User> users = authService.getAllUsers();

            List<UserWithPasswordResponse> userResponses = users.stream()
                .map(user -> new UserWithPasswordResponse(
                    user.getId(),
                    user.getNom(),
                    user.getPrenom(),
                    user.getEmail(),
                    user.getRole(),
                    user.getActif(),
                    user.getDateCreation(),
                    user.getTelephone(),
                    user.getSalaire(),
                    user.getMotDePasse()
                ))
                .collect(Collectors.toList());

            return ResponseEntity.ok(ApiResponse.success("Utilisateurs + mots de passe récupérés", userResponses));

        } catch (RuntimeException e) {
            logger.error("Erreur lors de la récupération des utilisateurs avec mot de passe: {}", e.getMessage());
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            logger.error("Erreur interne lors de la récupération des utilisateurs avec mot de passe: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Erreur interne du serveur"));
        }
    }

    /**
     * Compte des utilisateurs actifs (léger pour tableau de bord)
     */
    @GetMapping("/users/count")
    public ResponseEntity<ApiResponse> getActiveUsersCount() {
        try {
            logger.info("Calcul du nombre d'utilisateurs actifs");
            int count = 0;
            try {
                var users = authService.getAllActiveUsers();
                count = (users == null) ? 0 : users.size();
            } catch (Exception inner) {
                logger.warn("getAllActiveUsers a échoué, renvoi 0: {}", inner.getMessage());
            }
            return ResponseEntity.ok(ApiResponse.success("Nombre d'utilisateurs actifs", count));
        } catch (Exception e) {
            logger.error("Erreur lors du comptage des utilisateurs: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Erreur interne du serveur"));
        }
    }

    /**
     * Compte des joueurs actifs (role = JOUEUR)
     */
    @GetMapping("/users/count/joueurs")
    public ResponseEntity<ApiResponse> getActivePlayersCount() {
        try {
            logger.info("Calcul du nombre de joueurs actifs");
            long count = authService.countActiveUsersByRole(Role.JOUEUR);
            return ResponseEntity.ok(ApiResponse.success("Nombre de joueurs actifs", count));
        } catch (Exception e) {
            logger.error("Erreur lors du comptage des joueurs: {}", e.getMessage());
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

            // Réponse incluant le mot de passe en clair UNE SEULE FOIS (non stocké)
            CreateUserResult result = new CreateUserResult(
                user.getId(),
                user.getNom(),
                user.getPrenom(),
                user.getEmail(),
                user.getRole(),
                user.getActif(),
                user.getDateCreation(),
                user.getTelephone(),
                user.getSalaire(),
                createUserRequest.getMotDePasse()
            );

            logger.info("Utilisateur créé avec succès: {}", user.getEmail());
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Utilisateur créé avec succès", result));

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
                user.getDateCreation(),
                user.getTelephone(),
                user.getSalaire()
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
                user.getDateCreation(),
                user.getTelephone(),
                user.getSalaire()
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
     * Endpoint de récupération de tous les utilisateurs (actifs et inactifs)
     */
    @GetMapping("/users")
    public ResponseEntity<ApiResponse> getAllUsers() {
        try {
            logger.info("Récupération de tous les utilisateurs (actifs et inactifs)");

            List<User> users = authService.getAllUsers();

            // Inclure le mot de passe dans la réponse, comme demandé (DEV only)
            List<UserWithPasswordResponse> userResponses = users.stream()
                .map(user -> new UserWithPasswordResponse(
                    user.getId(),
                    user.getNom(),
                    user.getPrenom(),
                    user.getEmail(),
                    user.getRole(),
                    user.getActif(),
                    user.getDateCreation(),
                    user.getTelephone(),
                    user.getSalaire(),
                    user.getMotDePasse()
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
     * Endpoint de récupération d'un utilisateur par email
     */
    @GetMapping("/users/by-email")
    public ResponseEntity<ApiResponse> getUserByEmail(@RequestParam String email) {
        try {
            logger.info("Récupération d'utilisateur avec l'email: {}", email);

            User user = authService.getUserByEmail(email);

            UserResponse userResponse = new UserResponse(
                user.getId(),
                user.getNom(),
                user.getPrenom(),
                user.getEmail(),
                user.getRole(),
                user.getActif(),
                user.getDateCreation(),
                user.getTelephone(),
                user.getSalaire()
            );

            logger.info("Utilisateur récupéré avec succès: {}", user.getEmail());
            return ResponseEntity.ok(ApiResponse.success("Utilisateur récupéré avec succès", userResponse));

        } catch (RuntimeException e) {
            logger.error("Erreur lors de la récupération d'utilisateur par email: {}", e.getMessage());
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            logger.error("Erreur interne lors de la récupération d'utilisateur par email: {}", e.getMessage());
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
