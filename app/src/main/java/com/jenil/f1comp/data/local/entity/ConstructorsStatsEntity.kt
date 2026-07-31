package com.jenil.f1comp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import com.jenil.f1comp.data.model.TeamStats

@Entity("constructor_stats_table")
data class ConstructorsStatsEntity(
    @PrimaryKey val constructorId: String,
    val constructorName: String,
    val stats: TeamStats?
)