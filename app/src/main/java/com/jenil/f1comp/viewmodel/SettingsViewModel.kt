package com.jenil.f1comp.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.messaging.messaging
import com.jenil.f1comp.data.local.entity.ConstructorProfileEntity
import com.jenil.f1comp.data.local.entity.DriverProfileEntity
import com.jenil.f1comp.data.repository.ConstructorProfileRepository
import com.jenil.f1comp.data.repository.DataStoreRepository
import com.jenil.f1comp.data.repository.DriverProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val repository: DataStoreRepository,
    private val constructorProfileRepository: ConstructorProfileRepository,
    private val driverProfileRepository: DriverProfileRepository
) : ViewModel() {

    init {
        viewModelScope.launch {
            runCatching { constructorProfileRepository.refreshConstructorProfiles() }
            runCatching { driverProfileRepository.refreshDriverProfiles() }
        }
    }

    val constructorProfiles: StateFlow<List<ConstructorProfileEntity>> = constructorProfileRepository.getCachedConstructorProfiles()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val driverProfiles: StateFlow<List<DriverProfileEntity>> = driverProfileRepository.getCachedDriverProfiles()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

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

    val isFavoritesUpdatesEnabled: StateFlow<Boolean?> =
        repository.isFavoritesUpdateEnabled.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    val favoriteTeam: StateFlow<String?> = repository.favoriteTeam.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = null
    )

    val favoriteDriver: StateFlow<String?> = repository.favoriteDriver.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = null
    )

    val isFirstLaunch: StateFlow<Boolean?> = repository.isFirstLaunch.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    val selectedThemeId: StateFlow<String?> = repository.selectedThemeId.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    fun toggleDarkMode(isDarkMode: Boolean) {
        viewModelScope.launch {
            repository.setDarkMode(isDarkMode)
        }
    }

    fun setFirstLaunchCompleted() {
        viewModelScope.launch {
            repository.setFirstLaunchCompleted()
        }
    }

    fun setFavoriteTeam(rawTeam: String) {

        val team = cleanTeamName(rawTeam)
        viewModelScope.launch {
            val oldTeam = favoriteTeam.value
            val isNotificationsOn = isFavoritesUpdatesEnabled.value == true

            // Unsubscribe from the old team ONLY if notifications are currently ON
            if (isNotificationsOn && !oldTeam.isNullOrBlank() && oldTeam != team) {
                Firebase.messaging.unsubscribeFromTopic(sanitizeTopic("favorite_team_$oldTeam"))
            }

            // Subscribe to the new team ONLY if notifications are currently ON
            if (isNotificationsOn && team.isNotEmpty()) {
                Firebase.messaging.subscribeToTopic(sanitizeTopic("favorite_team_$team"))
            }

            // Finally, save the new team to the database
            repository.setFavoriteTeam(team)
        }
    }

    fun setFavoriteDriver(rawDriver: String) {
        val driver = cleanDriverName(rawDriver)
        viewModelScope.launch {
            val oldDriver = favoriteDriver.value
            val isNotificationsOn = isFavoritesUpdatesEnabled.value == true

            if (isNotificationsOn && !oldDriver.isNullOrBlank() && oldDriver != driver) {
                Firebase.messaging.unsubscribeFromTopic(sanitizeTopic("favorite_driver_$oldDriver"))
            }

            if (isNotificationsOn && driver.isNotEmpty()) {
                Firebase.messaging.subscribeToTopic(sanitizeTopic("favorite_driver_$driver"))
            }

            repository.setFavoriteDriver(driver)
        }
    }

    fun setSelectedThemeId(id: String) {
        viewModelScope.launch {
            repository.setSelectedThemeId(id)
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

    fun setFavoritesUpdatesEnabled(isEnabled: Boolean) {
        viewModelScope.launch {
            repository.setFavoritesUpdatesEnabled(isEnabled)
            val currentTeam = favoriteTeam.value
            val currentDriver = favoriteDriver.value

            Log.d("FCM_TEST", "Toggle is $isEnabled. Team is $currentTeam. Driver is $currentDriver")
            if (isEnabled) {
                if (!currentTeam.isNullOrBlank()) {
                    val topic = sanitizeTopic("favorite_team_$currentTeam")
                    Log.d("FCM_TEST", "Subscribing to $topic") // ADD THIS
                    Firebase.messaging.subscribeToTopic(topic)
                }
                if (!currentDriver.isNullOrBlank()) {
                    val topic = sanitizeTopic("favorite_driver_$currentDriver")
                    Log.d("FCM_TEST", "Subscribing to $topic") // ADD THIS
                    Firebase.messaging.subscribeToTopic(topic)
                }
            } else {
                if (!currentTeam.isNullOrBlank()) {
                    Firebase.messaging.unsubscribeFromTopic(sanitizeTopic("favorite_team_$currentTeam"))
                }
                if (!currentDriver.isNullOrBlank()) {
                    Firebase.messaging.unsubscribeFromTopic(sanitizeTopic("favorite_driver_$currentDriver"))
                }
            }
        }
    }

    private fun sanitizeTopic(input: String): String {
        return input.trim()
            .lowercase()
            .replace(Regex("[^a-z0-9-_.~%]"), "_")
            .replace(Regex("_+"), "_")
            .trim('_')
    }

    private fun cleanTeamName(team: String): String {
        val lower = team.lowercase()
        return when {
            lower.contains("mclaren") -> "McLaren"
            lower.contains("red bull") -> "Red Bull Racing"
            lower.contains("mercedes") -> "Mercedes"
            lower.contains("ferrari") -> "Ferrari"
            lower.contains("aston martin") -> "Aston Martin"
            lower.contains("alpine") -> "Alpine"
            lower.contains("williams") -> "Williams"
            lower.contains("haas") -> "Haas"
            lower.contains("sauber") || lower.contains("kick") || lower.contains("stake") -> "Kick Sauber"
            lower.contains("rb") || lower.contains("racing bulls") -> "RB"
            else -> team
        }
    }

    private fun cleanDriverName(driver: String): String {
        // Removes any numbers at the end (e.g. "Charles Leclerc 16" -> "Charles Leclerc")
        return driver.replace(Regex("\\s+#?\\d+$"), "").trim()
    }

}