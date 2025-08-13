package com.volleyball.messagingservice.repository;

import com.volleyball.messagingservice.model.Notification;
import com.volleyball.messagingservice.model.NotificationType;
import com.volleyball.messagingservice.model.NotificationPriority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository pour la gestion des notifications
 */
@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    /**
     * Trouve toutes les notifications d'un utilisateur
     */
    List<Notification> findByUserIdOrderByDateCreationDesc(Long userId);

    /**
     * Trouve toutes les notifications non lues d'un utilisateur
     */
    List<Notification> findByUserIdAndReadFalseOrderByDateCreationDesc(Long userId);

    /**
     * Trouve toutes les notifications lues d'un utilisateur
     */
    List<Notification> findByUserIdAndReadTrueOrderByDateCreationDesc(Long userId);

    /**
     * Trouve les notifications par type
     */
    List<Notification> findByUserIdAndTypeOrderByDateCreationDesc(Long userId, NotificationType type);

    /**
     * Trouve les notifications par priorité
     */
    List<Notification> findByUserIdAndPriorityOrderByDateCreationDesc(Long userId, NotificationPriority priority);

    /**
     * Trouve les notifications non envoyées
     */
    List<Notification> findBySentFalseOrderByDateCreationAsc();

    /**
     * Trouve les notifications non expirées
     */
    @Query("SELECT n FROM Notification n WHERE n.userId = :userId AND " +
           "(n.dateExpiration IS NULL OR n.dateExpiration > :currentDate) " +
           "ORDER BY n.dateCreation DESC")
    List<Notification> findActiveNotifications(@Param("userId") Long userId, 
                                               @Param("currentDate") LocalDateTime currentDate);

    /**
     * Compte les notifications non lues d'un utilisateur
     */
    long countByUserIdAndReadFalse(Long userId);

    /**
     * Trouve les notifications urgentes non lues
     */
    @Query("SELECT n FROM Notification n WHERE n.userId = :userId AND " +
           "n.priority IN ('HAUTE', 'URGENTE', 'CRITIQUE') AND n.read = false " +
           "ORDER BY n.priority DESC, n.dateCreation DESC")
    List<Notification> findUrgentUnreadNotifications(@Param("userId") Long userId);

    /**
     * Trouve les notifications par période
     */
    @Query("SELECT n FROM Notification n WHERE n.userId = :userId AND " +
           "n.dateCreation BETWEEN :startDate AND :endDate " +
           "ORDER BY n.dateCreation DESC")
    List<Notification> findByUserIdAndDateCreationBetween(@Param("userId") Long userId,
                                                          @Param("startDate") LocalDateTime startDate,
                                                          @Param("endDate") LocalDateTime endDate);

    /**
     * Trouve les notifications expirées
     */
    @Query("SELECT n FROM Notification n WHERE n.dateExpiration IS NOT NULL AND " +
           "n.dateExpiration < :currentDate")
    List<Notification> findExpiredNotifications(@Param("currentDate") LocalDateTime currentDate);

    /**
     * Trouve les notifications à envoyer par email
     */
    @Query("SELECT n FROM Notification n WHERE n.sent = true AND n.emailSent = false AND " +
           "n.priority IN ('HAUTE', 'URGENTE', 'CRITIQUE')")
    List<Notification> findNotificationsToSendByEmail();

    /**
     * Recherche de notifications par contenu
     */
    @Query("SELECT n FROM Notification n WHERE n.userId = :userId AND " +
           "(LOWER(n.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(n.message) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) " +
           "ORDER BY n.dateCreation DESC")
    List<Notification> searchNotifications(@Param("userId") Long userId, 
                                           @Param("searchTerm") String searchTerm);

    /**
     * Statistiques des notifications par type
     */
    @Query("SELECT n.type, COUNT(n) FROM Notification n WHERE n.userId = :userId GROUP BY n.type")
    List<Object[]> getNotificationStatsByType(@Param("userId") Long userId);

    /**
     * Statistiques des notifications par priorité
     */
    @Query("SELECT n.priority, COUNT(n) FROM Notification n WHERE n.userId = :userId GROUP BY n.priority")
    List<Object[]> getNotificationStatsByPriority(@Param("userId") Long userId);
}
