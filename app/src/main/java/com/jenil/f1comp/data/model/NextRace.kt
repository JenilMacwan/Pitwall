package com.jenil.f1comp.data.model

import com.google.gson.annotations.SerializedName

data class NextRace(
    val round: String,
    @SerializedName("race_name")
    val raceName: String,
    val circuit: String,
    val weather: RaceWeather?,
    val countdown: RaceCountdown,
    @SerializedName("next_session")
    val sessionName: String,
    @SerializedName("is_sprint_weekend")
    val sprint: Boolean,
    @SerializedName("flag_emoji")
    val flagEmoji: String,
    @SerializedName("ongoing_session")
    val ongoingSession: String?,
)

data class RaceWeather(
    val temp: String,
    val condition: String
)

data class RaceCountdown(
    val days: Int,
    val hours: Int,
    val minutes: Int,
    val seconds: Int
)