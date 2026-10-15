package com.jenil.f1comp.data.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.jenil.f1comp.data.local.dao.ChatMessageDao
import com.jenil.f1comp.data.local.entity.ChatMessageEntity
import com.jenil.f1comp.data.model.APEX_ACCOUNT_REQUIRED
import com.jenil.f1comp.data.model.ChatMessage
import com.jenil.f1comp.data.model.ChatReply
import com.jenil.f1comp.data.model.ChatRequest
import com.jenil.f1comp.data.model.Resource
import com.jenil.f1comp.data.remote.ChatApiService
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID
import javax.inject.Inject

private const val TAG = "ChatRepository"
private const val REFERENCE_SEPARATOR = "\u001F"

class ChatRepository @Inject constructor(
    private val apiService: ChatApiService,
    private val chatMessageDao: ChatMessageDao,
    private val dataStoreRepository: DataStoreRepository,
    private val firebaseAuth: FirebaseAuth
) {
    fun observeMessages(sessionId: String): Flow<List<ChatMessage>> =
        chatMessageDao.observeSession(sessionId).map { rows ->
            rows.map { row ->
                ChatMessage(
                    id = row.id,
                    text = row.text,
                    isUser = row.isUser,
                    timestamp = row.timestamp,
                    referenceDocuments = row.referenceDocuments.split(REFERENCE_SEPARATOR).filter(String::isNotBlank),
                    dataSources = row.dataSources.split(REFERENCE_SEPARATOR).filter(String::isNotBlank)
                )
            }
        }

    suspend fun getOrCreateSessionId(): String =
        dataStoreRepository.getOrCreateApexChatSessionId()

    suspend fun createSessionId(): String =
        dataStoreRepository.createApexChatSessionId()

    fun observeResponseStyle(): Flow<String> = dataStoreRepository.apexResponseStyle

    suspend fun setResponseStyle(style: String) {
        dataStoreRepository.setApexResponseStyle(style)
    }

    suspend fun saveMessage(sessionId: String, text: String, isUser: Boolean, references: List<String> = emptyList(), sources: List<String> = emptyList()) {
        val now = System.currentTimeMillis()
        chatMessageDao.insert(
            ChatMessageEntity(
                id = UUID.randomUUID().toString(),
                sessionId = sessionId,
                text = text,
                isUser = isUser,
                timestamp = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(now)),
                referenceDocuments = references.joinToString(REFERENCE_SEPARATOR),
                dataSources = sources.joinToString(REFERENCE_SEPARATOR),
                createdAt = now
            )
        )
    }

    fun sendMessage(
        sessionId: String,
        query: String,
        lang: String = "en",
        responseStyle: String = "concise",
): Flow<Resource<ChatReply>> = flow {
        Log.d(TAG, "Sending chat request (language=$lang, queryLength=${query.length})")
        emit(Resource.Loading)
        try {
            val user = firebaseAuth.currentUser ?: throw IllegalStateException("Sign in to use Apex chat.")
            if (user.isAnonymous) {
                emit(Resource.Error(APEX_ACCOUNT_REQUIRED))
                return@flow
            }
            val token = try {
                user.getIdToken(true).await().token?.takeIf(String::isNotBlank)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.w(TAG, "Could not refresh Firebase ID token for Apex chat", e)
                emit(Resource.Error("Could not refresh your sign-in token. Check your connection, then try again."))
                return@flow
            } ?: run {
                emit(Resource.Error("Could not refresh your sign-in token. Sign in again, then retry."))
                return@flow
            }
            val response = apiService.sendChatMessage("Bearer $token", ChatRequest(sessionId = sessionId, query = query, lang = lang, responseStyle = responseStyle))
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                if (body.response.contains("failed to initialize firebase token", ignoreCase = true)) {
                    Log.e(TAG, "Chat service returned a Firebase token initialization failure")
                    emit(Resource.Error("Apex could not initialize Firebase authentication. Please try again later."))
                    return@flow
                }
                val reply = ChatReply(body.response, body.referenceDocuments.orEmpty(), body.dataSources.orEmpty())
                saveMessage(sessionId, reply.response, isUser = false, references = reply.referenceDocuments, sources = reply.dataSources)
                emit(Resource.Success(reply))
            } else {
                Log.w(TAG, "Chat request failed (status=${response.code()})")
                val message = when {
                    response.code() == 401 || response.code() == 403 ->
                        "Your Apex sign-in was rejected. Sign out and back in, then try again."
                    response.code() >= 500 ->
                        "Apex could not validate the sign-in with Firebase right now. Please try again later."
                    else -> "Apex could not respond. Please try again."
                }
                emit(Resource.Error(message))
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Chat request failed", e)
            emit(Resource.Error("Apex could not connect. Check your connection and try again."))
        }
    }.flowOn(Dispatchers.IO)

    suspend fun clearSession(sessionId: String) {
        try {
            val token = firebaseAuth.currentUser?.getIdToken(false)?.await()?.token
            if (token != null) {
                val response = apiService.clearChatSession("Bearer $token", sessionId)
                if (!response.isSuccessful) Log.w(TAG, "Could not clear remote chat session (status=${response.code()})")
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.w(TAG, "Could not clear remote chat session", e)
        } finally {
            chatMessageDao.deleteSession(sessionId)
        }
    }

    suspend fun clearAllAccountChatData() {
        val sessionId = dataStoreRepository.getOrCreateApexChatSessionId()
        try {
            val user = firebaseAuth.currentUser
            val token = user?.getIdToken(false)?.await()?.token
            if (!token.isNullOrBlank()) {
                val response = apiService.clearChatSession("Bearer $token", sessionId)
                if (!response.isSuccessful) {
                    Log.w(TAG, "Could not clear remote chat session (status=${response.code()})")
                }
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.w(TAG, "Could not clear remote chat session", e)
        } finally {
            chatMessageDao.deleteAllMessages()
            dataStoreRepository.clearApexChatSessionId()
        }
    }
}
