package com.ObservatoireCampus.mobile.model.search

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