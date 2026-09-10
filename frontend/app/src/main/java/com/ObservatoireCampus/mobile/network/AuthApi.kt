package com.ObservatoireCampus.mobile.network

import com.ObservatoireCampus.mobile.model.auth.AuthResponseDto
import com.ObservatoireCampus.mobile.model.auth.LoginRequestDto
import com.ObservatoireCampus.mobile.model.auth.RegisterRequestDto
import com.ObservatoireCampus.mobile.model.auth.UserDto
import com.ObservatoireCampus.mobile.model.auth.ResetPasswordRequestDto
import com.ObservatoireCampus.mobile.model.auth.ForgotPasswordRequestDto
import com.ObservatoireCampus.mobile.model.auth.UpdateProfileRequestDto
import com.ObservatoireCampus.mobile.model.auth.ChangePasswordRequestDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT

interface AuthApi {
    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequestDto): Response<AuthResponseDto>

    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequestDto): Response<AuthResponseDto>

    @GET("api/users/me")
    suspend fun getCurrentUser(): Response<UserDto>

    @POST("api/auth/forgot-password")
    suspend fun requestForgotPassword(@Body request: ForgotPasswordRequestDto): Response<Unit>

    @POST("api/auth/reset-password")
    suspend fun resetPassword(@Body request: ResetPasswordRequestDto): Response<Unit>

    // Screen "Mon compte" : self-service, identifie via le JWT cote serveur
    @PUT("api/users/me")
    suspend fun updateProfile(@Body request: UpdateProfileRequestDto): Response<UserDto>

    @PUT("api/users/me/password")
    suspend fun changePassword(@Body request: ChangePasswordRequestDto): Response<Unit>

}