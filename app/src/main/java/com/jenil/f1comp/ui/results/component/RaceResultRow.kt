package com.jenil.f1comp.ui.results.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.rounded.ArrowDownward
import androidx.compose.material.icons.rounded.ArrowUpward
import androidx.compose.material.icons.rounded.Speed
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jenil.f1comp.data.local.entity.RaceResultEntity
import com.jenil.f1comp.data.local.entity.SprintResultEntity
import com.jenil.f1comp.ui.home.components.DriverProfileCircle
import com.jenil.f1comp.ui.theme.DeltaGain
import com.jenil.f1comp.ui.theme.DeltaLoss
import com.jenil.f1comp.ui.theme.FatestLap
import com.jenil.f1comp.ui.theme.PodiumBronze
import com.jenil.f1comp.ui.theme.PodiumGold
import com.jenil.f1comp.ui.theme.PodiumSilver
import com.jenil.f1comp.util.TeamUtils
import kotlin.math.abs

@Composable
fun RaceResultRow(
    result: RaceResultEntity,
    onDriverClick: () -> Unit,
    onConstructorClick: () -> Unit
) {
    RaceResultRowContent(
        position = result.position,
        positionText = result.positionText,
        driver = result.driver,
        driverImage = result.driverImage,
        constructor = result.constructor,
        points = result.points,
        grid = result.grid,
        time = result.time,
        status = result.status,
        fastestLap = result.fastestLap,
        onDriverClick = onDriverClick,
        onConstructorClick = onConstructorClick
    )
}

@Composable
fun RaceResultRow(
    result: SprintResultEntity,
    onDriverClick: () -> Unit,
    onConstructorClick: () -> Unit
) {
    RaceResultRowContent(
        position = result.position,
        positionText = result.positionText,
        driver = result.driver,
        driverImage = result.driverImage,
        constructor = result.constructor,
        points = result.points,
        grid = result.grid,
        time = result.time,
        status = result.status,
        fastestLap = result.fastestLap,
        onDriverClick = onDriverClick,
        onConstructorClick = onConstructorClick
    )
}

@Composable
private fun RaceResultRowContent(
    position: String,
    positionText: String?,
    driver: String,
    driverImage: String?,
    constructor: String,
    points: String,
    grid: String?,
    time: String,
    status: String,
    fastestLap: String?,
    onDriverClick: () -> Unit,
    onConstructorClick: () -> Unit
) {
    val posInt = position.toIntOrNull()
    val teamColor = TeamUtils.getTeamColor(constructor)
    val gridDelta = TeamUtils.calculateGridDelta(grid, position)

    val badgeBgColor = when (posInt) {
        1 -> PodiumGold
        2 -> PodiumSilver
        3 -> PodiumBronze
        else -> MaterialTheme.colorScheme.surfaceVariant
    }

    val badgeTextColor = when (posInt) {
        1, 2, 3 -> Color.Black
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    val podiumBorder = when (posInt) {
        1 -> BorderStroke(1.5.dp, PodiumGold.copy(alpha = 0.8f))
        2 -> BorderStroke(1.dp, PodiumSilver.copy(alpha = 0.6f))
        3 -> BorderStroke(1.dp, PodiumBronze.copy(alpha = 0.6f))
        else -> null
    }

    val hasFastestLap = !fastestLap.isNullOrBlank() && fastestLap != "0" && fastestLap != "NO"

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onDriverClick() },
        shape = RoundedCornerShape(14.dp),
        border = podiumBorder ?: BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.25f)),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (posInt != null && posInt in 1..3) 3.dp else 0.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .drawBehind {
                    drawRect(
                        color = teamColor,
                        topLeft = Offset.Zero,
                        size = Size(width = 5.dp.toPx(), height = size.height)
                    )
                }
                .padding(start = 5.dp) // shift content so left team stripe is visible
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Position Badge
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(badgeBgColor),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = positionText ?: position,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodyMedium,
                    color = badgeTextColor
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            // Driver Avatar with Team Color Border
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(teamColor.copy(alpha = 0.15f))
                    .padding(2.dp)
            ) {
                DriverProfileCircle(
                    imageUrl = driverImage,
                    driverName = driver,
                    size = 38.dp,
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Driver & Constructor Info
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = driver,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )

                    // Grid Delta Badge (+2 gain, -3 loss, = no change)
                    if (gridDelta != null) {
                        Spacer(modifier = Modifier.width(6.dp))
                        GridDeltaBadge(delta = gridDelta)
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 2.dp)
                ) {
                    Text(
                        text = constructor,
                        fontFamily = FontFamily.Monospace,
                        style = MaterialTheme.typography.bodySmall,
                        color = teamColor,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.clickable { onConstructorClick() }
                    )
                }

                if (hasFastestLap) {
                    Spacer(modifier = Modifier.height(4.dp))
                    FastestLapBadge(lapTime = fastestLap)
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Points & Time / Status Column
            Column(horizontalAlignment = Alignment.End) {
                val pointsVal = points.toDoubleOrNull() ?: 0.0
                val pointsDisplay = if (pointsVal > 0) {
                    if (pointsVal % 1.0 == 0.0) "${pointsVal.toInt()} pts" else "$points pts"
                } else "0 pts"

                Text(
                    text = pointsDisplay,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (pointsVal > 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                )

                val secondaryLabel = if (time.isNotBlank()) time else status
                if (secondaryLabel.isNotBlank()) {
                    val isDnf = status.contains("DNF", ignoreCase = true) ||
                            status.contains("Retired", ignoreCase = true) ||
                            status.contains("Accident", ignoreCase = true) ||
                            status.contains("Collision", ignoreCase = true)

                    Text(
                        text = secondaryLabel,
                        fontFamily = FontFamily.Monospace,
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                        color = if (isDnf) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun GridDeltaBadge(
    delta: Int,
    modifier: Modifier = Modifier
) {
    val isGain = delta > 0
    val isLoss = delta < 0
    val badgeColor = when {
        isGain -> DeltaGain
        isLoss -> DeltaLoss
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
            .background(badgeColor.copy(alpha = 0.15f))
            .padding(horizontal = 5.dp, vertical = 2.dp)
    ) {
        when {
            isGain -> {
                Icon(
                    imageVector = Icons.Rounded.ArrowUpward,
                    contentDescription = "Gained",
                    tint = badgeColor,
                    modifier = Modifier.size(10.dp)
                )
                Spacer(modifier = Modifier.width(2.dp))
                Text(
                    text = "+$delta",
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                    color = badgeColor,
                    maxLines = 1,
                    softWrap = false
                )
            }
            isLoss -> {
                Icon(
                    imageVector = Icons.Rounded.ArrowDownward,
                    contentDescription = "Lost",
                    tint = badgeColor,
                    modifier = Modifier.size(10.dp)
                )
                Spacer(modifier = Modifier.width(2.dp))
                Text(
                    text = "${abs(delta)}",
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                    color = badgeColor,
                    maxLines = 1,
                    softWrap = false
                )
            }
            else -> {
                Text(
                    text = "=",
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                    color = badgeColor,
                    maxLines = 1,
                    softWrap = false,
                    modifier = Modifier.padding(horizontal = 2.dp)
                )
            }
        }
    }
}

@Composable
private fun FastestLapBadge(lapTime: String?) {
    val label = if (!lapTime.isNullOrBlank() && lapTime.length <= 8) "FL $lapTime" else "FL"

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(FatestLap.copy(alpha = 0.18f))
            .padding(horizontal = 4.dp, vertical = 1.dp)
    ) {
        Icon(
            imageVector = Icons.Rounded.Speed,
            contentDescription = "Fastest Lap",
            tint = FatestLap,
            modifier = Modifier.size(10.dp)
        )
        Spacer(modifier = Modifier.width(2.dp))
        Text(
            text = label,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
            color = FatestLap
        )
    }
}
