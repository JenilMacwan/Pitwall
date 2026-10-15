package com.jenil.f1comp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jenil.f1comp.data.model.LiveTelemetryResponse
import com.jenil.f1comp.data.repository.DataStoreRepository
import com.jenil.f1comp.data.repository.LiveTelemetryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

sealed interface TelemetryUiState {
    object Idle : TelemetryUiState
    object Loading : TelemetryUiState
    object Success : TelemetryUiState
    data class Error(
        val message: String?,
        val throwable: Throwable? = null
    ) : TelemetryUiState
}

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class LiveTelemetryViewModel @Inject constructor(
    private val repository: LiveTelemetryRepository,
    private val dataStoreRepository: DataStoreRepository
) : ViewModel() {

    private val _telemetryUiState = MutableStateFlow<TelemetryUiState>(TelemetryUiState.Idle)
    val telemetryUiState: StateFlow<TelemetryUiState> = _telemetryUiState.asStateFlow()

    private val _syncDelayMs = MutableStateFlow(0L)
    val syncDelayMs = _syncDelayMs.asStateFlow()

    private val retrySignal = MutableSharedFlow<Unit>(replay = 1).apply {
        tryEmit(Unit)
    }

    init {
        viewModelScope.launch {
            dataStoreRepository.isTelemetrySyncDelayed.collect { isDelayed ->
                if (isDelayed != null) {
                    toggleSyncDelay(isDelayed)
                }
            }
        }
    }

    fun toggleSyncDelay(isDelayed: Boolean) {
        _syncDelayMs.value = if (isDelayed) 63000L else 0L
    }

    fun retryTelemetry() {
        retrySignal.tryEmit(Unit)
    }

    val telemetryData: StateFlow<LiveTelemetryResponse?> = retrySignal
        .flatMapLatest {
            flow {
                val dataQueue = mutableListOf<Pair<Long, LiveTelemetryResponse>>()

                coroutineScope {
                    launch {
                        repository.getLiveTimingStream()
                            .onStart {
                                if (_telemetryUiState.value !is TelemetryUiState.Success) {
                                    _telemetryUiState.value = TelemetryUiState.Loading
                                }
                            }
                            .onEach {
                                _telemetryUiState.value = TelemetryUiState.Success
                            }
                            .catch { e ->
                                _telemetryUiState.value = TelemetryUiState.Error(
                                    e.localizedMessage ?: "Failed to connect to live telemetry stream",
                                    e
                                )
                            }
                            .collect { data ->
                                dataQueue.add(Pair(System.currentTimeMillis(), data))
                            }
                    }

                    while (true) {
                        val currentDelay = _syncDelayMs.value
                        val targetTime = System.currentTimeMillis() - currentDelay
                        var latestValidData: LiveTelemetryResponse? = null

                        while (dataQueue.isNotEmpty() && dataQueue.first().first <= targetTime) {
                            latestValidData = dataQueue.removeAt(0).second
                        }
                        if (latestValidData != null) {
                            emit(latestValidData)
                        }
                        delay(100.milliseconds)
                    }
                }
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )
}
