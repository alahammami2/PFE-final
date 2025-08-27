package com.volleyball.performanceservice.service;

import com.volleyball.performanceservice.dto.CreatePerformanceFileRequest;
import com.volleyball.performanceservice.dto.PerformanceFileResponse;
import com.volleyball.performanceservice.model.Performance;
import com.volleyball.performanceservice.model.PerformanceFile;
import com.volleyball.performanceservice.repository.PerformanceFileRepository;
import com.volleyball.performanceservice.repository.PerformanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Map;
import java.util.HashMap;

/**
 * Service pour la gestion des fichiers de performance
 */
@Service
@Transactional
public class PerformanceFileService {

    @Autowired
    private PerformanceFileRepository performanceFileRepository;

    @Autowired
    private PerformanceRepository performanceRepository;

    /**
     * Créer un nouveau fichier de performance
     */
    public PerformanceFile createPerformanceFile(CreatePerformanceFileRequest request) {
        // Validation des données
        validateFileRequest(request);

        // Création de l'entité
        PerformanceFile performanceFile = new PerformanceFile();
        performanceFile.setOriginalName(request.getOriginalName());
        performanceFile.setFileType(request.getFileType());
        performanceFile.setFileSize(request.getFileSize());
        performanceFile.setFilePath(request.getFilePath());

        // Association avec une performance si spécifiée
        if (request.getPerformanceId() != null) {
            Performance performance = performanceRepository.findById(request.getPerformanceId())
                    .orElseThrow(() -> new RuntimeException("Performance non trouvée avec l'ID: " + request.getPerformanceId()));
            performanceFile.setPerformance(performance);
        }

        return performanceFileRepository.save(performanceFile);
    }

    /**
     * Upload d'un fichier binaire et stockage dans la base (colonne content)
     */
    public PerformanceFileResponse uploadAndStoreFile(MultipartFile multipart, Long performanceId) {
        try {
            if (multipart == null || multipart.isEmpty()) {
                throw new RuntimeException("Aucun fichier fourni");
            }

            String originalName = multipart.getOriginalFilename() != null ? multipart.getOriginalFilename() : "fichier";
            String ext = "";
            int idx = originalName.lastIndexOf('.')
                    ;
            if (idx >= 0 && idx < originalName.length() - 1) {
                ext = originalName.substring(idx + 1).toLowerCase();
            }

            CreatePerformanceFileRequest req = new CreatePerformanceFileRequest();
            req.setOriginalName(originalName);
            req.setFileType(ext);
            req.setFileSize(multipart.getSize());
            // Pas de chemin fichier réel, on stocke en DB
            req.setFilePath("");
            req.setPerformanceId(performanceId);

            // Valider les métadonnées (type/size)
            validateFileRequest(req);

            PerformanceFile entity = new PerformanceFile();
            entity.setOriginalName(req.getOriginalName());
            entity.setFileType(req.getFileType());
            entity.setFileSize(req.getFileSize());
            entity.setFilePath(req.getFilePath());
            entity.setContent(multipart.getBytes());

            if (performanceId != null) {
                Performance performance = performanceRepository.findById(performanceId)
                        .orElseThrow(() -> new RuntimeException("Performance non trouvée avec l'ID: " + performanceId));
                entity.setPerformance(performance);
            }

            PerformanceFile saved = performanceFileRepository.save(entity);
            return convertToResponse(saved);
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de l'upload du fichier: " + e.getMessage(), e);
        }
    }

    /**
     * Valider la requête de création de fichier
     */
    private void validateFileRequest(CreatePerformanceFileRequest request) {
        if (request.getFileSize() <= 0) {
            throw new RuntimeException("La taille du fichier doit être positive");
        }

        // Validation des types de fichiers autorisés
        String[] allowedTypes = {"pdf", "doc", "docx", "xls", "xlsx", "jpg", "jpeg", "png", "mp4", "avi", "mov"};
        boolean isValidType = false;
        for (String type : allowedTypes) {
            if (type.equalsIgnoreCase(request.getFileType())) {
                isValidType = true;
                break;
            }
        }
        if (!isValidType) {
            throw new RuntimeException("Type de fichier non autorisé: " + request.getFileType());
        }

        // Validation de la taille maximale (100 MB)
        if (request.getFileSize() > 100 * 1024 * 1024) {
            throw new RuntimeException("La taille du fichier ne peut pas dépasser 100 MB");
        }
    }

    /**
     * Obtenir tous les fichiers
     */
    @Transactional(readOnly = true)
    public List<PerformanceFileResponse> getAllPerformanceFiles() {
        // Version LITE: pas d'accès aux relations pour éviter toute erreur côté DB/mapping
        return performanceFileRepository.findAll().stream()
                .map(this::convertToLiteResponse)
                .collect(Collectors.toList());
    }

    /**
     * Obtenir un fichier par ID
     */
    @Transactional(readOnly = true)
    public PerformanceFileResponse getPerformanceFileById(Long id) {
        PerformanceFile file = performanceFileRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Fichier non trouvé avec l'ID: " + id));
        return convertToResponse(file);
    }

    @Transactional(readOnly = true)
    public PerformanceFile getEntity(Long id) {
        return performanceFileRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Fichier non trouvé avec l'ID: " + id));
    }

    /**
     * Obtenir des fichiers par performance
     */
    @Transactional(readOnly = true)
    public List<PerformanceFileResponse> getFilesByPerformance(Long performanceId) {
        return performanceFileRepository.findByPerformanceId(performanceId).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Obtenir des fichiers par type
     */
    @Transactional(readOnly = true)
    public List<PerformanceFileResponse> getFilesByType(String fileType) {
        return performanceFileRepository.findByFileType(fileType).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Obtenir des fichiers par taille (supérieure à une valeur)
     */
    @Transactional(readOnly = true)
    public List<PerformanceFileResponse> getFilesByMinSize(Long minSize) {
        return performanceFileRepository.findByFileSizeGreaterThan(minSize).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Obtenir des fichiers par période
     */
    @Transactional(readOnly = true)
    public List<PerformanceFileResponse> getFilesByPeriod(LocalDateTime dateDebut, LocalDateTime dateFin) {
        return performanceFileRepository.findByUploadDateBetween(dateDebut, dateFin).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Rechercher des fichiers par nom
     */
    @Transactional(readOnly = true)
    public List<PerformanceFileResponse> searchFilesByName(String searchTerm) {
        return performanceFileRepository.searchByFileName(searchTerm).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Obtenir les fichiers récents
     */
    @Transactional(readOnly = true)
    public List<PerformanceFileResponse> getRecentFiles() {
        return performanceFileRepository.findRecentFiles().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Obtenir les fichiers les plus volumineux
     */
    @Transactional(readOnly = true)
    public List<PerformanceFileResponse> getLargestFiles() {
        return performanceFileRepository.findLargestFiles().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Mettre à jour un fichier
     */
    public PerformanceFileResponse updatePerformanceFile(Long id, CreatePerformanceFileRequest request) {
        PerformanceFile file = performanceFileRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Fichier non trouvé avec l'ID: " + id));

        // Validation des données
        validateFileRequest(request);

        // Mise à jour des champs
        file.setOriginalName(request.getOriginalName());
        file.setFileType(request.getFileType());
        file.setFileSize(request.getFileSize());
        file.setFilePath(request.getFilePath());

        // Association avec une performance si spécifiée
        if (request.getPerformanceId() != null) {
            Performance performance = performanceRepository.findById(request.getPerformanceId())
                    .orElseThrow(() -> new RuntimeException("Performance non trouvée avec l'ID: " + request.getPerformanceId()));
            file.setPerformance(performance);
        } else {
            file.setPerformance(null);
        }

        PerformanceFile updatedFile = performanceFileRepository.save(file);
        return convertToResponse(updatedFile);
    }

    /**
     * Supprimer un fichier
     */
    public void deletePerformanceFile(Long id) {
        PerformanceFile file = performanceFileRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Fichier non trouvé avec l'ID: " + id));
        performanceFileRepository.delete(file);
    }

    /**
     * Obtenir les statistiques des fichiers
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getFileStatistics() {
        long totalFiles = performanceFileRepository.count();
        Long totalSize = performanceFileRepository.getTotalFileSize();
        List<Object[]> statsByType = performanceFileRepository.getFileStatsByType();

        Map<String, Object> statistics = new HashMap<>();
        statistics.put("totalFiles", totalFiles);
        statistics.put("totalSize", totalSize != null ? totalSize : 0L);
        statistics.put("statsByType", statsByType);
        
        return statistics;
    }

    /**
     * Compter tous les fichiers de performance
     */
    @Transactional(readOnly = true)
    public long countAllFiles() {
        return performanceFileRepository.count();
    }

    /**
     * Convertir une entité en DTO de réponse
     */
    private PerformanceFileResponse convertToResponse(PerformanceFile file) {
        PerformanceFileResponse response = new PerformanceFileResponse();
        response.setId(file.getId());
        response.setOriginalName(file.getOriginalName());
        response.setFileType(file.getFileType());
        response.setFileSize(file.getFileSize());
        response.setFilePath(file.getFilePath());
        response.setUploadDate(file.getUploadDate());
        
        if (file.getPerformance() != null) {
            var perf = file.getPerformance();
            response.setPerformanceId(perf.getId());
            String info;
            if (perf.getPlayer() != null) {
                String nom = perf.getPlayer().getNom() != null ? perf.getPlayer().getNom() : "";
                String prenom = perf.getPlayer().getPrenom() != null ? perf.getPlayer().getPrenom() : "";
                info = (nom + " " + prenom).trim();
            } else {
                info = "Performance " + perf.getId();
            }
            if (perf.getDateCreation() != null) {
                info = info + " - " + perf.getDateCreation();
            }
            response.setPerformanceInfo(info);
        }
        
        return response;
    }

    /**
     * Convertir une entité en DTO LITE (sans accès aux relations)
     */
    private PerformanceFileResponse convertToLiteResponse(PerformanceFile file) {
        PerformanceFileResponse response = new PerformanceFileResponse();
        response.setId(file.getId());
        response.setOriginalName(file.getOriginalName());
        response.setFileType(file.getFileType());
        response.setFileSize(file.getFileSize());
        response.setFilePath(file.getFilePath());
        response.setUploadDate(file.getUploadDate());
        if (file.getPerformance() != null) {
            response.setPerformanceId(file.getPerformance().getId());
        }
        return response;
    }
}
