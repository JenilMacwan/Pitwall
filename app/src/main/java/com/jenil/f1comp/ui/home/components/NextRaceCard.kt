package com.jenil.f1comp.ui.home.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.jenil.f1comp.R
import com.jenil.f1comp.data.local.entity.ScheduleEntity
import com.jenil.f1comp.data.model.RaceCountdown
import com.jenil.f1comp.data.model.RaceWeather
import com.jenil.f1comp.util.FlagImage
import com.jenil.f1comp.util.ProfileUtils
import kotlinx.coroutines.delay
import java.util.Locale
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun NextRaceCard(
    modifier: Modifier = Modifier,
    round: String,
    raceName: String,
    sessionName: String,
    circuit: String,
    country: String,
    countdown: RaceCountdown,
    ongoingSession: String?,
    sprint: Boolean,
    weather: RaceWeather?,
    flagEmoji: String,
    schedule: ScheduleEntity? = null,
    onDetailClick: () -> Unit
) {
    var timeRemaining by remember(countdown) {
        val totalSeconds: Long = countdown.let {
            (it.days * 86400L) +
                    (countdown.hours * 3600L) +
                    (countdown.minutes * 60L) +
                    countdown.seconds
        }
        mutableLongStateOf(totalSeconds)
    }
    LaunchedEffect(countdown) {
        while (timeRemaining > 0) {
            delay(1000L.milliseconds)
            timeRemaining-- // Subtract 1 second
        }
    }
    val d = (timeRemaining / 86400).toInt()
    val h = ((timeRemaining % 86400) / 3600).toInt()
    val m = ((timeRemaining % 3600) / 60).toInt()
    val s = (timeRemaining % 60).toInt()

    val daysStr = String.format(Locale.ROOT, "%02d", d)
    val hoursStr = String.format(Locale.ROOT, "%02d", h)
    val minutesStr = String.format(Locale.ROOT, "%02d", m)
    val secondsStr = String.format(Locale.ROOT, "%02d", s)

    val totalDaysFraction = d.toFloat() + (h / 24f) + (m / 1440f) + (s / 86400f)
    val daysProgress = when {
        timeRemaining <= 0 -> 0f
        d == 0 -> 1.0f // On session day (0 Days left), ring is 100% filled with red ring!
        else -> (totalDaysFraction / 14f).coerceIn(0.02f, 1.0f)
    }

    val weatherGradient = when {
        weather == null -> Brush.linearGradient(
            colors = listOf(Color.Transparent, Color.Transparent)
        )

        weather.condition.contains("Rainy", ignoreCase = true) ||
                weather.condition.contains("Wet", ignoreCase = true) ||
                weather.condition.contains("patchy rain nearby") -> {
            Brush.linearGradient(
                colors = listOf(
                    Color(0xFF1E3A8A).copy(alpha = 0.25f), // Deep Blue tint
                    Color(0xFF38BDF8).copy(alpha = 0.15f)  // Light Sky tint
                ),
                start = Offset(0f, 0f),
                end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
            )
        }

        weather.condition.contains("Sunny", ignoreCase = true) ||
                weather.condition.contains("Clear", ignoreCase = true) ||
                weather.condition.contains("Hot", ignoreCase = true) -> {
            Brush.linearGradient(
                colors = listOf(
                    Color(0xFFFFA000).copy(alpha = 0.20f), // Warm Amber
                    Color(0xFFFFD54F).copy(alpha = 0.10f)  // Pale Yellow
                ),
                start = Offset(0f, 0f),
                end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
            )
        }


        weather.condition.contains("Cloud", ignoreCase = true) ||
                weather.condition.contains("Overcast", ignoreCase = true) ||
                weather.condition.contains("Partly Cloudy", ignoreCase = true)-> {
            Brush.linearGradient(
                colors = listOf(
                    Color(0xFF607D8B).copy(alpha = 0.25f), // Blue Grey
                    Color(0xFFCFD8DC).copy(alpha = 0.15f)  // Light Grey
                ),
                start = Offset(0f, 0f),
                end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
            )
        }

        else -> {
            Brush.linearGradient(
                colors = listOf(
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.05f)
                ),
                start = Offset(0f, 0f),
                end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
            )
        }
    }

    val cleanSessionName = sessionName
        .replace("Session Name : ", "")
        .substringBefore(" Time Zone")
        .trim()

    val cleanOngoing = ongoingSession
        ?.replace("Session Name : ", "")
        ?.replace("Session Name: ", "")
        ?.substringBefore(" Time Zone")
        ?.trim() ?: "N/A"

    val isLive = !ongoingSession.isNullOrEmpty() && ongoingSession != "N/A"


    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
            .animateContentSize(),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${stringResource(R.string.home_next_race)} • ROUND $round",
                    fontFamily = FontFamily.Monospace,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (isLive) {
                    LiveSessionPill(text = cleanOngoing)
                } else {
                    SprintPill(text = cleanSessionName)
                }
            }
            Spacer(Modifier.height(16.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = raceName,
                    fontFamily = FontFamily.Monospace,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.width(8.dp))
                FlagImage(
                    flagUrl = ProfileUtils.getFlagUrl(emoji = flagEmoji),
                    width = 24.dp,
                    height = 18.dp
                )
            }

            Spacer(modifier = Modifier.height(5.dp))
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "$circuit • $country",
                    fontFamily = FontFamily.Monospace,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.height(12.dp))

            if(sprint) {
                LightsOutCard(
                    sessionTag = "sprint weekend"
                )
            } else {
                LightsOutCard(
                    sessionTag = "standard weekend"
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                DayCard(
                    value = daysStr,
                    unit = stringResource(R.string.home_days),
                    progress = daysProgress,
                    modifier = Modifier.weight(0.45f)
                )
                Column(
                    modifier = Modifier.weight(0.55f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        SmallCountdown(value = hoursStr, unit = stringResource(R.string.home_hours), modifier = Modifier.weight(1f))
                        SmallCountdown(value = minutesStr, unit = stringResource(R.string.home_mins), modifier = Modifier.weight(1f))
                        SmallCountdown(value = secondsStr, unit = stringResource(R.string.home_secs), modifier = Modifier.weight(1f))
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(brush = weatherGradient, shape = RoundedCornerShape(10.dp)),
                        shape = RoundedCornerShape(10.dp),
                        color = Color.Transparent,
                    ) {
                        if (weather != null) WeatherBadge(
                            weather = weather
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 16.dp),
                color = MaterialTheme.colorScheme.outlineVariant
            )
            Spacer(modifier = Modifier.height(8.dp))

            if (schedule != null) {
                SessionTimelineBar(
                    schedule = schedule,
                    currentSessionName = sessionName,
                    ongoingSession = ongoingSession
                )
                Spacer(modifier = Modifier.height(6.dp))
            }

            Text(
                text = "Session Schedule Details ->",
                fontFamily = FontFamily.Monospace,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable(
                    onClick = {
                        onDetailClick()
                    }
                )
            )
        }
    }
}

