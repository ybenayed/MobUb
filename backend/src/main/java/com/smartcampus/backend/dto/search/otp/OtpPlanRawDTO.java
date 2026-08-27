// OtpPlanRawDTO.java
package com.smartcampus.backend.dto.search.otp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OtpPlanRawDTO {
    private List<OtpItineraryRawDTO> itineraries;

    public List<OtpItineraryRawDTO> getItineraries() { return itineraries; }
    public void setItineraries(List<OtpItineraryRawDTO> itineraries) { this.itineraries = itineraries; }
}