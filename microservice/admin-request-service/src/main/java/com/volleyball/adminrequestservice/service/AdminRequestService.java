package com.volleyball.adminrequestservice.service;

import com.volleyball.adminrequestservice.model.AdminRequest;
import com.volleyball.adminrequestservice.model.RequestStatus;
import com.volleyball.adminrequestservice.model.RequestType;
import com.volleyball.adminrequestservice.model.RequestPriority;
import com.volleyball.adminrequestservice.repository.AdminRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Map;
import java.util.HashMap;

/**
 * Service pour la gestion des demandes administratives
 */
@Service
@Transactional
public class AdminRequestService {

    @Autowired
    private AdminRequestRepository adminRequestRepository;

    // CRUD Operations
    public AdminRequest createRequest(AdminRequest request) {
        request.setStatus(RequestStatus.BROUILLON);
        request.setCreatedAt(LocalDateTime.now());
        return adminRequestRepository.save(request);
    }

    public List<AdminRequest> getAllRequests() {
        return adminRequestRepository.findAll();
    }

    public Optional<AdminRequest> getRequestById(Long id) {
        return adminRequestRepository.findById(id);
    }

    public AdminRequest updateRequest(Long id, AdminRequest updatedRequest) {
        Optional<AdminRequest> existingRequest = adminRequestRepository.findById(id);
        if (existingRequest.isPresent()) {
            AdminRequest request = existingRequest.get();
            request.setDescription(updatedRequest.getDescription());
            request.setType(updatedRequest.getType());
            request.setPriority(updatedRequest.getPriority());
            request.setBudgetRequested(updatedRequest.getBudgetRequested());
            // Champs supprimés: title, dateNeeded, updatedAt
            return adminRequestRepository.save(request);
        }
        throw new RuntimeException("Demande non trouvée avec l'ID: " + id);
    }

    public void deleteRequest(Long id) {
        adminRequestRepository.deleteById(id);
    }

    // Business Logic Methods
    public AdminRequest submitRequest(Long id) {
        Optional<AdminRequest> requestOpt = adminRequestRepository.findById(id);
        if (requestOpt.isPresent()) {
            AdminRequest request = requestOpt.get();
            if (request.getStatus() == RequestStatus.BROUILLON) {
                request.setStatus(RequestStatus.SOUMISE);
                return adminRequestRepository.save(request);
            }
            throw new RuntimeException("Seules les demandes en brouillon peuvent être soumises");
        }
        throw new RuntimeException("Demande non trouvée avec l'ID: " + id);
    }

    // assignRequest supprimée car assignedTo supprimé

    // approveRequest supprimée car approvedAt/By et adminComments supprimés

    // rejectRequest supprimée car rejectionReason et processedAt supprimés

    public AdminRequest cancelRequest(Long id) {
        Optional<AdminRequest> requestOpt = adminRequestRepository.findById(id);
        if (requestOpt.isPresent()) {
            AdminRequest request = requestOpt.get();
            request.setStatus(RequestStatus.ANNULEE);
            return adminRequestRepository.save(request);
        }
        throw new RuntimeException("Demande non trouvée avec l'ID: " + id);
    }

    public AdminRequest completeRequest(Long id) {
        Optional<AdminRequest> requestOpt = adminRequestRepository.findById(id);
        if (requestOpt.isPresent()) {
            AdminRequest request = requestOpt.get();
            if (request.getStatus() == RequestStatus.APPROUVEE) {
                request.setStatus(RequestStatus.TERMINEE);
                return adminRequestRepository.save(request);
            }
            throw new RuntimeException("Seules les demandes approuvées peuvent être marquées comme terminées");
        }
        throw new RuntimeException("Demande non trouvée avec l'ID: " + id);
    }

    /**
     * Définir un statut arbitraire pour une demande (utilitaire pour le front)
     */
    public AdminRequest setRequestStatus(Long id, RequestStatus status) {
        Optional<AdminRequest> requestOpt = adminRequestRepository.findById(id);
        if (requestOpt.isPresent()) {
            AdminRequest request = requestOpt.get();
            request.setStatus(status);
            return adminRequestRepository.save(request);
        }
        throw new RuntimeException("Demande non trouvée avec l'ID: " + id);
    }

    // Query Methods
    public List<AdminRequest> getRequestsByRequester(Long requesterId) {
        return adminRequestRepository.findByRequesterId(requesterId);
    }

    public List<AdminRequest> getRequestsByStatus(RequestStatus status) {
        return adminRequestRepository.findByStatus(status);
    }

    public List<AdminRequest> getRequestsByType(RequestType type) {
        return adminRequestRepository.findByType(type);
    }

    public List<AdminRequest> getRequestsByPriority(RequestPriority priority) {
        return adminRequestRepository.findByPriority(priority);
    }

    // getAssignedRequests supprimée

    public List<AdminRequest> getRequesterRequestsByStatus(Long requesterId, RequestStatus status) {
        return adminRequestRepository.findByRequesterIdAndStatus(requesterId, status);
    }

    // getAssignedRequestsByStatus supprimée

    public List<AdminRequest> getPendingRequests() {
        return adminRequestRepository.findPendingRequests();
    }

    public List<AdminRequest> getUrgentPendingRequests() {
        return adminRequestRepository.findUrgentPendingRequests();
    }

    public List<AdminRequest> getRequestsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return adminRequestRepository.findByDateRange(startDate, endDate);
    }

    // getRequestsNeedingActionByDeadline supprimée car dateNeeded supprimé

    public List<AdminRequest> getRecentRequests(int days) {
        LocalDateTime sinceDate = LocalDateTime.now().minusDays(days);
        return adminRequestRepository.findRecentRequests(sinceDate);
    }

    public List<AdminRequest> searchRequests(String searchTerm) {
        return adminRequestRepository.searchRequests(searchTerm);
    }

    public List<AdminRequest> getRequestsWithBudget() {
        return adminRequestRepository.findRequestsWithBudget();
    }

    // getApprovedRequestsByDateRange supprimée car approvedAt supprimé

    public List<AdminRequest> getRequesterRequestsByType(Long requesterId, RequestType type) {
        return adminRequestRepository.findByRequesterIdAndType(requesterId, type);
    }

    public Long countRequesterRequestsByStatus(Long requesterId, RequestStatus status) {
        return adminRequestRepository.countByRequesterIdAndStatus(requesterId, status);
    }

    // getHighPriorityAssignedRequests supprimée

    // Statistics Methods
    public Map<String, Object> getRequestStatistics() {
        Map<String, Object> stats = new HashMap<>();
        
        // Statistiques par statut
        List<Object[]> statusStats = adminRequestRepository.getRequestCountByStatus();
        Map<String, Long> statusCounts = new HashMap<>();
        for (Object[] stat : statusStats) {
            statusCounts.put(stat[0].toString(), (Long) stat[1]);
        }
        stats.put("statusCounts", statusCounts);

        // Statistiques par type
        List<Object[]> typeStats = adminRequestRepository.getRequestCountByType();
        Map<String, Long> typeCounts = new HashMap<>();
        for (Object[] stat : typeStats) {
            typeCounts.put(stat[0].toString(), (Long) stat[1]);
        }
        stats.put("typeCounts", typeCounts);

        // Statistiques par priorité
        List<Object[]> priorityStats = adminRequestRepository.getRequestCountByPriority();
        Map<String, Long> priorityCounts = new HashMap<>();
        for (Object[] stat : priorityStats) {
            priorityCounts.put(stat[0].toString(), (Long) stat[1]);
        }
        stats.put("priorityCounts", priorityCounts);

        // Statistiques budgétaires
        List<Object[]> budgetStats = adminRequestRepository.getBudgetSumByStatus();
        Map<String, Double> budgetSums = new HashMap<>();
        for (Object[] stat : budgetStats) {
            budgetSums.put(stat[0].toString(), (Double) stat[1]);
        }
        stats.put("budgetSums", budgetSums);

        // Temps moyen de traitement supprimé

        // Nombre total de demandes
        stats.put("totalRequests", adminRequestRepository.count());

        return stats;
    }

    public Map<String, Long> getDashboardCounts() {
        Map<String, Long> counts = new HashMap<>();
        counts.put("pending", (long) getPendingRequests().size());
        counts.put("urgent", (long) getUrgentPendingRequests().size());
        counts.put("approved", (long) adminRequestRepository.findByStatus(RequestStatus.APPROUVEE).size());
        counts.put("rejected", (long) adminRequestRepository.findByStatus(RequestStatus.REJETEE).size());
        counts.put("total", adminRequestRepository.count());
        return counts;
    }
}
