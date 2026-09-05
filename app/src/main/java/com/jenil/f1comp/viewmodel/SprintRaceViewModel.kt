package com.jenil.f1comp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jenil.f1comp.data.repository.SprintResultRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SprintRaceViewModel @Inject constructor(
    private val repository: SprintResultRepository
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    private val _raceParams = MutableStateFlow<Pair<String, Int>?>(null)


    fun refreshSprintResults(
        raceId: String,
        year: Int
    ){
        viewModelScope.launch {
            _isLoading.value = true
            _raceParams.value = Pair(raceId, year)
            _error.value = null
            try {
                repository.refreshSprintResults(raceId, year)
            } catch (e: Exception) {
                val errorMessage = e.message ?: "An unknown error occurred"
                _error.value = errorMessage
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearError() {
        _error.value = null
    }

    fun sprintResultsFlow(raceId: String) = repository.getCachedSprintResults(raceId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

}