package com.volleyball.planningservice.controller;

import com.volleyball.planningservice.dto.CreateMatchRequest;
import com.volleyball.planningservice.dto.MatchResponse;
import com.volleyball.planningservice.dto.UpdateMatchRequest;
import com.volleyball.planningservice.service.MatchService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/planning/matches")
public class MatchController {

    private final MatchService matchService;

    public MatchController(MatchService matchService) {
        this.matchService = matchService;
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> create(@Valid @RequestBody CreateMatchRequest request) {
        try {
            MatchResponse created = matchService.create(request);
            Map<String, Object> resp = new HashMap<>();
            resp.put("success", true);
            resp.put("message", "Équipe ajoutée avec succès");
            resp.put("data", created);
            return ResponseEntity.ok(resp);
        } catch (Exception e) {
            Map<String, Object> resp = new HashMap<>();
            resp.put("success", false);
            resp.put("message", "Erreur lors de l'ajout: " + e.getMessage());
            return ResponseEntity.badRequest().body(resp);
        }
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> findAll() {
        try {
            List<MatchResponse> list = matchService.findAll();
            Map<String, Object> resp = new HashMap<>();
            resp.put("success", true);
            resp.put("message", "Éléments récupérés avec succès");
            resp.put("data", list);
            return ResponseEntity.ok(resp);
        } catch (Exception e) {
            Map<String, Object> resp = new HashMap<>();
            resp.put("success", false);
            resp.put("message", "Erreur lors de la récupération: " + e.getMessage());
            return ResponseEntity.badRequest().body(resp);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> findById(@PathVariable Long id) {
        try {
            MatchResponse item = matchService.findById(id);
            Map<String, Object> resp = new HashMap<>();
            resp.put("success", true);
            resp.put("message", "Élément récupéré avec succès");
            resp.put("data", item);
            return ResponseEntity.ok(resp);
        } catch (Exception e) {
            Map<String, Object> resp = new HashMap<>();
            resp.put("success", false);
            resp.put("message", "Erreur lors de la récupération: " + e.getMessage());
            return ResponseEntity.badRequest().body(resp);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> update(@PathVariable Long id, @Valid @RequestBody UpdateMatchRequest request) {
        try {
            MatchResponse updated = matchService.update(id, request);
            Map<String, Object> resp = new HashMap<>();
            resp.put("success", true);
            resp.put("message", "Équipe modifiée avec succès");
            resp.put("data", updated);
            return ResponseEntity.ok(resp);
        } catch (Exception e) {
            Map<String, Object> resp = new HashMap<>();
            resp.put("success", false);
            resp.put("message", "Erreur lors de la modification: " + e.getMessage());
            return ResponseEntity.badRequest().body(resp);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> delete(@PathVariable Long id) {
        try {
            matchService.delete(id);
            Map<String, Object> resp = new HashMap<>();
            resp.put("success", true);
            resp.put("message", "Équipe supprimée avec succès");
            return ResponseEntity.ok(resp);
        } catch (Exception e) {
            Map<String, Object> resp = new HashMap<>();
            resp.put("success", false);
            resp.put("message", "Erreur lors de la suppression: " + e.getMessage());
            return ResponseEntity.badRequest().body(resp);
        }
    }

    @PatchMapping("/{id}/points")
    public ResponseEntity<Map<String, Object>> incrementPoints(@PathVariable Long id, @RequestParam(name = "delta", defaultValue = "1") int delta) {
        try {
            MatchResponse updated = matchService.incrementPoints(id, delta);
            Map<String, Object> resp = new HashMap<>();
            resp.put("success", true);
            resp.put("message", "Points mis à jour avec succès");
            resp.put("data", updated);
            return ResponseEntity.ok(resp);
        } catch (Exception e) {
            Map<String, Object> resp = new HashMap<>();
            resp.put("success", false);
            resp.put("message", "Erreur lors de la mise à jour des points: " + e.getMessage());
            return ResponseEntity.badRequest().body(resp);
        }
    }
}
