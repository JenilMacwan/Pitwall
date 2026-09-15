package com.jenil.f1comp.ui.home.components

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Thunderstorm
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.outlined.Thermostat
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jenil.f1comp.data.model.RaceWeather

@Composable
fun WeatherBadge(
    weather: RaceWeather,
    modifier: Modifier = Modifier,
    iconTint: Color? = null
) {
    val condition = weather.condition.lowercase()

    val (weatherIcon, calculatedTint) = when {
        condition.contains("rain") || condition.contains("patchy rain nearby") || condition.contains("wet") || condition.contains("shower") || condition.contains("storm") -> {
            Icons.Default.Thunderstorm to Color(0xFF29B6F6) // Vibrant Rain Blue
        }
        condition.contains("cloud") || condition.contains("overcast") || condition.contains("partly") -> {
            Icons.Default.Cloud to Color(0xFF78909C) // Cool Slate / Blue-Grey
        }
        condition.contains("sun") || condition.contains("clear") || condition.contains("hot") || condition.contains("fair") -> {
            Icons.Default.WbSunny to Color(0xFFFFB300) // Golden Amber / Sun Yellow
        }
        else -> {
            Icons.Default.WbSunny to MaterialTheme.colorScheme.primary
        }
    }

    val finalIconTint = iconTint ?: calculatedTint

    Column(
        modifier = modifier
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Air Temp
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = weatherIcon,
                    contentDescription = null,
                    modifier = Modifier.size(15.dp),
                    tint = finalIconTint
                )
                Spacer(modifier = Modifier.width(3.dp))
                Text(
                    text = weather.temp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            // Track Temp (if available)
            if (weather.trackTemp.isNotBlank() && weather.trackTemp != "N/A") {
                Text(
                    text = "•",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.Thermostat,
                        contentDescription = "Track Temp",
                        modifier = Modifier.size(14.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = "Track Temp: ${weather.trackTemp}",
                        fontFamily = FontFamily.Monospace,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(3.dp))

        Text(
            text = weather.condition,
            fontFamily = FontFamily.Monospace,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Log.d("WeatherBadge", "Condition: $condition")
    }
}
