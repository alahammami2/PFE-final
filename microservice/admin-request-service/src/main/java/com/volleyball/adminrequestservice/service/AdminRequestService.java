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
        request.setUpdatedAt(LocalDateTime.now());
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
            request.setTitle(updatedRequest.getTitle());
            request.setDescription(updatedRequest.getDescription());
            request.setType(updatedRequest.getType());
            request.setPriority(updatedRequest.getPriority());
            request.setBudgetRequested(updatedRequest.getBudgetRequested());
            request.setDateNeeded(updatedRequest.getDateNeeded());
            request.setUpdatedAt(LocalDateTime.now());
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
                request.setUpdatedAt(LocalDateTime.now());
                return adminRequestRepository.save(request);
            }
            throw new RuntimeException("Seules les demandes en brouillon peuvent être soumises");
        }
        throw new RuntimeException("Demande non trouvée avec l'ID: " + id);
    }

    public AdminRequest assignRequest(Long id, Long assignedTo) {
        Optional<AdminRequest> requestOpt = adminRequestRepository.findById(id);
        if (requestOpt.isPresent()) {
            AdminRequest request = requestOpt.get();
            request.assign(assignedTo);
            request.setUpdatedAt(LocalDateTime.now());
            return adminRequestRepository.save(request);
        }
        throw new RuntimeException("Demande non trouvée avec l'ID: " + id);
    }

    public AdminRequest approveRequest(Long id, Long approvedBy, String comments) {
        Optional<AdminRequest> requestOpt = adminRequestRepository.findById(id);
        if (requestOpt.isPresent()) {
            AdminRequest request = requestOpt.get();
            request.approve(approvedBy);
            if (comments != null && !comments.trim().isEmpty()) {
                request.setAdminComments(comments);
            }
            request.setUpdatedAt(LocalDateTime.now());
            return adminRequestRepository.save(request);
        }
        throw new RuntimeException("Demande non trouvée avec l'ID: " + id);
    }

    public AdminRequest rejectRequest(Long id, String reason, Long rejectedBy) {
        Optional<AdminRequest> requestOpt = adminRequestRepository.findById(id);
        if (requestOpt.isPresent()) {
            AdminRequest request = requestOpt.get();
            request.reject(reason, rejectedBy);
            request.setUpdatedAt(LocalDateTime.now());
            return adminRequestRepository.save(request);
        }
        throw new RuntimeException("Demande non trouvée avec l'ID: " + id);
    }

    public AdminRequest cancelRequest(Long id) {
        Optional<AdminRequest> requestOpt = adminRequestRepository.findById(id);
        if (requestOpt.isPresent()) {
            AdminRequest request = requestOpt.get();
            request.setStatus(RequestStatus.ANNULEE);
            request.setUpdatedAt(LocalDateTime.now());
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
                request.setUpdatedAt(LocalDateTime.now());
                return adminRequestRepository.save(request);
            }
            throw new RuntimeException("Seules les demandes approuvées peuvent être marquées comme terminées");
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

    public List<AdminRequest> getAssignedRequests(Long assignedTo) {
        return adminRequestRepository.findByAssignedTo(assignedTo);
    }

    public List<AdminRequest> getRequesterRequestsByStatus(Long requesterId, RequestStatus status) {
        return adminRequestRepository.findByRequesterIdAndStatus(requesterId, status);
    }

    public List<AdminRequest> getAssignedRequestsByStatus(Long assignedTo, RequestStatus status) {
        return adminRequestRepository.findByAssignedToAndStatus(assignedTo, status);
    }

    public List<AdminRequest> getPendingRequests() {
        return adminRequestRepository.findPendingRequests();
    }

    public List<AdminRequest> getUrgentPendingRequests() {
        return adminRequestRepository.findUrgentPendingRequests();
    }

    public List<AdminRequest> getRequestsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return adminRequestRepository.findByDateRange(startDate, endDate);
    }

    public List<AdminRequest> getRequestsNeedingActionByDeadline(LocalDateTime deadline) {
        return adminRequestRepository.findRequestsNeedingActionByDeadline(deadline);
    }

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

    public List<AdminRequest> getApprovedRequestsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return adminRequestRepository.findApprovedRequestsByDateRange(startDate, endDate);
    }

    public List<AdminRequest> getRequesterRequestsByType(Long requesterId, RequestType type) {
        return adminRequestRepository.findByRequesterIdAndType(requesterId, type);
    }

    public Long countRequesterRequestsByStatus(Long requesterId, RequestStatus status) {
        return adminRequestRepository.countByRequesterIdAndStatus(requesterId, status);
    }

    public List<AdminRequest> getHighPriorityAssignedRequests(Long assignedTo) {
        return adminRequestRepository.findHighPriorityAssignedRequests(assignedTo);
    }

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

        // Temps moyen de traitement
        Double avgProcessingTime = adminRequestRepository.getAverageProcessingTimeInHours();
        stats.put("averageProcessingTimeHours", avgProcessingTime != null ? avgProcessingTime : 0.0);

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
