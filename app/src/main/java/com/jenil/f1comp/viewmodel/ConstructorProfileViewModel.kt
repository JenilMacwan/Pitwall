package com.jenil.f1comp.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jenil.f1comp.data.local.entity.ConstructorProfileEntity
import com.jenil.f1comp.data.repository.ConstructorProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ConstructorProfileViewModel @Inject constructor(
    private val repository: ConstructorProfileRepository
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    val constructorProfiles: StateFlow<List<ConstructorProfileEntity>> = repository.getCachedConstructorProfiles()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    init {
        refreshProfiles()
    }

    fun refreshProfiles() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                repository.refreshConstructorProfiles()
            } catch (e: Exception) {
                _error.value = e.message ?: "An unknown error occurred"
                Log.e("ConstructorProfileVM", "Error refreshing team profiles", e)
            } finally {
                _isLoading.value = false
            }
        }
    }
}
