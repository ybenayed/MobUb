package com.ObservatoireCampus.mobile.network

import com.ObservatoireCampus.mobile.model.BatimentDto
import com.ObservatoireCampus.mobile.model.InstitutionColorDto
import retrofit2.http.GET
import retrofit2.http.Path

interface BatimentApi {
    @GET("api/batiments/campus/{campusId}")
    suspend fun getBatimentsByCampus(@Path("campusId") campusId: Long): List<BatimentDto>

    @GET("api/batiments/legendes/couleurs")
    suspend fun getInstitutionColors(): List<InstitutionColorDto>
}