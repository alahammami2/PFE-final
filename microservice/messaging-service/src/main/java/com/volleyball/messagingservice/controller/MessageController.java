package com.volleyball.messagingservice.controller;

import com.volleyball.messagingservice.model.Message;
import com.volleyball.messagingservice.model.MessageType;
import com.volleyball.messagingservice.model.MessagePriority;
import com.volleyball.messagingservice.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Contrôleur REST pour la gestion des messages
 */
@RestController
@RequestMapping("/api/messaging/messages")
@CrossOrigin(origins = "*")
public class MessageController {

    @Autowired
    private MessageService messageService;

    /**
     * Créer un nouveau message
     */
    @PostMapping
    public ResponseEntity<Message> createMessage(@Valid @RequestBody Message message) {
        try {
            Message createdMessage = messageService.createMessage(message);
            return new ResponseEntity<>(createdMessage, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Récupérer tous les messages
     */
    @GetMapping
    public ResponseEntity<List<Message>> getAllMessages() {
        try {
            List<Message> messages = messageService.getAllMessages();
            return new ResponseEntity<>(messages, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Récupérer un message par ID
     */
    @GetMapping("/by-id")
    public ResponseEntity<Message> getMessageById(@RequestParam("id") Long id) {
        try {
            Optional<Message> message = messageService.getMessageById(id);
            if (message.isPresent()) {
                return new ResponseEntity<>(message.get(), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Récupérer les messages reçus par un utilisateur
     */
    @GetMapping("/received")
    public ResponseEntity<List<Message>> getReceivedMessages(@RequestParam("userId") Long userId) {
        try {
            List<Message> messages = messageService.getReceivedMessages(userId);
            return new ResponseEntity<>(messages, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Récupérer les messages envoyés par un utilisateur
     */
    @GetMapping("/sent")
    public ResponseEntity<List<Message>> getSentMessages(@RequestParam("userId") Long userId) {
        try {
            List<Message> messages = messageService.getSentMessages(userId);
            return new ResponseEntity<>(messages, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Récupérer les messages non lus d'un utilisateur
     */
    @GetMapping("/unread")
    public ResponseEntity<List<Message>> getUnreadMessages(@RequestParam("userId") Long userId) {
        try {
            List<Message> messages = messageService.getUnreadMessages(userId);
            return new ResponseEntity<>(messages, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Récupérer les messages archivés d'un utilisateur
     */
    @GetMapping("/archived")
    public ResponseEntity<List<Message>> getArchivedMessages(@RequestParam("userId") Long userId) {
        try {
            List<Message> messages = messageService.getArchivedMessages(userId);
            return new ResponseEntity<>(messages, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Récupérer les messages actifs (non archivés) d'un utilisateur
     */
    @GetMapping("/active")
    public ResponseEntity<List<Message>> getActiveMessages(@RequestParam("userId") Long userId) {
        try {
            List<Message> messages = messageService.getActiveMessages(userId);
            return new ResponseEntity<>(messages, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Récupérer les messages par type
     */
    @GetMapping("/by-type")
    public ResponseEntity<List<Message>> getMessagesByType(@RequestParam("type") MessageType type) {
        try {
            List<Message> messages = messageService.getMessagesByType(type);
            return new ResponseEntity<>(messages, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Récupérer les messages par priorité
     */
    @GetMapping("/by-priority")
    public ResponseEntity<List<Message>> getMessagesByPriority(@RequestParam("priority") MessagePriority priority) {
        try {
            List<Message> messages = messageService.getMessagesByPriority(priority);
            return new ResponseEntity<>(messages, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Récupérer la conversation entre deux utilisateurs
     */
    @GetMapping("/conversation")
    public ResponseEntity<List<Message>> getConversation(@RequestParam("userId1") Long userId1, @RequestParam("userId2") Long userId2) {
        try {
            List<Message> messages = messageService.getConversation(userId1, userId2);
            return new ResponseEntity<>(messages, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Marquer un message comme lu
     */
    @PutMapping("/mark-read")
    public ResponseEntity<Message> markAsRead(@RequestParam("id") Long id) {
        try {
            Message message = messageService.markAsRead(id);
            return new ResponseEntity<>(message, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Archiver un message
     */
    @PutMapping("/archive")
    public ResponseEntity<Message> archiveMessage(@RequestParam("id") Long id) {
        try {
            Message message = messageService.archiveMessage(id);
            return new ResponseEntity<>(message, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Désarchiver un message
     */
    @PutMapping("/unarchive")
    public ResponseEntity<Message> unarchiveMessage(@RequestParam("id") Long id) {
        try {
            Message message = messageService.unarchiveMessage(id);
            return new ResponseEntity<>(message, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Compter les messages non lus d'un utilisateur
     */
    @GetMapping("/unread-count")
    public ResponseEntity<Long> countUnreadMessages(@RequestParam("userId") Long userId) {
        try {
            long count = messageService.countUnreadMessages(userId);
            return new ResponseEntity<>(count, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Récupérer les messages urgents non lus
     */
    @GetMapping("/urgent")
    public ResponseEntity<List<Message>> getUrgentUnreadMessages(@RequestParam("userId") Long userId) {
        try {
            List<Message> messages = messageService.getUrgentUnreadMessages(userId);
            return new ResponseEntity<>(messages, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Rechercher des messages
     */
    @GetMapping("/search")
    public ResponseEntity<List<Message>> searchMessages(@RequestParam("userId") Long userId, 
                                                        @RequestParam("searchTerm") String searchTerm) {
        try {
            List<Message> messages = messageService.searchMessages(userId, searchTerm);
            return new ResponseEntity<>(messages, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Mettre à jour un message
     */
    @PutMapping("/update")
    public ResponseEntity<Message> updateMessage(@RequestParam("id") Long id, @Valid @RequestBody Message message) {
        try {
            Message updatedMessage = messageService.updateMessage(id, message);
            return new ResponseEntity<>(updatedMessage, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Supprimer un message
     */
    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteMessage(@RequestParam("id") Long id) {
        try {
            messageService.deleteMessage(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Envoyer un message de diffusion
     */
    @PostMapping("/broadcast")
    public ResponseEntity<List<Message>> sendBroadcastMessage(@RequestBody BroadcastMessageRequest request) {
        try {
            List<Message> messages = messageService.sendBroadcastMessage(
                request.getSenderId(),
                request.getReceiverIds(),
                request.getSubject(),
                request.getContent(),
                request.getType(),
                request.getPriority()
            );
            return new ResponseEntity<>(messages, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Classe interne pour les requêtes de diffusion
     */
    public static class BroadcastMessageRequest {
        private Long senderId;
        private List<Long> receiverIds;
        private String subject;
        private String content;
        private MessageType type;
        private MessagePriority priority;

        // Getters et Setters
        public Long getSenderId() { return senderId; }
        public void setSenderId(Long senderId) { this.senderId = senderId; }

        public List<Long> getReceiverIds() { return receiverIds; }
        public void setReceiverIds(List<Long> receiverIds) { this.receiverIds = receiverIds; }

        public String getSubject() { return subject; }
        public void setSubject(String subject) { this.subject = subject; }

        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }

        public MessageType getType() { return type; }
        public void setType(MessageType type) { this.type = type; }

        public MessagePriority getPriority() { return priority; }
        public void setPriority(MessagePriority priority) { this.priority = priority; }
    }
}
