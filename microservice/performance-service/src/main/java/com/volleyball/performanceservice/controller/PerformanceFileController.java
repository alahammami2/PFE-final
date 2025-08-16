package com.volleyball.performanceservice.controller;

import com.volleyball.performanceservice.dto.CreatePerformanceFileRequest;
import com.volleyball.performanceservice.dto.PerformanceFileResponse;
import com.volleyball.performanceservice.service.PerformanceFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Contrôleur pour la gestion des fichiers de performance
 */
@RestController
@RequestMapping("/api/performance/files")
@CrossOrigin(origins = "*")
public class PerformanceFileController {

    @Autowired
    private PerformanceFileService performanceFileService;

    // CRUD Endpoints
    @PostMapping
    public ResponseEntity<PerformanceFileResponse> createPerformanceFile(@Valid @RequestBody CreatePerformanceFileRequest request) {
        try {
            var file = performanceFileService.createPerformanceFile(request);
            return new ResponseEntity<>(performanceFileService.getPerformanceFileById(file.getId()), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping
    public ResponseEntity<List<PerformanceFileResponse>> getAllPerformanceFiles() {
        try {
            List<PerformanceFileResponse> files = performanceFileService.getAllPerformanceFiles();
            if (files.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(files, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<PerformanceFileResponse> getPerformanceFileById(@PathVariable Long id) {
        try {
            PerformanceFileResponse file = performanceFileService.getPerformanceFileById(id);
            return new ResponseEntity<>(file, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<PerformanceFileResponse> updatePerformanceFile(@PathVariable Long id, @Valid @RequestBody CreatePerformanceFileRequest request) {
        try {
            PerformanceFileResponse updatedFile = performanceFileService.updatePerformanceFile(id, request);
            return new ResponseEntity<>(updatedFile, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deletePerformanceFile(@PathVariable Long id) {
        try {
            performanceFileService.deletePerformanceFile(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Business Logic Endpoints
    @GetMapping("/performance/{performanceId}")
    public ResponseEntity<List<PerformanceFileResponse>> getFilesByPerformance(@PathVariable Long performanceId) {
        try {
            List<PerformanceFileResponse> files = performanceFileService.getFilesByPerformance(performanceId);
            return new ResponseEntity<>(files, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/type/{fileType}")
    public ResponseEntity<List<PerformanceFileResponse>> getFilesByType(@PathVariable String fileType) {
        try {
            List<PerformanceFileResponse> files = performanceFileService.getFilesByType(fileType);
            return new ResponseEntity<>(files, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/min-size")
    public ResponseEntity<List<PerformanceFileResponse>> getFilesByMinSize(@RequestParam Long minSize) {
        try {
            List<PerformanceFileResponse> files = performanceFileService.getFilesByMinSize(minSize);
            return new ResponseEntity<>(files, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/period")
    public ResponseEntity<List<PerformanceFileResponse>> getFilesByPeriod(
            @RequestParam String dateDebut, @RequestParam String dateFin) {
        try {
            LocalDateTime debut = LocalDateTime.parse(dateDebut);
            LocalDateTime fin = LocalDateTime.parse(dateFin);
            List<PerformanceFileResponse> files = performanceFileService.getFilesByPeriod(debut, fin);
            return new ResponseEntity<>(files, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/search")
    public ResponseEntity<List<PerformanceFileResponse>> searchFilesByName(@RequestParam String searchTerm) {
        try {
            List<PerformanceFileResponse> files = performanceFileService.searchFilesByName(searchTerm);
            return new ResponseEntity<>(files, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/recent")
    public ResponseEntity<List<PerformanceFileResponse>> getRecentFiles() {
        try {
            List<PerformanceFileResponse> files = performanceFileService.getRecentFiles();
            return new ResponseEntity<>(files, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/largest")
    public ResponseEntity<List<PerformanceFileResponse>> getLargestFiles() {
        try {
            List<PerformanceFileResponse> files = performanceFileService.getLargestFiles();
            return new ResponseEntity<>(files, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getFileStatistics() {
        try {
            Map<String, Object> statistics = performanceFileService.getFileStatistics();
            return new ResponseEntity<>(statistics, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Endpoints utilitaires
    @GetMapping("/performance/{performanceId}/count")
    public ResponseEntity<Long> countFilesByPerformance(@PathVariable Long performanceId) {
        try {
            // Cette méthode n'existe pas encore dans le service, on peut l'ajouter si nécessaire
            return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/type/{fileType}/count")
    public ResponseEntity<Long> countFilesByType(@PathVariable String fileType) {
        try {
            // Cette méthode n'existe pas encore dans le service, on peut l'ajouter si nécessaire
            return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
