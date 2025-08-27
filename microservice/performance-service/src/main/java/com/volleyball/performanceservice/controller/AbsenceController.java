package com.volleyball.performanceservice.controller;

import com.volleyball.performanceservice.dto.CreateAbsenceRequest;
import com.volleyball.performanceservice.model.Absence;
import com.volleyball.performanceservice.model.Player;
import com.volleyball.performanceservice.repository.PlayerRepository;
import com.volleyball.performanceservice.service.AbsenceService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/performance/absences")
public class AbsenceController {

    @Autowired
    private AbsenceService absenceService;

    @Autowired
    private PlayerRepository playerRepository;

    @PostMapping
    public ResponseEntity<Map<String, Object>> create(@Valid @RequestBody CreateAbsenceRequest request) {
        try {
            Absence created = absenceService.createAbsence(request);
            Map<String, Object> res = new HashMap<>();
            res.put("success", true);
            res.put("data", created);
            return ResponseEntity.status(HttpStatus.CREATED).body(res);
        } catch (IllegalArgumentException e) {
            Map<String, Object> res = new HashMap<>();
            res.put("success", false);
            res.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
        } catch (RuntimeException e) {
            Map<String, Object> res = new HashMap<>();
            res.put("success", false);
            res.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
        }
    }

    
    

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAll() {
        List<Absence> list = absenceService.getAllAbsences();
        Map<String, Object> res = new HashMap<>();
        res.put("success", true);
        res.put("data", list);
        res.put("count", list.size());
        return ResponseEntity.ok(res);
    }

    @PatchMapping("/{id}/approve")
    public ResponseEntity<Map<String, Object>> approve(@PathVariable Long id, @RequestParam(value = "commentaires", required = false) String commentaires) {
        try {
            Absence updated = absenceService.approuverAbsence(id, commentaires);
            Map<String, Object> res = new HashMap<>();
            res.put("success", true);
            res.put("data", updated);
            return ResponseEntity.ok(res);
        } catch (RuntimeException e) {
            Map<String, Object> res = new HashMap<>();
            res.put("success", false);
            res.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
        }
    }

    @PatchMapping("/{id}/refuse")
    public ResponseEntity<Map<String, Object>> refuse(@PathVariable Long id, @RequestParam(value = "commentaires", required = false) String commentaires) {
        try {
            Absence updated = absenceService.refuserAbsence(id, commentaires);
            Map<String, Object> res = new HashMap<>();
            res.put("success", true);
            res.put("data", updated);
            return ResponseEntity.ok(res);
        } catch (RuntimeException e) {
            Map<String, Object> res = new HashMap<>();
            res.put("success", false);
            res.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
        }
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<Map<String, Object>> cancel(@PathVariable Long id, @RequestParam(value = "commentaires", required = false) String commentaires) {
        try {
            Absence updated = absenceService.annulerAbsence(id, commentaires);
            Map<String, Object> res = new HashMap<>();
            res.put("success", true);
            res.put("data", updated);
            return ResponseEntity.ok(res);
        } catch (RuntimeException e) {
            Map<String, Object> res = new HashMap<>();
            res.put("success", false);
            res.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
        }
    }
}
