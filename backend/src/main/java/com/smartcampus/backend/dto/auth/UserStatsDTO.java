package com.smartcampus.backend.dto.auth;

import lombok.*;

import java.util.Map;
/**
 * DTO pour les statistiques des utilisateurs.
 * Contient le nombre total d'utilisateurs, d'administrateurs et d'utilisateurs standard,
 * ainsi que des statistiques par nationalité et par résidence.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserStatsDTO {
    private long totalUsers;
    private long totalAdmins;
    private long totalStandardUsers;
    private Map<String, Long> byNationality;
    private Map<String, Long> byResidence;
}