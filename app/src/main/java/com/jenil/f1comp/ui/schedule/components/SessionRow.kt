package com.jenil.f1comp.ui.schedule.components


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material.icons.rounded.Flag
import androidx.compose.material.icons.rounded.Speed
import androidx.compose.material.icons.rounded.SportsMotorsports
import androidx.compose.material.icons.rounded.Timer
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLocale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jenil.f1comp.data.model.RaceSession
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Composable
fun SessionCard(
    title: String,
    dateTime: RaceSession
) {
    val parsedTime = LocalTime.parse(dateTime.time.removeSuffix("Z"))
    val formatter = DateTimeFormatter.ofPattern("hh:mm a", LocalLocale.current.platformLocale)
    val time = parsedTime.format(formatter)

    val parsedDate = LocalDate.parse(dateTime.date)
    val dateformatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy", LocalLocale.current.platformLocale)
    val date = parsedDate.format(dateformatter)

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHighest
        )
    ) {

        Column(
            modifier = Modifier.padding(12.dp)
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                Icon(
                    imageVector = iconFor(title),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp)
                )

                Spacer(Modifier.width(6.dp))

                Text(
                    title,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                )
            }

            Spacer(Modifier.height(10.dp))

            Text(
                text = "$date • $time",
                style = MaterialTheme.typography.bodySmall,
                fontFamily = FontFamily.Monospace,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

fun iconFor(session: String) = when(session) {
    "Practice 1" -> Icons.Rounded.SportsMotorsports
    "Practice 2" -> Icons.Rounded.SportsMotorsports
    "Practice 3" -> Icons.Rounded.SportsMotorsports
    "Sprint Quali" -> Icons.Rounded.Bolt
    "Sprint" -> Icons.Rounded.Speed
    "Qualifying" -> Icons.Rounded.Timer
    "Grand Prix" -> Icons.Rounded.EmojiEvents
    else -> Icons.Rounded.Flag
}