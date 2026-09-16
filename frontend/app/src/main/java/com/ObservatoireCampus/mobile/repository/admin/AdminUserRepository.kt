package com.ObservatoireCampus.mobile.repository.admin

import com.ObservatoireCampus.mobile.model.auth.UserDto
import com.ObservatoireCampus.mobile.network.AdminUserApi

class AdminUserRepository(private val api: AdminUserApi) {

    suspend fun getUsers(): Result<List<UserDto>> {
        return try {
            val response = api.getAllUsers()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Erreur lors du chargement des utilisateurs (${response.code()})"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteUser(id: Long): Result<Unit> {
        return try {
            val response = api.deleteUser(id)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Erreur lors de la suppression (${response.code()})"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}