package com.jenil.f1comp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jenil.f1comp.data.local.entity.TeamRadioEntity
import com.jenil.f1comp.data.repository.TeamRadioRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TeamRadioViewModel @Inject constructor(
    private val repository: TeamRadioRepository
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    private val _selectedDriverCode = MutableStateFlow("ALL")
    val selectedDriverCode = _selectedDriverCode.asStateFlow()

    val teamRadios: StateFlow<List<TeamRadioEntity>> = combine(
        repository.getCachedTeamRadio(),
        _selectedDriverCode
    ) { radios, selectedCode ->
        if (selectedCode.equals("ALL", ignoreCase = true)) {
            radios
        } else {
            radios.filter { it.driverCode.equals(selectedCode, ignoreCase = true) }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    init {
        refreshTeamRadio()
    }

    fun selectDriverCode(code: String) {
        _selectedDriverCode.value = code
    }

    fun refreshTeamRadio() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                repository.refreshTeamRadio()
            } catch (e: Exception) {
                _error.value = e.message ?: "An unknown error occurred"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearError() {
        _error.value = null
    }
}
