package com.smartcampus.backend.dto.admin;

import lombok.*;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class ModeCountDTO {
    private String mode;
    private long count;
}