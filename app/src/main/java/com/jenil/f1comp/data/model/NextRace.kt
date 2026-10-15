package com.jenil.f1comp.data.model

import com.google.gson.annotations.SerializedName

data class NextRace(
    val round: String,
    @SerializedName("race_name")
    val raceName: String,
    val circuit: String,
    val country: String,
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
    @SerializedName("track_temp")
    val trackTemp: String,
    val condition: String
)

data class RaceCountdown(
    val days: Int,
    val hours: Int,
    val minutes: Int,
    val seconds: Int,
    val calculatedAtEpochMillis: Long = 0L
)

fun RaceCountdown.remainingSeconds(nowEpochMillis: Long = System.currentTimeMillis()): Long {
    val durationSeconds = days.coerceAtLeast(0) * 86_400L +
        hours.coerceAtLeast(0) * 3_600L +
        minutes.coerceAtLeast(0) * 60L +
        seconds.coerceAtLeast(0)
    val elapsedSeconds = if (calculatedAtEpochMillis > 0L) {
        ((nowEpochMillis - calculatedAtEpochMillis).coerceAtLeast(0L) / 1_000L)
    } else {
        0L
    }
    return (durationSeconds - elapsedSeconds).coerceAtLeast(0L)
}
