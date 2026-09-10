package com.ObservatoireCampus.mobile.model.auth

/**
 * Correspond a ChangePasswordRequest cote backend.
 * Different de ResetPasswordRequestDto (mot de passe oublie via code email) :
 * ici l'utilisateur est connecte et fournit son ancien mot de passe.
 */
data class ChangePasswordRequestDto(
    val oldPassword: String,
    val newPassword: String
)