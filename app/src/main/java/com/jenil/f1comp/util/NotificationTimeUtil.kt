package com.jenil.f1comp.util

import java.time.Instant

object NotificationTimeUtils {

    fun calculateNotificationTime(date: String, time: String, minutesBefore: Int = 15): Long {
        return try {
            val isoString = "${date}T${time}"
            val sessionTimeMillis = Instant.parse(isoString).toEpochMilli()
            sessionTimeMillis - (minutesBefore * 60 * 1000L)
        } catch (_: Exception) {
            0L
        }
    }
}