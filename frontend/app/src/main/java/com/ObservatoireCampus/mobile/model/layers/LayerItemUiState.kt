package com.ObservatoireCampus.mobile.model.layers

// Etat generique d'un item de sous-liste dans un layer (Parking, Velo, Bus...)
data class LayerItemUiState(
        val key: String,       // identifiant technique taype pou parkig par ewemple
        val label: String,
        val count: Long,
        val visible: Boolean = false
)