package com.ObservatoireCampus.mobile.repository

import com.ObservatoireCampus.mobile.model.search.*
import com.ObservatoireCampus.mobile.network.RetrofitClient

class ItineraryRepository {

    /**
     * POST /api/itinerary — utilisé pour DURATION / LEAST_WALKING / FEWEST_TRANSFERS / ECO_FRIENDLY.
     * Ces 4 tris ne changent pas la requête OTP, seulement l'ordre d'affichage
     * (fait ensuite côté client par ItinerarySortOption.sortedByOption dans le ViewModel).
     * Liste vide si 204 (rien trouvé), 400 (requête invalide) ou erreur réseau.
     */
    suspend fun computeItinerary(
        origin: SearchResultDto,
        destination: SearchResultDto,
        filters: ItineraryFilters
    ): List<ItineraryOptionDto> {
        val response = RetrofitClient.itineraryApi.computeItinerary(
            filters.toRequestDto(origin, destination)
        )
        return if (response.isSuccessful) response.body() ?: emptyList() else emptyList()
    }

    /**
     * POST /api/itinerary/accessible — nécessaire car wheelchair=true change le calcul
     * OTP lui-même (routes/quais filtrés côté serveur), pas juste l'ordre d'affichage.
     * Appelé par le ViewModel quand filters.wheelchair est actif.
     */
    suspend fun computeAccessibleItinerary(
        origin: SearchResultDto,
        destination: SearchResultDto,
        filters: ItineraryFilters
    ): List<ItineraryOptionDto> {
        val response = RetrofitClient.itineraryApi.computeAccessibleItinerary(
            filters.toRequestDto(origin, destination)
        )
        return if (response.isSuccessful) response.body() ?: emptyList() else emptyList()
    }

    /**
     * POST /api/itinerary/bicycle — le backend fait 3 appels OTP (un par profil vélo :
     * rapide / sûr / plat) et fusionne le résultat dédoublonné. Appelé par le ViewModel
     * quand le seul mode sélectionné est "Vélo perso" (voir ItineraryFilters.isPersonalBikeOnly).
     */
    suspend fun computeBicycleItineraries(
        origin: SearchResultDto,
        destination: SearchResultDto,
        filters: ItineraryFilters
    ): List<ItineraryOptionDto> {
        val response = RetrofitClient.itineraryApi.computeBicycleItineraries(
            filters.toRequestDto(origin, destination)
        )
        return if (response.isSuccessful) response.body() ?: emptyList() else emptyList()
    }
}