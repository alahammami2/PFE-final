package com.volleyball.authservice.dto;

import com.volleyball.authservice.model.Role;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class UserWithPasswordResponse {
    private Long id;
    private String nom;
    private String prenom;
    private String email;
    private Role role;
    private Boolean actif;
    private LocalDateTime dateCreation;
    private String telephone;
    private BigDecimal salaire;
    private String motDePasse;

    public UserWithPasswordResponse() {}

    public UserWithPasswordResponse(Long id, String nom, String prenom, String email, Role role, Boolean actif,
                                    LocalDateTime dateCreation, String telephone, BigDecimal salaire, String motDePasse) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.role = role;
        this.actif = actif;
        this.dateCreation = dateCreation;
        this.telephone = telephone;
        this.salaire = salaire;
        this.motDePasse = motDePasse;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public Boolean getActif() { return actif; }
    public void setActif(Boolean actif) { this.actif = actif; }

    public LocalDateTime getDateCreation() { return dateCreation; }
    public void setDateCreation(LocalDateTime dateCreation) { this.dateCreation = dateCreation; }

    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }

    public BigDecimal getSalaire() { return salaire; }
    public void setSalaire(BigDecimal salaire) { this.salaire = salaire; }

    public String getMotDePasse() { return motDePasse; }
    public void setMotDePasse(String motDePasse) { this.motDePasse = motDePasse; }
}
