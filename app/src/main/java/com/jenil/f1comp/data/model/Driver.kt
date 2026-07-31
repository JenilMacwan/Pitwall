package com.jenil.f1comp.data.model

import com.google.gson.annotations.SerializedName

data class DriverProfileResponse(
    val season: String,
    @SerializedName("total_drivers")
    val totalDrivers: Int,
    val drivers: List<Driver>
)

data class Driver(
    @SerializedName("driver_id")
    val driverId: String,
    @SerializedName("first_name")
    val firstName: String,
    @SerializedName("last_name")
    val lastName: String,
    @SerializedName("full_name")
    val fullName: String,
    val number: String,
    val code: String?,
    val nationality: String?,
    val image: String?,
    val team: String?,
    @SerializedName("career_stats")
    val careerStats: CareerStats? = null
)

data class CareerStats(
    @SerializedName("world_championships")
    val worldChampionships: Int,
    @SerializedName("total_races")
    val totalRaces: Int,
    @SerializedName("total_poles")
    val totalPoles: Int,
    @SerializedName("total_wins")
    val totalWins: Int,
    @SerializedName("total_podiums")
    val totalPodiums: Int,
    @SerializedName("career_points")
    val careerPoints: Double,
    @SerializedName("total_seasons")
    val totalSeasons: Int,
    @SerializedName("current_season")
    val currentSeason: CurrentSeason? = null
)

data class CurrentSeason(
    val year: String,
    val position: String,
    val points: String
)