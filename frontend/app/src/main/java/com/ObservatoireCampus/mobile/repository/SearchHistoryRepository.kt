package com.ObservatoireCampus.mobile.repository

import com.ObservatoireCampus.mobile.model.search.ItineraryOptionDto
import com.ObservatoireCampus.mobile.model.search.SaveSearchHistoryRequestDto
import com.ObservatoireCampus.mobile.model.search.SearchResultDto
import com.ObservatoireCampus.mobile.model.search.history.SearchHistoryDto
import com.ObservatoireCampus.mobile.network.RetrofitClient

class SearchHistoryRepository {

    suspend fun saveToHistory(
        origin: SearchResultDto,
        destination: SearchResultDto,
        itinerary: ItineraryOptionDto
    ): Result<Unit> {
        return try {
            val response = RetrofitClient.searchHistoryApi.save(
                SaveSearchHistoryRequestDto(origin, destination, itinerary)
            )
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Erreur lors de l'enregistrement (${response.code()})"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getMyHistory(): Result<List<SearchHistoryDto>> {
        return try {
            val response = RetrofitClient.searchHistoryApi.getMyHistory()
            if (response.isSuccessful) {
                Result.success(response.body() ?: emptyList())
            } else {
                Result.failure(Exception("Erreur lors du chargement de l'historique (${response.code()})"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteHistoryItem(id: Long): Result<Unit> {
        return try {
            val response = RetrofitClient.searchHistoryApi.delete(id)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Erreur lors de la suppression (${response.code()})"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}