// OtpItineraryRawDTO.java
package com.smartcampus.backend.dto.search.otp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.smartcampus.backend.dto.search.otp.OtpLegRawDTO;
import java.util.List;
import lombok.*;
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class OtpItineraryRawDTO {
    private long duration;
    private List<OtpLegRawDTO> legs;

    public long getDuration() { return duration; }
    public void setDuration(long duration) { this.duration = duration; }
    public List<OtpLegRawDTO> getLegs() { return legs; }
    public void setLegs(List<OtpLegRawDTO> legs) { this.legs = legs; }
}