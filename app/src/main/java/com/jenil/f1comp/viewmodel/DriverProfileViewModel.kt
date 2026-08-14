package com.jenil.f1comp.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jenil.f1comp.data.local.entity.DriverProfileEntity
import com.jenil.f1comp.data.repository.DriverProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DriverProfileViewModel @Inject constructor(
    private val repository: DriverProfileRepository
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    val driverProfiles: StateFlow<List<DriverProfileEntity>> = repository.getCachedDriverProfiles()
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
                repository.refreshDriverProfiles()
            } catch (e: Exception) {
                _error.value = e.message ?: "An unknown error occurred"
                Log.e("DriverProfileVM", "Error refreshing driver profiles", e)
            } finally {
                _isLoading.value = false
            }
        }
    }
}
