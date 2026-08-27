// OtpRouteRawDTO.java
package com.smartcampus.backend.dto.search.otp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OtpRouteRawDTO {
    private String shortName;
    private String longName;

    public String getShortName() { return shortName; }
    public void setShortName(String shortName) { this.shortName = shortName; }
    public String getLongName() { return longName; }
    public void setLongName(String longName) { this.longName = longName; }
}