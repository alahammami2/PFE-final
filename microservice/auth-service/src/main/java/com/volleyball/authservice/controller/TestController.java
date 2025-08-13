package com.volleyball.authservice.controller;

import com.volleyball.authservice.dto.ApiResponse;
import com.volleyball.authservice.model.User;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping
@CrossOrigin(origins = "*", maxAge = 3600)
public class TestController {

    /**
     * Endpoint accessible à tous les utilisateurs authentifiés
     */
    @GetMapping("/profile")
    public ResponseEntity<ApiResponse> getProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        
        return ResponseEntity.ok(ApiResponse.success("Profil récupéré", user));
    }

    /**
     * Endpoint accessible uniquement aux administrateurs
     */
    @GetMapping("/admin/dashboard")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> getAdminDashboard() {
        return ResponseEntity.ok(ApiResponse.success("Tableau de bord administrateur"));
    }

    /**
     * Endpoint accessible aux coaches et administrateurs
     */
    @GetMapping("/coach/team")
    @PreAuthorize("hasRole('ADMIN') or hasRole('COACH')")
    public ResponseEntity<ApiResponse> getTeamInfo() {
        return ResponseEntity.ok(ApiResponse.success("Informations de l'équipe"));
    }

    /**
     * Endpoint accessible aux joueurs, coaches et administrateurs
     */
    @GetMapping("/joueur/stats")
    @PreAuthorize("hasRole('ADMIN') or hasRole('COACH') or hasRole('JOUEUR')")
    public ResponseEntity<ApiResponse> getPlayerStats() {
        return ResponseEntity.ok(ApiResponse.success("Statistiques du joueur"));
    }

    /**
     * Endpoint de test pour vérifier l'authentification
     */
    @GetMapping("/test/auth")
    public ResponseEntity<ApiResponse> testAuth() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        String authorities = authentication.getAuthorities().toString();
        
        return ResponseEntity.ok(ApiResponse.success("Authentification vérifiée", 
            "Utilisateur: " + username + ", Rôles: " + authorities));
    }


}
