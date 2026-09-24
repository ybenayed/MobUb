package com.ObservatoireCampus.mobile.network

import com.ObservatoireCampus.mobile.model.search.SaveSearchHistoryRequestDto
import com.ObservatoireCampus.mobile.model.search.history.SearchHistoryDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface SearchHistoryApi {

    @POST("/api/search-history")
    suspend fun save(@Body request: SaveSearchHistoryRequestDto): Response<SearchHistoryDto>

    @GET("/api/search-history/me")
    suspend fun getMyHistory(): Response<List<SearchHistoryDto>>

    @DELETE("/api/search-history/{id}")
    suspend fun delete(@Path("id") id: Long): Response<Unit>
}