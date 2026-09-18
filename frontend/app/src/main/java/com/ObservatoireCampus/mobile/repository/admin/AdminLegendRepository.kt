package com.ObservatoireCampus.mobile.repository.admin

import com.ObservatoireCampus.mobile.model.InstitutionColorDto
import com.ObservatoireCampus.mobile.network.AdminLegendApi

class AdminLegendRepository(private val api: AdminLegendApi) {

    suspend fun getColors(): Result<List<InstitutionColorDto>> {
        return try {
            val response = api.getColors()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Erreur chargement couleurs (${response.code()})"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createOrUpdateColor(institution: String, color: String): Result<InstitutionColorDto> {
        return try {
            val response = api.createOrUpdateColor(InstitutionColorDto(institution, color))
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Erreur lors de l'enregistrement (${response.code()})"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteColor(institution: String): Result<Unit> {
        return try {
            val response = api.deleteColor(institution)
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