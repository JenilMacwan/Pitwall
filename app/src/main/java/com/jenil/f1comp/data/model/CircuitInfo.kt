package com.jenil.f1comp.data.model

import com.google.gson.annotations.SerializedName

data class CircuitResponse(
    val circuits: List<CircuitInfo>
)
data class CircuitInfo(
    @SerializedName("circuitid")
    val circuitId: String,

    @SerializedName("circuitname")
    val circuitName: String,

    @SerializedName("circuitlocation")
    val circuitLocation: String,

    @SerializedName("circuitcountry")
    val circuitCountry: String,

    @SerializedName("circuitlayout")
    val circuitLayout: String?
)