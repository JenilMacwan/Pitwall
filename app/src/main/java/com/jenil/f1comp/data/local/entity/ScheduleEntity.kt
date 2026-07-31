package com.jenil.f1comp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.jenil.f1comp.data.model.RaceSession

@Entity(tableName = "schedule_table")
data class ScheduleEntity(
    val round: String,
    @PrimaryKey
    val flag: String,
    val raceName: String,
    val circuitId: String,
    val circuitName: String,
    val circuitLocation: String,
    val circuitCountry: String,
    val grandPrix: String,
    val time: String,
    val firstPractice: RaceSession?,
    val secondPractice: RaceSession?,
    val thirdPractice: RaceSession?,
    val qualifying: RaceSession?,
    val sprintQualifying: RaceSession?,
    val sprint: RaceSession?,
    val isCompleted: Boolean
)