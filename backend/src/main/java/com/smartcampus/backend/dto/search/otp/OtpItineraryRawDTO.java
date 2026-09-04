// OtpItineraryRawDTO.java
package com.smartcampus.backend.dto.search.otp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;
import lombok.*;

/**
 * Miroir du type GraphQL Itinerary (nouveau schema OTP2 "planConnection").
 * numberOfTransfers (pas "transfers"), walkDistance et emissionsPerPerson.co2
 * sont fournis nativement par OTP : plus besoin de les recalculer a la main
 * cote backend (voir OtpItineraryService, EmissionFactors.java supprime).
 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class OtpItineraryRawDTO {
    private long duration;
    private int numberOfTransfers;
    private Double walkDistance;       // metres, nullable selon schema
    private Double accessibilityScore; // 0.0 (pas accessible) a 1.0 (accessible), nullable
    private OtpEmissionsRawDTO emissionsPerPerson;
    private List<OtpLegRawDTO> legs;
}