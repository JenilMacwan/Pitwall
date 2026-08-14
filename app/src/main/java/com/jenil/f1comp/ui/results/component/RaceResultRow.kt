package com.jenil.f1comp.ui.results.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
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
import com.jenil.f1comp.data.local.entity.RaceResultEntity
import com.jenil.f1comp.ui.home.components.DriverProfileCircle

@Composable
fun RaceResultRow(
    result: RaceResultEntity,
    onDriverClick: () -> Unit,
    onConstructorClick: () -> Unit
) {
    val position = result.position.toIntOrNull()

    val badgeTextColor = when (position) {
        1 -> MaterialTheme.colorScheme.tertiary
        2 -> MaterialTheme.colorScheme.secondary
        3 -> MaterialTheme.colorScheme.secondaryContainer
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    val podiumBorder = when (position) {
        1 -> BorderStroke(1.5.dp, MaterialTheme.colorScheme.tertiary.copy(alpha = 0.8f))
        2 -> BorderStroke(1.dp, MaterialTheme.colorScheme.secondary.copy(alpha = 0.6f))
        3 -> BorderStroke(1.dp, MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.6f))
        else -> null
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onDriverClick() },
        shape = RoundedCornerShape(14.dp),
        border = podiumBorder,
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (position != null && position in 1..3) 2.dp else 0.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = result.positionText ?: result.position,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodyMedium,
                color = badgeTextColor
            )

            Spacer(modifier = Modifier.width(8.dp))

            DriverProfileCircle(
                imageUrl = result.driverImage,
                driverName = result.driver,
                size = 40.dp,
            )
            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = result.driver,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = result.constructor,
                    fontFamily = FontFamily.Monospace,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.clickable { onConstructorClick() }
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${result.points} pts",
                    fontFamily = FontFamily.Monospace,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                val secondaryLabel = if (result.time.isNotBlank()) result.time else result.status
                if (secondaryLabel.isNotBlank()) {
                    Text(
                        text = secondaryLabel,
                        fontFamily = FontFamily.Monospace,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}