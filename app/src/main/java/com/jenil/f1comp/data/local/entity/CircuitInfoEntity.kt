package com.jenil.f1comp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity("circuits_table")
data class CircuitInfoEntity(
    @PrimaryKey
    val circuitId: String,
    val circuitName: String,
    val circuitLocation: String,
    val circuitCountry: String,
    val circuitLayout: String?
)