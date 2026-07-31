package com.jenil.f1comp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "driver_standings")
data class DriverStandingsEntity(
    @PrimaryKey
    val driverId: String,
    val position: Int,
    val points: Double,
    val name: String,
    val nationality: String,
    val driverImage: String?,
    val url: String,
    val team: String?
)
