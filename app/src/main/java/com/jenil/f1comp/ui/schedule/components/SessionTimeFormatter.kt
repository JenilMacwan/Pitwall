package com.jenil.f1comp.ui.schedule.components

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

/** Formats Ergast/Jolpica UTC session timestamps in the device's local time zone. */
internal fun formatSessionDateTime(date: String, time: String): Pair<String, String> {
    val zone = ZoneId.systemDefault()
    val locale = Locale.getDefault()
    val dateFormatter = DateTimeFormatter.ofPattern("EEE, MMM d, yyyy", locale)
    val timeFormatter = DateTimeFormatter.ofPattern("h:mm a", locale)

    val zoned = runCatching { Instant.parse("${date}T$time").atZone(zone) }
        .recoverCatching {
            LocalDateTime.parse("${date}T${time.removeSuffix("Z")}").atZone(zone)
        }
        .getOrNull()
        ?: return date to time

    return zoned.format(dateFormatter) to "${zoned.format(timeFormatter)} ${zoned.zone.getDisplayName(java.time.format.TextStyle.SHORT, locale)}"
}
