// OtpLegRawDTO.java
package com.smartcampus.backend.dto.search.otp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

/**
 * Miroir du type GraphQL Leg (nouveau schema OTP2 "planConnection").
 * startTime/endTime epoch millis n'existent plus directement : il faut passer
 * par start.scheduledTime / end.scheduledTime (OffsetDateTime ISO-8601),
 * convertis en epoch millis cote service (voir OtpItineraryService.parseEpochMillis).
 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class OtpLegRawDTO {
    private String mode;
    private Double distance; // metres
    private Boolean rentedBike; // true = vélo en libre-service (Vcub), false/null = vélo perso ou autre mode
    private OtpPlaceRawDTO from;
    private OtpPlaceRawDTO to;
    private OtpRouteRawDTO route;
    private OtpLegGeometryRawDTO legGeometry;
    private OtpLegTimeRawDTO start;
    private OtpLegTimeRawDTO end;
}