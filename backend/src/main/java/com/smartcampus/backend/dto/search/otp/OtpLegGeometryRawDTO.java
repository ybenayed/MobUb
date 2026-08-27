// OtpLegGeometryRawDTO.java
package com.smartcampus.backend.dto.search.otp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OtpLegGeometryRawDTO {
    private String points; // polyline encodé (Google Encoded Polyline Algorithm, precision 5)

    public String getPoints() { return points; }
    public void setPoints(String points) { this.points = points; }
}