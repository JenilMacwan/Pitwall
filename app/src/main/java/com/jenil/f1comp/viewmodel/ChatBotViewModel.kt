package com.jenil.f1comp.viewmodel

import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jenil.f1comp.data.model.Resource
import com.jenil.f1comp.data.repository.ChatRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

private const val TAG = "ChatViewModel"

sealed class ChatUiState {
    object Idle : ChatUiState()
    object Loading : ChatUiState()
    data class Success(val response: String) : ChatUiState()
    data class Error(val message: String) : ChatUiState()
}

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val repository: ChatRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<ChatUiState>(ChatUiState.Idle)
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    fun sendMessage(sessionId: String, query: String, lang: String? = null) {
        val activeLang = lang ?: run {
            val locales = AppCompatDelegate.getApplicationLocales()
            if (!locales.isEmpty) {
                locales.get(0)?.language?.ifEmpty { null }
            } else {
                null
            }
        } ?: Locale.getDefault().language.ifEmpty { "en" }

        Log.d(TAG, "[VIEWMODEL TRIGGER] Sending query: \"$query\" | SessionID: $sessionId | Lang: $activeLang")
        viewModelScope.launch {
            repository.sendMessage(sessionId = sessionId, query = query, lang = activeLang).collect { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        Log.d(TAG, "[VIEWMODEL STATE] ChatUiState -> Loading")
                        _uiState.value = ChatUiState.Loading
                    }
                    is Resource.Success -> {
                        Log.d(TAG, "[VIEWMODEL STATE] ChatUiState -> Success | Length: ${resource.data.length}")
                        _uiState.value = ChatUiState.Success(resource.data)
                    }
                    is Resource.Error -> {
                        Log.e(TAG, "[VIEWMODEL STATE] ChatUiState -> Error: ${resource.message}")
                        _uiState.value = ChatUiState.Error(resource.message)
                    }
                }
            }
        }
    }
}
