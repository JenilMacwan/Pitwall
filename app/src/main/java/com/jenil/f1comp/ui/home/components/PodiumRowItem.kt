package com.jenil.f1comp.ui.home.components



import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

@Composable
fun PodiumRowItem(
    position: String,
    name: String,
    team: String?,
    points: String,
    imageUrl: String? = null,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 1. Position Number (P1, P2, P3 styled with theme colors)
        Text(
            text = position,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            color = when (position) {
                "1" -> MaterialTheme.colorScheme.tertiary
                "2" -> MaterialTheme.colorScheme.secondary
                "3" -> MaterialTheme.colorScheme.secondaryContainer
                else -> MaterialTheme.colorScheme.onSurfaceVariant
            },
            textAlign = TextAlign.Center,
            modifier = Modifier.width(28.dp)
        )

        Spacer(modifier = Modifier.width(8.dp))

        // 2. Driver Image Circle (with initial fallback)
        DriverProfileCircle(
            imageUrl = imageUrl,
            driverName = name,
            size = 40.dp
        )

        Spacer(modifier = Modifier.width(12.dp))

        // 3. Driver Name & Team
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

        // 4. Points
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = points,
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