package com.jenil.f1comp.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jenil.f1comp.data.local.entity.CircuitInfoEntity
import com.jenil.f1comp.data.repository.CircuitInfoRepository
import com.jenil.f1comp.data.repository.ScheduleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ScheduleViewModel @Inject constructor(
    private val repository: ScheduleRepository,
    private val circuitInfoRepository: CircuitInfoRepository
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()


    init {
        refreshSchedule()
    }


    val schedule = repository.getCachedScheduleInfo()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val circuitsMap: StateFlow<Map<String, CircuitInfoEntity>> = circuitInfoRepository.getCachedCircuitInfo()
        .map { list -> list.associateBy { it.circuitId } }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyMap()
        )

    fun refreshSchedule() {
        viewModelScope.launch {
            Log.d("ScheduleViewModel", "refreshSchedule() called")
            _isLoading.value = true
            _error.value = null
            try {
                repository.refreshSchedule()
                runCatching { circuitInfoRepository.refreshCircuitInfo() }
                Log.d("ScheduleViewModel", "refreshSchedule() success")
            } catch (e: Exception) {
                Log.e("ScheduleViewModel", "refreshSchedule() error: ${e.message}", e)
                _error.value = e.message ?: "An unknown error occurred"
            } finally {
                _isLoading.value = false
            }
        }
    }
}
