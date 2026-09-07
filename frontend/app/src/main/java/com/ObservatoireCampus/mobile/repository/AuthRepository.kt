package com.ObservatoireCampus.mobile.repository

import com.ObservatoireCampus.mobile.data.TokenManager
import com.ObservatoireCampus.mobile.model.auth.AuthResponseDto
import com.ObservatoireCampus.mobile.model.auth.LoginRequestDto
import com.ObservatoireCampus.mobile.model.auth.RegisterRequestDto
import com.ObservatoireCampus.mobile.model.auth.UserDto
import com.ObservatoireCampus.mobile.network.AuthApi

class AuthRepository(
    private val authApi: AuthApi,
    private val tokenManager: TokenManager
) {
    suspend fun login(loginRequest: LoginRequestDto): Result<AuthResponseDto> {
        return try {
            val response = authApi.login(loginRequest)
            if (response.isSuccessful && response.body() != null) {
                val authResponse = response.body()!!
                tokenManager.saveToken(authResponse.token)
                Result.success(authResponse)
            } else {
                Result.failure(Exception("Identifiants incorrects ou erreur serveur (${response.code()})"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun register(registerRequest: RegisterRequestDto): Result<AuthResponseDto> {
        return try {
            val response = authApi.register(registerRequest)
            if (response.isSuccessful && response.body() != null) {
                val authResponse = response.body()!!
                tokenManager.saveToken(authResponse.token)
                Result.success(authResponse)
            } else {
                Result.failure(Exception("Erreur lors de l'inscription (${response.code()})"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getCurrentUser(): Result<UserDto> {
        return try {
            val response = authApi.getCurrentUser()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Session expirée ou invalide"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun logout() {
        tokenManager.clearToken()
    }

    fun isLoggedIn(): Boolean = tokenManager.hasToken()
}