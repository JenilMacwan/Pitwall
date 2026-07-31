package com.jenil.f1comp.data.model

import com.google.gson.annotations.SerializedName



data class DriverStandingsResponse(
    @SerializedName("drivers")
    val driversStandings: List<Standings>
)
data class ConstructorStandingsResponse(
    @SerializedName("constructors")
    val constructorsStandings: List<Standings>
)
data class Standings(
    val position: String,
    val points: String,
    @SerializedName("driverid")
    val driverId: String?,
    val name: String,
    val nationality: String,
    @SerializedName("constructor_logo")
    val constructorLogo: String?,
    @SerializedName("driver_image")
    val driverImage: String?,
    val url: String?,
    @SerializedName("team_name")
    val team: String?,
    val drivers: List<String>?
)