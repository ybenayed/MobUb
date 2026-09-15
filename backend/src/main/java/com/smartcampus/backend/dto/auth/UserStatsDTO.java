package com.smartcampus.backend.dto.auth;

import lombok.*;

import java.util.Map;

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