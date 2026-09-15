package com.smartcampus.backend.dto;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CampusUpdateDTO {
    private String name;
    private String city;
}