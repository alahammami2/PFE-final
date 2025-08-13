package com.volleyball.messagingservice.repository;

import com.volleyball.messagingservice.model.Message;
import com.volleyball.messagingservice.model.MessageType;
import com.volleyball.messagingservice.model.MessagePriority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository pour la gestion des messages
 */
@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    /**
     * Trouve tous les messages reçus par un utilisateur
     */
    List<Message> findByReceiverIdOrderByDateCreationDesc(Long receiverId);

    /**
     * Trouve tous les messages envoyés par un utilisateur
     */
    List<Message> findBySenderIdOrderByDateCreationDesc(Long senderId);

    /**
     * Trouve tous les messages non lus d'un utilisateur
     */
    List<Message> findByReceiverIdAndReadFalseOrderByDateCreationDesc(Long receiverId);

    /**
     * Trouve tous les messages archivés d'un utilisateur
     */
    List<Message> findByReceiverIdAndArchivedTrueOrderByDateCreationDesc(Long receiverId);

    /**
     * Trouve tous les messages non archivés d'un utilisateur
     */
    List<Message> findByReceiverIdAndArchivedFalseOrderByDateCreationDesc(Long receiverId);

    /**
     * Trouve les messages par type
     */
    List<Message> findByTypeOrderByDateCreationDesc(MessageType type);

    /**
     * Trouve les messages par priorité
     */
    List<Message> findByPriorityOrderByDateCreationDesc(MessagePriority priority);

    /**
     * Trouve les messages entre deux utilisateurs
     */
    @Query("SELECT m FROM Message m WHERE " +
           "(m.senderId = :userId1 AND m.receiverId = :userId2) OR " +
           "(m.senderId = :userId2 AND m.receiverId = :userId1) " +
           "ORDER BY m.dateCreation ASC")
    List<Message> findConversationBetweenUsers(@Param("userId1") Long userId1, 
                                               @Param("userId2") Long userId2);

    /**
     * Compte les messages non lus d'un utilisateur
     */
    long countByReceiverIdAndReadFalse(Long receiverId);

    /**
     * Trouve les messages par période
     */
    @Query("SELECT m FROM Message m WHERE m.receiverId = :receiverId AND " +
           "m.dateCreation BETWEEN :startDate AND :endDate " +
           "ORDER BY m.dateCreation DESC")
    List<Message> findByReceiverIdAndDateCreationBetween(@Param("receiverId") Long receiverId,
                                                         @Param("startDate") LocalDateTime startDate,
                                                         @Param("endDate") LocalDateTime endDate);

    /**
     * Trouve les messages urgents non lus
     */
    @Query("SELECT m FROM Message m WHERE m.receiverId = :receiverId AND " +
           "m.priority IN ('HAUTE', 'URGENTE') AND m.read = false " +
           "ORDER BY m.priority DESC, m.dateCreation DESC")
    List<Message> findUrgentUnreadMessages(@Param("receiverId") Long receiverId);

    /**
     * Recherche de messages par contenu
     */
    @Query("SELECT m FROM Message m WHERE m.receiverId = :receiverId AND " +
           "(LOWER(m.subject) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(m.content) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) " +
           "ORDER BY m.dateCreation DESC")
    List<Message> searchMessages(@Param("receiverId") Long receiverId, 
                                 @Param("searchTerm") String searchTerm);
}
