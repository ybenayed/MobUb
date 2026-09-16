package com.ObservatoireCampus.mobile.repository.admin

import com.ObservatoireCampus.mobile.model.admin.AdminStationVDto
import com.ObservatoireCampus.mobile.model.admin.AdminStationVRequestDto
import com.ObservatoireCampus.mobile.network.AdminInfrastructureApi

class AdminInfrastructureRepository(private val api: AdminInfrastructureApi) {

    suspend fun getStationsV(): Result<List<AdminStationVDto>> {
        return try {
            val response = api.getAllStationsV()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Erreur chargement stations vélo (${response.code()})"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createStationV(request: AdminStationVRequestDto): Result<AdminStationVDto> {
        return try {
            val response = api.createStationV(request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Erreur lors de la création (${response.code()})"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateStationV(id: Long, request: AdminStationVRequestDto): Result<AdminStationVDto> {
        return try {
            val response = api.updateStationV(id, request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Erreur lors de la modification (${response.code()})"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteStationV(id: Long): Result<Unit> {
        return try {
            val response = api.deleteStationV(id)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Erreur lors de la suppression (${response.code()})"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getStationsV(query: String? = null): Result<List<AdminStationVDto>> {
        return try {
            val response = api.getAllStationsV(query)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Erreur chargement stations vélo (${response.code()})"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}