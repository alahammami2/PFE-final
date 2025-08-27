package com.volleyball.planningservice.dto;

public class MatchResponse {
    private Long id;
    private String nomEquipe;
    private Integer points;

    public MatchResponse() {}

    public MatchResponse(Long id, String nomEquipe, Integer points) {
        this.id = id;
        this.nomEquipe = nomEquipe;
        this.points = points;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNomEquipe() {
        return nomEquipe;
    }

    public void setNomEquipe(String nomEquipe) {
        this.nomEquipe = nomEquipe;
    }

    public Integer getPoints() {
        return points;
    }

    public void setPoints(Integer points) {
        this.points = points;
    }
}
