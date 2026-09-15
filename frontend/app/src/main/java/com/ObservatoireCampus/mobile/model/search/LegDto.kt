// model/search/LegDto.kt
package com.ObservatoireCampus.mobile.model.search

/**
 * Miroir de LegDTO.java côté backend.
 * distance   : mètres, utile pour afficher "350 m à pied" sur un leg de marche.
 * rentedBike : true = vélo en libre-service (Vcub), permet de distinguer
 *              visuellement 🚲 (perso) de 🛴 (Vcub) alors que mode="BICYCLE" pour les deux.
 *
 * CORRECTIF : fromLat/fromLon/toLat/toLon ajoutes. Le backend (LegDTO.java)
 * renvoie deja ces champs dans la reponse de /api/itinerary, mais comme ce
 * DTO Android ne les declarait pas, ils etaient silencieusement ignores a la
 * desererialisation. Consequence : quand on reutilisait ce meme objet pour
 * POST /api/search-history (SaveSearchHistoryRequestDto.itinerary.legs), le
 * JSON envoye ne contenait pas les coordonnees -> le backend les stockait a
 * null -> l'API d'historique (/me) les renvoyait a 0.0 par defaut -> l'ecran
 * carte filtrait tout (fromLat != 0.0 && toLat != 0.0) et n'affichait rien
 * pour "Voir sur la carte" depuis l'Historique.
 */
data class LegDto(
    val mode: String,
    val fromName: String?,
    val fromLat: Double = 0.0,
    val fromLon: Double = 0.0,
    val toName: String?,
    val toLat: Double = 0.0,
    val toLon: Double = 0.0,
    val startTime: Long,
    val endTime: Long,
    val distance: Double = 0.0,
    val rentedBike: Boolean = false,
    val routeName: String?,
    val geometry: List<GeoPointDto> = emptyList()
)