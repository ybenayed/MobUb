package com.ObservatoireCampus.mobile.model.auth

data class LoginRequestDto(
    val username: String,
    val email: String,
    val password: String
)