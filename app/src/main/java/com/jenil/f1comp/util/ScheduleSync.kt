package com.jenil.f1comp.util


import android.content.ContentValues
import android.content.Context
import android.provider.CalendarContract
import android.widget.Toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.jenil.f1comp.data.local.entity.ScheduleEntity
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.TimeZone

fun syncRacesToCalendar(context: Context, races: List<ScheduleEntity>) {
    // Run DB operations on IO thread
    CoroutineScope(Dispatchers.IO).launch {
        val contentResolver = context.contentResolver
        var calendarId: Long? = null

        // 1. Query for the Primary Calendar ID
        val projection = arrayOf(CalendarContract.Calendars._ID)
        val selection = "${CalendarContract.Calendars.IS_PRIMARY} = 1"

        try {
            contentResolver.query(
                CalendarContract.Calendars.CONTENT_URI,
                projection,
                selection,
                null,
                null
            )?.use { cursor ->
                if (cursor.moveToFirst()) {
                    calendarId = cursor.getLong(0)
                }
            }
        } catch (e: SecurityException) {
            e.printStackTrace() // Permissions were revoked
        }

        if (calendarId == null) {
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "No primary calendar found", Toast.LENGTH_SHORT).show()
            }
            return@launch
        }

        // 2. Insert upcoming races
        var insertedCount = 0
        races.forEach { race ->
            try {
                val startTimeMillis = parseF1TimeToMillis(race.grandPrix, race.time)
                val endTimeMillis = startTimeMillis + (2 * 60 * 60 * 1000) // Approx 2-hour duration

                val values = ContentValues().apply {
                    put(CalendarContract.Events.DTSTART, startTimeMillis)
                    put(CalendarContract.Events.DTEND, endTimeMillis)
                    put(CalendarContract.Events.TITLE, "F1: ${race.raceName}")
                    put(CalendarContract.Events.DESCRIPTION, "Round ${race.round}: ${race.circuitName}, ${race.circuitLocation}")
                    put(CalendarContract.Events.CALENDAR_ID, calendarId)
                    put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().id)
                }

                contentResolver.insert(CalendarContract.Events.CONTENT_URI, values)
                insertedCount++
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        withContext(Dispatchers.Main) {
            Toast.makeText(context, "Successfully synced $insertedCount races", Toast.LENGTH_SHORT).show()
        }
    }
}

fun removeRacesFromCalendar(context: Context) {
    CoroutineScope(Dispatchers.IO).launch {
        val contentResolver = context.contentResolver
        val selection = "${CalendarContract.Events.TITLE} LIKE ?"
        val selectionArgs = arrayOf("F1:%")

        try {
            val deletedCount = contentResolver.delete(
                CalendarContract.Events.CONTENT_URI,
                selection,
                selectionArgs
            )
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Successfully removed $deletedCount events", Toast.LENGTH_SHORT).show()
            }
        } catch (e: SecurityException) {
            e.printStackTrace()
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }
}


private fun parseF1TimeToMillis(date: String, time: String): Long {
    return try {
        val parsedDate = LocalDate.parse(date)
        val parsedTime = LocalTime.parse(time.removeSuffix("Z"))
        val zonedDateTime = ZonedDateTime.of(parsedDate, parsedTime, ZoneId.of("UTC"))
        zonedDateTime.toInstant().toEpochMilli()
    } catch (e: Exception) {
        e.printStackTrace()
        System.currentTimeMillis()
    }
}

