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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
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
        return performanceFileRepository.findAll().stream()
                .map(this::convertToResponse)
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
            response.setPerformanceId(file.getPerformance().getId());
            response.setPerformanceInfo(file.getPerformance().getPlayer().getNom() + " " + 
                                    file.getPerformance().getPlayer().getPrenom() + " - " + 
                                    file.getPerformance().getDatePerformance());
        }
        
        return response;
    }
}
