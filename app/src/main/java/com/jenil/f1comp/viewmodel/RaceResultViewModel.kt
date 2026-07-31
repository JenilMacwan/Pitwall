package com.jenil.f1comp.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jenil.f1comp.data.repository.RaceResultRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RaceResultViewModel @Inject constructor(
    private val repository: RaceResultRepository
) : ViewModel(){
    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    private val _raceParams = MutableStateFlow<Pair<String, Int>?>(null)


    fun refreshRaceResult(raceId: String, year: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            _raceParams.value = Pair(raceId, year)
            _error.value = null
            try {
                repository.refreshRaceResults(raceId, year)
                Log.d("RaceResult", "Result refreshed for $raceId in $year")
            } catch (e: Exception) {
                val errorMessage = e.message ?: "An unknown error occurred"
                _error.value = errorMessage
                Log.e("RaceResult", "Error refreshing race results: $errorMessage", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearError() {
        _error.value = null
    }

    fun raceResultsFlow(raceId: String) = repository.getCachedRaceResults(raceId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
}