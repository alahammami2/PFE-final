package com.volleyball.planningservice.repository;

import com.volleyball.planningservice.model.Match;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MatchRepository extends JpaRepository<Match, Long> {
    List<Match> findByNomEquipeIgnoreCase(String nomEquipe);
}
