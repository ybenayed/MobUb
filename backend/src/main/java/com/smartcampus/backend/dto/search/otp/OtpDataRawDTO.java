package com.smartcampus.backend.dto.search.otp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
//dto pour la reponse brute de OTP (JSON) pour un calcul d'itineraire
@JsonIgnoreProperties(ignoreUnknown = true)
public class OtpDataRawDTO {
    private OtpPlanConnectionRawDTO planConnection;

    public OtpPlanConnectionRawDTO getPlanConnection() { return planConnection; }
    public void setPlanConnection(OtpPlanConnectionRawDTO planConnection) { this.planConnection = planConnection; }
}