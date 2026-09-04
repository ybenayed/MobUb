package com.smartcampus.backend.dto.search.otp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;
import lombok.*;

/**
 * Remplace OtpPlanRawDTO (obsolete, a supprimer) : le champ racine n'est plus
 * "plan { itineraries }" mais "planConnection { edges { node } }" (pagination
 * Relay), propre a la nouvelle API GraphQL "GTFS GraphQL v2" d'OTP2.
 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class OtpPlanConnectionRawDTO {
    private List<OtpPlanEdgeRawDTO> edges;
}