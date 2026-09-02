package com.jenil.f1comp.data.model

import com.google.gson.annotations.SerializedName

data class TeamRadioResponse(
    @SerializedName("session_key")
    val sessionKey: String? = null,
    @SerializedName("session_name")
    val sessionName: String? = null,
    @SerializedName("event_name")
    val eventName: String? = null,
    val round: Int? = null,
    @SerializedName("last_name")
    val lastName: String? = null,
    @SerializedName("total_messages")
    val totalRadioCount: Int = 0,
    val messages: List<TeamRadioMessage> = emptyList()
)

data class TeamRadioMessage(
    val timestamp: String = "",
    @SerializedName("driver_number")
    val driverNumber: Int = 0,
    @SerializedName("recording_url")
    val radioUrl: String = "",
    @SerializedName("session_key")
    val sessionKey: Int = 0
) {
    val driverCode: String
        get() = radioUrl.substringAfterLast("/").substringBefore("_", "")
}