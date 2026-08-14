package com.jenil.f1comp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.jenil.f1comp.data.model.TeamCareerStats

@Entity(tableName = "constructor_profile")
data class ConstructorProfileEntity(
    @PrimaryKey
    val constructorId: String,
    val name: String,
    val nationality: String,
    val logo: String,
    val car: String,
    val drivers: List<String>,
    val careerStats: TeamCareerStats?
)
