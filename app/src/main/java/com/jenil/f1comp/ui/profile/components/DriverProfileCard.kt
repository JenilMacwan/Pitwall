package com.jenil.f1comp.ui.profile.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.jenil.f1comp.data.local.entity.TeammateHeadtoHeadEntity
import com.jenil.f1comp.data.model.DriverPointsProgression
import com.jenil.f1comp.ui.home.components.DriverProfileCircle
import com.jenil.f1comp.util.ProfileUtils

@Composable
fun DriverProfileCard(
    driverId: String,
    driverName: String,
    teamName: String,
    driverNumber: String,
    nationality: String,
    imageUrl: String?,
    wins: String,
    podiums: String,
    points: String,
    pointsProgression: List<DriverPointsProgression>,
    h2hData: TeammateHeadtoHeadEntity?,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. HERO HEADER CARD
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                DriverProfileCircle(
                    imageUrl = imageUrl,
                    driverName = driverName,
                    size = 64.dp
                )

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = driverName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "$teamName • $nationality ${ProfileUtils.getFlagEmoji(nationality)}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = driverNumber,
                        style = MaterialTheme.typography.headlineSmall,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "CAR",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }

        // 2. SEASON PERFORMANCE GRID
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "Season Overview",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatsCard(modifier = Modifier.weight(1f), cardName = "Points", cardStats = points)
                StatsCard(modifier = Modifier.weight(1f), cardName = "Wins", cardStats = wins)
                StatsCard(modifier = Modifier.weight(1f), cardName = "Podiums", cardStats = podiums)
            }
        }

        // 3. POINTS PROGRESSION CHART
        if (pointsProgression.isNotEmpty()) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Points Progression",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f))
                ) {
                    PointsProgressionChart(
                        pointsProgression = pointsProgression,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }
        }

        // 4. TEAMMATE HEAD-TO-HEAD
        h2hData?.let { data ->
            val isDriverA = data.driverA.driverId == driverId
            val teammate = if (isDriverA) data.driverB else data.driverA

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Teammate Comparison",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Teammate Header
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            DriverProfileCircle(
                                imageUrl = teammate.image,
                                driverName = teammate.name,
                                size = 36.dp
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "vs. ${teammate.name}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Teammate Battle",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        HorizontalDivider(
                            Modifier,
                            DividerDefaults.Thickness,
                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                        )

                        if (data.qualifying.totalCompleted > 0) {
                            ComparisonRow(
                                label = "Qualifying",
                                driverWins = if (isDriverA) data.qualifying.driverAWins else data.qualifying.driverBWins,
                                teammateWins = if (isDriverA) data.qualifying.driverBWins else data.qualifying.driverAWins
                            )
                        }

                        if (data.race.totalCompleted > 0) {
                            ComparisonRow(
                                label = "Races Ahead",
                                driverWins = if (isDriverA) data.race.driverAWins else data.race.driverBWins,
                                teammateWins = if (isDriverA) data.race.driverBWins else data.race.driverAWins
                            )
                        }
                        data.sprintQualifying?.totalCompleted?.let {
                            if (it > 0){
                                ComparisonRow(
                                    label = "Sprint Qualifying",
                                    driverWins = if (isDriverA) data.sprintQualifying.driverAWins else data.sprintQualifying.driverBWins,
                                    teammateWins = if (isDriverA) data.sprintQualifying.driverBWins else data.sprintQualifying.driverAWins
                                )
                            }
                        }
                        data.sprintRace?.totalCompleted?.let {
                            if (it > 0){
                                ComparisonRow(
                                    label = "Sprint Race",
                                    driverWins = if (isDriverA) data.sprintRace.driverAWins else data.sprintRace.driverBWins,
                                    teammateWins = if (isDriverA) data.sprintRace.driverBWins else data.sprintRace.driverAWins
                                )
                            }
                        }
                    }
                }
            }
        }

        // 5. BIOGRAPHY / ABOUT CARD
        val driverAbout = ProfileUtils.getDriverAbout(driverId)
        if (driverAbout.about.isNotBlank()) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Biography",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f))
                ) {
                    Text(
                        text = driverAbout.about,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    }
}