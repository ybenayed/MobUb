// OtpPlanEdgeRawDTO.java
package com.smartcampus.backend.dto.search.otp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

/** Un "edge" de la pagination Relay utilisee par planConnection : { cursor, node }. */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class OtpPlanEdgeRawDTO {
    private OtpItineraryRawDTO node;
}