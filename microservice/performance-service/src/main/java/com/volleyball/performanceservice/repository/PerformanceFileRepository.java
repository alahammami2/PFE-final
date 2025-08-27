package com.volleyball.performanceservice.repository;

import com.volleyball.performanceservice.model.PerformanceFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository pour la gestion des fichiers de performance
 */
@Repository
public interface PerformanceFileRepository extends JpaRepository<PerformanceFile, Long> {

    /**
     * Trouver un fichier par son nom original
     */
    Optional<PerformanceFile> findByOriginalName(String originalName);

    /**
     * Trouver des fichiers par type
     */
    List<PerformanceFile> findByFileType(String fileType);

    /**
     * Trouver des fichiers par performance
     */
    List<PerformanceFile> findByPerformanceId(Long performanceId);

    /**
     * Trouver des fichiers par taille (supérieure à une valeur)
     */
    List<PerformanceFile> findByFileSizeGreaterThan(Long fileSize);

    /**
     * Trouver des fichiers par taille (dans une fourchette)
     */
    List<PerformanceFile> findByFileSizeBetween(Long minSize, Long maxSize);

    /**
     * Trouver des fichiers par date d'upload
     */
    List<PerformanceFile> findByUploadDateBetween(LocalDateTime dateDebut, LocalDateTime dateFin);

    /**
     * Trouver des fichiers récents (après une date)
     */
    List<PerformanceFile> findByUploadDateAfter(LocalDateTime date);

    /**
     * Trouver des fichiers par performance et type
     */
    List<PerformanceFile> findByPerformanceIdAndFileType(Long performanceId, String fileType);

    /**
     * Compter les fichiers par performance
     */
    Long countByPerformanceId(Long performanceId);

    /**
     * Compter les fichiers par type
     */
    Long countByFileType(String fileType);

    /**
     * Obtenir la taille totale des fichiers par performance
     */
    @Query("SELECT SUM(pf.fileSize) FROM PerformanceFile pf WHERE pf.performance.id = :performanceId")
    Long getTotalFileSizeByPerformance(@Param("performanceId") Long performanceId);

    /**
     * Obtenir la taille totale de tous les fichiers
     */
    @Query("SELECT SUM(pf.fileSize) FROM PerformanceFile pf")
    Long getTotalFileSize();

    /**
     * Rechercher des fichiers par nom (LIKE)
     */
    @Query("SELECT pf FROM PerformanceFile pf WHERE pf.originalName LIKE %:searchTerm%")
    List<PerformanceFile> searchByFileName(@Param("searchTerm") String searchTerm);

    /**
     * Obtenir les fichiers les plus récents
     */
    @Query("SELECT pf FROM PerformanceFile pf ORDER BY pf.uploadDate DESC")
    List<PerformanceFile> findRecentFiles();

    /**
     * Obtenir les fichiers les plus volumineux
     */
    @Query("SELECT pf FROM PerformanceFile pf ORDER BY pf.fileSize DESC")
    List<PerformanceFile> findLargestFiles();

    /**
     * Obtenir les statistiques des fichiers par type
     */
    @Query("SELECT pf.fileType, COUNT(pf), SUM(pf.fileSize) FROM PerformanceFile pf GROUP BY pf.fileType")
    List<Object[]> getFileStatsByType();

    /**
     * Charger tous les fichiers avec leurs associations nécessaires pour éviter les lazy-loading issues
     */
    @Query("SELECT DISTINCT pf FROM PerformanceFile pf " +
           "LEFT JOIN FETCH pf.performance p " +
           "LEFT JOIN FETCH p.player")
    List<PerformanceFile> findAllWithPerformanceAndPlayer();
}
