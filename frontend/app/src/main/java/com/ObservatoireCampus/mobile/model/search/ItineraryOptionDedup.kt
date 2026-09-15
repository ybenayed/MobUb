// model/search/ItineraryOptionDedup.kt
package com.ObservatoireCampus.mobile.model.search

/**
 * Signature basee sur la sequence de modes (WALK, BUS, TRAM...) d'un itineraire.
 * Deux itineraires WALK->BUS->WALK sur des lignes ou horaires differents
 * partagent la meme signature.
 */
private fun ItineraryOptionDto.modeSequenceSignature(): String =
    legs.joinToString("->") { it.mode.uppercase() }

/**
 * Ne garde qu'UNE SEULE proposition par sequence de modes distincte : celle
 * de duree la plus courte. Evite d'afficher plusieurs fois "WALK -> BUS -> WALK"
 * (meme si OTP renvoie plusieurs variantes avec des lignes/horaires differents).
 */
fun List<ItineraryOptionDto>.deduplicatedByModeSequence(): List<ItineraryOptionDto> {
    val bestBySignature = LinkedHashMap<String, ItineraryOptionDto>()
    for (option in this) {
        val signature = option.modeSequenceSignature()
        val current = bestBySignature[signature]
        if (current == null || option.duration < current.duration) {
            bestBySignature[signature] = option
        }
    }
    return bestBySignature.values.toList()
}