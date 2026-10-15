package com.jenil.f1comp.data.remote

import com.jenil.f1comp.data.model.ChatRequest
import com.jenil.f1comp.data.model.ChatResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface ChatApiService {

    @DELETE("api/v1/chat/{session_id}")
    suspend fun clearChatSession(
        @Header("Authorization") authorization: String,
        @Path("session_id") sessionId: String
    ): Response<Unit>

    @POST("api/v1/chat")
    suspend fun sendChatMessage(
        @Header("Authorization") authorization: String,
        @Body request: ChatRequest
    ): Response<ChatResponse>
}
