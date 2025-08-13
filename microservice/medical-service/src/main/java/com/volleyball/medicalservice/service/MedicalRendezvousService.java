package com.volleyball.medicalservice.service;

import com.volleyball.medicalservice.model.MedicalRendezvous;
import com.volleyball.medicalservice.repository.MedicalRendezvousRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class MedicalRendezvousService {

    private final MedicalRendezvousRepository repository;

    public MedicalRendezvousService(MedicalRendezvousRepository repository) {
        this.repository = repository;
    }

    // CRUD
    public MedicalRendezvous create(MedicalRendezvous rv) { return repository.save(rv); }

    public List<MedicalRendezvous> getAll() { return repository.findAll(); }

    public Optional<MedicalRendezvous> getById(Long id) { return repository.findById(id); }

    public MedicalRendezvous update(Long id, MedicalRendezvous input) {
        MedicalRendezvous existing = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rendez-vous non trouvé"));
        existing.setPlayerId(input.getPlayerId());
        existing.setPlayerName(input.getPlayerName());
        existing.setRendezvousDatetime(input.getRendezvousDatetime());
        existing.setKineName(input.getKineName());
        existing.setLieu(input.getLieu());
        existing.setPriority(input.getPriority());
        existing.setStatus(input.getStatus());
        existing.setNotes(input.getNotes());
        return repository.save(existing);
    }

    public void delete(Long id) { repository.deleteById(id); }

    // Queries
    public List<MedicalRendezvous> byPlayer(Long playerId) {
        return repository.findByPlayerIdOrderByRendezvousDatetimeDesc(playerId);
    }

    public List<MedicalRendezvous> today() {
        LocalDate today = LocalDate.now();
        LocalDateTime start = today.atStartOfDay();
        LocalDateTime end = today.atTime(LocalTime.MAX);
        return repository.findBetween(start, end);
    }

    public List<MedicalRendezvous> upcoming() { return repository.findUpcoming(); }

    public List<MedicalRendezvous> byKine(String kineName) { return repository.findByKineNameOrderByRendezvousDatetimeAsc(kineName); }

    public List<MedicalRendezvous> searchByPlayerName(String name) { return repository.searchByPlayerName(name); }

    // Actions statut
    public MedicalRendezvous setStatus(Long id, String status) {
        MedicalRendezvous existing = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rendez-vous non trouvé"));
        existing.setStatus(status);
        return repository.save(existing);
    }

    public MedicalRendezvous reschedule(Long id, LocalDateTime newDateTime) {
        MedicalRendezvous existing = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rendez-vous non trouvé"));
        existing.setRendezvousDatetime(newDateTime);
        return repository.save(existing);
    }

    // Stats simples
    public Map<String, Object> statistics() {
        Map<String, Object> out = new HashMap<>();
        long total = repository.count();
        out.put("totalRendezvous", total);
        out.put("scheduled", repository.countByStatus("SCHEDULED"));
        out.put("confirmed", repository.countByStatus("CONFIRMED"));
        out.put("completed", repository.countByStatus("COMPLETED"));
        out.put("cancelled", repository.countByStatus("CANCELLED"));
        out.put("upcoming", repository.findUpcoming().size());
        out.put("today", today().size());
        return out;
    }
}
