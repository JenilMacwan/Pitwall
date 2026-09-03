package com.jenil.f1comp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jenil.f1comp.data.model.Resource
import com.jenil.f1comp.data.repository.ChatRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

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

    fun sendMessage(sessionId: String, query: String) {
        viewModelScope.launch {
            repository.sendMessage(sessionId, query).collect { resource ->
                when (resource) {
                    is Resource.Loading -> _uiState.value = ChatUiState.Loading
                    is Resource.Success -> _uiState.value = ChatUiState.Success(resource.data)
                    is Resource.Error -> _uiState.value = ChatUiState.Error(resource.message)
                }
            }
        }
    }
}
