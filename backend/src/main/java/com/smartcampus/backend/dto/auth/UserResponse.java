package com.smartcampus.backend.dto.auth;

import com.smartcampus.backend.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Ce qu'on renvoie au client apres login/consultation.
 * Ne contient JAMAIS le mot de passe, meme hashe.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {

    private Long id;
    private String username;
    private String email;
    private String phoneNumber;
    private String nationality;
    private String residence;
    private String role;
    private LocalDateTime createdAt;

    public static UserResponse fromEntity(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .nationality(user.getNationality())
                .residence(user.getResidence())
                .role(user.getRole().name())
                .createdAt(user.getCreatedAt())
                .build();
    }
}