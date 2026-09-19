package com.jenil.f1comp.ui.profile.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ShowChart
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jenil.f1comp.data.model.DriverPointsProgression
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.compose.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberAxisLabelComponent
import com.patrykandpatrick.vico.compose.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.compose.cartesian.data.CartesianValueFormatter
import com.patrykandpatrick.vico.compose.cartesian.data.lineModel
import com.patrykandpatrick.vico.compose.cartesian.layer.LineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLine
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.common.Fill
import com.patrykandpatrick.vico.compose.common.component.rememberLineComponent
import com.patrykandpatrick.vico.compose.common.component.rememberShapeComponent
import java.util.Locale

@Composable
fun PointsProgressionChart(
    pointsProgression: List<DriverPointsProgression>,
    modifier: Modifier = Modifier
) {
    if (pointsProgression.isEmpty()) {
        EmptyProgressionState(modifier)
        return
    }

    val modelProducer = remember { CartesianChartModelProducer() }

    LaunchedEffect(pointsProgression) {
        modelProducer.runTransaction {
            lineModel {
                series(pointsProgression.map { it.cumulativePoints })
            }
        }
    }

    val primaryColor = MaterialTheme.colorScheme.primary
    val textColor = MaterialTheme.colorScheme.onSurface
    val axisLabelColor = MaterialTheme.colorScheme.onSurface
    val axisGuidelineColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.20f)
    val axisLineColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.40f)

    val axisLabelComponent = rememberAxisLabelComponent(
        style = TextStyle(
            color = axisLabelColor,
            fontFamily = FontFamily.Monospace,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
    )

    val axisGuidelineComponent = rememberLineComponent(
        fill = Fill(axisGuidelineColor),
        thickness = 1.dp
    )

    val axisLineComponent = rememberLineComponent(
        fill = Fill(axisLineColor),
        thickness = 1.dp
    )

    val customLineStyle = LineCartesianLayer.rememberLine(
        fill = LineCartesianLayer.LineFill.single(Fill(primaryColor)),
        areaFill = LineCartesianLayer.AreaFill.single(
            Fill(
                Brush.verticalGradient(
                    listOf(primaryColor.copy(alpha = 0.32f), Color.Transparent)
                )
            )
        ),
        pointProvider = LineCartesianLayer.PointProvider.single(
            LineCartesianLayer.Point(
                component = rememberShapeComponent(
                    fill = Fill(Color.White),
                    strokeFill = Fill(primaryColor),
                    strokeThickness = 2.dp
                )
            )
        ),
        interpolator = LineCartesianLayer.Interpolator.cubic()
    )


    val totalPoints = pointsProgression.last().cumulativePoints
    val lastRoundGain = pointsProgression.last().points
    val bestRound = pointsProgression.maxByOrNull { it.points }

    Column(modifier = modifier.fillMaxWidth()) {

        // ── Header: running total + last-round delta ──
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            Column {
                Text(
                    text = "Points Progression",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = textColor
                )
                Text(
                    text = "${formatPoints(totalPoints)} pts",
                    style = MaterialTheme.typography.titleLarge,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = textColor
                )
            }

            if (lastRoundGain > 0.0) {
                Text(
                    text = "+${formatPoints(lastRoundGain)} last round",
                    style = MaterialTheme.typography.labelSmall,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = primaryColor
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // ── Chart ──
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .padding(top = 12.dp, bottom = 8.dp)
        ) {
            CartesianChartHost(
                chart = rememberCartesianChart(
                    rememberLineCartesianLayer(
                        LineCartesianLayer.LineProvider.series(customLineStyle)
                    ),
                    startAxis = VerticalAxis.rememberStart(
                        label = axisLabelComponent,
                        line = axisLineComponent,
                        tick = axisLineComponent,
                        guideline = axisGuidelineComponent,
                        valueFormatter = remember {
                            CartesianValueFormatter { _, value: Double, _ -> formatPoints(value) }
                        }
                    ),
                    bottomAxis = HorizontalAxis.rememberBottom(
                        label = axisLabelComponent,
                        line = axisLineComponent,
                        tick = axisLineComponent,
                        guideline = axisGuidelineComponent,
                        valueFormatter = remember {
                            CartesianValueFormatter { _, value: Double, _ -> "R${value.toInt() + 1}" }
                        }
                    )
                ),
                modelProducer = modelProducer,
                modifier = Modifier.fillMaxSize()
            )
        }

        if (bestRound != null && bestRound.points > 0.0) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.ShowChart,
                    contentDescription = null,
                    tint = primaryColor,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Best round: R${bestRound.round} · ${bestRound.raceName} · +${formatPoints(bestRound.points)} pts",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                    fontWeight = FontWeight.Medium,
                    color = textColor,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
private fun EmptyProgressionState(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(180.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.ShowChart,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "No season data available",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

private fun formatPoints(value: Double): String {
    return if (value == value.toInt().toDouble()) {
        value.toInt().toString()
    } else {
        String.format(Locale.getDefault(), "%.1f", value)
    }
}
