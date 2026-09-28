package com.jenil.f1comp.ui.telemetry.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jenil.f1comp.data.model.RaceControlMessage
import com.jenil.f1comp.ui.theme.F1CompTheme
import com.jenil.f1comp.ui.theme.F1Red
import com.jenil.f1comp.util.formatUtcToLocalDateTime

fun Modifier.drawScrollbar(
    scrollState: ScrollState,
    color: Color,
    width: Dp = 4.dp
): Modifier = drawWithContent {
    drawContent()
    val needDrawScrollbar = scrollState.maxValue > 0
    if (needDrawScrollbar) {
        val elementHeight = this.size.height
        val scrollMax = scrollState.maxValue.toFloat()
        val scrollPos = scrollState.value.toFloat()
        val scrollbarHeight = (elementHeight / (scrollMax + elementHeight)) * elementHeight
        val scrollbarOffsetY = (scrollPos / scrollMax) * (elementHeight - scrollbarHeight)

        drawRoundRect(
            color = color,
            topLeft = Offset(this.size.width - width.toPx(), scrollbarOffsetY),
            size = Size(width.toPx(), scrollbarHeight),
            cornerRadius = CornerRadius(width.toPx() / 2, width.toPx() / 2)
        )
    }
}

@Composable
fun RaceControlCard(
    modifier: Modifier = Modifier,
    time: String? = null,
    message: String? = null,
    category: String? = null,
    flag: String? = null,
    messages: List<RaceControlMessage> = emptyList()
) {
    val displayMessages = when {
        messages.isNotEmpty() -> messages
        !message.isNullOrBlank() -> listOf(
            RaceControlMessage(
                utc = time ?: "",
                message = message,
                category = category,
                flag = flag
            )
        )
        else -> emptyList()
    }

    val warningColor = Color(0xFFFFB300)
    val feedScrollState = rememberScrollState()
    val scrollbarColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
        ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header Row: FIA RACE CONTROL Title & Feed Status Pill
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Warning,
                        contentDescription = "Race Control Warning",
                        tint = warningColor,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "FIA RACE CONTROL",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = warningColor,
                            fontSize = 14.sp,
                            letterSpacing = 1.sp
                        )
                    )
                }

                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                    shape = RoundedCornerShape(6.dp),
                    border = BorderStroke(
                        1.dp,
                        MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
                    )
                ) {
                    Text(
                        text = "AUTO-FEED ACTIVE",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                            fontSize = 10.sp,
                            letterSpacing = 0.8.sp
                        )
                    )
                }
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 12.dp),
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.25f)
            )

            if (displayMessages.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "NO RACE CONTROL MESSAGES",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                            fontSize = 12.sp,
                            letterSpacing = 0.8.sp
                        )
                    )
                }
            } else {
                // Scrollable Race Control Messages List with custom scrollbar
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 180.dp)
                        .drawScrollbar(feedScrollState, color = scrollbarColor)
                        .verticalScroll(feedScrollState)
                        .padding(end = 6.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    displayMessages.forEach { item ->
                        RaceControlMessageItem(
                            dateTime = formatUtcToLocalDateTime(item.utc),
                            rawMessage = item.message ?: "",
                            category = item.category,
                            flag = item.flag
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RaceControlMessageItem(
    dateTime: String,
    rawMessage: String,
    category: String?,
    flag: String?
) {
    val annotatedMessage = buildAnnotatedRaceControlMessage(
        message = rawMessage,
        category = category,
        flag = flag
    )

    val itemBorderColor = when {
        category.equals("SafetyCar", ignoreCase = true) || category.equals("Safety Car", ignoreCase = true) ->
            Color(0xFFFFD600).copy(alpha = 0.4f)
        category.equals("Flag", ignoreCase = true) && flag.equals("RED", ignoreCase = true) ->
            F1Red.copy(alpha = 0.4f)
        else -> MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.25f)
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
        border = BorderStroke(1.dp, itemBorderColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = dateTime,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Normal,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                    fontSize = 11.sp
                )
            )

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = annotatedMessage,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                ),
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun buildAnnotatedRaceControlMessage(
    message: String,
    category: String?,
    flag: String?
): AnnotatedString {
    val text = message.uppercase()

    return buildAnnotatedString {
        append(text)

        val isSafetyCar = category.equals("SafetyCar", ignoreCase = true) ||
                category.equals("Safety Car", ignoreCase = true) ||
                text.contains("SAFETY CAR")

        val isRedFlag = (category.equals("Flag", ignoreCase = true) && flag.equals("RED", ignoreCase = true)) ||
                text.contains("RED FLAG")

        val isYellowFlag = (category.equals("Flag", ignoreCase = true) && flag.equals("YELLOW", ignoreCase = true)) ||
                text.contains("YELLOW FLAG")

        when {
            isSafetyCar || isYellowFlag -> {
                addStyle(
                    style = SpanStyle(color = Color(0xFFFFD600), fontWeight = FontWeight.Bold),
                    start = 0,
                    end = text.length
                )
            }
            isRedFlag -> {
                addStyle(
                    style = SpanStyle(color = F1Red, fontWeight = FontWeight.Bold),
                    start = 0,
                    end = text.length
                )
            }
        }

        val greenPhrases = listOf("TRACK CLEAR", "DRS ENABLED", "GREEN FLAG", "ALL CLEAR")
        val yellowPhrases = listOf("SAFETY CAR", "VSC", "YELLOW FLAG", "SLICK", "OIL", "SECTOR 1", "SECTOR 2", "SECTOR 3")
        val redPhrases = listOf("RED FLAG", "STOPPED", "ACCIDENT", "COLLISION", "DISQUALIFIED")
        val cyanPhrases = listOf(
            "INCIDENT", "NOTED", "INVESTIGATION", "TURN 1", "TURN 2", "TURN 3",
            "TURN 4", "TURN 5", "TURN 6", "TURN 7", "TURN 8", "TURN 9", "TURN 10"
        )

        fun highlightPhrase(phrase: String, color: Color) {
            var startIndex = text.indexOf(phrase)
            while (startIndex != -1) {
                val endIndex = startIndex + phrase.length
                addStyle(
                    style = SpanStyle(color = color, fontWeight = FontWeight.Bold),
                    start = startIndex,
                    end = endIndex
                )
                startIndex = text.indexOf(phrase, endIndex)
            }
        }

        greenPhrases.forEach { highlightPhrase(it, Color(0xFF00E676)) }
        yellowPhrases.forEach { highlightPhrase(it, Color(0xFFFFD600)) }
        redPhrases.forEach { highlightPhrase(it, F1Red) }
        cyanPhrases.forEach { highlightPhrase(it, Color(0xFF00E5FF)) }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun RaceControlCardPreview() {
    F1CompTheme {
        Column(modifier = Modifier.padding(8.dp)) {
            RaceControlCard(
                messages = listOf(
                    RaceControlMessage(utc = "2026-03-28T14:42:18Z", message = "TRACK CLEAR - TRACK SURFACE SLICK IN SECTOR 2", category = "TrackStatus"),
                    RaceControlMessage(utc = "2026-03-28T14:38:05Z", message = "INCIDENT INVOLVING CAR 1 (NORRIS) AT TURN 7 NOTED", category = "Incident"),
                    RaceControlMessage(utc = "2026-03-28T14:35:10Z", message = "DRS ENABLED IN ZONES 1 & 2", category = "Drs"),
                    RaceControlMessage(utc = "2026-03-28T14:30:00Z", message = "SAFETY CAR DEPLOYED", category = "SafetyCar"),
                    RaceControlMessage(utc = "2026-03-28T14:25:12Z", message = "YELLOW FLAG IN SECTOR 3", category = "Flag", flag = "YELLOW"),
                    RaceControlMessage(utc = "2026-03-28T14:20:00Z", message = "GREEN FLAG - ALL CLEAR", category = "Flag", flag = "GREEN")
                )
            )
        }
    }
}
