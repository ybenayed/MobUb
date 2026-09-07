package com.ObservatoireCampus.mobile.model.auth

data class AuthResponseDto(
    val token: String,
    val tokenType: String,
    val user: UserDto
)