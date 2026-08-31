package com.jenil.f1comp.data.model

import com.google.gson.annotations.SerializedName

data class TeammateHead2HeadResponse(
    val season: String,
    @SerializedName("total_teams")
    val totalTeams: Int,
    @SerializedName("head_to_head")
    val headToHead: List<ConstructorHead2Head>,
)

data class ConstructorHead2Head(
    val constructor: String,
    @SerializedName("constructor_id")
    val constructorId: String,
    @SerializedName("constructor_logo")
    val constructorLogo: String,
    val drivers: TeammateDrivers,
    val qualifying: SessionComparison,
    @SerializedName("sprint_qualifying")
    val sprintQualifying: SessionComparison?,
    @SerializedName("sprint_race")
    val sprintRace: SessionComparison?,
    val race: SessionComparison,
    val standings: StandingsComparison
)

data class TeammateDrivers(
    @SerializedName("driver_a")
    val driverA: TeammateDriverInfo,
    @SerializedName("driver_b")
    val driverB: TeammateDriverInfo
)

data class TeammateDriverInfo(
    @SerializedName("driver_id")
    val driverId: String,
    val name: String,
    val code: String,
    val image: String
)

data class SessionComparison(
    @SerializedName("driver_a_wins")
    val driverAWins: Int,
    @SerializedName("driver_b_wins")
    val driverBWins: Int,
    val ties: Int,
    @SerializedName("total_completed")
    val totalCompleted: Int,
    val rounds: List<RoundResult>
)

data class RoundResult(
    val round: String,
    @SerializedName("race_name")
    val raceName: String,
    @SerializedName("driver_a_position")
    val driverAPosition: String,
    @SerializedName("driver_b_position")
    val driverBPosition: String,
    val winner: String
)

data class StandingsComparison(
    @SerializedName("driver_a")
    val driverA: DriverStandingPoints,
    @SerializedName("driver_b")
    val driverB: DriverStandingPoints,
    @SerializedName("points_delta")
    val pointsDelta: Double,
    @SerializedName("positions_delta")
    val positionsDelta: Int
)

data class DriverStandingPoints(
    val position: String,
    val points: String
)
