package com.ObservatoireCampus.mobile.model.search


private fun ItineraryOptionDto.modeSequenceSignature(): String =
    legs.joinToString("->") { it.mode.uppercase() }


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