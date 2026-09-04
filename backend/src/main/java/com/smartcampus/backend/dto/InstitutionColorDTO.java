package com.smartcampus.backend.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InstitutionColorDTO {
    private String institution;
    private String color;
}