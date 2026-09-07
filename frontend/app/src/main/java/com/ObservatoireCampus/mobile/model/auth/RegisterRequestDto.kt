package com.ObservatoireCampus.mobile.model.auth

data class RegisterRequestDto(
    val username: String,
    val email: String,
    val phoneNumber: String,
    val nationality: String,
    val residence: String,
    val password: String
)