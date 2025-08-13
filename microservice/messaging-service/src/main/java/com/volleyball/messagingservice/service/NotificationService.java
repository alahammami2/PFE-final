package com.volleyball.messagingservice.service;

import com.volleyball.messagingservice.model.Notification;
import com.volleyball.messagingservice.model.NotificationType;
import com.volleyball.messagingservice.model.NotificationPriority;
import com.volleyball.messagingservice.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Map;
import java.util.HashMap;

/**
 * Service pour la gestion des notifications
 */
@Service
@Transactional
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    /**
     * Créer une nouvelle notification
     */
    public Notification createNotification(Notification notification) {
        if (notification.getPriority() == null) {
            notification.setPriority(NotificationPriority.NORMALE);
        }
        return notificationRepository.save(notification);
    }

    /**
     * Récupérer toutes les notifications
     */
    @Transactional(readOnly = true)
    public List<Notification> getAllNotifications() {
        return notificationRepository.findAll();
    }

    /**
     * Récupérer une notification par ID
     */
    @Transactional(readOnly = true)
    public Optional<Notification> getNotificationById(Long id) {
        return notificationRepository.findById(id);
    }

    /**
     * Récupérer les notifications d'un utilisateur
     */
    @Transactional(readOnly = true)
    public List<Notification> getUserNotifications(Long userId) {
        return notificationRepository.findByUserIdOrderByDateCreationDesc(userId);
    }

    /**
     * Récupérer les notifications non lues d'un utilisateur
     */
    @Transactional(readOnly = true)
    public List<Notification> getUnreadNotifications(Long userId) {
        return notificationRepository.findByUserIdAndReadFalseOrderByDateCreationDesc(userId);
    }

    /**
     * Récupérer les notifications lues d'un utilisateur
     */
    @Transactional(readOnly = true)
    public List<Notification> getReadNotifications(Long userId) {
        return notificationRepository.findByUserIdAndReadTrueOrderByDateCreationDesc(userId);
    }

    /**
     * Récupérer les notifications par type
     */
    @Transactional(readOnly = true)
    public List<Notification> getNotificationsByType(Long userId, NotificationType type) {
        return notificationRepository.findByUserIdAndTypeOrderByDateCreationDesc(userId, type);
    }

    /**
     * Récupérer les notifications par priorité
     */
    @Transactional(readOnly = true)
    public List<Notification> getNotificationsByPriority(Long userId, NotificationPriority priority) {
        return notificationRepository.findByUserIdAndPriorityOrderByDateCreationDesc(userId, priority);
    }

    /**
     * Récupérer les notifications actives (non expirées)
     */
    @Transactional(readOnly = true)
    public List<Notification> getActiveNotifications(Long userId) {
        return notificationRepository.findActiveNotifications(userId, LocalDateTime.now());
    }

    /**
     * Récupérer les notifications urgentes non lues
     */
    @Transactional(readOnly = true)
    public List<Notification> getUrgentUnreadNotifications(Long userId) {
        return notificationRepository.findUrgentUnreadNotifications(userId);
    }

    /**
     * Marquer une notification comme lue
     */
    public Notification markAsRead(Long notificationId) {
        Optional<Notification> notificationOpt = notificationRepository.findById(notificationId);
        if (notificationOpt.isPresent()) {
            Notification notification = notificationOpt.get();
            notification.markAsRead();
            return notificationRepository.save(notification);
        }
        throw new RuntimeException("Notification non trouvée avec l'ID: " + notificationId);
    }

    /**
     * Marquer toutes les notifications d'un utilisateur comme lues
     */
    public void markAllAsRead(Long userId) {
        List<Notification> unreadNotifications = getUnreadNotifications(userId);
        unreadNotifications.forEach(notification -> {
            notification.markAsRead();
            notificationRepository.save(notification);
        });
    }

    /**
     * Marquer une notification comme envoyée
     */
    public Notification markAsSent(Long notificationId) {
        Optional<Notification> notificationOpt = notificationRepository.findById(notificationId);
        if (notificationOpt.isPresent()) {
            Notification notification = notificationOpt.get();
            notification.markAsSent();
            return notificationRepository.save(notification);
        }
        throw new RuntimeException("Notification non trouvée avec l'ID: " + notificationId);
    }

    /**
     * Compter les notifications non lues d'un utilisateur
     */
    @Transactional(readOnly = true)
    public long countUnreadNotifications(Long userId) {
        return notificationRepository.countByUserIdAndReadFalse(userId);
    }

    /**
     * Rechercher des notifications
     */
    @Transactional(readOnly = true)
    public List<Notification> searchNotifications(Long userId, String searchTerm) {
        return notificationRepository.searchNotifications(userId, searchTerm);
    }

    /**
     * Récupérer les notifications par période
     */
    @Transactional(readOnly = true)
    public List<Notification> getNotificationsByPeriod(Long userId, LocalDateTime startDate, LocalDateTime endDate) {
        return notificationRepository.findByUserIdAndDateCreationBetween(userId, startDate, endDate);
    }

    /**
     * Récupérer les notifications non envoyées
     */
    @Transactional(readOnly = true)
    public List<Notification> getPendingNotifications() {
        return notificationRepository.findBySentFalseOrderByDateCreationAsc();
    }

    /**
     * Récupérer les notifications expirées
     */
    @Transactional(readOnly = true)
    public List<Notification> getExpiredNotifications() {
        return notificationRepository.findExpiredNotifications(LocalDateTime.now());
    }

    /**
     * Récupérer les notifications à envoyer par email
     */
    @Transactional(readOnly = true)
    public List<Notification> getNotificationsToSendByEmail() {
        return notificationRepository.findNotificationsToSendByEmail();
    }

    /**
     * Mettre à jour une notification
     */
    public Notification updateNotification(Long notificationId, Notification updatedNotification) {
        Optional<Notification> notificationOpt = notificationRepository.findById(notificationId);
        if (notificationOpt.isPresent()) {
            Notification notification = notificationOpt.get();
            notification.setTitle(updatedNotification.getTitle());
            notification.setMessage(updatedNotification.getMessage());
            notification.setType(updatedNotification.getType());
            notification.setPriority(updatedNotification.getPriority());
            notification.setDateExpiration(updatedNotification.getDateExpiration());
            notification.setMetadata(updatedNotification.getMetadata());
            return notificationRepository.save(notification);
        }
        throw new RuntimeException("Notification non trouvée avec l'ID: " + notificationId);
    }

    /**
     * Supprimer une notification
     */
    public void deleteNotification(Long notificationId) {
        if (notificationRepository.existsById(notificationId)) {
            notificationRepository.deleteById(notificationId);
        } else {
            throw new RuntimeException("Notification non trouvée avec l'ID: " + notificationId);
        }
    }

    /**
     * Supprimer les notifications expirées
     */
    public void cleanupExpiredNotifications() {
        List<Notification> expiredNotifications = getExpiredNotifications();
        expiredNotifications.forEach(notification -> 
            notificationRepository.deleteById(notification.getId())
        );
    }

    /**
     * Créer une notification de diffusion pour plusieurs utilisateurs
     */
    public List<Notification> createBroadcastNotification(List<Long> userIds, String title, 
                                                          String message, NotificationType type, 
                                                          NotificationPriority priority) {
        return userIds.stream()
                .map(userId -> {
                    Notification notification = new Notification(userId, title, message, type);
                    notification.setPriority(priority);
                    return createNotification(notification);
                })
                .toList();
    }

    /**
     * Obtenir les statistiques des notifications par type
     */
    @Transactional(readOnly = true)
    public Map<NotificationType, Long> getNotificationStatsByType(Long userId) {
        List<Object[]> results = notificationRepository.getNotificationStatsByType(userId);
        Map<NotificationType, Long> stats = new HashMap<>();
        
        for (Object[] result : results) {
            NotificationType type = (NotificationType) result[0];
            Long count = (Long) result[1];
            stats.put(type, count);
        }
        
        return stats;
    }

    /**
     * Obtenir les statistiques des notifications par priorité
     */
    @Transactional(readOnly = true)
    public Map<NotificationPriority, Long> getNotificationStatsByPriority(Long userId) {
        List<Object[]> results = notificationRepository.getNotificationStatsByPriority(userId);
        Map<NotificationPriority, Long> stats = new HashMap<>();
        
        for (Object[] result : results) {
            NotificationPriority priority = (NotificationPriority) result[0];
            Long count = (Long) result[1];
            stats.put(priority, count);
        }
        
        return stats;
    }

    /**
     * Créer une notification avec expiration
     */
    public Notification createNotificationWithExpiration(Long userId, String title, String message, 
                                                         NotificationType type, NotificationPriority priority, 
                                                         LocalDateTime expirationDate) {
        Notification notification = new Notification(userId, title, message, type);
        notification.setPriority(priority);
        notification.setDateExpiration(expirationDate);
        return createNotification(notification);
    }
}
