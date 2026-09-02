package com.jenil.f1comp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.jenil.f1comp.data.model.TeamRadioMessage

@Entity(tableName = "team_radio_table")
data class TeamRadioEntity(
    @PrimaryKey
    val radioUrl: String,
    val timestamp: String,
    val driverNumber: Int,
    val driverCode: String,
    val sessionKey: Int,
    val sessionName: String? = null,
    val eventName: String? = null,
    val cachedAt: Long = System.currentTimeMillis()
)

fun TeamRadioMessage.toEntity(
    sessionName: String? = null,
    eventName: String? = null
): TeamRadioEntity = TeamRadioEntity(
    radioUrl = radioUrl,
    timestamp = timestamp,
    driverNumber = driverNumber,
    driverCode = driverCode,
    sessionKey = sessionKey,
    sessionName = sessionName,
    eventName = eventName
)

fun TeamRadioEntity.toMessage(): TeamRadioMessage = TeamRadioMessage(
    timestamp = timestamp,
    driverNumber = driverNumber,
    radioUrl = radioUrl,
    sessionKey = sessionKey
)
