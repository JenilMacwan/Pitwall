package com.jenil.f1comp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.messaging
import com.jenil.f1comp.data.repository.DataStoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val repository: DataStoreRepository
) : ViewModel() {

    val isDarkMode: StateFlow<Boolean?> = repository.isDarkMode.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    val isRaceRemindersEnabled: StateFlow<Boolean?> = repository.isRaceRemindersEnabled.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )
    val isSessionRemindersEnabled: StateFlow<Boolean?> = repository.isSessionRemindersEnabled.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )
    val isBreakingNewsEnabled: StateFlow<Boolean?> = repository.isBreakingNewsEnabled.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )
    val isLiveRaceEventsEnabled: StateFlow<Boolean?> = repository.isLiveRaceEventsEnabled.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )
    val isStandingsUpdatesEnabled: StateFlow<Boolean?> = repository.isStandingsUpdatesEnabled.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    fun toggleDarkMode(isDarkMode: Boolean) {
        viewModelScope.launch {
            repository.setDarkMode(isDarkMode)
        }
    }

    fun setRaceRemindersEnabled(isEnabled: Boolean) {
        viewModelScope.launch {
            repository.setRaceRemindersEnabled(isEnabled)
        }
    }
    fun setSessionRemindersEnabled(isEnabled: Boolean) {
        viewModelScope.launch {
            repository.setSessionRemindersEnabled(isEnabled)
        }
    }
    fun setBreakingNewsEnabled(isEnabled: Boolean) {
        viewModelScope.launch {
            repository.setBreakingNewsEnabled(isEnabled)
            if(isEnabled) {
                Firebase.messaging.subscribeToTopic("breaking_news")
            } else {
                Firebase.messaging.unsubscribeFromTopic("breaking_news")
            }
        }
    }
    fun setLiveRaceEventsEnabled(isEnabled: Boolean) {
        viewModelScope.launch {
            repository.setLiveRaceEventsEnabled(isEnabled)
            if (isEnabled) {
                Firebase.messaging.subscribeToTopic("live_race_events")
            } else {
                Firebase.messaging.unsubscribeFromTopic("live_race_events")
            }
        }
    }
    fun setStandingsUpdatesEnabled(isEnabled: Boolean) {
        viewModelScope.launch {
            repository.setStandingsUpdatesEnabled(isEnabled)
            if (isEnabled) {
                Firebase.messaging.subscribeToTopic("standings_updates")
            } else {
                Firebase.messaging.unsubscribeFromTopic("standings_updates")
            }
        }
    }
}
