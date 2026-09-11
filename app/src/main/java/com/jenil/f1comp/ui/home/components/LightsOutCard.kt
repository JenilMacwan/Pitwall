package com.jenil.f1comp.ui.home.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
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
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun LightsOutCard(
    modifier: Modifier = Modifier,
    sessionTag: String
) {
    var litCount by remember { mutableIntStateOf(0) }

    val infiniteTransition = rememberInfiniteTransition(label = "status_pulse")

    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 2.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1500),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulse_scale"
    )
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1500),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulse_alpha"
    )

    LaunchedEffect(Unit) {
        while (true) {
            litCount = 0
            delay(1200L.milliseconds)

            // Turn on lights 1 by 1 every second
            for (i in 1..5) {
                litCount = i
                delay(1000L.milliseconds)
            }

            delay(1400L.milliseconds)

            // LIGHTS OUT!
            litCount = 0
            delay(1500L.milliseconds)
        }
    }

    // 1. Pill-Shaped Container
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = CircleShape,
        color = Color(0xFF121212),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f))
    ) {
        // 2. Single Horizontal Row Layout
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp), // Tighter inner padding
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            // Left Side: Label & Lights
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "LIGHTS OUT",
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 10.sp, // Scaled down to fit one line
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                // Group the lights slightly tighter
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    for (lightIndex in 1..5) {
                        GantryLight(
                            isLit = litCount >= lightIndex,
                            outerSize = 16.dp // Shrunk from 22.dp to 16.dp
                        )
                    }
                }
            }

            // Right Side: Session Indicator
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End,
                modifier = Modifier
                    .padding(start = 8.dp)
                    .weight(1f, fill = false) // Prevents long text from pushing lights off-screen
            ) {
                // Blinking/Active Green Status Dot
                Box(contentAlignment = Alignment.Center) {
                    Box(
                        modifier = Modifier
                            .padding(end = 6.dp)
                            .size(8.dp)
                            .graphicsLayer(
                                scaleX = scale,
                                scaleY = scale
                            )
                            .alpha(pulseAlpha)
                            .background(Color(0xFF00D26A).copy(alpha = 0.4f), shape = CircleShape)
                    )
                    Box(
                        modifier = Modifier
                            .padding(end = 6.dp)
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF00D26A))
                    )
                }
                Text(
                    text = sessionTag.uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 10.sp, // Scaled down
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.tertiary,
                    letterSpacing = 0.5.sp,
                    maxLines = 1,
                )
            }
        }
    }
}

@Composable
private fun GantryLight(
    isLit: Boolean,
    modifier: Modifier = Modifier,
    outerSize: androidx.compose.ui.unit.Dp = 22.dp // Made size dynamic
) {
    val unlitColor = Color(0xFF260A0C)
    val litColor = F1Red

    val animatedColor by animateColorAsState(
        targetValue = if (isLit) litColor else unlitColor,
        animationSpec = tween(durationMillis = 150),
        label = "light_bulb_color"
    )

    // Calculate inner bulb size proportionally (roughly 65% of the outer bezel)
    val innerSize = outerSize * 0.65f

    Box(
        modifier = modifier
            .size(outerSize)
            .clip(CircleShape)
            .background(Color(0xFF191919))
            .border(
                width = 1.dp,
                color = if (isLit) litColor.copy(alpha = 0.5f) else Color.White.copy(alpha = 0.1f),
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(innerSize)
                .clip(CircleShape)
                .background(animatedColor)
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun LightsOutCardPreview() {
    F1CompTheme {
        LightsOutCard(
            sessionTag = "sprint weekend"
        )
    }
}