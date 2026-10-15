package com.jenil.f1comp.ui.telemetry.component

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.Whatshot
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jenil.f1comp.ui.theme.F1CompTheme
import com.jenil.f1comp.ui.theme.F1Red

/**
 * Compact version of telemetry header showing session type, track status, track temp, lap and remaining time only.
 */
@Composable
fun CompactTelemetryHeader(
    modifier: Modifier = Modifier,
    sessionType: String = "RACE",
    sessionPart: Int? = null,
    trackStatus: String = "All Clear",
    trackTemp: String = "37.2°C",
    currentLap: String = "34",
    remainingTime: String = "48:22",
    isExtrapolating: Boolean = false
) {
    val displaySessionType = remember(sessionType, sessionPart) {
        val type = sessionType.ifBlank { "N/A" }.uppercase()
        if (type.contains("QUALIFYING") && sessionPart != null && sessionPart in 1..3) {
            "Q$sessionPart"
        } else {
            type
        }
    }

    val (statusText, statusColor) = remember(trackStatus) {
        when {
            trackStatus.equals("All Clear", ignoreCase = true) ||
                    trackStatus == "1" ||
                    trackStatus.equals("AllClear", ignoreCase = true) ||
                    trackStatus.equals("Track Clear", ignoreCase = true) ->
                "TRACK CLEAR" to Color(0xFF00E676)

            trackStatus.equals("Yellow", ignoreCase = true) ||
                    trackStatus == "2" ||
                    trackStatus.equals("YellowFlag", ignoreCase = true) ->
                "YELLOW FLAG" to Color(0xFFFFD600)

            trackStatus.equals("SCDeployed", ignoreCase = true) ||
                    trackStatus == "4" ||
                    trackStatus.equals("SafetyCar", ignoreCase = true) ->
                "SAFETY CAR" to Color(0xFFFF9100)

            trackStatus.equals("VSCDeployed", ignoreCase = true) ||
                    trackStatus == "6" ->
                "VSC DEPLOYED" to Color(0xFFFF9100)

            trackStatus.equals("Red", ignoreCase = true) ||
                    trackStatus == "5" ->
                "RED FLAG" to F1Red

            else ->
                if (trackStatus.isBlank() || trackStatus.equals("N/A", ignoreCase = true)) "N/A" to Color(0xFF00E676)
                else trackStatus.uppercase() to Color(0xFF00E676)
        }
    }

    val formattedTrackTemp = remember(trackTemp) {
        val trimmed = trackTemp.trim()
        when {
            trimmed.isBlank() -> "--°C"
            trimmed.endsWith("°C", ignoreCase = true) -> trimmed
            trimmed.endsWith("°") -> "${trimmed}C"
            else -> "$trimmed°C"
        }
    }

    val isPracticeOrQuali = remember(sessionType) {
        sessionType.contains("Practice", ignoreCase = true) || sessionType.contains("Qualifying", ignoreCase = true)
    }

    var liveRemainingTime by remember(remainingTime) { androidx.compose.runtime.mutableStateOf(remainingTime) }

    androidx.compose.runtime.LaunchedEffect(isExtrapolating, liveRemainingTime) {
        if (isExtrapolating && liveRemainingTime.isNotBlank() && liveRemainingTime != "--:--") {
            kotlinx.coroutines.delay(1000)

            // Format: hh:mm:ss
            val parts = liveRemainingTime.split(":")
            if (parts.size == 3) {
                var hours = parts[0].toIntOrNull() ?: 0
                var minutes = parts[1].toIntOrNull() ?: 0
                var seconds = parts[2].toIntOrNull() ?: 0

                var totalSeconds = hours * 3600 + minutes * 60 + seconds - 1
                if (totalSeconds < 0) totalSeconds = 0

                hours = totalSeconds / 3600
                minutes = (totalSeconds % 3600) / 60
                seconds = totalSeconds % 60

                liveRemainingTime = String.format(java.util.Locale.US, "%02d:%02d:%02d", hours, minutes, seconds)
            }
        }
    }

    val formattedRemainingTime = remember(liveRemainingTime) {
        val trimmed = liveRemainingTime.trim()
        trimmed.ifBlank { "--:--" }
    }

    val displayCurrentLap = remember(currentLap) {
        currentLap.ifBlank { "--" }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "compact_status_pulse")

    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 2.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1500),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulse_scale"
    )
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1500),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulse_alpha"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
        ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            // Top Row: Pulsing Live Indicator + Session Type & Track Status Pill
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .graphicsLayer {
                                    scaleX = scale
                                    scaleY = scale
                                    alpha = pulseAlpha
                                }
                                .clip(CircleShape)
                                .background(F1Red)
                        )
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(F1Red)
                        )
                    }

                    Text(
                        text = displaySessionType,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = F1Red,
                            fontSize = 14.sp,
                            letterSpacing = 0.8.sp
                        )
                    )
                }

                // Track Status Pill
                Surface(
                    color = statusColor.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(50),
                    border = BorderStroke(1.dp, statusColor.copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(statusColor)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = statusText,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                color = statusColor,
                                fontSize = 10.sp,
                                letterSpacing = 0.8.sp
                            )
                        )
                    }
                }
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 8.dp),
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.25f)
            )

            // Metrics Row: Track Temp, Lap & Remaining Time
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Track Temp
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.Whatshot,
                        contentDescription = "Track Temp",
                        tint = F1Red,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = formattedTrackTemp,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = F1Red,
                            fontSize = 13.sp
                        )
                    )
                }

                // Lap Counter
                if (!isPracticeOrQuali) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "LAP: ",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Normal,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 12.sp
                            )
                        )
                        Text(
                            text = displayCurrentLap,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 14.sp
                            )
                        )
                    }
                }

                // Remaining Time
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.Schedule,
                        contentDescription = "Clock",
                        tint = F1Red,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = formattedRemainingTime,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 14.sp
                        )
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CompactTelemetryHeaderPreview() {
    F1CompTheme {
        Column(modifier = Modifier.padding(8.dp)) {
            CompactTelemetryHeader(
                sessionType = "Race",
                trackStatus = "All Clear",
                trackTemp = "37.2°C",
                currentLap = "34",
                remainingTime = "48:22"
            )
        }
    }
}
