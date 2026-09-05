package com.jenil.f1comp.data.local.entity

import androidx.room.Entity

@Entity(tableName = "sprint_result_table", primaryKeys = ["raceId", "driver"])
data class SprintResultEntity(
    val raceId: String,
    val raceName: String,
    val position: String,
    val positionText: String?,
    val driver: String,
    val driverId: String? = null,
    val driverImage: String? = null,
    val constructor: String,
    val points: String,
    val grid: String,
    val status: String,
    val time: String,
    val fastestLap: String
)