package com.jenil.f1comp.data.model

import com.google.gson.annotations.SerializedName

data class RaceResultResponse(
    val season: String,
    val round: String?,
    @SerializedName("race_id")
    val raceId: String?,
    @SerializedName("race_name")
    val raceName: String,
    val results: List<RaceResult>
)

data class RaceResult(
    val position: String,
    @SerializedName("position_text")
    val positionText: String? = null,
    val driver: String,
    @SerializedName("driver_id")
    val driverId: String? = null,
    @SerializedName("driver_image")
    val driverImage: String? = null,
    val constructor: String,
    val points: String,
    val grid: String,
    val status: String,
    val time: String? = null,
    @SerializedName("fastest_lap_time")
    val fastestLap: String? = null
)

data class QualifyingResultResponse(
    val season: String,
    @SerializedName("race_id")
    val raceId: String?,
    val round: String?,
    @SerializedName("race_name")
    val raceName: String,
    val results: List<QualiResult>
)

data class QualiResult(
    val position: String,
    val driver: String,
    @SerializedName("driver_image")
    val driverImage: String? = null,
    val constructor: String,
    val q1: String? = null,
    val q2: String? = null,
    val q3: String? = null
)