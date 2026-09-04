package com.smartcampus.backend.dto.search.otp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

/**
 * Represente le type GraphQL LegTime (departure/arrival sur un Leg).
 * On ne lit que scheduledTime (horaire theorique) ; "estimated" (temps reel)
 * est ignore pour l'instant, ignoreUnknown=true s'en charge.
 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class OtpLegTimeRawDTO {
    private String scheduledTime; // ISO-8601 OffsetDateTime, ex: "2026-09-01T14:30:00+02:00"
}