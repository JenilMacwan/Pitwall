package com.jenil.f1comp.data.repository

import com.jenil.f1comp.data.model.LiveTelemetryResponse
import com.jenil.f1comp.data.remote.F1ApiService
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

class LiveTelemetryRepository @Inject constructor(
    private val apiService: F1ApiService
) {
    fun getLiveTimingStream(pollIntervalMs: Long = 1000L): Flow<LiveTelemetryResponse> = flow {
        while (true) {
            emit(apiService.getLiveTiming())
            delay(pollIntervalMs.milliseconds)
        }
    }
}
