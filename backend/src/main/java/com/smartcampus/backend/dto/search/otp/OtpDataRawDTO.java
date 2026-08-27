// OtpDataRawDTO.java
package com.smartcampus.backend.dto.search.otp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OtpDataRawDTO {
    private OtpPlanRawDTO plan;

    public OtpPlanRawDTO getPlan() { return plan; }
    public void setPlan(OtpPlanRawDTO plan) { this.plan = plan; }
}