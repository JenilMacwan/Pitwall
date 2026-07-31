package com.jenil.f1comp.ui.home.components

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.jenil.f1comp.data.local.entity.RaceResultEntity

private const val TAG = "RaceResultCard"

@Composable
fun RaceResultCard(
    podium: List<RaceResultEntity>,
    modifier: Modifier = Modifier
){

    LaunchedEffect(podium) {
        Log.d(TAG, "RaceResultCard - podium size: ${podium.size}")
        if (podium.isEmpty()) {
            Log.w(TAG, "RaceResultCard - podium list is EMPTY")
        } else {
            val raceName = podium.firstOrNull()?.raceName
            Log.d(TAG, "Displaying results for Race Name: '$raceName'")
            podium.forEachIndexed { index, result ->
                Log.d(
                    TAG,
                    "Item [$index] -> Pos: ${result.position}, Driver: ${result.driver}, DriverId: ${result.driverId}, Team: ${result.constructor}, Points: ${result.points}, ImageUrl: ${result.driverImage}"
                )
            }
        }
    }

    if (podium.isEmpty()) return

    val raceName = podium.firstOrNull()?.raceName ?: "Race Result"

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "$raceName Result",
                    style = MaterialTheme.typography.titleMedium,
                    fontFamily = FontFamily.Monospace,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            podium.forEach { result ->
                PodiumRowItem(
                    position = result.position,
                    name = result.driver,
                    team = result.constructor,
                    points = result.points,
                    imageUrl = result.driverImage
                )
            }
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "View Full Result →",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary // F1 Red
                )
            }
        }
    }
}