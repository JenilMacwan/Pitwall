package com.jenil.f1comp.data.model

import com.google.gson.annotations.SerializedName

const val APEX_ACCOUNT_REQUIRED = "APEX_ACCOUNT_REQUIRED"

data class ChatRequest(
    @SerializedName("session_id") val sessionId: String,
    val query: String,
    @SerializedName("lang") val lang: String = "en",
    @SerializedName("response_style") val responseStyle: String = "concise"
)

data class ChatResponse(
    val response: String,
    @SerializedName("reference_documents") val referenceDocuments: List<String>? = null,
    @SerializedName("data_sources") val dataSources: List<String>? = null
)

data class ChatReply(
    val response: String,
    val referenceDocuments: List<String> = emptyList(),
    val dataSources: List<String> = emptyList()
)

data class ChatMessage(
    val id: String,
    val text: String,
    val isUser: Boolean,
    val timestamp: String,
    val referenceDocuments: List<String> = emptyList(),
    val dataSources: List<String> = emptyList(),
    val retryQuery: String? = null
)

sealed class Resource<out T> {
    object Loading : Resource<Nothing>()
    data class Success<out T>(val data: T) : Resource<T>()
    data class Error(val message: String) : Resource<Nothing>()
}
