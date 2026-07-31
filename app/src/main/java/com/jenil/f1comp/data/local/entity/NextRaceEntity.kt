package com.jenil.f1comp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import com.jenil.f1comp.data.model.RaceCountdown
import com.jenil.f1comp.data.model.RaceWeather

@Entity("nextrace_table")
data class NextRaceEntity(
    @PrimaryKey
    val id: Int = 1,
    val round: String,
    val raceName: String,
    val circuit: String,
    val weather: RaceWeather?,
    val countdown: RaceCountdown,
    val sessionName: String,
    val ongoingSession: String?,
    val sprint: Boolean,
    val flagEmoji: String
)