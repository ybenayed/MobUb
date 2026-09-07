package com.smartcampus.backend.dto.auth;

import com.smartcampus.backend.validation.ValidPassword;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Champs recus depuis l'ecran "Creer un compte".
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {

    @NotBlank(message = "Le nom d'utilisateur est obligatoire")
    @Size(min = 3, max = 50, message = "Le nom d'utilisateur doit contenir entre 3 et 50 caracteres")
    private String username;

    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "Format d'email invalide")
    private String email;

    @NotBlank(message = "Le numero de telephone est obligatoire")
    private String phoneNumber;

    @NotBlank(message = "La nationalite est obligatoire")
    private String nationality;

    @NotBlank(message = "Le lieu de residence est obligatoire")
    private String residence;

    @NotBlank(message = "Le mot de passe est obligatoire")
    @ValidPassword
    private String password;
}