// OtpLegRawDTO.java
package com.smartcampus.backend.dto.search.otp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class OtpLegRawDTO {
    private String mode;
    private long startTime;
    private long endTime;
    private OtpPlaceRawDTO from;
    private OtpPlaceRawDTO to;
    private OtpRouteRawDTO route;
    private OtpLegGeometryRawDTO legGeometry; 
}