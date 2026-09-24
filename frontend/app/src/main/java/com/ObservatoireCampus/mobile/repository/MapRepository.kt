package com.ObservatoireCampus.mobile.repository

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.ObservatoireCampus.mobile.network.RetrofitClient
import java.io.File
import com.ObservatoireCampus.mobile.model.CampusDto
import com.ObservatoireCampus.mobile.model.BatimentDto
import com.ObservatoireCampus.mobile.model.InstitutionColorDto

class MapRepository(private val context: Context) {

    private val campusCacheFile = File(context.filesDir, "campus_cache.json")
    private val legendCacheFile = File(context.filesDir, "legend_cache.json")
    private val gson = Gson()

    private fun getBatimentCacheFile(campusId: Long) = File(context.filesDir, "batiments_cache_$campusId.json")

    suspend fun getCampus(): List<CampusDto> {
        return try {
            val data = RetrofitClient.campusApi.getAllCampus()
            saveToCache(campusCacheFile, data)
            data
        } catch (e: Exception) {
            loadCampusCache() ?: throw e
        }
    }

    suspend fun getBatiments(campusId: Long): List<BatimentDto> {
        val cacheFile = getBatimentCacheFile(campusId)
        return try {
            val data = RetrofitClient.batimentApi.getBatimentsByCampus(campusId)
            saveToCache(cacheFile, data)
            data
        } catch (e: Exception) {
            loadBatimentsCache(cacheFile) ?: throw e
        }
    }

    suspend fun getInstitutionColors(): List<InstitutionColorDto> {
        return try {
            val data = RetrofitClient.campusApi.getInstitutionColors()
            saveToCache(legendCacheFile, data)
            data
        } catch (e: Exception) {
            loadLegendCache() ?: throw e
        }
    }

    private fun <T> saveToCache(file: File, data: List<T>) {
        try { file.writeText(gson.toJson(data)) } catch (_: Exception) {}
    }

    private fun loadCampusCache(): List<CampusDto>? {
        if (!campusCacheFile.exists()) return null
        return try {
            val type = object : TypeToken<List<CampusDto>>() {}.type
            gson.fromJson(campusCacheFile.readText(), type)
        } catch (_: Exception) { null }
    }

    private fun loadBatimentsCache(file: File): List<BatimentDto>? {
        if (!file.exists()) return null
        return try {
            val type = object : TypeToken<List<BatimentDto>>() {}.type
            gson.fromJson(file.readText(), type)
        } catch (_: Exception) { null }
    }

    private fun loadLegendCache(): List<InstitutionColorDto>? {
        if (!legendCacheFile.exists()) return null
        return try {
            val type = object : TypeToken<List<InstitutionColorDto>>() {}.type
            gson.fromJson(legendCacheFile.readText(), type)
        } catch (_: Exception) { null }
    }
}