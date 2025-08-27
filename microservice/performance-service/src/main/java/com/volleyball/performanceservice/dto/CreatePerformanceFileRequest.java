package com.volleyball.performanceservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * DTO pour la création d'un fichier de performance
 */
public class CreatePerformanceFileRequest {

    @NotBlank(message = "Le nom original du fichier est obligatoire")
    @Size(max = 255, message = "Le nom du fichier ne peut pas dépasser 255 caractères")
    private String originalName;

    @NotBlank(message = "Le type de fichier est obligatoire")
    @Size(max = 10, message = "Le type de fichier ne peut pas dépasser 10 caractères")
    private String fileType;

    @NotNull(message = "La taille du fichier est obligatoire")
    private Long fileSize;

    @NotNull(message = "Le chemin du fichier ne peut pas être null")
    @Size(max = 500, message = "Le chemin du fichier ne peut pas dépasser 500 caractères")
    private String filePath;

    private Long performanceId; // Optionnel

    // Constructeurs
    public CreatePerformanceFileRequest() {
    }

    public CreatePerformanceFileRequest(String originalName, String fileType, Long fileSize, String filePath) {
        this.originalName = originalName;
        this.fileType = fileType;
        this.fileSize = fileSize;
        this.filePath = filePath;
    }

    // Getters et Setters
    public String getOriginalName() {
        return originalName;
    }

    public void setOriginalName(String originalName) {
        this.originalName = originalName;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public Long getPerformanceId() {
        return performanceId;
    }

    public void setPerformanceId(Long performanceId) {
        this.performanceId = performanceId;
    }
}
