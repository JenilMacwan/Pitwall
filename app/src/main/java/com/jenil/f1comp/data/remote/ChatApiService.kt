package com.jenil.f1comp.data.remote

import com.jenil.f1comp.data.model.ChatRequest
import com.jenil.f1comp.data.model.ChatResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface ChatApiService {

    @POST("api/v1/chat")
    suspend fun sendChatMessage(
        @Header("X-API-Key") apiKey: String,
        @Body request: ChatRequest
    ): Response<ChatResponse>

}