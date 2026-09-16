package com.ObservatoireCampus.mobile.network

import com.ObservatoireCampus.mobile.model.admin.AdminStationVDto
import com.ObservatoireCampus.mobile.model.admin.AdminStationVRequestDto
import retrofit2.Response
import retrofit2.http.*

interface AdminInfrastructureApi {
    // Lecture publique existante, reutilisee (pas besoin de role admin pour lire)
    @GET("api/stationV")
    suspend fun getAllStationsV(): Response<List<AdminStationVDto>>

    @POST("api/admin/stations/v")
    suspend fun createStationV(@Body request: AdminStationVRequestDto): Response<AdminStationVDto>

    @PUT("api/admin/stations/v/{id}")
    suspend fun updateStationV(@Path("id") id: Long, @Body request: AdminStationVRequestDto): Response<AdminStationVDto>

    @DELETE("api/admin/stations/v/{id}")
    suspend fun deleteStationV(@Path("id") id: Long): Response<Unit>

    @GET("admin/stations/v")
    suspend fun getAllStationsV(@Query("query") query: String? = null): Response<List<AdminStationVDto>>
}