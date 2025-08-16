package com.volleyball.adminrequestservice.controller;

import com.volleyball.adminrequestservice.model.AdminRequest;
import com.volleyball.adminrequestservice.model.RequestStatus;
import com.volleyball.adminrequestservice.model.RequestType;
import com.volleyball.adminrequestservice.model.RequestPriority;
import com.volleyball.adminrequestservice.service.AdminRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.Map;

/**
 * Contrôleur REST pour la gestion des demandes administratives
 */
@RestController
@RequestMapping("/api/admin-requests")
@CrossOrigin(origins = "*")
public class AdminRequestController {

    @Autowired
    private AdminRequestService adminRequestService;

    /**
     * Créer une nouvelle demande administrative
     */
    @PostMapping
    public ResponseEntity<AdminRequest> createRequest(@Valid @RequestBody AdminRequest request) {
        try {
            AdminRequest createdRequest = adminRequestService.createRequest(request);
            return new ResponseEntity<>(createdRequest, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Récupérer toutes les demandes administratives
     */
    @GetMapping
    public ResponseEntity<List<AdminRequest>> getAllRequests() {
        try {
            List<AdminRequest> requests = adminRequestService.getAllRequests();
            return new ResponseEntity<>(requests, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Récupérer une demande par ID
     */
    @GetMapping("/by-id")
    public ResponseEntity<AdminRequest> getRequestById(@RequestParam("id") Long id) {
        try {
            Optional<AdminRequest> request = adminRequestService.getRequestById(id);
            if (request.isPresent()) {
                return new ResponseEntity<>(request.get(), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Récupérer les demandes par demandeur
     */
    @GetMapping("/requester")
    public ResponseEntity<List<AdminRequest>> getRequestsByRequester(@RequestParam("requesterId") Long requesterId) {
        try {
            List<AdminRequest> requests = adminRequestService.getRequestsByRequester(requesterId);
            return new ResponseEntity<>(requests, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Récupérer les demandes par statut
     */
    @GetMapping("/by-status")
    public ResponseEntity<List<AdminRequest>> getRequestsByStatus(@RequestParam("status") RequestStatus status) {
        try {
            List<AdminRequest> requests = adminRequestService.getRequestsByStatus(status);
            return new ResponseEntity<>(requests, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Récupérer les demandes par type
     */
    @GetMapping("/by-type")
    public ResponseEntity<List<AdminRequest>> getRequestsByType(@RequestParam("type") RequestType type) {
        try {
            List<AdminRequest> requests = adminRequestService.getRequestsByType(type);
            return new ResponseEntity<>(requests, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Récupérer les demandes par priorité
     */
    @GetMapping("/by-priority")
    public ResponseEntity<List<AdminRequest>> getRequestsByPriority(@RequestParam("priority") RequestPriority priority) {
        try {
            List<AdminRequest> requests = adminRequestService.getRequestsByPriority(priority);
            return new ResponseEntity<>(requests, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Récupérer les demandes en attente
     */
    @GetMapping("/pending")
    public ResponseEntity<List<AdminRequest>> getPendingRequests() {
        try {
            List<AdminRequest> requests = adminRequestService.getPendingRequests();
            return new ResponseEntity<>(requests, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Récupérer les demandes urgentes en attente
     */
    @GetMapping("/urgent")
    public ResponseEntity<List<AdminRequest>> getUrgentPendingRequests() {
        try {
            List<AdminRequest> requests = adminRequestService.getUrgentPendingRequests();
            return new ResponseEntity<>(requests, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Récupérer les demandes récentes
     */
    @GetMapping("/recent")
    public ResponseEntity<List<AdminRequest>> getRecentRequests(@RequestParam(value = "days", defaultValue = "7") int days) {
        try {
            List<AdminRequest> requests = adminRequestService.getRecentRequests(days);
            return new ResponseEntity<>(requests, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Rechercher des demandes
     */
    @GetMapping("/search")
    public ResponseEntity<List<AdminRequest>> searchRequests(@RequestParam("searchTerm") String searchTerm) {
        try {
            List<AdminRequest> requests = adminRequestService.searchRequests(searchTerm);
            return new ResponseEntity<>(requests, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Récupérer les demandes avec budget
     */
    @GetMapping("/with-budget")
    public ResponseEntity<List<AdminRequest>> getRequestsWithBudget() {
        try {
            List<AdminRequest> requests = adminRequestService.getRequestsWithBudget();
            return new ResponseEntity<>(requests, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Mettre à jour une demande
     */
    @PutMapping("/update")
    public ResponseEntity<AdminRequest> updateRequest(@RequestParam("id") Long id, @Valid @RequestBody AdminRequest request) {
        try {
            AdminRequest updatedRequest = adminRequestService.updateRequest(id, request);
            return new ResponseEntity<>(updatedRequest, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Soumettre une demande
     */
    @PutMapping("/submit")
    public ResponseEntity<AdminRequest> submitRequest(@RequestParam("id") Long id) {
        try {
            AdminRequest submittedRequest = adminRequestService.submitRequest(id);
            return new ResponseEntity<>(submittedRequest, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Annuler une demande
     */
    @PutMapping("/cancel")
    public ResponseEntity<AdminRequest> cancelRequest(@RequestParam("id") Long id) {
        try {
            AdminRequest cancelledRequest = adminRequestService.cancelRequest(id);
            return new ResponseEntity<>(cancelledRequest, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Marquer une demande comme terminée
     */
    @PutMapping("/complete")
    public ResponseEntity<AdminRequest> completeRequest(@RequestParam("id") Long id) {
        try {
            AdminRequest completedRequest = adminRequestService.completeRequest(id);
            return new ResponseEntity<>(completedRequest, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Supprimer une demande
     */
    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteRequest(@RequestParam("id") Long id) {
        try {
            adminRequestService.deleteRequest(id);
            return new ResponseEntity<>("Demande supprimée avec succès", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Erreur lors de la suppression", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Récupérer les statistiques des demandes
     */
    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getRequestStatistics() {
        try {
            Map<String, Object> statistics = adminRequestService.getRequestStatistics();
            return new ResponseEntity<>(statistics, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Récupérer les compteurs du tableau de bord
     */
    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Long>> getDashboardCounts() {
        try {
            Map<String, Long> counts = adminRequestService.getDashboardCounts();
            return new ResponseEntity<>(counts, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Compter les demandes par statut pour un demandeur
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countRequesterRequestsByStatus(@RequestParam("requesterId") Long requesterId, 
                                                              @RequestParam("status") RequestStatus status) {
        try {
            Long count = adminRequestService.countRequesterRequestsByStatus(requesterId, status);
            return new ResponseEntity<>(count, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
