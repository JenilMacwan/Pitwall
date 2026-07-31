package com.jenil.f1comp.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jenil.f1comp.data.repository.ConstructorStandingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ConstructorStandingsViewModel @Inject constructor(
    private val repository: ConstructorStandingsRepository
): ViewModel(){
    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    init {
        refreshConstructorStandingsInfo()
    }

    fun refreshConstructorStandingsInfo(){
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                Log.d("F1Debug", "ViewModel: Calling Constructor Repository...")
                repository.refreshConstructorStandings()
            } catch (e: Exception) {
                Log.e("F1Debug", "ViewModel: FATAL ERROR fetching constructors!", e)
                _error.value = e.message ?: "An unknown error occurred"
            } finally {
                _isLoading.value = false
            }
        }
    }

    val constructorStandings = repository.getCachedConstructorStanding()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
}