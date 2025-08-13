package com.volleyball.performanceservice.dto;

import com.volleyball.performanceservice.model.TypePerformance;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

/**
 * DTO pour la création d'une performance
 */
public class CreatePerformanceRequest {

    @NotNull(message = "L'ID du joueur est obligatoire")
    private Long playerId;

    @NotNull(message = "La date de performance est obligatoire")
    private LocalDate datePerformance;

    @NotNull(message = "Le type de performance est obligatoire")
    private TypePerformance typePerformance;

    // Statistiques offensives
    @Min(value = 0, message = "Le nombre d'attaques ne peut pas être négatif")
    private Integer attaquesTotales = 0;

    @Min(value = 0, message = "Le nombre d'attaques réussies ne peut pas être négatif")
    private Integer attaquesReussies = 0;

    @Min(value = 0, message = "Le nombre d'aces ne peut pas être négatif")
    private Integer aces = 0;

    // Statistiques défensives
    @Min(value = 0, message = "Le nombre de blocs ne peut pas être négatif")
    private Integer blocs = 0;

    @Min(value = 0, message = "Le nombre de réceptions ne peut pas être négatif")
    private Integer receptionsTotales = 0;

    @Min(value = 0, message = "Le nombre de réceptions réussies ne peut pas être négatif")
    private Integer receptionsReussies = 0;

    @Min(value = 0, message = "Le nombre de défenses ne peut pas être négatif")
    private Integer defenses = 0;

    // Statistiques de service
    @Min(value = 0, message = "Le nombre de services ne peut pas être négatif")
    private Integer servicesTotaux = 0;

    @Min(value = 0, message = "Le nombre de services réussis ne peut pas être négatif")
    private Integer servicesReussis = 0;

    // Erreurs
    @Min(value = 0, message = "Le nombre d'erreurs d'attaque ne peut pas être négatif")
    private Integer erreursAttaque = 0;

    @Min(value = 0, message = "Le nombre d'erreurs de service ne peut pas être négatif")
    private Integer erreursService = 0;

    @Min(value = 0, message = "Le nombre d'erreurs de réception ne peut pas être négatif")
    private Integer erreursReception = 0;

    // Temps de jeu
    @Min(value = 0, message = "Le temps de jeu ne peut pas être négatif")
    private Integer tempsJeuMinutes = 0;

    // Note globale (sur 10)
    @DecimalMin(value = "0.0", message = "La note ne peut pas être négative")
    @DecimalMax(value = "10.0", message = "La note ne peut pas dépasser 10")
    private Double noteGlobale;

    @Size(max = 1000, message = "Les commentaires ne peuvent pas dépasser 1000 caractères")
    private String commentaires;

    // Constructeurs
    public CreatePerformanceRequest() {
    }

    public CreatePerformanceRequest(Long playerId, LocalDate datePerformance, TypePerformance typePerformance) {
        this.playerId = playerId;
        this.datePerformance = datePerformance;
        this.typePerformance = typePerformance;
    }

    // Getters et Setters
    public Long getPlayerId() {
        return playerId;
    }

    public void setPlayerId(Long playerId) {
        this.playerId = playerId;
    }

    public LocalDate getDatePerformance() {
        return datePerformance;
    }

    public void setDatePerformance(LocalDate datePerformance) {
        this.datePerformance = datePerformance;
    }

    public TypePerformance getTypePerformance() {
        return typePerformance;
    }

    public void setTypePerformance(TypePerformance typePerformance) {
        this.typePerformance = typePerformance;
    }

    public Integer getAttaquesTotales() {
        return attaquesTotales;
    }

    public void setAttaquesTotales(Integer attaquesTotales) {
        this.attaquesTotales = attaquesTotales;
    }

    public Integer getAttaquesReussies() {
        return attaquesReussies;
    }

    public void setAttaquesReussies(Integer attaquesReussies) {
        this.attaquesReussies = attaquesReussies;
    }

    public Integer getAces() {
        return aces;
    }

    public void setAces(Integer aces) {
        this.aces = aces;
    }

    public Integer getBlocs() {
        return blocs;
    }

    public void setBlocs(Integer blocs) {
        this.blocs = blocs;
    }

    public Integer getReceptionsTotales() {
        return receptionsTotales;
    }

    public void setReceptionsTotales(Integer receptionsTotales) {
        this.receptionsTotales = receptionsTotales;
    }

    public Integer getReceptionsReussies() {
        return receptionsReussies;
    }

    public void setReceptionsReussies(Integer receptionsReussies) {
        this.receptionsReussies = receptionsReussies;
    }

    public Integer getDefenses() {
        return defenses;
    }

    public void setDefenses(Integer defenses) {
        this.defenses = defenses;
    }

    public Integer getServicesTotaux() {
        return servicesTotaux;
    }

    public void setServicesTotaux(Integer servicesTotaux) {
        this.servicesTotaux = servicesTotaux;
    }

    public Integer getServicesReussis() {
        return servicesReussis;
    }

    public void setServicesReussis(Integer servicesReussis) {
        this.servicesReussis = servicesReussis;
    }

    public Integer getErreursAttaque() {
        return erreursAttaque;
    }

    public void setErreursAttaque(Integer erreursAttaque) {
        this.erreursAttaque = erreursAttaque;
    }

    public Integer getErreursService() {
        return erreursService;
    }

    public void setErreursService(Integer erreursService) {
        this.erreursService = erreursService;
    }

    public Integer getErreursReception() {
        return erreursReception;
    }

    public void setErreursReception(Integer erreursReception) {
        this.erreursReception = erreursReception;
    }

    public Integer getTempsJeuMinutes() {
        return tempsJeuMinutes;
    }

    public void setTempsJeuMinutes(Integer tempsJeuMinutes) {
        this.tempsJeuMinutes = tempsJeuMinutes;
    }

    public Double getNoteGlobale() {
        return noteGlobale;
    }

    public void setNoteGlobale(Double noteGlobale) {
        this.noteGlobale = noteGlobale;
    }

    public String getCommentaires() {
        return commentaires;
    }

    public void setCommentaires(String commentaires) {
        this.commentaires = commentaires;
    }

    @Override
    public String toString() {
        return "CreatePerformanceRequest{" +
                "playerId=" + playerId +
                ", datePerformance=" + datePerformance +
                ", typePerformance=" + typePerformance +
                ", attaquesTotales=" + attaquesTotales +
                ", attaquesReussies=" + attaquesReussies +
                ", noteGlobale=" + noteGlobale +
                '}';
    }
}
