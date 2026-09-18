package com.ObservatoireCampus.mobile.repository.admin

import com.ObservatoireCampus.mobile.model.admin.AdminStationVDto
import com.ObservatoireCampus.mobile.model.admin.AdminStationVRequestDto
import com.ObservatoireCampus.mobile.model.admin.AdminStationTBDto
import com.ObservatoireCampus.mobile.model.admin.AdminStationTBRequestDto
import com.ObservatoireCampus.mobile.network.AdminInfrastructureApi
import com.ObservatoireCampus.mobile.model.admin.AdminStationTerDto
import com.ObservatoireCampus.mobile.model.admin.AdminStationTerRequestDto
import com.ObservatoireCampus.mobile.model.admin.AdminParkingDto
import com.ObservatoireCampus.mobile.model.admin.AdminParkingRequestDto

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
            val response = api.getStationsV(query)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Erreur chargement stations vélo (${response.code()})"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // --- Tram & Bus (StationTB partagé) ---
    suspend fun getStationsTB(): Result<List<AdminStationTBDto>> {
        return try {
            val response = api.getAllStationsTB()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Erreur chargement stations Tram/Bus (${response.code()})"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createStationTB(request: AdminStationTBRequestDto): Result<AdminStationTBDto> {
        return try {
            val response = api.createStationTB(request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Erreur lors de la création (${response.code()})"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateStationTB(id: Long, request: AdminStationTBRequestDto): Result<AdminStationTBDto> {
        return try {
            val response = api.updateStationTB(id, request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Erreur lors de la modification (${response.code()})"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteStationTB(id: Long): Result<Unit> {
        return try {
            val response = api.deleteStationTB(id)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Erreur lors de la suppression (${response.code()})"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // --- TER ---
    suspend fun getStationsTer(): Result<List<AdminStationTerDto>> {
        return try {
            val response = api.getAllStationsTer()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Erreur chargement gares TER (${response.code()})"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createStationTer(request: AdminStationTerRequestDto): Result<AdminStationTerDto> {
        return try {
            val response = api.createStationTer(request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Erreur lors de la création (${response.code()})"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateStationTer(id: Long, request: AdminStationTerRequestDto): Result<AdminStationTerDto> {
        return try {
            val response = api.updateStationTer(id, request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Erreur lors de la modification (${response.code()})"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteStationTer(id: Long): Result<Unit> {
        return try {
            val response = api.deleteStationTer(id)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Erreur lors de la suppression (${response.code()})"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // --- PARKING ---
    suspend fun getParkings(): Result<List<AdminParkingDto>> {
        return try {
            val response = api.getAllParkings()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Erreur chargement parkings (${response.code()})"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createParking(request: AdminParkingRequestDto): Result<AdminParkingDto> {
        return try {
            val response = api.createParking(request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Erreur lors de la création (${response.code()})"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateParking(id: Long, request: AdminParkingRequestDto): Result<AdminParkingDto> {
        return try {
            val response = api.updateParking(id, request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Erreur lors de la modification (${response.code()})"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteParking(id: Long): Result<Unit> {
        return try {
            val response = api.deleteParking(id)
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