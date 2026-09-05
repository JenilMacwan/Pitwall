package com.jenil.f1comp.ui.results.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jenil.f1comp.data.local.entity.QualifyingResultEntity
import com.jenil.f1comp.data.local.entity.SprintQualifyingResultEntity
import com.jenil.f1comp.ui.home.components.DriverProfileCircle

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

    val badgeTextColor = when (posInt) {
        1 -> MaterialTheme.colorScheme.tertiary
        2 -> MaterialTheme.colorScheme.secondary
        3 -> MaterialTheme.colorScheme.secondaryContainer
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    val podiumBorder = when (posInt) {
        1 -> BorderStroke(1.5.dp, MaterialTheme.colorScheme.tertiary.copy(alpha = 0.8f))
        2 -> BorderStroke(1.dp, MaterialTheme.colorScheme.secondary.copy(alpha = 0.6f))
        3 -> BorderStroke(1.dp, MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.6f))
        else -> null
    }

    val prefix = if (isSprintQuali) "SQ" else "Q"

    val (bestTime, bestSession) = when {
        !q3.isNullOfBlank() -> Pair(q3, "${prefix}3")
        !q2.isNullOfBlank() -> Pair(q2, "${prefix}2")
        !q1.isNullOfBlank() -> Pair(q1, "${prefix}1")
        else -> Pair("No Time", "")
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onDriverClick() },
        shape = RoundedCornerShape(14.dp),
        border = podiumBorder,
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (posInt != null && posInt in 1..3) 2.dp else 0.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = position,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodyMedium,
                    color = badgeTextColor
                )

                Spacer(modifier = Modifier.width(8.dp))

                DriverProfileCircle(
                    imageUrl = driverImage,
                    driverName = driver,
                    size = 40.dp,
                )
                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = driver,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = constructor,
                        fontFamily = FontFamily.Monospace,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.clickable { onConstructorClick() }
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = bestTime ?: "",
                        fontFamily = FontFamily.Monospace,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    if (bestSession.isNotBlank()) {
                        Text(
                            text = bestSession,
                            fontFamily = FontFamily.Monospace,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            val hasSubTimes = !q1.isNullOfBlank() || !q2.isNullOfBlank() || !q3.isNullOfBlank()
            if (hasSubTimes) {
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.End)
                ) {
                    if (!q1.isNullOfBlank()) {
                        Text(
                            text = "${prefix}1: $q1",
                            fontFamily = FontFamily.Monospace,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    if (!q2.isNullOfBlank()) {
                        Text(
                            text = "${prefix}2: $q2",
                            fontFamily = FontFamily.Monospace,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    if (!q3.isNullOfBlank()) {
                        Text(
                            text = "${prefix}3: $q3",
                            fontFamily = FontFamily.Monospace,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

private fun String?.isNullOfBlank(): Boolean = this.isNullOrBlank()
