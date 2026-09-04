package com.ObservatoireCampus.mobile.network

import com.ObservatoireCampus.mobile.model.CampusDto
import com.ObservatoireCampus.mobile.model.InstitutionColorDto
import retrofit2.http.GET

interface CampusApi {
    @GET("api/campus")
    suspend fun getAllCampus(): List<CampusDto>
    @GET("api/batiments/legendes/couleurs")
    suspend fun getInstitutionColors(): List<InstitutionColorDto>
}