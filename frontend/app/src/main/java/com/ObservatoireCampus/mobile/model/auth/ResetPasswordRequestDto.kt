package com.ObservatoireCampus.mobile.model.auth

data class ResetPasswordRequestDto(
    val email: String,
    val code: String,
    val newPassword: String
)