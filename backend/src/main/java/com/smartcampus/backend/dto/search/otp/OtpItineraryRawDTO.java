package com.smartcampus.backend.dto.search.otp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;
import lombok.*;

/**
 * numberOfTransfers (pas "transfers"), walkDistance et emissionsPerPerson.co2
 * sont fournis nativement par OTP : plus besoin de les recalculer a la main
 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class OtpItineraryRawDTO {
    private long duration;
    private int numberOfTransfers;
    private Double walkDistance;       // metres, nullable selon schema
    private Double accessibilityScore; 
    private OtpEmissionsRawDTO emissionsPerPerson;
    private List<OtpLegRawDTO> legs;
}