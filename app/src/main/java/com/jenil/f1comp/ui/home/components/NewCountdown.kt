package com.jenil.f1comp.ui.home.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun SmallCountdown(
    value: String,
    unit: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
            )
            .padding(vertical = 12.dp, horizontal = 4.dp),
        contentAlignment = Alignment.Center
    ) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            AnimatedContent(
                targetState = value,
                transitionSpec = {
                    val enter = slideInVertically(
                        initialOffsetY = { it / 2 },
                        animationSpec = tween(durationMillis = 280, easing = FastOutSlowInEasing)
                    ) + fadeIn(animationSpec = tween(220)) + scaleIn(
                        initialScale = 0.92f, animationSpec = tween(280)
                    )

                    val exit = slideOutVertically(
                        targetOffsetY = { -it / 2 },
                        animationSpec = tween(durationMillis = 280, easing = FastOutSlowInEasing)
                    ) + fadeOut(animationSpec = tween(180)) + scaleOut(
                        targetScale = 1.08f, animationSpec = tween(280)
                    )

                    enter togetherWith exit
                },
                label = "countdown_animation"
            ) { animatedValue ->

                Text(
                    text = animatedValue,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontFeatureSettings = "tnum"
                    ),
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = unit.uppercase(),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontFamily = FontFamily.Monospace,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun DayCard(
    value: String,
    unit: String,
    progress: Float = 0.75f,
    modifier: Modifier = Modifier
) {

    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(
            durationMillis = 800,
            easing = FastOutSlowInEasing
        ),
        label = "ring_progress"
    )

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(100.dp)
            ) {
                val trackColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                val primaryColor = MaterialTheme.colorScheme.primary

                Canvas(modifier = Modifier.fillMaxSize()) {
                    val strokeWidth = 8.dp.toPx()

                    drawCircle(
                        color = trackColor,
                        style = Stroke(strokeWidth)
                    )

                    drawArc(
                        color = primaryColor,
                        startAngle = -90f,
                        sweepAngle = animatedProgress * 360f,
                        useCenter = false,
                        style = Stroke(
                            cap = StrokeCap.Round,
                            width = strokeWidth
                        )
                    )
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    AnimatedContent(
                        targetState = value,
                        transitionSpec = {
                            val enter = slideInVertically(
                                initialOffsetY = { it / 2 },
                                animationSpec = tween(durationMillis = 280, easing = FastOutSlowInEasing)
                            ) + fadeIn(animationSpec = tween(220)) + scaleIn(
                                initialScale = 0.92f, animationSpec = tween(280)
                            )

                            val exit = slideOutVertically(
                                targetOffsetY = { -it / 2 },
                                animationSpec = tween(durationMillis = 280, easing = FastOutSlowInEasing)
                            ) + fadeOut(animationSpec = tween(180)) + scaleOut(
                                targetScale = 1.08f, animationSpec = tween(280)
                            )

                            enter togetherWith exit
                        },
                        label = "countdown_animation"
                    ) { animatedValue ->
                        Text(
                            text = animatedValue,
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontFeatureSettings = "tnum"
                            ),
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = unit,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }
    }
}