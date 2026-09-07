package com.ObservatoireCampus.mobile.model.auth

data class UserDto(
    val id: Long,
    val username: String,
    val email: String,
    val phoneNumber: String?,
    val nationality: String?,
    val residence: String?,
    val role: String,
    val createdAt: String
)