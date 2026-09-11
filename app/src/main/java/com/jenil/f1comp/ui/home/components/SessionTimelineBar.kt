package com.jenil.f1comp.ui.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jenil.f1comp.data.local.entity.ScheduleEntity
import com.jenil.f1comp.data.model.RaceSession
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

data class TimelineSession(
    val id: String,
    val label: String,
    val dayStr: String,
    val timeStr: String,
    val isLive: Boolean,
    val isCurrentTarget: Boolean,
    val isCompleted: Boolean,
    val isMainRace: Boolean
)

@Composable
fun SessionTimelineBar(
    schedule: ScheduleEntity?,
    currentSessionName: String?,
    ongoingSession: String?,
    modifier: Modifier = Modifier
) {
    val sessions = remember(schedule, currentSessionName, ongoingSession) {
        buildTimelineSessions(schedule, currentSessionName, ongoingSession)
    }

    if (sessions.isEmpty()) return

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        sessions.forEachIndexed { index, session ->
            if (index > 0) {
                Text(
                    text = "•",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.35f),
                    modifier = Modifier.padding(horizontal = 2.dp)
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 2.dp)
            ) {
                if (session.isLive || session.isCurrentTarget) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF00E676))
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                }

                Column(verticalArrangement = Arrangement.Center) {
                    val textColor = when {
                        session.isMainRace -> MaterialTheme.colorScheme.primary
                        session.isLive || session.isCurrentTarget -> MaterialTheme.colorScheme.onSurface
                        session.isCompleted -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.38f)
                        else -> MaterialTheme.colorScheme.onSurfaceVariant
                    }

                    val displayTitle = when {
                        session.isMainRace -> if (session.dayStr.isNotBlank()) "RACE: ${session.dayStr}" else "RACE"
                        session.id == "QUAL" -> if (session.dayStr.isNotBlank()) "QUAL: ${session.dayStr}" else "QUAL"
                        session.isCurrentTarget || session.isLive -> if (session.dayStr.isNotBlank()) "${session.label}: ${session.dayStr}" else session.label
                        else -> session.label
                    }

                    Text(
                        text = displayTitle,
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                        fontFamily = FontFamily.Monospace,
                        fontWeight = if (session.isMainRace || session.isCurrentTarget || session.isLive) FontWeight.Bold else FontWeight.Medium,
                        color = textColor
                    )

                    if (session.timeStr.isNotBlank() && (session.isCurrentTarget || session.isLive)) {
                        Text(
                            text = session.timeStr,
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = textColor
                        )
                    }
                }
            }
        }
    }
}

private fun buildTimelineSessions(
    schedule: ScheduleEntity?,
    currentSessionName: String?,
    ongoingSession: String?
): List<TimelineSession> {
    if (schedule == null) return emptyList()

    val rawCurrent = (ongoingSession?.takeIf { it.isNotBlank() && it != "N/A" } ?: currentSessionName)
        ?.lowercase() ?: ""

    val isLive = ongoingSession?.takeIf { it.isNotBlank() && it != "N/A" } != null

    val rawSessions = mutableListOf<Pair<String, RaceSession?>>()

    if (schedule.sprint != null || schedule.sprintQualifying != null) {
        rawSessions.add("FP1" to schedule.firstPractice)
        schedule.sprintQualifying?.let { rawSessions.add("SQ" to it) }
        schedule.sprint?.let { rawSessions.add("SPR" to it) }
        schedule.qualifying?.let { rawSessions.add("QUAL" to it) }
    } else {
        rawSessions.add("FP1" to schedule.firstPractice)
        schedule.secondPractice?.let { rawSessions.add("FP2" to it) }
        schedule.thirdPractice?.let { rawSessions.add("FP3" to it) }
        schedule.qualifying?.let { rawSessions.add("QUAL" to it) }
    }

    val raceSession = if (schedule.grandPrix.isNotBlank()) {
        RaceSession(date = schedule.grandPrix, time = schedule.time)
    } else null
    rawSessions.add("RACE" to raceSession)

    var activeIndex = -1
    rawSessions.forEachIndexed { index, (code, _) ->
        val nameLower = when (code) {
            "FP1" -> "first practice"
            "FP2" -> "second practice"
            "FP3" -> "third practice"
            "SQ" -> "sprint qualifying"
            "SPR" -> "sprint"
            "QUAL" -> "qualifying"
            "RACE" -> "grand prix"
            else -> ""
        }
        if (rawCurrent.contains(code.lowercase()) || (nameLower.isNotBlank() && rawCurrent.contains(nameLower))) {
            activeIndex = index
        }
    }

    val nowMillis = System.currentTimeMillis()
    if (activeIndex == -1) {
        activeIndex = rawSessions.indexOfFirst { (_, session) ->
            if (session == null || session.date.isBlank()) false
            else {
                val startMillis = parseToMillis(session.date, session.time)
                startMillis > nowMillis
            }
        }.takeIf { it >= 0 } ?: (rawSessions.size - 1)
    }

    return rawSessions.mapIndexed { index, (code, session) ->
        val (dayStr, timeStr) = parseSessionDayAndTime(session?.date, session?.time)
        val isCurrent = index == activeIndex
        val isPast = index < activeIndex

        TimelineSession(
            id = code,
            label = code,
            dayStr = dayStr,
            timeStr = timeStr,
            isLive = isCurrent && isLive,
            isCurrentTarget = isCurrent,
            isCompleted = isPast,
            isMainRace = code == "RACE"
        )
    }
}

private fun parseSessionDayAndTime(dateStr: String?, timeStr: String?): Pair<String, String> {
    if (dateStr.isNullOrBlank()) return Pair("", "")
    return try {
        val parsedDate = LocalDate.parse(dateStr.trim())
        val dayStr = parsedDate.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.ENGLISH)

        val formattedTime = if (!timeStr.isNullOrBlank()) {
            val cleanTime = timeStr.removeSuffix("Z").trim()
            val parsedTime = LocalTime.parse(cleanTime)
            parsedTime.format(DateTimeFormatter.ofPattern("HH:mm"))
        } else ""

        Pair(dayStr, formattedTime)
    } catch (_: Exception) {
        Pair("", "")
    }
}

private fun parseToMillis(dateStr: String, timeStr: String?): Long {
    return try {
        val cleanTime = timeStr?.removeSuffix("Z")?.trim() ?: "00:00:00"
        val parsedDate = LocalDate.parse(dateStr.trim())
        val parsedTime = LocalTime.parse(cleanTime)
        ZonedDateTime.of(parsedDate, parsedTime, ZoneId.of("UTC")).toInstant().toEpochMilli()
    } catch (_: Exception) {
        0L
    }
}
