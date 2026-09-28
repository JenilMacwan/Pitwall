package com.jenil.f1comp.data.model

data class UserProfile(
    val uid: String = "",
    val email: String = "",
    val displayName: String = "",
    val photoUrl: String = "",
    val favoriteTeam: String = "",
    val favoriteDriver: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
