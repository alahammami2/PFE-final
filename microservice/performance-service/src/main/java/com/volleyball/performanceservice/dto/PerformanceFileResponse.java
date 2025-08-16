package com.volleyball.performanceservice.dto;

import java.time.LocalDateTime;

/**
 * DTO pour la réponse d'un fichier de performance
 */
public class PerformanceFileResponse {

    private Long id;
    private String originalName;
    private String fileType;
    private Long fileSize;
    private String filePath;
    private LocalDateTime uploadDate;
    private Long performanceId;
    private String performanceInfo; // Informations sur la performance associée

    // Constructeurs
    public PerformanceFileResponse() {
    }

    public PerformanceFileResponse(Long id, String originalName, String fileType, Long fileSize, 
                                 String filePath, LocalDateTime uploadDate, Long performanceId) {
        this.id = id;
        this.originalName = originalName;
        this.fileType = fileType;
        this.fileSize = fileSize;
        this.filePath = filePath;
        this.uploadDate = uploadDate;
        this.performanceId = performanceId;
    }

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public LocalDateTime getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(LocalDateTime uploadDate) {
        this.uploadDate = uploadDate;
    }

    public Long getPerformanceId() {
        return performanceId;
    }

    public void setPerformanceId(Long performanceId) {
        this.performanceId = performanceId;
    }

    public String getPerformanceInfo() {
        return performanceInfo;
    }

    public void setPerformanceInfo(String performanceInfo) {
        this.performanceInfo = performanceInfo;
    }
}
