package com.ObservatoireCampus.mobile.repository

import com.ObservatoireCampus.mobile.data.TokenManager
import com.ObservatoireCampus.mobile.model.auth.AuthResponseDto
import com.ObservatoireCampus.mobile.model.auth.ForgotPasswordRequestDto
import com.ObservatoireCampus.mobile.model.auth.LoginRequestDto
import com.ObservatoireCampus.mobile.model.auth.RegisterRequestDto
import com.ObservatoireCampus.mobile.model.auth.ResetPasswordRequestDto
import com.ObservatoireCampus.mobile.model.auth.UpdateProfileRequestDto
import com.ObservatoireCampus.mobile.model.auth.ChangePasswordRequestDto
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

    suspend fun requestPasswordReset(email: String): Result<Unit> {
        return try {
            val response = authApi.requestForgotPassword(ForgotPasswordRequestDto(email))
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Impossible d'envoyer le code (${response.code()})"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun confirmPasswordReset(request: ResetPasswordRequestDto): Result<Unit> {
        return try {
            val response = authApi.resetPassword(request)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Code invalide ou expiré (${response.code()})"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun isLoggedIn(): Boolean = tokenManager.hasToken()

    suspend fun updateProfile(request: UpdateProfileRequestDto): Result<UserDto> {
        return try {
            val response = authApi.updateProfile(request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Erreur lors de la mise à jour du profil (${response.code()})"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun changePassword(request: ChangePasswordRequestDto): Result<Unit> {
        return try {
            val response = authApi.changePassword(request)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else if (response.code() == 400 || response.code() == 401) {
                Result.failure(Exception("Ancien mot de passe incorrect"))
            } else {
                Result.failure(Exception("Erreur lors du changement de mot de passe (${response.code()})"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}