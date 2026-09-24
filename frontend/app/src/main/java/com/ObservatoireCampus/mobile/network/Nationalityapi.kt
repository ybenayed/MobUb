package com.ObservatoireCampus.mobile.network

import retrofit2.Response
import retrofit2.http.GET

interface NationalityApi {
    @GET("api/nationalities")
    suspend fun getNationalities(): Response<List<String>>
}