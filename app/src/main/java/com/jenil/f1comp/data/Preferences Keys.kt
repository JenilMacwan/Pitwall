package com.jenil.f1comp.data

import androidx.datastore.preferences.core.booleanPreferencesKey

object PreferencesKeys {
    val IS_DARK_MODE = booleanPreferencesKey("is_dark_mode")
    val RACE_REMINDERS = booleanPreferencesKey("race_reminders")
    val SESSION_REMINDERS = booleanPreferencesKey("session_reminders")
    val BREAKING_NEWS = booleanPreferencesKey("breaking_news")
    val LIVE_RACE_EVENTS = booleanPreferencesKey("live_race_events")
    val STANDINGS_UPDATES = booleanPreferencesKey("standings_updates")
}
