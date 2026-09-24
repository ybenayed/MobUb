package com.ObservatoireCampus.mobile.model.auth

/**
 * Correspond a UpdateUserRequest cote backend.
 */
data class UpdateProfileRequestDto(
    val email: String,
    val phoneNumber: String?,
    val nationality: String?,
    val residence: String?
)