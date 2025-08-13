package com.volleyball.performanceservice.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entité représentant les performances d'un joueur lors d'un match ou entraînement
 */
@Entity
@Table(name = "performances")
public class Performance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Le joueur est obligatoire")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player_id", nullable = false)
    @JsonIgnoreProperties({"absences", "performances", "hibernateLazyInitializer", "handler"})
    private Player player;

    @NotNull(message = "La date est obligatoire")
    @Column(name = "date_performance", nullable = false)
    private LocalDate datePerformance;

    @NotNull(message = "Le type de performance est obligatoire")
    @Enumerated(EnumType.STRING)
    @Column(name = "type_performance", nullable = false)
    private TypePerformance typePerformance;

    // Statistiques offensives
    @Min(value = 0, message = "Le nombre d'attaques ne peut pas être négatif")
    @Column(name = "attaques_totales")
    private Integer attaquesTotales = 0;

    @Min(value = 0, message = "Le nombre d'attaques réussies ne peut pas être négatif")
    @Column(name = "attaques_reussies")
    private Integer attaquesReussies = 0;

    @Min(value = 0, message = "Le nombre d'aces ne peut pas être négatif")
    @Column(name = "aces")
    private Integer aces = 0;

    // Statistiques défensives
    @Min(value = 0, message = "Le nombre de blocs ne peut pas être négatif")
    @Column(name = "blocs")
    private Integer blocs = 0;

    @Min(value = 0, message = "Le nombre de réceptions ne peut pas être négatif")
    @Column(name = "receptions_totales")
    private Integer receptionsTotales = 0;

    @Min(value = 0, message = "Le nombre de réceptions réussies ne peut pas être négatif")
    @Column(name = "receptions_reussies")
    private Integer receptionsReussies = 0;

    @Min(value = 0, message = "Le nombre de défenses ne peut pas être négatif")
    @Column(name = "defenses")
    private Integer defenses = 0;

    // Statistiques de service
    @Min(value = 0, message = "Le nombre de services ne peut pas être négatif")
    @Column(name = "services_totaux")
    private Integer servicesTotaux = 0;

    @Min(value = 0, message = "Le nombre de services réussis ne peut pas être négatif")
    @Column(name = "services_reussis")
    private Integer servicesReussis = 0;

    // Erreurs
    @Min(value = 0, message = "Le nombre d'erreurs d'attaque ne peut pas être négatif")
    @Column(name = "erreurs_attaque")
    private Integer erreursAttaque = 0;

    @Min(value = 0, message = "Le nombre d'erreurs de service ne peut pas être négatif")
    @Column(name = "erreurs_service")
    private Integer erreursService = 0;

    @Min(value = 0, message = "Le nombre d'erreurs de réception ne peut pas être négatif")
    @Column(name = "erreurs_reception")
    private Integer erreursReception = 0;

    // Temps de jeu
    @Min(value = 0, message = "Le temps de jeu ne peut pas être négatif")
    @Column(name = "temps_jeu_minutes")
    private Integer tempsJeuMinutes = 0;

    // Note globale (sur 10)
    @Column(name = "note_globale")
    private Double noteGlobale;

    @Column(name = "commentaires", length = 1000)
    private String commentaires;

    @Column(name = "date_creation", nullable = false, updatable = false)
    private LocalDateTime dateCreation;

    @Column(name = "date_modification")
    private LocalDateTime dateModification;

    // Constructeurs
    public Performance() {
    }

    public Performance(Player player, LocalDate datePerformance, TypePerformance typePerformance) {
        this.player = player;
        this.datePerformance = datePerformance;
        this.typePerformance = typePerformance;
    }

    // Méthodes de cycle de vie JPA
    @PrePersist
    protected void onCreate() {
        dateCreation = LocalDateTime.now();
        dateModification = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        dateModification = LocalDateTime.now();
    }

    // Méthodes de calcul
    public double getPourcentageAttaque() {
        if (attaquesTotales == 0) return 0.0;
        return (double) attaquesReussies / attaquesTotales * 100;
    }

    public double getPourcentageReception() {
        if (receptionsTotales == 0) return 0.0;
        return (double) receptionsReussies / receptionsTotales * 100;
    }

    public double getPourcentageService() {
        if (servicesTotaux == 0) return 0.0;
        return (double) servicesReussis / servicesTotaux * 100;
    }

    public int getTotalErreurs() {
        return erreursAttaque + erreursService + erreursReception;
    }

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
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

    public LocalDateTime getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(LocalDateTime dateCreation) {
        this.dateCreation = dateCreation;
    }

    public LocalDateTime getDateModification() {
        return dateModification;
    }

    public void setDateModification(LocalDateTime dateModification) {
        this.dateModification = dateModification;
    }

    @Override
    public String toString() {
        return "Performance{" +
                "id=" + id +
                ", player=" + (player != null ? player.getNom() + " " + player.getPrenom() : null) +
                ", datePerformance=" + datePerformance +
                ", typePerformance=" + typePerformance +
                ", attaquesTotales=" + attaquesTotales +
                ", attaquesReussies=" + attaquesReussies +
                ", noteGlobale=" + noteGlobale +
                '}';
    }
}
