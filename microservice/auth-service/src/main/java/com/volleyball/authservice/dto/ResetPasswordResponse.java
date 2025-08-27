package com.volleyball.authservice.dto;

public class ResetPasswordResponse {
    private Long userId;
    private String motDePasseTemporaire;

    public ResetPasswordResponse() {}

    public ResetPasswordResponse(Long userId, String motDePasseTemporaire) {
        this.userId = userId;
        this.motDePasseTemporaire = motDePasseTemporaire;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getMotDePasseTemporaire() {
        return motDePasseTemporaire;
    }

    public void setMotDePasseTemporaire(String motDePasseTemporaire) {
        this.motDePasseTemporaire = motDePasseTemporaire;
    }
}
