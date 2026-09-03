package com.jenil.f1comp.data.repository

import com.jenil.f1comp.BuildConfig
import com.jenil.f1comp.data.model.ChatRequest
import com.jenil.f1comp.data.model.Resource
import com.jenil.f1comp.data.remote.ChatApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class ChatRepository @Inject constructor(
    private val apiService: ChatApiService
) {
    fun sendMessage(sessionId: String, query: String, apiKey: String = BuildConfig.CHAT_API_KEY): Flow<Resource<String>> = flow {
        emit(Resource.Loading)
        try {
            val response = apiService.sendChatMessage(apiKey, ChatRequest(sessionId, query))
            if (response.isSuccessful && response.body() != null) {
                emit(Resource.Success(response.body()!!.response))
            } else {
                emit(Resource.Error("API Error: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "An unexpected error occurred"))
        }
    }.flowOn(Dispatchers.IO)
}
