package com.jenil.f1comp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import okio.Source

@Entity(tableName = "news_table")
data class NewsEntity(
    @PrimaryKey
    val link: String,
    val title: String,
    val description: String,
    val published: String,
    val image: String?,
    val source: String,
    val cachedAt: Long
)