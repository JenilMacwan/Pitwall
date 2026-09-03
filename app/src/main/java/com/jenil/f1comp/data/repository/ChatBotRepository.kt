package com.jenil.f1comp.data.repository

import android.util.Log
import com.jenil.f1comp.BuildConfig
import com.jenil.f1comp.data.model.ChatRequest
import com.jenil.f1comp.data.model.Resource
import com.jenil.f1comp.data.remote.ChatApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

private const val TAG = "ChatRepository"

class ChatRepository @Inject constructor(
    private val apiService: ChatApiService
) {
    fun sendMessage(sessionId: String, query: String, apiKey: String = BuildConfig.CHAT_API_KEY): Flow<Resource<String>> = flow {
        Log.d(TAG, "[API REQUEST] Endpoint: POST api/v1/chat | SessionID: $sessionId | Query: \"$query\" | ApiKeyProvided: ${apiKey.isNotBlank()}")
        emit(Resource.Loading)
        try {
            val response = apiService.sendChatMessage(apiKey, ChatRequest(sessionId, query))
            if (response.isSuccessful && response.body() != null) {
                val chatResponse = response.body()!!.response
                Log.d(TAG, "[API SUCCESS] Status: ${response.code()} | Response: \"$chatResponse\"")
                emit(Resource.Success(chatResponse))
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMsg = "API Error ${response.code()} (${response.message()}): $errorBody"
                Log.e(TAG, "[API ERROR] $errorMsg")
                emit(Resource.Error(errorMsg))
            }
        } catch (e: Exception) {
            Log.e(TAG, "[API EXCEPTION] Failed to send chat message for query: \"$query\"", e)
            emit(Resource.Error(e.localizedMessage ?: "An unexpected error occurred: ${e.javaClass.simpleName}"))
        }
    }.flowOn(Dispatchers.IO)
}
