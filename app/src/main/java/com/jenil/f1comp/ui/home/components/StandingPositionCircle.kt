package com.jenil.f1comp.ui.home.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight

@Composable
fun StandingPositionCircle(position: Int) {
    val textColor = when (position) {
        1 -> MaterialTheme.colorScheme.tertiary
        2 -> MaterialTheme.colorScheme.secondary
        3 -> MaterialTheme.colorScheme.secondaryContainer
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    Text(
        text = position.toString(),
        color = textColor,
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.Bold,
        fontFamily = FontFamily.Monospace,
    )
}
