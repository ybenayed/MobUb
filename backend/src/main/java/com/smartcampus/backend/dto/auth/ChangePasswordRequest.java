package com.smartcampus.backend.dto.auth;

import com.smartcampus.backend.validation.ValidPassword;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Requete du screen "Mon compte" -> "Changer le mot de passe".
 * ici l'utilisateur est deja connecte et doit fournir son ancien mot de passe.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChangePasswordRequest {

    @NotBlank(message = "L'ancien mot de passe est requis")
    private String oldPassword;

    // Reutilise la meme regle que RegisterRequest (@ValidPassword) au lieu de
    @NotBlank(message = "Le nouveau mot de passe est requis")
    @ValidPassword
    private String newPassword;
}