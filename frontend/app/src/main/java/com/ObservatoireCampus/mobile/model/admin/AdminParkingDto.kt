package com.ObservatoireCampus.mobile.model.admin

data class AdminParkingDto(
    val id: Long,
    val ident: String,
    val nom: String?,
    val adresse: String?,
    val taType: String?,
    val type: String?,
    val npTotal: Int?,
    val latitude: Double?,
    val longitude: Double?,
    val url: String?,
    val thHeur: Double?,
    val thQuar: Double?,
    val thDemi: Double?,
    val th2: Double?,
    val th3: Double?,
    val th4: Double?,
    val th10: Double?,
    val th24: Double?,
    val thNuit: Double?,
    val taTitul: Double?,
    val taNtitul: Double?,
    val taResmoi: Double?,
    val taNres7j: Double?
)

data class AdminParkingRequestDto(
    val ident: String,
    val nom: String?,
    val adresse: String?,
    val taType: String?,
    val type: String?,
    val npTotal: Int?,
    val latitude: Double?,
    val longitude: Double?,
    val url: String?,
    val thHeur: Double?,
    val thQuar: Double?,
    val thDemi: Double?,
    val th2: Double?,
    val th3: Double?,
    val th4: Double?,
    val th10: Double?,
    val th24: Double?,
    val thNuit: Double?,
    val taTitul: Double?,
    val taNtitul: Double?,
    val taResmoi: Double?,
    val taNres7j: Double?
)

/** Valeurs reelles vues en base (SELECT DISTINCT ta_type FROM parking). */
val PARKING_TA_TYPES = listOf(
    "GRATUIT",
    "PAYANT_RESERVE_ABONNES",
    "PAYANT_TARIF_HORAIRE",
    "PAYANT_TARIF_PARC_RELAIS"
)

/** Type de structure physique, commentaire de l'entite backend (pas un enum reel, String libre). */
val PARKING_STRUCTURE_TYPES = listOf("SURFACE", "SILO", "ENTERRE", "MIXTE")