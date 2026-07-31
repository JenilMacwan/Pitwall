package com.jenil.f1comp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.jenil.f1comp.data.model.Stats


@Entity(tableName = "driver_stats_table")
data class DriverStatsEntity(
    @PrimaryKey
    val driverId: String,
    val driverName: String,
    val careerStats: Stats?,
)