package com.jenil.f1comp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "driver_table")
data class DriverEntity(
    @PrimaryKey
    val driverId: String,
    val firstName: String,
    val lastName: String,
    val fullName: String,
    val number: String,
    val code: String?,
    val nationality: String?,
    val image: String?,      // Added for headshot URL
    val team: String?,       // Added for current team
    // Optional stats for offline caching
    val worldChampionships: Int = 0,
    val totalWins: Int = 0,
    val totalPodiums: Int = 0,
    val currentSeasonPosition: String? = null,
    val currentSeasonPoints: String? = null
)