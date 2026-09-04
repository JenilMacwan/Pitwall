package com.jenil.f1comp.util

import android.content.ContentValues
import android.content.Context
import android.provider.CalendarContract
import android.widget.Toast
import com.jenil.f1comp.data.local.entity.ScheduleEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.TimeZone

private data class SessionItem(
    val sessionName: String,
    val date: String,
    val time: String,
    val durationHours: Long = 1L
)

fun syncRacesToCalendar(context: Context, races: List<ScheduleEntity>) {
    CoroutineScope(Dispatchers.IO).launch {
        val contentResolver = context.contentResolver
        var calendarId: Long? = null

        // 1. Query for Primary Calendar ID
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
            e.printStackTrace()
        }

        if (calendarId == null) {
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "No primary calendar found", Toast.LENGTH_SHORT).show()
            }
            return@launch
        }

        var insertedCount = 0

        races.forEach { race ->
            val sessionsToSync = mutableListOf<SessionItem>()

            // FP1
            race.firstPractice?.let {
                if (it.date.isNotBlank() && it.time.isNotBlank()) {
                    sessionsToSync.add(SessionItem("Practice 1 (FP1)", it.date, it.time, 1L))
                }
            }
            // FP2
            race.secondPractice?.let {
                if (it.date.isNotBlank() && it.time.isNotBlank()) {
                    sessionsToSync.add(SessionItem("Practice 2 (FP2)", it.date, it.time, 1L))
                }
            }
            // FP3
            race.thirdPractice?.let {
                if (it.date.isNotBlank() && it.time.isNotBlank()) {
                    sessionsToSync.add(SessionItem("Practice 3 (FP3)", it.date, it.time, 1L))
                }
            }
            // Sprint Shootout / Qualifying
            race.sprintQualifying?.let {
                if (it.date.isNotBlank() && it.time.isNotBlank()) {
                    sessionsToSync.add(SessionItem("Sprint Shootout", it.date, it.time, 1L))
                }
            }
            // Sprint Race
            race.sprint?.let {
                if (it.date.isNotBlank() && it.time.isNotBlank()) {
                    sessionsToSync.add(SessionItem("Sprint Race", it.date, it.time, 1L))
                }
            }
            // Qualifying
            race.qualifying?.let {
                if (it.date.isNotBlank() && it.time.isNotBlank()) {
                    sessionsToSync.add(SessionItem("Qualifying", it.date, it.time, 1L))
                }
            }
            // Main Grand Prix
            if (race.grandPrix.isNotBlank() && race.time.isNotBlank()) {
                sessionsToSync.add(SessionItem("Grand Prix", race.grandPrix, race.time, 2L))
            }

            sessionsToSync.forEach { session ->
                val startTimeMillis = parseF1TimeToMillis(session.date, session.time)
                if (startTimeMillis > 0) {
                    val endTimeMillis = startTimeMillis + (session.durationHours * 60 * 60 * 1000)
                    val eventTitle = "🏎️ PitWall | ${race.raceName} - ${session.sessionName}"
                    val description = """
                        🏎️ Synced via PitWall F1 App
                        Round ${race.round}: ${race.raceName}
                        Session: ${session.sessionName}
                        Circuit: ${race.circuitName}, ${race.circuitLocation} (${race.circuitCountry})
                    """.trimIndent()

                    try {
                        // Check if event already exists in this calendar
                        val eventProj = arrayOf(CalendarContract.Events._ID)
                        val eventSel = "${CalendarContract.Events.CALENDAR_ID} = ? AND ${CalendarContract.Events.TITLE} = ? AND ${CalendarContract.Events.DTSTART} = ?"
                        val eventArgs = arrayOf(calendarId.toString(), eventTitle, startTimeMillis.toString())

                        var alreadyExists = false
                        contentResolver.query(
                            CalendarContract.Events.CONTENT_URI,
                            eventProj,
                            eventSel,
                            eventArgs,
                            null
                        )?.use { cursor ->
                            if (cursor.moveToFirst()) {
                                alreadyExists = true
                            }
                        }

                        if (!alreadyExists) {
                            val values = ContentValues().apply {
                                put(CalendarContract.Events.DTSTART, startTimeMillis)
                                put(CalendarContract.Events.DTEND, endTimeMillis)
                                put(CalendarContract.Events.TITLE, eventTitle)
                                put(CalendarContract.Events.DESCRIPTION, description)
                                put(CalendarContract.Events.EVENT_LOCATION, "${race.circuitName}, ${race.circuitLocation}")
                                put(CalendarContract.Events.CALENDAR_ID, calendarId)
                                put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().id)
                            }
                            contentResolver.insert(CalendarContract.Events.CONTENT_URI, values)
                            insertedCount++
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }

        withContext(Dispatchers.Main) {
            Toast.makeText(context, "Successfully synced $insertedCount F1 sessions to Calendar (PitWall)", Toast.LENGTH_SHORT).show()
        }
    }
}

fun removeRacesFromCalendar(context: Context) {
    CoroutineScope(Dispatchers.IO).launch {
        val contentResolver = context.contentResolver
        val selection = "${CalendarContract.Events.TITLE} LIKE ? OR ${CalendarContract.Events.DESCRIPTION} LIKE ?"
        val selectionArgs = arrayOf("%PitWall%", "%PitWall%")

        try {
            val deletedCount = contentResolver.delete(
                CalendarContract.Events.CONTENT_URI,
                selection,
                selectionArgs
            )
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Successfully removed $deletedCount PitWall events", Toast.LENGTH_SHORT).show()
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
        val parsedDate = LocalDate.parse(date.trim())
        val cleanTime = time.removeSuffix("Z").trim()
        val parsedTime = LocalTime.parse(cleanTime)
        val zonedDateTime = ZonedDateTime.of(parsedDate, parsedTime, ZoneId.of("UTC"))
        zonedDateTime.toInstant().toEpochMilli()
    } catch (_: Exception) {
        -1L
    }
}
