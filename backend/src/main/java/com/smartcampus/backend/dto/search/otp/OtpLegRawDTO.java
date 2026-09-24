package com.smartcampus.backend.dto.search.otp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class OtpLegRawDTO {
    private String mode;
    private Double distance; // metres
    private Boolean rentedBike; 
    private OtpPlaceRawDTO from;
    private OtpPlaceRawDTO to;
    private OtpRouteRawDTO route;
    private OtpLegGeometryRawDTO legGeometry;
    private OtpLegTimeRawDTO start;
    private OtpLegTimeRawDTO end;
}