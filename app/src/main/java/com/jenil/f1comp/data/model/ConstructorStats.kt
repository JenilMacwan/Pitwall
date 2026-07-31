package com.jenil.f1comp.data.model

import com.google.gson.annotations.SerializedName

data class ConstructorStatsResponse(
    val season: String,
    @SerializedName("total_constructors") val totalConstructors: Int,
    @SerializedName("constructor_stats") val constructorStats: List<ConstructorStats>
)

data class ConstructorStats(
    @SerializedName("constructor_id")
    val constructorId: String,
    @SerializedName("constructor_name")
    val constructorName: String,
    val stats: TeamStats?
)

data class TeamStats(
    @SerializedName("constructor_championships")
    val constructorChampionships: Int,
    @SerializedName("driver_championships")
    val driverChampionships: Int,
    @SerializedName("total_races")
    val races: Int,
    val wins: Int,
    val podiums: Int,
    @SerializedName("win_percentage")
    val winPercentage: String?,
    @SerializedName("podium_percentage")
    val podiumPercentage: String?,
    @SerializedName("current_season")
    val currentStats: CurrentTeamStats?
)

data class CurrentTeamStats(
    val year: String,
    val position: String,
    val points: String
)
