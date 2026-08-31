package com.jenil.f1comp.data.model

data class TeamAboutInfo(
    val fullName: String,
    val base: String,
    val base2: String? = null,
    val about: String
)

data class DriverAboutInfo(
    val team: String,
    val country: String,
    val number: String,
    val about: String
)