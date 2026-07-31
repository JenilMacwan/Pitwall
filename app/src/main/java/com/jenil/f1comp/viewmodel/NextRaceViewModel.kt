package com.jenil.f1comp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jenil.f1comp.data.local.entity.NextRaceEntity
import com.jenil.f1comp.data.repository.NextRaceRepository
import com.jenil.f1comp.ui.state.NextRaceUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.collections.emptyList

@HiltViewModel
class NextRaceViewModel @Inject constructor(
    private val repository: NextRaceRepository
) : ViewModel(){
    val uiState: StateFlow<NextRaceUiState> =
        repository.getCachedNextRaceInfo()
            .map { raceEntity ->

                NextRaceUiState(
                    isLoading = false,
                    nextRace = raceEntity,
                    error = null
                )
            }
            .catch { e ->
                emit(
                    NextRaceUiState(
                        isLoading = false,
                        error = e.message
                    )
                )
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = NextRaceUiState(isLoading = true)
            )

    init {
        refreshNextRaceInfo()
    }

    fun refreshNextRaceInfo() {
        viewModelScope.launch {
            try {
                repository.refreshNextRaceInfo()
            } catch (e: Exception) {

            }
        }
    }
}