package com.ObservatoireCampus.mobile.repository

import com.ObservatoireCampus.mobile.network.NationalityApi

class NationalityRepository(
    private val nationalityApi: NationalityApi
) {
    suspend fun getNationalities(): Result<List<String>> {
        return try {
            val response = nationalityApi.getNationalities()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Impossible de charger les nationalités (${response.code()})"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}