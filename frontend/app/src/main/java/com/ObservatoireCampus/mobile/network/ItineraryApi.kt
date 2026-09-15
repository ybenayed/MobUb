package com.ObservatoireCampus.mobile.network

import com.ObservatoireCampus.mobile.model.search.ItineraryRequestDto
import com.ObservatoireCampus.mobile.model.search.ItineraryOptionDto
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.Response

interface ItineraryApi {

    /**
     * Un seul appel, réutilisé pour les tris "rapide / moins de marche /
     * moins de correspondances / plus écologique" : ces tris ne changent pas
     * la requête, seulement l'ordre d'affichage (fait côté client ensuite).
     */
    @POST("/api/itinerary")
    suspend fun computeItinerary(@Body request: ItineraryRequestDto): Response<List<ItineraryOptionDto>>

    /**
     * Le backend fait 3 appels OTP (profils vélo : rapide / sûr / plat) et
     * fusionne le résultat dédoublonné -> plusieurs itinéraires vélo perso
     * au lieu d'un seul trajet direct.
     */
    @POST("/api/itinerary/bicycle")
    suspend fun computeBicycleItineraries(@Body request: ItineraryRequestDto): Response<List<ItineraryOptionDto>>

    // L'enregistrement dans l'historique est gere par SearchHistoryApi.save(),
    // pas ici -> evite d'avoir 2 chemins differents pour la meme action.
}