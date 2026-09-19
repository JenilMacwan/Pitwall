package com.jenil.f1comp.data

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

object PreferencesKeys {
    val IS_DARK_MODE = booleanPreferencesKey("is_dark_mode")
    val RACE_REMINDERS = booleanPreferencesKey("race_reminders")
    val SESSION_REMINDERS = booleanPreferencesKey("session_reminders")
    val BREAKING_NEWS = booleanPreferencesKey("breaking_news")
    val LIVE_RACE_EVENTS = booleanPreferencesKey("live_race_events")
    val STANDINGS_UPDATES = booleanPreferencesKey("standings_updates")
    val FAVORITE_TEAM = stringPreferencesKey("favorite_team")
    val FAVORITE_DRIVER = stringPreferencesKey("favorite_driver")
    val IS_FIRST_LAUNCH = booleanPreferencesKey("is_first_launch")
}
