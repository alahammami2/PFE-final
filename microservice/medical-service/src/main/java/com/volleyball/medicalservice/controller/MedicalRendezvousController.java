package com.volleyball.medicalservice.controller;

import com.volleyball.medicalservice.model.MedicalRendezvous;
import com.volleyball.medicalservice.service.MedicalRendezvousService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/medical/rendezvous")
@CrossOrigin(origins = "*")
public class MedicalRendezvousController {

    private final MedicalRendezvousService service;

    public MedicalRendezvousController(MedicalRendezvousService service) {
        this.service = service;
    }

    // CRUD
    @PostMapping
    public ResponseEntity<MedicalRendezvous> create(@RequestBody MedicalRendezvous rv) {
        return new ResponseEntity<>(service.create(rv), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<MedicalRendezvous>> all() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MedicalRendezvous> byId(@PathVariable Long id) {
        Optional<MedicalRendezvous> rv = service.getById(id);
        return rv.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<MedicalRendezvous> update(@PathVariable Long id, @RequestBody MedicalRendezvous input) {
        return ResponseEntity.ok(service.update(id, input));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    // Queries
    @GetMapping("/joueur/{playerId}")
    public ResponseEntity<List<MedicalRendezvous>> byPlayer(@PathVariable Long playerId) {
        return ResponseEntity.ok(service.byPlayer(playerId));
    }

    @GetMapping("/aujourdhui")
    public ResponseEntity<List<MedicalRendezvous>> today() {
        return ResponseEntity.ok(service.today());
    }

    @GetMapping("/prochains")
    public ResponseEntity<List<MedicalRendezvous>> upcoming() {
        return ResponseEntity.ok(service.upcoming());
    }

    @GetMapping("/kine")
    public ResponseEntity<List<MedicalRendezvous>> byKine(@RequestParam String kineName) {
        return ResponseEntity.ok(service.byKine(kineName));
    }

    @GetMapping("/recherche")
    public ResponseEntity<List<MedicalRendezvous>> search(@RequestParam String name) {
        return ResponseEntity.ok(service.searchByPlayerName(name));
    }

    // Actions statut
    @PutMapping("/{id}/confirmer")
    public ResponseEntity<MedicalRendezvous> confirm(@PathVariable Long id) {
        return ResponseEntity.ok(service.setStatus(id, "CONFIRMED"));
    }

    @PutMapping("/{id}/annuler")
    public ResponseEntity<MedicalRendezvous> cancel(@PathVariable Long id) {
        return ResponseEntity.ok(service.setStatus(id, "CANCELLED"));
    }

    @PutMapping("/{id}/terminer")
    public ResponseEntity<MedicalRendezvous> complete(@PathVariable Long id) {
        return ResponseEntity.ok(service.setStatus(id, "COMPLETED"));
    }

    @PutMapping("/{id}/replanifier")
    public ResponseEntity<MedicalRendezvous> reschedule(@PathVariable Long id, @RequestParam("datetime") String datetime) {
        return ResponseEntity.ok(service.reschedule(id, LocalDateTime.parse(datetime)));
    }

    // Statistiques
    @GetMapping("/statistiques")
    public ResponseEntity<Map<String, Object>> stats() {
        return ResponseEntity.ok(service.statistics());
    }
}
