package com.jenil.f1comp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "constructor_table")
data class ConstructorEntity(
    @PrimaryKey
    val constructorId: String,
    val name: String,
    val nationality: String,
    val url: String
)