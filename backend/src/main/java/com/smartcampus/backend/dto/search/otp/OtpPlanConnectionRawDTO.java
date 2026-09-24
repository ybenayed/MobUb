package com.smartcampus.backend.dto.search.otp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;
import lombok.*;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class OtpPlanConnectionRawDTO {
    private List<OtpPlanEdgeRawDTO> edges;
}