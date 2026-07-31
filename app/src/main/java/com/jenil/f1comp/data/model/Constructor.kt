package com.jenil.f1comp.data.model

import com.google.gson.annotations.SerializedName

data class ConstructorResponse(
    val constructors: List<Constructor>
)
data class Constructor(
    @SerializedName("constructorid")
    val constructorId: String,
    val name: String,
    val nationality: String,
    val url: String
)