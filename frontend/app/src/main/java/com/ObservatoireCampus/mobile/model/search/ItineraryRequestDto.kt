package com.ObservatoireCampus.mobile.model.search

/**
 * Corps envoyé à POST /api/itinerary (et variante /bicycle).
 * Miroir exact de ItineraryRequestDTO.java côté backend.
 * wheelchair n'est plus jamais positionné depuis le client (PMR retire de l'app) ;
 * le champ reste present cote backend/DTO pour ne pas casser le contrat JSON,
 * mais toRequestDto() ne le renseigne plus jamais.
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
        arriveBy = arriveBy
    )
}