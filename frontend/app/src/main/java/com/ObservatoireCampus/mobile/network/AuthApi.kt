package com.ObservatoireCampus.mobile.network

import com.ObservatoireCampus.mobile.model.auth.AuthResponseDto
import com.ObservatoireCampus.mobile.model.auth.LoginRequestDto
import com.ObservatoireCampus.mobile.model.auth.RegisterRequestDto
import com.ObservatoireCampus.mobile.model.auth.UserDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface AuthApi {
    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequestDto): Response<AuthResponseDto>

    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequestDto): Response<AuthResponseDto>

    @GET("api/users/me")
    suspend fun getCurrentUser(): Response<UserDto>
}