package com.volleyball.performanceservice.controller;

import com.volleyball.performanceservice.dto.CreatePerformanceFileRequest;
import com.volleyball.performanceservice.dto.PerformanceFileResponse;
import com.volleyball.performanceservice.service.PerformanceFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Contrôleur pour la gestion des fichiers de performance
 */
@RestController
@RequestMapping("/api/performance/files")
@CrossOrigin(
        origins = "http://localhost:4200",
        methods = { RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS },
        allowedHeaders = { "*" },
        allowCredentials = "true"
)
public class PerformanceFileController {

    @Autowired
    private PerformanceFileService performanceFileService;

    private static final Logger log = LoggerFactory.getLogger(PerformanceFileController.class);

    // CRUD Endpoints
    @PostMapping
    public ResponseEntity<PerformanceFileResponse> createPerformanceFile(@Valid @RequestBody CreatePerformanceFileRequest request) {
        try {
            var file = performanceFileService.createPerformanceFile(request);
            return new ResponseEntity<>(performanceFileService.getPerformanceFileById(file.getId()), HttpStatus.CREATED);
        } catch (Exception e) {
            log.error("Erreur lors de la création du fichier de performance", e);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<byte[]> downloadFile(@PathVariable Long id) {
        try {
            var entity = performanceFileService.getEntity(id);
            byte[] content = entity.getContent();
            if (content == null || content.length == 0) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            String type = entity.getFileType() != null ? entity.getFileType().toLowerCase() : "";
            String mime = switch (type) {
                case "pdf" -> "application/pdf";
                case "png" -> "image/png";
                case "jpg", "jpeg" -> "image/jpeg";
                case "csv" -> "text/csv";
                case "xls" -> "application/vnd.ms-excel";
                case "xlsx" -> "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
                case "doc" -> "application/msword";
                case "docx" -> "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
                default -> "application/octet-stream";
            };

            String filename = entity.getOriginalName() != null ? entity.getOriginalName() : ("file." + type);

            return ResponseEntity.ok()
                    .header("Content-Type", mime)
                    .header("Content-Disposition", "attachment; filename=\"" + filename + "\"")
                    .body(content);
        } catch (RuntimeException e) {
            log.warn("Fichier non trouvé pour téléchargement: {}", id, e);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            log.error("Erreur lors du téléchargement du fichier: {}", id, e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Upload binaire
    @PostMapping("/upload")
    public ResponseEntity<PerformanceFileResponse> uploadPerformanceFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "performanceId", required = false) Long performanceId
    ) {
        try {
            PerformanceFileResponse saved = performanceFileService.uploadAndStoreFile(file, performanceId);
            return new ResponseEntity<>(saved, HttpStatus.CREATED);
        } catch (Exception e) {
            log.error("Erreur lors de l'upload du fichier de performance", e);
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
            log.error("Erreur lors de la récupération de tous les fichiers de performance", e);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<PerformanceFileResponse> getPerformanceFileById(@PathVariable Long id) {
        try {
            PerformanceFileResponse file = performanceFileService.getPerformanceFileById(id);
            return new ResponseEntity<>(file, HttpStatus.OK);
        } catch (RuntimeException e) {
            log.warn("Fichier de performance non trouvé: {}", id, e);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            log.error("Erreur lors de la récupération du fichier de performance par ID: {}", id, e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<PerformanceFileResponse> updatePerformanceFile(@PathVariable Long id, @Valid @RequestBody CreatePerformanceFileRequest request) {
        try {
            PerformanceFileResponse updatedFile = performanceFileService.updatePerformanceFile(id, request);
            return new ResponseEntity<>(updatedFile, HttpStatus.OK);
        } catch (RuntimeException e) {
            log.warn("Fichier de performance introuvable pour mise à jour: {}", id, e);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            log.error("Erreur lors de la mise à jour du fichier de performance: {}", id, e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deletePerformanceFile(@PathVariable Long id) {
        try {
            performanceFileService.deletePerformanceFile(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (RuntimeException e) {
            log.warn("Fichier de performance introuvable pour suppression: {}", id, e);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            log.error("Erreur lors de la suppression du fichier de performance: {}", id, e);
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
            log.error("Erreur lors de la récupération des fichiers par performance: {}", performanceId, e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/type/{fileType}")
    public ResponseEntity<List<PerformanceFileResponse>> getFilesByType(@PathVariable String fileType) {
        try {
            List<PerformanceFileResponse> files = performanceFileService.getFilesByType(fileType);
            return new ResponseEntity<>(files, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Erreur lors de la récupération des fichiers par type: {}", fileType, e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/min-size")
    public ResponseEntity<List<PerformanceFileResponse>> getFilesByMinSize(@RequestParam Long minSize) {
        try {
            List<PerformanceFileResponse> files = performanceFileService.getFilesByMinSize(minSize);
            return new ResponseEntity<>(files, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Erreur lors de la récupération des fichiers par taille minimale: {}", minSize, e);
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
            log.error("Erreur lors de la récupération des fichiers par période: {} - {}", dateDebut, dateFin, e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/search")
    public ResponseEntity<List<PerformanceFileResponse>> searchFilesByName(@RequestParam String searchTerm) {
        try {
            List<PerformanceFileResponse> files = performanceFileService.searchFilesByName(searchTerm);
            return new ResponseEntity<>(files, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Erreur lors de la recherche de fichiers par nom: {}", searchTerm, e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/recent")
    public ResponseEntity<List<PerformanceFileResponse>> getRecentFiles() {
        try {
            List<PerformanceFileResponse> files = performanceFileService.getRecentFiles();
            return new ResponseEntity<>(files, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Erreur lors de la récupération des fichiers récents", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/largest")
    public ResponseEntity<List<PerformanceFileResponse>> getLargestFiles() {
        try {
            List<PerformanceFileResponse> files = performanceFileService.getLargestFiles();
            return new ResponseEntity<>(files, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Erreur lors de la récupération des fichiers les plus volumineux", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getFileStatistics() {
        try {
            Map<String, Object> statistics = performanceFileService.getFileStatistics();
            return new ResponseEntity<>(statistics, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Erreur lors de la récupération des statistiques des fichiers", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(value = "/count")
    public ResponseEntity<Map<String, Long>> countAllFiles() {
        try {
            long total = performanceFileService.countAllFiles();
            return new ResponseEntity<>(Map.of("count", total), HttpStatus.OK);
        } catch (Exception e) {
            log.error("Erreur lors du comptage des fichiers de performance", e);
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
            log.error("Erreur lors du comptage des fichiers par performance: {}", performanceId, e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/type/{fileType}/count")
    public ResponseEntity<Long> countFilesByType(@PathVariable String fileType) {
        try {
            // Cette méthode n'existe pas encore dans le service, on peut l'ajouter si nécessaire
            return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
        } catch (Exception e) {
            log.error("Erreur lors du comptage des fichiers par type: {}", fileType, e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
