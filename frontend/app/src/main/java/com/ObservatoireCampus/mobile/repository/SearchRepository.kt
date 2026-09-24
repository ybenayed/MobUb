package com.ObservatoireCampus.mobile.repository

import com.ObservatoireCampus.mobile.model.search.SearchResultDto
import com.ObservatoireCampus.mobile.network.RetrofitClient


class SearchRepository {

    suspend fun searchPlaces(query: String): List<SearchResultDto> {
        return RetrofitClient.geocodingApi.search(query)
    }
}