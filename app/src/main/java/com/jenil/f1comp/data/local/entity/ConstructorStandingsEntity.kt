package com.jenil.f1comp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "constructors_standings")
data class ConstructorStandingsEntity(
    @PrimaryKey
    val name: String,
    val position: Int,
    val points: Double,
    val nationality: String,
    val constructorLogo: String?,
    val drivers: String

)