package com.jenil.f1comp.ui.telemetry.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jenil.f1comp.ui.theme.F1Red

private data class TrackConditionStyle(
    val title: String,
    val badge: String,
    val color: Color
)

@Composable
fun RaceConditionBanner(
    trackCondition: String,
    latestMessage: String?,
    latestCategory: String?,
    latestFlag: String?,
    modifier: Modifier = Modifier
) {
    val normalized = trackCondition.trim().replace(" ", "").lowercase()
    val statusStyle = when {
        normalized in setOf("red", "5", "redflag") ->
            TrackConditionStyle("RED FLAG", "STOP", F1Red)
        normalized in setOf("scdeployed", "safetycar", "4") ->
            TrackConditionStyle("SAFETY CAR DEPLOYED", "SC", Color(0xFFFF9D00))
        normalized in setOf("vscdeployed", "6") ->
            TrackConditionStyle("VIRTUAL SAFETY CAR", "VSC", Color(0xFFFFB300))
        normalized in setOf("yellow", "2", "yellowflag", "doubleyellow") ->
            TrackConditionStyle("YELLOW FLAG", "FLAG", Color(0xFFFFD600))
        normalized in setOf("allclear", "allcleartrack", "trackclear", "1", "green", "greenflag") ->
            TrackConditionStyle("TRACK CLEAR", "GREEN", Color(0xFF00C978))
        else -> null
    }
    val style = statusStyle ?: when {
        latestFlag.equals("RED", true) -> TrackConditionStyle("RED FLAG", "STOP", F1Red)
        latestFlag.equals("YELLOW", true) -> TrackConditionStyle("YELLOW FLAG", "FLAG", Color(0xFFFFD600))
        else -> null
    } ?: return

    val message = latestMessage?.trim()?.takeIf { it.isNotEmpty() }
        ?.takeIf { text ->
            val upper = text.uppercase()
            when (style.title) {
                "RED FLAG" -> upper.contains("RED FLAG") || latestFlag.equals("RED", true)
                "SAFETY CAR DEPLOYED" -> upper.contains("SAFETY CAR") && !upper.contains("IN THIS LAP")
                "VIRTUAL SAFETY CAR" -> upper.contains("VSC")
                "YELLOW FLAG" -> upper.contains("YELLOW") || latestFlag.equals("YELLOW", true)
                "TRACK CLEAR" -> upper.contains("CLEAR") || upper.contains("GREEN FLAG")
                else -> false
            }
        }
        ?: "Race control is reporting this condition"

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        shape = RoundedCornerShape(14.dp),
        color = style.color.copy(alpha = 0.12f),
        border = BorderStroke(1.dp, style.color.copy(alpha = 0.58f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Surface(color = style.color, shape = RoundedCornerShape(7.dp)) {
                Text(
                    text = style.badge,
                    modifier = Modifier.padding(horizontal = 9.dp, vertical = 7.dp),
                    color = Color(0xFF171717),
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.5.sp
                    )
                )
            }
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Text(
                    text = style.title,
                    color = style.color,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.6.sp
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = message,
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
