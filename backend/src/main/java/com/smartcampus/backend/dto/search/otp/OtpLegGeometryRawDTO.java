package com.smartcampus.backend.dto.search.otp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
// dto pour la reponse brute de OTP (JSON) pour un calcul d'itineraire
@JsonIgnoreProperties(ignoreUnknown = true)
public class OtpLegGeometryRawDTO {
    private String points;

    public String getPoints() { return points; }
    public void setPoints(String points) { this.points = points; }
}