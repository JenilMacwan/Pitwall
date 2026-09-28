package com.jenil.f1comp.util

import androidx.compose.ui.graphics.Color
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import android.graphics.Color as AndroidColor

fun extractGap(gapField: Any?): String {
    return when (gapField) {
        is String -> gapField
        is Map<*, *> -> gapField["Value"]?.toString() ?: ""
        else -> ""
    }
}

fun parseTeamColor(hex: String?): Color {
    if (hex.isNullOrBlank()) return Color.Gray
    return try {
        Color(AndroidColor.parseColor(if (hex.startsWith("#")) hex else "#$hex"))
    } catch (e: Exception) {
        Color.Gray
    }
}

fun formatUtcToLocalDateTime(utcTimeString: String?): String {
    if (utcTimeString.isNullOrBlank()) return "--:--:--"
    val trimmed = utcTimeString.trim()

    return try {
        val instant = try {
            Instant.parse(trimmed)
        } catch (e: Exception) {
            val timeParts = trimmed.split(":")
            if (timeParts.size >= 2) {
                val hour = timeParts[0].toIntOrNull() ?: 0
                val minute = timeParts[1].toIntOrNull() ?: 0
                val second = if (timeParts.size > 2) timeParts[2].take(2).toIntOrNull() ?: 0 else 0

                LocalTime.of(hour, minute, second)
                    .atDate(LocalDate.now(ZoneOffset.UTC))
                    .atZone(ZoneOffset.UTC)
                    .toInstant()
            } else {
                null
            }
        }

        if (instant != null) {
            val zonedDateTime = instant.atZone(ZoneId.systemDefault())
            val formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)
            formatter.format(zonedDateTime)
        } else {
            trimmed
        }
    } catch (e: Exception) {
        trimmed
    }
}