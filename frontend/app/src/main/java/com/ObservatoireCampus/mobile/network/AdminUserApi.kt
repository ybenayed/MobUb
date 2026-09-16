package com.ObservatoireCampus.mobile.network

import com.ObservatoireCampus.mobile.model.auth.UserDto
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Path

interface AdminUserApi {
    // Renvoie tous les utilisateurs (USER + ADMIN) ; le filtrage "USER seulement"
    // se fait cote ViewModel pour ne pas dupliquer d'endpoint backend.
    @GET("api/admin/users")
    suspend fun getAllUsers(): Response<List<UserDto>>

    @DELETE("api/admin/users/{id}")
    suspend fun deleteUser(@Path("id") id: Long): Response<Unit>
}