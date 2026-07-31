package com.jenil.f1comp.util

import java.time.Duration
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.Locale

object DateParserUtil {

    // RFC_1123_DATE_TIME handles formats like:
    // "Wed, 15 Jul 2026 13:00:00 BST"
    // "Tue, 14 Jul 2026 16:37:27 +0000"
    private val apiFormatters = listOf(
        DateTimeFormatter.RFC_1123_DATE_TIME,
        DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss z", Locale.ENGLISH),
        DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss Z", Locale.ENGLISH)
    )



    private fun parseDate(dateString: String): ZonedDateTime? {
        for (formatter in apiFormatters) {
            try {
                return ZonedDateTime.parse(dateString, formatter)
            } catch (e: DateTimeParseException) {

            }
        }
        return null
    }


    fun getNormalizedDate(dateString: String?): String {
        if (dateString.isNullOrBlank()) return ""

        val parsedDate = parseDate(dateString) ?: return dateString


        val outputFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.getDefault())
        return parsedDate.format(outputFormatter)
    }

    fun getTimeAgo(dateString: String?): String {
        if (dateString.isNullOrBlank()) return ""

        val parsedDate = parseDate(dateString) ?: return dateString

        val now = ZonedDateTime.now(ZoneId.systemDefault())
        val duration = Duration.between(parsedDate, now)

        return when {
            duration.toMinutes() < 1 -> "Just now"
            duration.toHours() < 1 -> "${duration.toMinutes()}m ago"
            duration.toDays() < 1 -> "${duration.toHours()}h ago"
            duration.toDays() < 7 -> "${duration.toDays()}d ago"
            else -> {
                // If it's older than a week, fallback to a short date format
                val outputFormatter = DateTimeFormatter.ofPattern("dd MMM", Locale.getDefault())
                parsedDate.format(outputFormatter)
            }
        }
    }

    fun getSortableTimestamp(dateString: String?): Long {
        if (dateString.isNullOrBlank()) return 0L

        val parsedDate = parseDate(dateString) ?: return 0L

        return parsedDate.toEpochSecond()
    }
}

