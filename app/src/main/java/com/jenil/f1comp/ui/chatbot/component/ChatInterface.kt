package com.jenil.f1comp.ui.chatbot.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.VolumeUp
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.jenil.f1comp.R
import com.jenil.f1comp.data.model.ChatMessage

@Composable
fun ApexAvatar(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(32.dp)
            .clip(CircleShape)
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary,
                        Color(0xFF8B0000)
                    )
                )
            )
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = apexMark,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(18.dp)
        )
    }
}

@Composable
fun ChatBubble(
    message: ChatMessage,
    onCopy: (String) -> Unit,
    onShare: (String) -> Unit,
    onReadAloud: (String) -> Unit,
    onRetry: (String) -> Unit,
    onOpenRelatedData: (String) -> Unit
) {
    val isUser = message.isUser
    val configuration = androidx.compose.ui.platform.LocalConfiguration.current
    val maxBubbleWidth = (configuration.screenWidthDp * 0.82f).dp

    AnimatedVisibility(
        visible = true,
        enter = fadeIn(tween(220)) + slideInVertically(tween(220)) { it / 4 }
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().semantics {
                if (!isUser) liveRegion = LiveRegionMode.Polite
            },
            horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start,
            verticalAlignment = Alignment.Bottom
        ) {
            if (!isUser) ApexAvatar(modifier = Modifier.padding(end = 6.dp))

            Surface(
                shape = RoundedCornerShape(
                    topStart = 16.dp,
                    topEnd = 16.dp,
                    bottomStart = if (isUser) 16.dp else 4.dp,
                    bottomEnd = if (isUser) 4.dp else 16.dp
                ),
                color = if (isUser) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                border = if (isUser) null else BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                modifier = Modifier.widthIn(max = maxBubbleWidth)
            ) {
                Column {
                    SelectionContainer {
                        Text(
                            text = message.text,
                            modifier = Modifier.padding(12.dp),
                            color = if (isUser) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    if (!isUser && message.dataSources.isNotEmpty()) {
                        Text(
                            text = stringResource(R.string.chatbot_data_sources, message.dataSources.joinToString()),
                            modifier = Modifier.padding(start = 12.dp, end = 12.dp, bottom = 8.dp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                    if (!isUser && message.referenceDocuments.isNotEmpty()) {
                        Text(
                            text = stringResource(
                                R.string.chatbot_references,
                                message.referenceDocuments.joinToString()
                            ),
                            modifier = Modifier.padding(start = 12.dp, end = 12.dp, bottom = 8.dp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                    if (!isUser) {
                        val relatedActions = message.dataSources.mapNotNull { source ->
                            when {
                                source.contains("standings", ignoreCase = true) -> "standings" to R.string.chatbot_open_standings
                                source.contains("schedule", ignoreCase = true) || source.contains("next race", ignoreCase = true) -> "schedule" to R.string.chatbot_open_schedule
                                source.contains("race control", ignoreCase = true) -> "telemetry" to R.string.chatbot_open_telemetry
                                source.contains("news", ignoreCase = true) -> "news" to R.string.chatbot_open_news
                                else -> null
                            }
                        }.distinctBy { it.first }
                        if (relatedActions.isNotEmpty()) {
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.padding(horizontal = 8.dp)) {
                                relatedActions.forEach { (route, label) ->
                                    AssistChip(onClick = { onOpenRelatedData(route) }, label = { Text(stringResource(label)) })
                                }
                            }
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(onClick = { onCopy(message.text) }) {
                                Icon(Icons.Outlined.ContentCopy, contentDescription = stringResource(R.string.chatbot_copy_response))
                            }
                            IconButton(onClick = { onShare(message.text) }) {
                                Icon(Icons.Outlined.Share, contentDescription = stringResource(R.string.chatbot_share_response))
                            }
                            IconButton(onClick = { onReadAloud(message.text) }) {
                                Icon(Icons.AutoMirrored.Outlined.VolumeUp, contentDescription = stringResource(R.string.chatbot_read_aloud))
                            }
                            message.retryQuery?.let { query ->
                                TextButton(onClick = { onRetry(query) }) {
                                    Text(stringResource(R.string.chatbot_retry))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
@Composable
fun ThinkBubble() {
    val typingDescription = stringResource(R.string.chatbot_thinking_status)
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.Bottom
    ) {
        ApexAvatar(modifier = Modifier.padding(end = 6.dp))

        Surface(
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = 4.dp,
                bottomEnd = 16.dp
            ),
            color = MaterialTheme.colorScheme.surfaceVariant,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
            modifier = Modifier
                .widthIn(max = 80.dp)
                .semantics {
                    contentDescription = typingDescription
                    liveRegion = LiveRegionMode.Polite
                }
        ) {
            BouncingDots()
        }
    }
}

@Composable
fun BouncingDots() {
    val dots = listOf(0, 1, 2)
    val infiniteTransition = rememberInfiniteTransition(label = "bouncing_dots")

    val duration = 600
    val maxOffset = -12f

    val animValues = dots.map { index ->
        infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = maxOffset,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = duration / 2, easing = FastOutSlowInEasing, delayMillis = index * (duration / 3)),
                repeatMode = RepeatMode.Reverse
            ),
            label = "dot_$index"
        )
    }

    Row(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 18.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        animValues.forEach { anim ->
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .offset(y = anim.value.dp)
                    .background(MaterialTheme.colorScheme.onSurfaceVariant, CircleShape)
            )
        }
    }
}

@Composable
fun QuickPromptBar(
    onPromptClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val prompts = listOf(
        stringResource(R.string.prompt_aero_rules),
        stringResource(R.string.prompt_driver_standings),
        stringResource(R.string.prompt_next_race),
        stringResource(R.string.prompt_teammate_h2h)
    )

    LazyRow(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(prompts) { prompt ->
            AssistChip(
                onClick = { onPromptClick(prompt) },
                label = {
                    Text(
                        text = prompt,
                        style = MaterialTheme.typography.labelMedium
                    )
                },
                colors = AssistChipDefaults.assistChipColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                border = AssistChipDefaults.assistChipBorder(
                    borderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
                    enabled = true,
                    borderWidth = 1.dp
                ),
                shape = RoundedCornerShape(16.dp)
            )
        }
    }
}
