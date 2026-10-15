package com.jenil.f1comp.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "chat_messages",
    indices = [Index(value = ["sessionId", "createdAt"])]
)
data class ChatMessageEntity(
    @PrimaryKey val id: String,
    val sessionId: String,
    val text: String,
    val isUser: Boolean,
    val timestamp: String,
    val referenceDocuments: String,
    val dataSources: String,
    val createdAt: Long
)
