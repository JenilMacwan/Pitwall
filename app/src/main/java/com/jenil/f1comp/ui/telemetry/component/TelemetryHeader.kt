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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Air
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.Thermostat
import androidx.compose.material.icons.outlined.WaterDrop
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jenil.f1comp.ui.theme.F1Red

@Composable
fun TelemetryHeader(
    modifier: Modifier = Modifier,
    sessionName: String = "UNKNOWN",
    sessionType: String = "N/A",
    sessionPart: Int? = null,
    trackStatus: String = "N/A",
    currentLap: String = "--",
    remainingTime: String = "--:--",
    isExtrapolating: Boolean = false,
    airTemp: String = "--°C",
    trackTemp: String = "--°C",
    humidity: String = "--%",
    wind: String = "--"
) {
    val displaySessionName = remember(sessionName) {
        sessionName.ifBlank { "UNKNOWN" }.uppercase()
    }
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

    val formattedAirTemp = remember(airTemp) {
        val trimmed = airTemp.trim()
        when {
            trimmed.isBlank() -> "--°C"
            trimmed.endsWith("°C", ignoreCase = true) -> trimmed
            trimmed.endsWith("°") -> "${trimmed}C"
            else -> "$trimmed°C"
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

    val formattedHumidity = remember(humidity) {
        val trimmed = humidity.trim()
        when {
            trimmed.isBlank() -> "--%"
            trimmed.endsWith("%") -> trimmed
            else -> "$trimmed%"
        }
    }

    val formattedWind = remember(wind) {
        val trimmed = wind.trim()
        trimmed.ifBlank { "--" }
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

    val infiniteTransition = rememberInfiniteTransition(label = "status_pulse")

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
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
        ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header Top Row: Pulse Dot + Session Name/Type Column & Status Pill
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Session Info in Column
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(12.dp)
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
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(F1Red)
                        )
                    }

                    Column {
                        Text(
                            text = displaySessionName,
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                color = F1Red,
                                letterSpacing = 0.8.sp
                            )
                        )
                        Text(
                            text = displaySessionType,
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                color = F1Red,
                                letterSpacing = 0.8.sp
                            )
                        )
                    }
                }

                // Track Status Pill
                Surface(
                    color = statusColor.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(50),
                    border = BorderStroke(1.dp, statusColor.copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
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
                                letterSpacing = 1.sp
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Timing Row: Lap information on left, Clock and Estimated Remaining on right
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = if (isPracticeOrQuali) Arrangement.End else Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Lap Counter
                if (!isPracticeOrQuali) {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "LAP: ",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Normal,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                            Text(
                                text = displayCurrentLap,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            )
                        }
                    }
                }

                // Remaining Time & Clock
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Schedule,
                        contentDescription = "Clock",
                        tint = F1Red,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = formattedRemainingTime,
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "EST",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Normal,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 9.sp,
                                lineHeight = 11.sp
                            )
                        )
                        Text(
                            text = "REMAINING",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Normal,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 9.sp,
                                lineHeight = 11.sp
                            )
                        )
                    }
                }
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 12.dp),
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
            )

            // Weather Metrics Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                WeatherMetricCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Outlined.Thermostat,
                    iconTint = MaterialTheme.colorScheme.onSurfaceVariant,
                    label = "AIR",
                    labelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    value = formattedAirTemp,
                    valueColor = MaterialTheme.colorScheme.onSurface
                )
                WeatherMetricCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Outlined.Whatshot,
                    iconTint = F1Red,
                    label = "TRACK",
                    labelColor = F1Red,
                    value = formattedTrackTemp,
                    valueColor = F1Red
                )
                WeatherMetricCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Outlined.WaterDrop,
                    iconTint = Color(0xFF00E5FF),
                    label = "HUM",
                    labelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    value = formattedHumidity,
                    valueColor = MaterialTheme.colorScheme.onSurface
                )
                WeatherMetricCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Outlined.Air,
                    iconTint = MaterialTheme.colorScheme.onSurfaceVariant,
                    label = "WIND",
                    labelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    value = formattedWind,
                    valueColor = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}
