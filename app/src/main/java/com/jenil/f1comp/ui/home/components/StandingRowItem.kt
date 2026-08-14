package com.jenil.f1comp.ui.home.components

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp

@Composable
fun StandingRowItem(
    modifier: Modifier = Modifier,
    position: Int,
    name: String,
    team: String?,
    points: Int,
    imageUrl: String? = null,
    isConstructor: Boolean = false,
    onClick: () -> Unit = {},
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {

        StandingPositionCircle(position)

        Spacer(modifier = Modifier.width(12.dp))

        if (isConstructor) {
            TeamLogoCircle(
                logoUrl = imageUrl,
                teamName = name,
                size = 32.dp
            )
        } else {
            DriverProfileCircle(
                imageUrl = imageUrl,
                driverName = name,
                size = 40.dp
            )
            Log.d("StandingRowItem", "Image URL: $imageUrl , Driver Name: $name")
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Middle Column
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = name,
                style = MaterialTheme.typography.titleMedium,
                fontFamily = FontFamily.Monospace,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = team ?: "N/A",
                style = MaterialTheme.typography.bodySmall,
                fontFamily = FontFamily.Monospace,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Right Column (Points)
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = points.toString(),
                style = MaterialTheme.typography.titleMedium,
                fontFamily = FontFamily.Monospace,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "pts",
                style = MaterialTheme.typography.labelSmall,
                fontFamily = FontFamily.Monospace,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}