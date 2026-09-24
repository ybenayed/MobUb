package com.smartcampus.backend.entity.search;

import jakarta.persistence.Embeddable;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Embeddable
public class PlaceRef {
    private String name;
    private Double lat;
    private Double lon;
}