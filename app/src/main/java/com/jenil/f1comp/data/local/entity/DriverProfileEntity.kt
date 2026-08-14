package com.jenil.f1comp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.jenil.f1comp.data.model.DriverCareerStats

@Entity(tableName = "driver_profile")
data class DriverProfileEntity(
    @PrimaryKey
    val driverId: String,
    val firstName: String,
    val lastName: String,
    val fullName: String,
    val number: String,
    val code: String?,
    val nationality: String?,
    val image: String?,
    val team: String?,
    val careerStats: DriverCareerStats?
)
