package com.volleyball.planningservice.repository;

import com.volleyball.planningservice.model.Event;
import com.volleyball.planningservice.model.EventType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    // Trouver tous les événements actifs
    List<Event> findByActifTrue();

    // Trouver les événements par type
    List<Event> findByTypeAndActifTrue(EventType type);

    // Trouver les événements dans une période donnée
    @Query("SELECT e FROM Event e WHERE e.actif = true AND e.dateDebut >= :dateDebut AND e.dateFin <= :dateFin")
    List<Event> findEventsInPeriod(@Param("dateDebut") LocalDateTime dateDebut, @Param("dateFin") LocalDateTime dateFin);

    // Trouver les événements à venir
    @Query("SELECT e FROM Event e WHERE e.actif = true AND e.dateDebut >= :now ORDER BY e.dateDebut ASC")
    List<Event> findUpcomingEvents(@Param("now") LocalDateTime now);

    // Trouver les événements qui chevauchent une période [start, end)
    // Un événement est inclus s'il commence avant la fin et finit après le début
    @Query("SELECT e FROM Event e WHERE e.actif = true AND e.dateDebut < :end AND e.dateFin >= :start ORDER BY e.dateDebut ASC")
    List<Event> findEventsOverlapping(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    // Compter les événements par type
    @Query("SELECT e.type, COUNT(e) FROM Event e WHERE e.actif = true GROUP BY e.type")
    List<Object[]> countEventsByType();
}