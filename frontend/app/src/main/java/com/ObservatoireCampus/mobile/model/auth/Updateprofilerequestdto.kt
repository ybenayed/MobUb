package com.ObservatoireCampus.mobile.model.auth

/**
 * Correspond a UpdateUserRequest cote backend.
 * Le username n'est pas modifiable (identifiant du compte / sujet du JWT).
 */
data class UpdateProfileRequestDto(
    val email: String,
    val phoneNumber: String?,
    val nationality: String?,
    val residence: String?
)