package com.jenil.f1comp.data.model

import com.google.gson.annotations.SerializedName

data class ConstructorProfileResponse(
    val season: String,
    @SerializedName("total_constructors")
    val totalConstructors: Int,
    val constructors: List<Constructor>
)

data class Constructor(
    @SerializedName("constructor_id")
    val constructorId: String,
    val name: String,
    val nationality: String,
    val logo: String,
    val car: String,
    val drivers: List<String>,
    @SerializedName("career_stats")
    val careerStats: TeamCareerStats
)

data class TeamCareerStats(
    @SerializedName("constructor_championships")
    val constructorChampionships: Int,
    @SerializedName("driver_championships")
    val driverChampionships: Int,
    @SerializedName("total_races")
    val totalRaces: Int,
    val wins: Int,
    @SerializedName("win_percentage")
    val winPercentage: String,
    @SerializedName("podium_percentage")
    val podiumPercentage: String,
    val podiums: Int,
    @SerializedName("current_season")
    val currentSeason: TeamCurrentSeason? = null
)

data class TeamCurrentSeason(
    val year: String,
    val position: String,
    val points: String,
    val podiums: Int,
    @SerializedName("points_progression")
    val pointsProgression: List<TeamPointsProgression>
)

data class TeamPointsProgression(
    val round: String,
    @SerializedName("race_name")
    val raceName: String,
    val points: Double,
    @SerializedName("cumulative_points")
    val cumulativePoints: Double
)