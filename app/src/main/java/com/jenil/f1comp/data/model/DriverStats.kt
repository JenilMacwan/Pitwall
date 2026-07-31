package com.jenil.f1comp.data.model

import com.google.gson.annotations.SerializedName

data class DriverStatsResponse(
    val season: String,
    @SerializedName("total_drivers") val totalDrivers: Int,
    @SerializedName("driver_stats") val gridStats: List<DriverStats>
)

data class DriverStats(
    @SerializedName("driver_id")
    val driverId: String,

    @SerializedName("driver_name")
    val driverName: String,

    @SerializedName("career_stats")
    val careerStats: Stats?,
)

data class Stats(
    @SerializedName("world_championships")
    val championships: Int,

    @SerializedName("total_races")
    val races: Int,

    @SerializedName("total_pole")
    val poles: Int,

    @SerializedName("total_wins")
    val wins: Int,

    @SerializedName("total_podiums")
    val podiums: Int,

    @SerializedName("career_points")
    val points: Double,

    @SerializedName("total_seasons")
    val seasons: Int,

    @SerializedName("current_season")
    val currentStats: CurrentStats?,
)

data class CurrentStats(
    val year: String,
    val position: String,
    val points: String,
)