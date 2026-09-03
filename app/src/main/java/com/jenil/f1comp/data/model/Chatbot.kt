package com.jenil.f1comp.data.model

import com.google.gson.annotations.SerializedName

data class ChatRequest(
    @SerializedName("session_id")
    val sessionId: String,
    val query: String
)
data class ChatResponse(
    val response: String
)
sealed class Resource<out T> {
    object Loading : Resource<Nothing>()
    data class Success<out T>(val data: T) : Resource<T>()
    data class Error(val message: String) : Resource<Nothing>()
}
