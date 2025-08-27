package com.volleyball.planningservice.service;

import com.volleyball.planningservice.dto.CreateMatchRequest;
import com.volleyball.planningservice.dto.MatchResponse;
import com.volleyball.planningservice.dto.UpdateMatchRequest;
import com.volleyball.planningservice.model.Match;
import com.volleyball.planningservice.repository.MatchRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class MatchService {

    private final MatchRepository matchRepository;

    public MatchService(MatchRepository matchRepository) {
        this.matchRepository = matchRepository;
    }

    private MatchResponse toResponse(Match m) {
        return new MatchResponse(m.getId(), m.getNomEquipe(), m.getPoints());
    }

    public MatchResponse create(CreateMatchRequest req) {
        Match m = new Match();
        m.setNomEquipe(req.getNomEquipe());
        m.setPoints(req.getPoints());
        return toResponse(matchRepository.save(m));
    }

    public List<MatchResponse> findAll() {
        return matchRepository.findAll().stream().map(this::toResponse).collect(Collectors.toList());
    }

    public MatchResponse findById(Long id) {
        Match m = matchRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Match introuvable: " + id));
        return toResponse(m);
    }

    public MatchResponse update(Long id, UpdateMatchRequest req) {
        Match m = matchRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Match introuvable: " + id));
        m.setNomEquipe(req.getNomEquipe());
        m.setPoints(req.getPoints());
        return toResponse(matchRepository.save(m));
    }

    public void delete(Long id) {
        if (!matchRepository.existsById(id)) {
            throw new IllegalArgumentException("Match introuvable: " + id);
        }
        matchRepository.deleteById(id);
    }

    public MatchResponse incrementPoints(Long id, int delta) {
        Match m = matchRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Match introuvable: " + id));
        int newPoints = (m.getPoints() == null ? 0 : m.getPoints()) + delta;
        if (newPoints < 0) newPoints = 0;
        m.setPoints(newPoints);
        return toResponse(matchRepository.save(m));
    }
}
