package com.ObservatoireCampus.mobile.network

import com.ObservatoireCampus.mobile.model.search.SearchResultDto
import retrofit2.http.GET
import retrofit2.http.Query


interface GeocodingApi {

    @GET("api/search")
    suspend fun search(@Query("q") query: String): List<SearchResultDto>
}