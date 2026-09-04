package com.ObservatoireCampus.mobile.model.search

/**
 * Corps envoyé à POST /api/itinerary (et variantes /accessible, /bicycle).
 * Miroir exact de ItineraryRequestDTO.java côté backend.
 *
 * numItineraries A ETE RETIRE : le backend n'a plus ce champ, et Jackson
 * rejette par défaut les propriétés JSON inconnues (pas de
 * @JsonIgnoreProperties(ignoreUnknown = true) sur ItineraryRequestDTO.java).
 * L'envoyer provoquait potentiellement une erreur 400 sur toutes les recherches.
 */
data class ItineraryRequestDto(
    val origin: SearchResultDto,
    val destination: SearchResultDto,
    val modes: List<String>? = null,
    val date: String? = null,
    val time: String? = null,
    val arriveBy: Boolean? = null,
    val walkSpeed: Double? = null,
    val bikeSpeed: Double? = null,
    val wheelchair: Boolean? = null
)

/** Convertit l'état UI (ItineraryFilters) en payload réseau. */
fun ItineraryFilters.toRequestDto(origin: SearchResultDto, destination: SearchResultDto): ItineraryRequestDto {
    return ItineraryRequestDto(
        origin = origin,
        destination = destination,
        modes = modes.map { it.apiValue }.ifEmpty { null },
        date = date,
        time = time,
        arriveBy = arriveBy,
        wheelchair = wheelchair.takeIf { it } // null si false, pour ne pas forcer le PMR par défaut
    )
}