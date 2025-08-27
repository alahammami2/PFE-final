package com.volleyball.performanceservice.controller;

import com.volleyball.performanceservice.dto.CreatePlayerRequest;
import com.volleyball.performanceservice.model.Player;
import com.volleyball.performanceservice.model.Position;
import com.volleyball.performanceservice.model.StatutJoueur;
import com.volleyball.performanceservice.service.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/performance/players")
public class PlayerController {

    @Autowired
    private PlayerService playerService;

    @PostMapping
    public ResponseEntity<Player> createPlayer(@Valid @RequestBody CreatePlayerRequest request) {
        Player created = playerService.createPlayer(request);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Player>> getAllPlayers() {
        return ResponseEntity.ok(playerService.getAllPlayers());
    }

    @GetMapping("/active")
    public ResponseEntity<List<Player>> getAllActivePlayers() {
        return ResponseEntity.ok(playerService.getAllActivePlayers());
    }

    @GetMapping("/{id:\\d+}")
    public ResponseEntity<Player> getPlayerById(@PathVariable Long id) {
        return ResponseEntity.ok(playerService.getPlayerById(id));
    }

    @GetMapping("/position/{position}")
    public ResponseEntity<List<Player>> getPlayersByPosition(@PathVariable Position position) {
        return ResponseEntity.ok(playerService.getPlayersByPosition(position));
    }

    @GetMapping("/statut/{statut}")
    public ResponseEntity<List<Player>> getPlayersByStatut(@PathVariable StatutJoueur statut) {
        return ResponseEntity.ok(playerService.getPlayersByStatut(statut));
    }

    @GetMapping("/search")
    public ResponseEntity<List<Player>> searchPlayers(@RequestParam("q") String q) {
        return ResponseEntity.ok(playerService.searchPlayersByName(q));
    }

    @PutMapping("/{id:\\d+}")
    public ResponseEntity<Player> updatePlayer(@PathVariable Long id, @Valid @RequestBody CreatePlayerRequest request) {
        return ResponseEntity.ok(playerService.updatePlayer(id, request));
    }

    @PutMapping("/{id}/statut")
    public ResponseEntity<Player> changeStatus(@PathVariable Long id, @RequestParam("statut") StatutJoueur statut) {
        return ResponseEntity.ok(playerService.changePlayerStatus(id, statut));
    }

    @DeleteMapping("/{id:\\d+}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        playerService.deletePlayer(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/by-email")
    public ResponseEntity<Void> deleteByEmail(@RequestParam("email") String email) {
        playerService.deleteByEmail(email);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/by-email")
    public ResponseEntity<Player> updateByEmail(@RequestParam("email") String email, @Valid @RequestBody CreatePlayerRequest request) {
        return ResponseEntity.ok(playerService.updateByEmail(email, request));
    }
}


