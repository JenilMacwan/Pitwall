package com.jenil.f1comp.ui.auth.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp

@Composable
fun PillButton(
    selectedTab: String,
    onTabSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
    tabs: List<Pair<String, String>> = listOf(
        "Sign-In" to "Sign-In",
        "Register" to "Register",
    )
) {
    val selectedIndex = tabs.indexOfFirst { it.first == selectedTab }.coerceAtLeast(0)

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")

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

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(25),
        color = MaterialTheme.colorScheme.onSecondary.copy(alpha = 0.65f)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp)
        ) {
            BoxWithConstraints(modifier = Modifier.matchParentSize()) {
                val segmentWidth = maxWidth / tabs.size
                val offset by animateDpAsState(
                    targetValue = segmentWidth * selectedIndex,
                    animationSpec = spring(
                        dampingRatio = 0.75f,
                        stiffness = Spring.StiffnessMediumLow
                    ),
                    label = "indicatorOffset"
                )

                Box(
                    modifier = Modifier
                        .offset { IntOffset(x = offset.roundToPx(), y = 0) }
                        .width(segmentWidth)
                        .fillMaxHeight()
                        .background(
                            color = MaterialTheme.colorScheme.primary,
                            shape = RoundedCornerShape(25)
                        )
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                tabs.forEach { (tabKey, tabLabel) ->
                    val isSelected = selectedTab == tabKey
                    val textColor by animateColorAsState(
                        targetValue = if (isSelected) Color.White else Color.White.copy(alpha = 0.85f),
                        label = "tabTextColor_$tabKey"
                    )

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(25))
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                                onClick = { onTabSelected(tabKey) }
                            )
                            .padding(vertical = 8.dp, horizontal = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                       Row(
                           verticalAlignment = Alignment.CenterVertically
                       ) {
                           Box(
                               contentAlignment = Alignment.Center
                           ) {
                               Box(
                                   modifier = Modifier
                                       .size(8.dp)
                                       .graphicsLayer(
                                           scaleX = scale,
                                           scaleY = scale,
                                       )
                                       .alpha(pulseAlpha)
                                       .background(
                                           color = Color.White.copy(alpha = 0.4f),
                                           shape = CircleShape
                                       )
                               )
                               Box(
                                   modifier = Modifier
                                       .size(8.dp)
                                       .clip(CircleShape)
                                       .background(Color.White)
                               )
                           }
                           Spacer(modifier = Modifier.width(6.dp))
                           Text(
                               text = tabLabel,
                               color = textColor,
                               style = MaterialTheme.typography.labelLarge,
                               fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                               maxLines = 1,
                               overflow = TextOverflow.Ellipsis
                           )
                       }
                    }
                }
            }
        }
    }
}