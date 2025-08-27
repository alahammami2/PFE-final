package com.volleyball.performanceservice.dto;

import jakarta.validation.constraints.*;


/**
 * DTO pour la création d'une performance
 */
public class CreatePerformanceRequest {

    @NotNull(message = "L'ID du joueur est obligatoire")
    private Long playerId;

    // datePerformance et typePerformance supprimées

    // Statistiques offensives (attaques supprimées)

    @Min(value = 0, message = "Le nombre d'aces ne peut pas être négatif")
    private Integer aces = 0;

    // Statistiques défensives (blocs supprimés)

    @Min(value = 0, message = "Le nombre de réceptions ne peut pas être négatif")
    private Integer receptionsTotales = 0;

    @Min(value = 0, message = "Le nombre de réceptions réussies ne peut pas être négatif")
    private Integer receptionsReussies = 0;

    // Blocs
    @Min(value = 0, message = "Le nombre de blocs ne peut pas être négatif")
    private Integer bloc = 0;


    // Statistiques de service
    @Min(value = 0, message = "Le nombre de services ne peut pas être négatif")
    private Integer servicesTotaux = 0;

    @Min(value = 0, message = "Le nombre de services réussis ne peut pas être négatif")
    private Integer servicesReussis = 0;

    // Erreurs (erreurs d'attaque supprimées)

    @Min(value = 0, message = "Le nombre d'erreurs de service ne peut pas être négatif")
    private Integer erreursService = 0;

    @Min(value = 0, message = "Le nombre d'erreurs de réception ne peut pas être négatif")
    private Integer erreursReception = 0;

    // Temps de jeu


    // Note globale (sur 10)
    @DecimalMin(value = "0.0", message = "La note ne peut pas être négative")
    @DecimalMax(value = "10.0", message = "La note ne peut pas dépasser 10")
    private Double noteGlobale;

    @Size(max = 1000, message = "Les commentaires ne peuvent pas dépasser 1000 caractères")
    private String commentaires;

    // Constructeurs
    public CreatePerformanceRequest() {
    }

    public CreatePerformanceRequest(Long playerId) {
        this.playerId = playerId;
    }

    // Getters et Setters
    public Long getPlayerId() {
        return playerId;
    }

    public void setPlayerId(Long playerId) {
        this.playerId = playerId;
    }

    // Getters/setters supprimés pour datePerformance/typePerformance

    // Attaques supprimées

    public Integer getAces() {
        return aces;
    }

    public void setAces(Integer aces) {
        this.aces = aces;
    }

    // Blocs supprimés

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

    public Integer getBloc() {
        return bloc;
    }

    public void setBloc(Integer bloc) {
        this.bloc = bloc;
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

    // Erreurs d'attaque supprimées

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
                
                
                ", bloc=" + bloc +
                ", noteGlobale=" + noteGlobale +
                '}';
    }
}
