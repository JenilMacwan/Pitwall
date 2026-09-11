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
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jenil.f1comp.data.local.entity.QualifyingResultEntity
import com.jenil.f1comp.data.local.entity.SprintQualifyingResultEntity
import com.jenil.f1comp.ui.home.components.DriverProfileCircle
import com.jenil.f1comp.ui.theme.PodiumBronze
import com.jenil.f1comp.ui.theme.PodiumGold
import com.jenil.f1comp.ui.theme.PodiumSilver
import com.jenil.f1comp.util.TeamUtils

@Composable
fun QualifyingResultRow(
    result: QualifyingResultEntity,
    onDriverClick: () -> Unit,
    onConstructorClick: () -> Unit
) {
    QualifyingResultRowContent(
        position = result.position,
        driver = result.driver,
        driverImage = result.driverImage,
        constructor = result.constructor,
        q1 = result.q1,
        q2 = result.q2,
        q3 = result.q3,
        isSprintQuali = false,
        onDriverClick = onDriverClick,
        onConstructorClick = onConstructorClick
    )
}

@Composable
fun QualifyingResultRow(
    result: SprintQualifyingResultEntity,
    onDriverClick: () -> Unit,
    onConstructorClick: () -> Unit
) {
    QualifyingResultRowContent(
        position = result.position,
        driver = result.driver,
        driverImage = result.driverImage,
        constructor = result.constructor,
        q1 = result.q1,
        q2 = result.q2,
        q3 = result.q3,
        isSprintQuali = true,
        onDriverClick = onDriverClick,
        onConstructorClick = onConstructorClick
    )
}

private data class QualiSegment(val label: String, val time: String?, val isBest: Boolean)

@Composable
private fun QualifyingResultRowContent(
    position: String,
    driver: String,
    driverImage: String?,
    constructor: String,
    q1: String?,
    q2: String?,
    q3: String?,
    isSprintQuali: Boolean,
    onDriverClick: () -> Unit,
    onConstructorClick: () -> Unit
) {
    val posInt = position.toIntOrNull()
    val prefix = if (isSprintQuali) "SQ" else "Q"
    val teamColor = TeamUtils.getTeamColor(constructor)

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

    val hasQ1 = !q1.isNullOrBlank()
    val hasQ2 = !q2.isNullOrBlank()
    val hasQ3 = !q3.isNullOrBlank()
    val hasAnyTime = hasQ1 || hasQ2 || hasQ3

    val bestTime: String?
    val bestSessionLabel: String
    when {
        hasQ3 -> {
            bestTime = q3
            bestSessionLabel = "${prefix}3"
        }
        hasQ2 -> {
            bestTime = q2
            bestSessionLabel = "${prefix}2"
        }
        hasQ1 -> {
            bestTime = q1
            bestSessionLabel = "${prefix}1"
        }
        else -> {
            bestTime = null
            bestSessionLabel = ""
        }
    }

    val eliminatedLabel: String? = when {
        !hasAnyTime -> null
        hasQ3 -> null
        hasQ2 -> "Out in ${prefix}2"
        else -> "Out in ${prefix}1"
    }

    val segments = listOf(
        QualiSegment("${prefix}1", q1, bestSessionLabel == "${prefix}1"),
        QualiSegment("${prefix}2", q2, bestSessionLabel == "${prefix}2"),
        QualiSegment("${prefix}3", q3, bestSessionLabel == "${prefix}3")
    )

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
        Column(
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
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Position Badge
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape)
                        .background(badgeBgColor),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = position,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodyMedium,
                        color = badgeTextColor
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                // Driver Profile Circle with Team Color
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

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = driver,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        if (posInt == 1 && hasAnyTime) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(
                                imageVector = Icons.Rounded.Star,
                                contentDescription = if (isSprintQuali) "Sprint pole" else "Pole position",
                                tint = PodiumGold,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = constructor,
                            fontFamily = FontFamily.Monospace,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.SemiBold,
                            color = teamColor,
                            modifier = Modifier.clickable { onConstructorClick() }
                        )
                        if (eliminatedLabel != null) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "• $eliminatedLabel",
                                fontFamily = FontFamily.Monospace,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.error.copy(alpha = 0.85f)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = bestTime ?: "No Time",
                        fontFamily = FontFamily.Monospace,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (bestTime != null) teamColor else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                    if (bestSessionLabel.isNotBlank()) {
                        Text(
                            text = "Best · $bestSessionLabel",
                            fontFamily = FontFamily.Monospace,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            if (hasAnyTime) {
                Spacer(modifier = Modifier.height(8.dp))
                SessionProgressionRow(segments = segments, teamColor = teamColor)
            }
        }
    }
}

@Composable
private fun SessionProgressionRow(segments: List<QualiSegment>, teamColor: Color) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        segments.forEach { segment ->
            SessionPill(segment = segment, teamColor = teamColor)
        }
    }
}

@Composable
private fun SessionPill(segment: QualiSegment, teamColor: Color) {
    val hasTime = !segment.time.isNullOrBlank()

    val containerColor = when {
        segment.isBest && hasTime -> teamColor.copy(alpha = 0.15f)
        hasTime -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)
    }
    val borderColor = if (segment.isBest && hasTime) {
        teamColor.copy(alpha = 0.6f)
    } else {
        MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
    }
    val labelColor = if (hasTime) MaterialTheme.colorScheme.onSurfaceVariant
    else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
    val timeColor = when {
        segment.isBest && hasTime -> teamColor
        hasTime -> MaterialTheme.colorScheme.onSurface
        else -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
    }

    Surface(
        shape = RoundedCornerShape(8.dp),
        color = containerColor,
        border = BorderStroke(1.dp, borderColor)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (segment.isBest && hasTime) {
                Icon(
                    imageVector = Icons.Rounded.Bolt,
                    contentDescription = null,
                    tint = teamColor,
                    modifier = Modifier.size(12.dp)
                )
                Spacer(modifier = Modifier.width(3.dp))
            }
            Text(
                text = segment.label,
                fontFamily = FontFamily.Monospace,
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                color = labelColor
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = segment.time?.takeIf { it.isNotBlank() } ?: "—",
                fontFamily = FontFamily.Monospace,
                fontWeight = if (segment.isBest && hasTime) FontWeight.Bold else FontWeight.Normal,
                style = MaterialTheme.typography.labelSmall,
                color = timeColor
            )
        }
    }
}
