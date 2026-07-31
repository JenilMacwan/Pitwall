package com.jenil.f1comp.ui.home.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import java.util.Locale
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.jenil.f1comp.data.model.RaceCountdown
import com.jenil.f1comp.data.model.RaceWeather
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds


@Composable
fun NextRaceCard(
    modifier: Modifier = Modifier,
    raceName: String,
    sessionName: String,
    circuit: String,
    countdown: RaceCountdown,
    ongoingSession: String?,
    sprint: Boolean,
    weather: RaceWeather?,
    flagEmoji: String
) {
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
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "NEXT RACE",
                    fontFamily = FontFamily.Monospace,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (isLive) {
                    RacePill(text = "LIVE: $cleanOngoing")
                } else {
                    RacePill(text = cleanSessionName)
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
                Text(
                    text = flagEmoji,
                    style = MaterialTheme.typography.titleLarge
                )

            }

            Spacer(modifier = Modifier.height(5.dp))
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = circuit,
                    fontFamily = FontFamily.Monospace,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.width(10.dp))
                if (weather != null) WeatherBadge(weather = weather)
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Countdown(value = daysStr, unit = "Days", modifier = Modifier.weight(1f))
                Countdown(value = hoursStr, unit = "Hours", modifier = Modifier.weight(1f))
                Countdown(value = minutesStr, unit = "Min", modifier = Modifier.weight(1f))
                Countdown(value = secondsStr, unit = "Sec", modifier = Modifier.weight(1f))
            }


            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = if (sprint) "🏁 Sprint Weekend" else "Standard Race Weekend",
                fontFamily = FontFamily.Monospace,
                style = MaterialTheme.typography.labelSmall,
                color = if (sprint) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

