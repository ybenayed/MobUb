package com.smartcampus.backend.dto.search.otp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** Racine de la reponse GraphQL brute d'OTP.  */
@JsonIgnoreProperties(ignoreUnknown = true)
public class OtpGraphQlResponseDTO {
    private OtpDataRawDTO data;

    public OtpDataRawDTO getData() { return data; }
    public void setData(OtpDataRawDTO data) { this.data = data; }
}