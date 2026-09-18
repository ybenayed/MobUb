package com.ObservatoireCampus.mobile.network

import com.ObservatoireCampus.mobile.model.admin.AdminStationVDto
import com.ObservatoireCampus.mobile.model.admin.AdminStationVRequestDto
import com.ObservatoireCampus.mobile.model.admin.AdminStationTBDto
import com.ObservatoireCampus.mobile.model.admin.AdminStationTBRequestDto
import com.ObservatoireCampus.mobile.model.admin.AdminStationTerDto
import com.ObservatoireCampus.mobile.model.admin.AdminStationTerRequestDto
import com.ObservatoireCampus.mobile.model.admin.AdminParkingDto
import com.ObservatoireCampus.mobile.model.admin.AdminParkingRequestDto

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
    suspend fun getStationsV(@Query("query") query: String? = null): Response<List<AdminStationVDto>>


    // --- TRAM & BUS (StationTB) ---
    @GET("api/stationTB")
    suspend fun getAllStationsTB(): Response<List<AdminStationTBDto>>

    @POST("api/admin/stations/tb") // Ajuste selon ton endpoint admin backend exact si besoin
    suspend fun createStationTB(@Body request: AdminStationTBRequestDto): Response<AdminStationTBDto>

    @PUT("api/admin/stations/tb/{id}")
    suspend fun updateStationTB(@Path("id") id: Long, @Body request: AdminStationTBRequestDto): Response<AdminStationTBDto>

    @DELETE("api/admin/stations/tb/{id}")
    suspend fun deleteStationTB(@Path("id") id: Long): Response<Unit>

    // --- TER (StationTer) ---
    @GET("api/stationTer")
    suspend fun getAllStationsTer(): Response<List<AdminStationTerDto>>

    @POST("api/admin/stations/ter")
    suspend fun createStationTer(@Body request: AdminStationTerRequestDto): Response<AdminStationTerDto>

    @PUT("api/admin/stations/ter/{id}")
    suspend fun updateStationTer(@Path("id") id: Long, @Body request: AdminStationTerRequestDto): Response<AdminStationTerDto>

    @DELETE("api/admin/stations/ter/{id}")
    suspend fun deleteStationTer(@Path("id") id: Long): Response<Unit>


    // --- PARKING ---
    @GET("api/parking")
    suspend fun getAllParkings(): Response<List<AdminParkingDto>>

    @POST("api/admin/parking")
    suspend fun createParking(@Body request: AdminParkingRequestDto): Response<AdminParkingDto>

    @PUT("api/admin/parking/{id}")
    suspend fun updateParking(@Path("id") id: Long, @Body request: AdminParkingRequestDto): Response<AdminParkingDto>

    @DELETE("api/admin/parking/{id}")
    suspend fun deleteParking(@Path("id") id: Long): Response<Unit>
}