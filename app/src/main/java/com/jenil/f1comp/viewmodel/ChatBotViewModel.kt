package com.jenil.f1comp.viewmodel

import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jenil.f1comp.data.model.ChatMessage
import com.jenil.f1comp.data.model.Resource
import com.jenil.f1comp.data.repository.ChatRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

sealed class ChatUiState {
    object Idle : ChatUiState()
    object Loading : ChatUiState()
    data class Success(val reply: String) : ChatUiState()
    data class Error(val message: String) : ChatUiState()
}

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val repository: ChatRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<ChatUiState>(ChatUiState.Idle)
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()
    private val _activeSessionId = MutableStateFlow<String?>(null)
    val activeSessionId: StateFlow<String?> = _activeSessionId.asStateFlow()
    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()
    private val _responseStyle = MutableStateFlow("concise")
    val responseStyle: StateFlow<String> = _responseStyle.asStateFlow()

    private var requestJob: Job? = null
    private var observationJob: Job? = null

    init {
        viewModelScope.launch {
            observeSession(repository.getOrCreateSessionId())
        }
    }

    private fun observeSession(sessionId: String) {
        _activeSessionId.value = sessionId
        observationJob?.cancel()
        observationJob = viewModelScope.launch {
            repository.observeMessages(sessionId).collect { _messages.value = it }
        }
    }

    fun sendMessage(sessionId: String, query: String, lang: String? = null) {
        submitMessage(sessionId, query, lang, saveUserMessage = true)
    }

    fun toggleResponseStyle() {
        viewModelScope.launch {
            val updatedStyle = if (_responseStyle.value == "concise") "detailed" else "concise"
            repository.setResponseStyle(updatedStyle)
            _responseStyle.value = updatedStyle
        }
    }

    fun retryMessage(sessionId: String, query: String) {
        submitMessage(sessionId, query, lang = null, saveUserMessage = false)
    }

    private fun submitMessage(sessionId: String, query: String, lang: String?, saveUserMessage: Boolean) {
        val requestedLang = lang ?: run {
            val locales = AppCompatDelegate.getApplicationLocales()
            if (!locales.isEmpty) locales.get(0)?.language?.ifEmpty { null } else null
        } ?: Locale.getDefault().language.ifEmpty { "en" }
        val activeLang = requestedLang.takeIf { it in setOf("en", "es", "it", "de", "nl", "fr") } ?: "en"

        requestJob?.cancel()
        requestJob = viewModelScope.launch {
            if (saveUserMessage) repository.saveMessage(sessionId, query, isUser = true)
            repository.sendMessage(sessionId = sessionId, query = query, lang = activeLang, responseStyle = _responseStyle.value).collect { resource ->
                when (resource) {
                    is Resource.Loading -> _uiState.value = ChatUiState.Loading
                    is Resource.Success -> _uiState.value = ChatUiState.Success(resource.data.response)
                    is Resource.Error -> _uiState.value = ChatUiState.Error(resource.message)
                }
            }
        }
    }
    fun cancelRequest() {
        requestJob?.cancel()
        requestJob = null
        _uiState.value = ChatUiState.Idle
    }

    fun clearSession(sessionId: String) {
        cancelRequest()
        viewModelScope.launch {
            repository.clearSession(sessionId)
            observeSession(repository.createSessionId())
        }
    }
}
