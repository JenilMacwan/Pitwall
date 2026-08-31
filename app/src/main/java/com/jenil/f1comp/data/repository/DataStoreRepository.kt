package com.jenil.f1comp.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.jenil.f1comp.data.PreferencesKeys
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DataStoreRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    val isDarkMode: Flow<Boolean?> = dataStore.data.map { preferences ->
        preferences[PreferencesKeys.IS_DARK_MODE]
    }

    val isRaceRemindersEnabled: Flow<Boolean?> = dataStore.data.map { preferences ->
        preferences[PreferencesKeys.RACE_REMINDERS]
    }

    val isSessionRemindersEnabled: Flow<Boolean?> = dataStore.data.map { preferences ->
        preferences[PreferencesKeys.SESSION_REMINDERS]
    }

    val isBreakingNewsEnabled: Flow<Boolean?> = dataStore.data.map { preferences ->
        preferences[PreferencesKeys.BREAKING_NEWS]
    }

    val isLiveRaceEventsEnabled: Flow<Boolean?> = dataStore.data.map { preferences ->
        preferences[PreferencesKeys.LIVE_RACE_EVENTS]
    }

    val isStandingsUpdatesEnabled: Flow<Boolean?> = dataStore.data.map { preferences ->
        preferences[PreferencesKeys.STANDINGS_UPDATES]
    }



    suspend fun setDarkMode(isDarkMode: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.IS_DARK_MODE] = isDarkMode
        }
    }


    suspend fun setRaceRemindersEnabled(isEnabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.RACE_REMINDERS] = isEnabled
        }
    }

    suspend fun setSessionRemindersEnabled(isEnabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.SESSION_REMINDERS] = isEnabled
        }
    }

    suspend fun setBreakingNewsEnabled(isEnabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.BREAKING_NEWS] = isEnabled
        }
    }

    suspend fun setLiveRaceEventsEnabled(isEnabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.LIVE_RACE_EVENTS] = isEnabled
        }
    }

    suspend fun setStandingsUpdatesEnabled(isEnabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.STANDINGS_UPDATES] = isEnabled
        }
    }

}
