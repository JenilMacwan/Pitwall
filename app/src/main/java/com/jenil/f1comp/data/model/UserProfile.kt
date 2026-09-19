package com.jenil.f1comp.data.model

data class UserProfile(
    val uid: String = "",
    val email: String = "",
    val displayName: String = "",
    val photoUrl: String = "",
    val favoriteTeam: String = "Scuderia Ferrari HP",
    val favoriteDriver: String = "Charles Leclerc #16",
    val createdAt: Long = System.currentTimeMillis()
)
