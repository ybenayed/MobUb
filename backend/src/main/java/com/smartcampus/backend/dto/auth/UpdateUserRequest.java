package com.smartcampus.backend.dto.auth;

import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// Requete du screen "Mon compte" -> "Modifier mes informations personnelles".
//mot de passe non requis ici , juste les infos personnelles (email, phoneNumber, nationality, residence)
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