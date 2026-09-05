package com.jenil.f1comp.data.local.entity

import androidx.room.Entity


@Entity(tableName = "sprint_quali_result_table", primaryKeys = ["raceId", "driver"])

data class SprintQualifyingResultEntity(
    val raceId: String,
    val season: String,
    val round: String?,
    val raceName: String,
    val position: String,
    val driver: String,
    val driverImage: String? = null,
    val constructor: String,
    val q1: String? = null,
    val q2: String? = null,
    val q3: String? = null
)