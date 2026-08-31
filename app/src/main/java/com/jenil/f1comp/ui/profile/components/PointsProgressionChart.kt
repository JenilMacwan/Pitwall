package com.jenil.f1comp.ui.profile.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.jenil.f1comp.data.model.DriverPointsProgression

@Composable
fun PointsProgressionChart(
    pointsProgression: List<DriverPointsProgression>,
    modifier: Modifier = Modifier
) {
    if (pointsProgression.isEmpty()) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(180.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No data available",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        return
    }

    val primaryColor = MaterialTheme.colorScheme.primary

    Column(modifier = modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .padding(top = 16.dp, bottom = 8.dp)
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val width = size.width
                val height = size.height
                val spacing = width / (pointsProgression.size - 1).coerceAtLeast(1)
                val maxPoints = pointsProgression.maxOf { it.cumulativePoints }.toFloat().coerceAtLeast(1f)

                val points = pointsProgression.mapIndexed { index, data ->
                    Offset(
                        x = index * spacing,
                        y = height - (data.cumulativePoints.toFloat() / maxPoints) * height
                    )
                }

                // Draw background gradient path
                val fillPath = Path().apply {
                    moveTo(0f, height)
                    points.forEach { lineTo(it.x, it.y) }
                    lineTo(width, height)
                    close()
                }

                drawPath(
                    path = fillPath,
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            primaryColor.copy(alpha = 0.3f),
                            primaryColor.copy(alpha = 0f)
                        )
                    )
                )

                // Draw the line
                val strokePath = Path().apply {
                    points.forEachIndexed { index, offset ->
                        if (index == 0) moveTo(offset.x, offset.y)
                        else lineTo(offset.x, offset.y)
                    }
                }

                drawPath(
                    path = strokePath,
                    color = primaryColor,
                    style = Stroke(
                        width = 3.dp.toPx(),
                        cap = StrokeCap.Round
                    )
                )

                // Draw dots at each round
                points.forEach { offset ->
                    drawCircle(
                        color = Color.White,
                        radius = 4.dp.toPx(),
                        center = offset
                    )
                    drawCircle(
                        color = primaryColor,
                        radius = 4.dp.toPx(),
                        center = offset,
                        style = Stroke(width = 2.dp.toPx())
                    )
                }
            }
        }

        // Labels for all rounds
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            pointsProgression.forEach { data ->
                Text(
                    text = "R${data.round}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
