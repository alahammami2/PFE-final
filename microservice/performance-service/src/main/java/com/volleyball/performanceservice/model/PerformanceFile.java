package com.volleyball.performanceservice.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;

/**
 * Entité représentant les fichiers associés aux performances
 */
@Entity
@Table(name = "performance_files")
public class PerformanceFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le nom original du fichier est obligatoire")
    @Size(max = 255, message = "Le nom du fichier ne peut pas dépasser 255 caractères")
    @Column(name = "original_name", nullable = false)
    private String originalName;

    @NotBlank(message = "Le type de fichier est obligatoire")
    @Size(max = 10, message = "Le type de fichier ne peut pas dépasser 10 caractères")
    @Column(name = "file_type", nullable = false)
    private String fileType;

    @NotNull(message = "La taille du fichier est obligatoire")
    @Column(name = "file_size", nullable = false)
    private Long fileSize;

    @NotNull(message = "Le chemin du fichier ne peut pas être null")
    @Size(max = 500, message = "Le chemin du fichier ne peut pas dépasser 500 caractères")
    @Column(name = "file_path", nullable = false)
    private String filePath;

    @Column(name = "upload_date", nullable = false, updatable = false)
    private LocalDateTime uploadDate;

    // Association optionnelle à une performance
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "performance_id")
    private Performance performance;

    // Contenu binaire du fichier (stocké en base)
    @Lob
    @Basic(fetch = FetchType.LAZY)
    @JdbcTypeCode(SqlTypes.BINARY)
    @Column(name = "content", columnDefinition = "bytea")
    private byte[] content;


    // Constructeurs
    public PerformanceFile() {
    }

    public PerformanceFile(String originalName, String fileType, Long fileSize, String filePath) {
        this.originalName = originalName;
        this.fileType = fileType;
        this.fileSize = fileSize;
        this.filePath = filePath;
        this.uploadDate = LocalDateTime.now();
    }

    // Méthodes de cycle de vie JPA
    @PrePersist
    protected void onCreate() {
        if (uploadDate == null) {
            uploadDate = LocalDateTime.now();
        }
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

    public Performance getPerformance() {
        return performance;
    }

    public void setPerformance(Performance performance) {
        this.performance = performance;
    }
    
    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }
    
    @Override
    public String toString() {
        return "PerformanceFile{" +
                "id=" + id +
                ", originalName='" + originalName + '\'' +
                ", fileType='" + fileType + '\'' +
                ", fileSize=" + fileSize +
                ", filePath='" + filePath + '\'' +
                ", uploadDate=" + uploadDate +
                '}';
    }
}
