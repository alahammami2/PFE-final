package com.volleyball.adminrequestservice.repository;

import com.volleyball.adminrequestservice.model.AdminRequest;
import com.volleyball.adminrequestservice.model.RequestPriority;
import com.volleyball.adminrequestservice.model.RequestStatus;
import com.volleyball.adminrequestservice.model.RequestType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository pour la gestion des demandes administratives
 */
@Repository
public interface AdminRequestRepository extends JpaRepository<AdminRequest, Long> {

    // Recherche par demandeur
    List<AdminRequest> findByRequesterId(Long requesterId);

    // Recherche par statut
    List<AdminRequest> findByStatus(RequestStatus status);

    // Recherche par type
    List<AdminRequest> findByType(RequestType type);

    // Recherche par priorité
    List<AdminRequest> findByPriority(RequestPriority priority);

    // Recherche par demandeur et statut
    List<AdminRequest> findByRequesterIdAndStatus(Long requesterId, RequestStatus status);

    // Demandes en attente (soumises ou en cours)
    @Query("SELECT ar FROM AdminRequest ar WHERE ar.status IN ('SOUMISE', 'EN_COURS') ORDER BY ar.priority DESC, ar.createdAt ASC")
    List<AdminRequest> findPendingRequests();

    // Demandes urgentes non traitées
    @Query("SELECT ar FROM AdminRequest ar WHERE ar.priority IN ('URGENTE', 'CRITIQUE') AND ar.status NOT IN ('APPROUVEE', 'REJETEE', 'TERMINEE', 'ANNULEE') ORDER BY ar.createdAt ASC")
    List<AdminRequest> findUrgentPendingRequests();

    // Demandes par période
    @Query("SELECT ar FROM AdminRequest ar WHERE ar.createdAt BETWEEN :startDate AND :endDate ORDER BY ar.createdAt DESC")
    List<AdminRequest> findByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    // Champs supprimés: dateNeeded, assignedTo, approvedAt, processedAt

    // Statistiques par statut
    @Query("SELECT ar.status, COUNT(ar) FROM AdminRequest ar GROUP BY ar.status")
    List<Object[]> getRequestCountByStatus();

    // Statistiques par type
    @Query("SELECT ar.type, COUNT(ar) FROM AdminRequest ar GROUP BY ar.type")
    List<Object[]> getRequestCountByType();

    // Statistiques par priorité
    @Query("SELECT ar.priority, COUNT(ar) FROM AdminRequest ar GROUP BY ar.priority")
    List<Object[]> getRequestCountByPriority();

    // Demandes récentes (derniers X jours)
    @Query("SELECT ar FROM AdminRequest ar WHERE ar.createdAt >= :sinceDate ORDER BY ar.createdAt DESC")
    List<AdminRequest> findRecentRequests(@Param("sinceDate") LocalDateTime sinceDate);

    // Recherche textuelle dans description
    @Query("SELECT ar FROM AdminRequest ar WHERE LOWER(ar.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')) ORDER BY ar.createdAt DESC")
    List<AdminRequest> searchRequests(@Param("searchTerm") String searchTerm);

    // Demandes avec budget
    @Query("SELECT ar FROM AdminRequest ar WHERE ar.budgetRequested IS NOT NULL AND ar.budgetRequested > 0 ORDER BY ar.budgetRequested DESC")
    List<AdminRequest> findRequestsWithBudget();

    // Somme des budgets demandés par statut
    @Query("SELECT ar.status, SUM(ar.budgetRequested) FROM AdminRequest ar WHERE ar.budgetRequested IS NOT NULL GROUP BY ar.status")
    List<Object[]> getBudgetSumByStatus();

    // Métriques basées sur processedAt/approvedAt supprimées

    // Demandes par demandeur et type
    List<AdminRequest> findByRequesterIdAndType(Long requesterId, RequestType type);

    // Compter les demandes par statut pour un demandeur
    @Query("SELECT COUNT(ar) FROM AdminRequest ar WHERE ar.requesterId = :requesterId AND ar.status = :status")
    Long countByRequesterIdAndStatus(@Param("requesterId") Long requesterId, @Param("status") RequestStatus status);
}
