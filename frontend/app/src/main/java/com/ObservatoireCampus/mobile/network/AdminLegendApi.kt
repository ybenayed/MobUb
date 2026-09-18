package com.ObservatoireCampus.mobile.network

import com.ObservatoireCampus.mobile.model.InstitutionColorDto
import retrofit2.Response
import retrofit2.http.*

interface AdminLegendApi {
    // --- Couleurs institutions ---
    @GET("api/admin/legends/colors")
    suspend fun getColors(): Response<List<InstitutionColorDto>>

    @POST("api/admin/legends/colors")
    suspend fun createOrUpdateColor(@Body request: InstitutionColorDto): Response<InstitutionColorDto>

    @DELETE("api/admin/legends/colors/{institution}")
    suspend fun deleteColor(@Path("institution") institution: String): Response<Unit>


}