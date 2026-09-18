package com.ObservatoireCampus.mobile.model.admin

data class AdminDashboardStatsDto(
    val totalSearches: Long,
    val activeUsersLast30Days: Long,
    val totalCo2GramsSaved: Double,
    val mostUsedModes: List<AdminModeCountDto>
)

data class AdminModeCountDto(
    val mode: String,
    val count: Long
)