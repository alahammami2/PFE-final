package com.volleyball.messagingservice.service;

import com.volleyball.messagingservice.model.Message;
import com.volleyball.messagingservice.model.MessageType;
import com.volleyball.messagingservice.model.MessagePriority;
import com.volleyball.messagingservice.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service pour la gestion des messages
 */
@Service
@Transactional
public class MessageService {

    @Autowired
    private MessageRepository messageRepository;

    /**
     * Créer un nouveau message
     */
    public Message createMessage(Message message) {
        if (message.getPriority() == null) {
            message.setPriority(MessagePriority.NORMALE);
        }
        return messageRepository.save(message);
    }

    /**
     * Récupérer tous les messages
     */
    @Transactional(readOnly = true)
    public List<Message> getAllMessages() {
        return messageRepository.findAll();
    }

    /**
     * Récupérer un message par ID
     */
    @Transactional(readOnly = true)
    public Optional<Message> getMessageById(Long id) {
        return messageRepository.findById(id);
    }

    /**
     * Récupérer les messages reçus par un utilisateur
     */
    @Transactional(readOnly = true)
    public List<Message> getReceivedMessages(Long userId) {
        return messageRepository.findByReceiverIdOrderByDateCreationDesc(userId);
    }

    /**
     * Récupérer les messages envoyés par un utilisateur
     */
    @Transactional(readOnly = true)
    public List<Message> getSentMessages(Long userId) {
        return messageRepository.findBySenderIdOrderByDateCreationDesc(userId);
    }

    /**
     * Récupérer les messages non lus d'un utilisateur
     */
    @Transactional(readOnly = true)
    public List<Message> getUnreadMessages(Long userId) {
        return messageRepository.findByReceiverIdAndReadFalseOrderByDateCreationDesc(userId);
    }

    /**
     * Récupérer les messages archivés d'un utilisateur
     */
    @Transactional(readOnly = true)
    public List<Message> getArchivedMessages(Long userId) {
        return messageRepository.findByReceiverIdAndArchivedTrueOrderByDateCreationDesc(userId);
    }

    /**
     * Récupérer les messages non archivés d'un utilisateur
     */
    @Transactional(readOnly = true)
    public List<Message> getActiveMessages(Long userId) {
        return messageRepository.findByReceiverIdAndArchivedFalseOrderByDateCreationDesc(userId);
    }

    /**
     * Récupérer les messages par type
     */
    @Transactional(readOnly = true)
    public List<Message> getMessagesByType(MessageType type) {
        return messageRepository.findByTypeOrderByDateCreationDesc(type);
    }

    /**
     * Récupérer les messages par priorité
     */
    @Transactional(readOnly = true)
    public List<Message> getMessagesByPriority(MessagePriority priority) {
        return messageRepository.findByPriorityOrderByDateCreationDesc(priority);
    }

    /**
     * Récupérer la conversation entre deux utilisateurs
     */
    @Transactional(readOnly = true)
    public List<Message> getConversation(Long userId1, Long userId2) {
        return messageRepository.findConversationBetweenUsers(userId1, userId2);
    }

    /**
     * Marquer un message comme lu
     */
    public Message markAsRead(Long messageId) {
        Optional<Message> messageOpt = messageRepository.findById(messageId);
        if (messageOpt.isPresent()) {
            Message message = messageOpt.get();
            message.setRead(true);
            return messageRepository.save(message);
        }
        throw new RuntimeException("Message non trouvé avec l'ID: " + messageId);
    }

    /**
     * Archiver un message
     */
    public Message archiveMessage(Long messageId) {
        Optional<Message> messageOpt = messageRepository.findById(messageId);
        if (messageOpt.isPresent()) {
            Message message = messageOpt.get();
            message.setArchived(true);
            return messageRepository.save(message);
        }
        throw new RuntimeException("Message non trouvé avec l'ID: " + messageId);
    }

    /**
     * Désarchiver un message
     */
    public Message unarchiveMessage(Long messageId) {
        Optional<Message> messageOpt = messageRepository.findById(messageId);
        if (messageOpt.isPresent()) {
            Message message = messageOpt.get();
            message.setArchived(false);
            return messageRepository.save(message);
        }
        throw new RuntimeException("Message non trouvé avec l'ID: " + messageId);
    }

    /**
     * Compter les messages non lus d'un utilisateur
     */
    @Transactional(readOnly = true)
    public long countUnreadMessages(Long userId) {
        return messageRepository.countByReceiverIdAndReadFalse(userId);
    }

    /**
     * Récupérer les messages urgents non lus
     */
    @Transactional(readOnly = true)
    public List<Message> getUrgentUnreadMessages(Long userId) {
        return messageRepository.findUrgentUnreadMessages(userId);
    }

    /**
     * Rechercher des messages
     */
    @Transactional(readOnly = true)
    public List<Message> searchMessages(Long userId, String searchTerm) {
        return messageRepository.searchMessages(userId, searchTerm);
    }

    /**
     * Récupérer les messages par période
     */
    @Transactional(readOnly = true)
    public List<Message> getMessagesByPeriod(Long userId, LocalDateTime startDate, LocalDateTime endDate) {
        return messageRepository.findByReceiverIdAndDateCreationBetween(userId, startDate, endDate);
    }

    /**
     * Mettre à jour un message
     */
    public Message updateMessage(Long messageId, Message updatedMessage) {
        Optional<Message> messageOpt = messageRepository.findById(messageId);
        if (messageOpt.isPresent()) {
            Message message = messageOpt.get();
            message.setSubject(updatedMessage.getSubject());
            message.setContent(updatedMessage.getContent());
            message.setType(updatedMessage.getType());
            message.setPriority(updatedMessage.getPriority());
            return messageRepository.save(message);
        }
        throw new RuntimeException("Message non trouvé avec l'ID: " + messageId);
    }

    /**
     * Supprimer un message
     */
    public void deleteMessage(Long messageId) {
        if (messageRepository.existsById(messageId)) {
            messageRepository.deleteById(messageId);
        } else {
            throw new RuntimeException("Message non trouvé avec l'ID: " + messageId);
        }
    }

    /**
     * Envoyer un message à plusieurs destinataires
     */
    public List<Message> sendBroadcastMessage(Long senderId, List<Long> receiverIds, 
                                              String subject, String content, 
                                              MessageType type, MessagePriority priority) {
        return receiverIds.stream()
                .map(receiverId -> {
                    Message message = new Message(senderId, receiverId, subject, content, type);
                    message.setPriority(priority);
                    return createMessage(message);
                })
                .toList();
    }
}
