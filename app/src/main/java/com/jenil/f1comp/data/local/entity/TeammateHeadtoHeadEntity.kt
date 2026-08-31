package com.jenil.f1comp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "head_to_head_table")
data class TeammateHeadtoHeadEntity(
    @PrimaryKey val constructorId: String,
    val season: String,
    val constructorName: String,
    val constructorLogo: String,
    val driverA: DriverInfo,
    val driverB: DriverInfo,
    val qualifying: ComparisonSet,
    val sprintQualifying: ComparisonSet?,
    val sprintRace: ComparisonSet?,
    val race: ComparisonSet,
    val standings: StandingsComparison,
    val cachedAt: Long = System.currentTimeMillis(),
)

data class DriverInfo(
    val driverId: String,
    val name: String,
    val code: String,
    val image: String
)

data class ComparisonSet(
    val driverAWins: Int,
    val driverBWins: Int,
    val ties: Int,
    val totalCompleted: Int,
    val rounds: List<RoundResult>
)

data class RoundResult(
    val round: String,
    val raceName: String,
    val driverAPosition: String,
    val driverBPosition: String,
    val winner: String
)

data class StandingsComparison(
    val driverAPosition: String,
    val driverAPoints: String,
    val driverBPosition: String,
    val driverBPoints: String,
    val pointsDelta: Double,
    val positionsDelta: Int
)
