package com.volleyball.messagingservice.controller;

import com.volleyball.messagingservice.model.Notification;
import com.volleyball.messagingservice.model.NotificationType;
import com.volleyball.messagingservice.model.NotificationPriority;
import com.volleyball.messagingservice.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Map;

/**
 * Contrôleur REST pour la gestion des notifications
 */
@RestController
@RequestMapping("/api/messaging/notifications")
@CrossOrigin(origins = "*")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    /**
     * Créer une nouvelle notification
     */
    @PostMapping
    public ResponseEntity<Notification> createNotification(@Valid @RequestBody Notification notification) {
        try {
            Notification createdNotification = notificationService.createNotification(notification);
            return new ResponseEntity<>(createdNotification, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Récupérer toutes les notifications
     */
    @GetMapping
    public ResponseEntity<List<Notification>> getAllNotifications() {
        try {
            List<Notification> notifications = notificationService.getAllNotifications();
            return new ResponseEntity<>(notifications, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Récupérer une notification par ID
     */
    @GetMapping("/by-id")
    public ResponseEntity<Notification> getNotificationById(@RequestParam("id") Long id) {
        try {
            Optional<Notification> notification = notificationService.getNotificationById(id);
            if (notification.isPresent()) {
                return new ResponseEntity<>(notification.get(), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Récupérer les notifications d'un utilisateur
     */
    @GetMapping("/user")
    public ResponseEntity<List<Notification>> getUserNotifications(@RequestParam("userId") Long userId) {
        try {
            List<Notification> notifications = notificationService.getUserNotifications(userId);
            return new ResponseEntity<>(notifications, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Récupérer les notifications non lues d'un utilisateur
     */
    @GetMapping("/user/unread")
    public ResponseEntity<List<Notification>> getUnreadNotifications(@RequestParam("userId") Long userId) {
        try {
            List<Notification> notifications = notificationService.getUnreadNotifications(userId);
            return new ResponseEntity<>(notifications, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Récupérer les notifications lues d'un utilisateur
     */
    @GetMapping("/user/read")
    public ResponseEntity<List<Notification>> getReadNotifications(@RequestParam("userId") Long userId) {
        try {
            List<Notification> notifications = notificationService.getReadNotifications(userId);
            return new ResponseEntity<>(notifications, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Récupérer les notifications par type
     */
    @GetMapping("/user/by-type")
    public ResponseEntity<List<Notification>> getNotificationsByType(@RequestParam("userId") Long userId, 
                                                                     @RequestParam("type") NotificationType type) {
        try {
            List<Notification> notifications = notificationService.getNotificationsByType(userId, type);
            return new ResponseEntity<>(notifications, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Récupérer les notifications par priorité
     */
    @GetMapping("/user/by-priority")
    public ResponseEntity<List<Notification>> getNotificationsByPriority(@RequestParam("userId") Long userId, 
                                                                          @RequestParam("priority") NotificationPriority priority) {
        try {
            List<Notification> notifications = notificationService.getNotificationsByPriority(userId, priority);
            return new ResponseEntity<>(notifications, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Récupérer les notifications actives (non expirées)
     */
    @GetMapping("/user/active")
    public ResponseEntity<List<Notification>> getActiveNotifications(@RequestParam("userId") Long userId) {
        try {
            List<Notification> notifications = notificationService.getActiveNotifications(userId);
            return new ResponseEntity<>(notifications, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Récupérer les notifications urgentes non lues
     */
    @GetMapping("/user/urgent")
    public ResponseEntity<List<Notification>> getUrgentUnreadNotifications(@RequestParam("userId") Long userId) {
        try {
            List<Notification> notifications = notificationService.getUrgentUnreadNotifications(userId);
            return new ResponseEntity<>(notifications, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Marquer une notification comme lue
     */
    @PutMapping("/mark-read")
    public ResponseEntity<Notification> markAsRead(@RequestParam("id") Long id) {
        try {
            Notification notification = notificationService.markAsRead(id);
            return new ResponseEntity<>(notification, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Marquer toutes les notifications d'un utilisateur comme lues
     */
    @PutMapping("/user/read-all")
    public ResponseEntity<Void> markAllAsRead(@RequestParam("userId") Long userId) {
        try {
            notificationService.markAllAsRead(userId);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Marquer une notification comme envoyée
     */
    @PutMapping("/mark-sent")
    public ResponseEntity<Notification> markAsSent(@RequestParam("id") Long id) {
        try {
            Notification notification = notificationService.markAsSent(id);
            return new ResponseEntity<>(notification, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Compter les notifications non lues d'un utilisateur
     */
    @GetMapping("/user/unread-count")
    public ResponseEntity<Long> countUnreadNotifications(@RequestParam("userId") Long userId) {
        try {
            long count = notificationService.countUnreadNotifications(userId);
            return new ResponseEntity<>(count, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Rechercher des notifications
     */
    @GetMapping("/user/search")
    public ResponseEntity<List<Notification>> searchNotifications(@RequestParam("userId") Long userId, 
                                                                  @RequestParam("searchTerm") String searchTerm) {
        try {
            List<Notification> notifications = notificationService.searchNotifications(userId, searchTerm);
            return new ResponseEntity<>(notifications, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Récupérer les notifications par période
     */
    @GetMapping("/user/period")
    public ResponseEntity<List<Notification>> getNotificationsByPeriod(@RequestParam("userId") Long userId,
                                                                        @RequestParam("startDate") String startDate,
                                                                        @RequestParam("endDate") String endDate) {
        try {
            LocalDateTime start = LocalDateTime.parse(startDate);
            LocalDateTime end = LocalDateTime.parse(endDate);
            List<Notification> notifications = notificationService.getNotificationsByPeriod(userId, start, end);
            return new ResponseEntity<>(notifications, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Récupérer les notifications non envoyées
     */
    @GetMapping("/pending")
    public ResponseEntity<List<Notification>> getPendingNotifications() {
        try {
            List<Notification> notifications = notificationService.getPendingNotifications();
            return new ResponseEntity<>(notifications, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Récupérer les notifications expirées
     */
    @GetMapping("/expired")
    public ResponseEntity<List<Notification>> getExpiredNotifications() {
        try {
            List<Notification> notifications = notificationService.getExpiredNotifications();
            return new ResponseEntity<>(notifications, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Récupérer les notifications à envoyer par email
     */
    @GetMapping("/email-pending")
    public ResponseEntity<List<Notification>> getNotificationsToSendByEmail() {
        try {
            List<Notification> notifications = notificationService.getNotificationsToSendByEmail();
            return new ResponseEntity<>(notifications, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Mettre à jour une notification
     */
    @PutMapping("/update")
    public ResponseEntity<Notification> updateNotification(@RequestParam("id") Long id, 
                                                           @Valid @RequestBody Notification notification) {
        try {
            Notification updatedNotification = notificationService.updateNotification(id, notification);
            return new ResponseEntity<>(updatedNotification, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Supprimer une notification
     */
    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteNotification(@RequestParam("id") Long id) {
        try {
            notificationService.deleteNotification(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Nettoyer les notifications expirées
     */
    @DeleteMapping("/cleanup-expired")
    public ResponseEntity<Void> cleanupExpiredNotifications() {
        try {
            notificationService.cleanupExpiredNotifications();
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Créer une notification de diffusion
     */
    @PostMapping("/broadcast")
    public ResponseEntity<List<Notification>> createBroadcastNotification(@RequestBody BroadcastNotificationRequest request) {
        try {
            List<Notification> notifications = notificationService.createBroadcastNotification(
                request.getUserIds(),
                request.getTitle(),
                request.getMessage(),
                request.getType(),
                request.getPriority()
            );
            return new ResponseEntity<>(notifications, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Créer une notification avec expiration
     */
    @PostMapping("/with-expiration")
    public ResponseEntity<Notification> createNotificationWithExpiration(@RequestBody NotificationWithExpirationRequest request) {
        try {
            Notification notification = notificationService.createNotificationWithExpiration(
                request.getUserId(),
                request.getTitle(),
                request.getMessage(),
                request.getType(),
                request.getPriority(),
                request.getExpirationDate()
            );
            return new ResponseEntity<>(notification, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Obtenir les statistiques des notifications par type
     */
    @GetMapping("/user/stats/type")
    public ResponseEntity<Map<NotificationType, Long>> getNotificationStatsByType(@RequestParam("userId") Long userId) {
        try {
            Map<NotificationType, Long> stats = notificationService.getNotificationStatsByType(userId);
            return new ResponseEntity<>(stats, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Obtenir les statistiques des notifications par priorité
     */
    @GetMapping("/user/stats/priority")
    public ResponseEntity<Map<NotificationPriority, Long>> getNotificationStatsByPriority(@RequestParam("userId") Long userId) {
        try {
            Map<NotificationPriority, Long> stats = notificationService.getNotificationStatsByPriority(userId);
            return new ResponseEntity<>(stats, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Classe interne pour les requêtes de diffusion de notifications
     */
    public static class BroadcastNotificationRequest {
        private List<Long> userIds;
        private String title;
        private String message;
        private NotificationType type;
        private NotificationPriority priority;

        // Getters et Setters
        public List<Long> getUserIds() { return userIds; }
        public void setUserIds(List<Long> userIds) { this.userIds = userIds; }

        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }

        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }

        public NotificationType getType() { return type; }
        public void setType(NotificationType type) { this.type = type; }

        public NotificationPriority getPriority() { return priority; }
        public void setPriority(NotificationPriority priority) { this.priority = priority; }
    }

    /**
     * Classe interne pour les requêtes de notification avec expiration
     */
    public static class NotificationWithExpirationRequest {
        private Long userId;
        private String title;
        private String message;
        private NotificationType type;
        private NotificationPriority priority;
        private LocalDateTime expirationDate;

        // Getters et Setters
        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }

        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }

        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }

        public NotificationType getType() { return type; }
        public void setType(NotificationType type) { this.type = type; }

        public NotificationPriority getPriority() { return priority; }
        public void setPriority(NotificationPriority priority) { this.priority = priority; }

        public LocalDateTime getExpirationDate() { return expirationDate; }
        public void setExpirationDate(LocalDateTime expirationDate) { this.expirationDate = expirationDate; }
    }
}
