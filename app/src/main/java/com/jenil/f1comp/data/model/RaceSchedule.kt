package com.jenil.f1comp.data.model

import com.google.gson.annotations.SerializedName


data class ScheduleResponse(
    val schedule: List<RaceSchedule>
)
data class RaceSchedule(
    val round: String,
    @SerializedName("flag_emoji")
    val flag: String,
    @SerializedName("racename")
    val raceName: String,

    @SerializedName("circuitid")
    val circuitId: String,

    @SerializedName("circuitname")
    val circuitName: String,

    @SerializedName("circuitlocation")
    val circuitLocation: String,

    @SerializedName("circuitcountry")
    val circuitCountry: String,

    @SerializedName("GrandPrix")
    val grandPrix: String,

    val time: String,

    @SerializedName("FirstPractice")
    val firstPractice: RaceSession?,

    @SerializedName("SecondPractice")
    val secondPractice: RaceSession?,

    @SerializedName("ThirdPractice")
    val thirdPractice: RaceSession?,

    @SerializedName("Qualifying")
    val qualifying: RaceSession?,

    @SerializedName("SprintQualifying")
    val sprintQualifying: RaceSession?,

    @SerializedName("Sprint")
    val sprint: RaceSession?,

    @SerializedName("is_completed")
    val isCompleted: Boolean

)

data class RaceSession(
    val date: String,
    val time: String
)
