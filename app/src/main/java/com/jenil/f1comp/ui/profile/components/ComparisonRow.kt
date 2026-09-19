package com.jenil.f1comp.ui.profile.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun ComparisonRow(
    label: String,
    driverWins: Int,
    teammateWins: Int,
    accentColor: Color = MaterialTheme.colorScheme.primary
) {
    val total = (driverWins + teammateWins).coerceAtLeast(1)
    val driverRatio = driverWins.toFloat() / total

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "$driverWins",
                fontFamily = FontFamily.Monospace,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = if (driverWins > teammateWins) accentColor else MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = label,
                fontFamily = FontFamily.Monospace,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "$teammateWins",
                fontFamily = FontFamily.Monospace,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = if (teammateWins > driverWins) accentColor else MaterialTheme.colorScheme.onSurface
            )
        }

        // Head-to-Head Ratio Progress Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(if (driverRatio > 0f) driverRatio else 0.01f)
                    .background(if (driverWins >= teammateWins) accentColor else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f))
            )
            Spacer(modifier = Modifier.width(2.dp))
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(if ((1f - driverRatio) > 0f) (1f - driverRatio) else 0.01f)
                    .background(if (teammateWins > driverWins) accentColor else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
            )
        }
    }
}


@Composable
fun GapToP1Card(
    currentPoints: Int,
    leaderPoints: Int,
    modifier: Modifier = Modifier
) {
    val pointsGap = (leaderPoints - currentPoints).coerceAtLeast(0)
    val progress = if (leaderPoints > 0) {
        (currentPoints.toFloat() / leaderPoints.toFloat()).coerceIn(0f, 1f)
    } else 1f

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (pointsGap == 0) "Championship Leader" else "-$pointsGap PTS to Leader",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (pointsGap == 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "$currentPoints / $leaderPoints PTS",
                    style = MaterialTheme.typography.labelMedium,
                    fontFamily = FontFamily.Monospace,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
                strokeCap = StrokeCap.Round
            )
        }
    }
}