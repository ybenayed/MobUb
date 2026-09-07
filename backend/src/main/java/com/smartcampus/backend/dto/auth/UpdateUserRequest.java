package com.smartcampus.backend.dto.auth;

import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Champs modifiables sur le profil. Tous optionnels :
 * seuls les champs non nuls envoyes par le client seront mis a jour.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserRequest {

    @Email(message = "Format d'email invalide")
    private String email;

    private String phoneNumber;

    private String nationality;

    private String residence;
}