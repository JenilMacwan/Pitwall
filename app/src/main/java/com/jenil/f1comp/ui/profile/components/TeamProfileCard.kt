package com.jenil.f1comp.ui.profile.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jenil.f1comp.data.local.entity.DriverProfileEntity
import com.jenil.f1comp.ui.home.components.TeamLogoCircle
import com.jenil.f1comp.util.ProfileUtils

@Composable
fun TeamProfileCard(
    teamName: String,
    nationality: String,
    modifier: Modifier = Modifier,
    imageUrl: String? = null,
    carUrl: String? = null,
    chassis: String = "W16",
    powerUnit: String = "Mercedes-AMG",
    teamBoss: String = "Toto Wolff",
    standing: String = "0",
    points: String = "0",
    podiums: String = "0",
    wdc: String = "0",
    wcc: String = "0",
    leaderPoints: Int = 0,
    drivers: List<DriverProfileEntity> = emptyList()
) {
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
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // 1. HEADER ROW (Logo + Team Name & Country)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    if (!imageUrl.isNullOrBlank()) {
                        TeamLogoCircle(
                            logoUrl = imageUrl,
                            teamName = teamName,
                            size = 50.dp
                        )
                    } else {
                        Text(
                            text = teamName.take(1).uppercase(),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = teamName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "$nationality ${ProfileUtils.getFlagEmoji(nationality)}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 2. TECHNICAL SPECIFICATIONS
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    SpecRow(label = "Chassis", value = chassis)
                    SpecRow(label = "Power Unit", value = powerUnit)
                    SpecRow(label = "Team Principal", value = teamBoss)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 3. 2026 DRIVER PAIRING
            Text(
                text = "2026 Driver Lineup",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                drivers.forEach { driver ->
                    DriverPairingCard(
                        modifier = Modifier.weight(1f),
                        imageUrl = driver.image,
                        driverName = driver.lastName,
                        driverNumber = driver.number
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 4. SEASON PERFORMANCE (Reusing StatsCard)
            Text(
                text = "Season Performance",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatsCard(
                    modifier = Modifier.weight(1f),
                    cardName = "Standing",
                    cardStats = "#$standing"
                )
                StatsCard(
                    modifier = Modifier.weight(1f),
                    cardName = "Points",
                    cardStats = points
                )
                StatsCard(
                    modifier = Modifier.weight(1f),
                    cardName = "Podiums",
                    cardStats = podiums
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 5. CAREER PERFORMANCE (Reusing StatsCard)
            Text(
                text = "Career Performance",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatsCard(
                    modifier = Modifier.weight(1f),
                    cardName = "WDC",
                    cardStats = wdc
                )
                StatsCard(
                    modifier = Modifier.weight(1f),
                    cardName = "WCC",
                    cardStats = wcc
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 5. CHAMPIONSHIP GAP PROGRESS CARD
            Text(
                text = "Championship Gap to P1",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(12.dp))

            GapToP1Card(
                currentPoints = points.toDoubleOrNull()?.toInt() ?: 0,
                leaderPoints = leaderPoints
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 6. CAR CHALLENGER CARD
            CarImageCard(
                carUrl = carUrl,
                chassisName = chassis
            )
        }
    }
}

@Composable
fun SpecRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}



@Composable
private fun GapToP1Card(
    currentPoints: Int,
    leaderPoints: Int,
    modifier: Modifier = Modifier
) {
    val pointsGap = (leaderPoints - currentPoints).coerceAtLeast(0)
    val progress = if (leaderPoints > 0) {
        (currentPoints.toFloat() / leaderPoints.toFloat()).coerceIn(0f, 1f)
    } else 1f

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (pointsGap == 0) "Championship Leader" else "-$pointsGap PTS to Leader",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (pointsGap == 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "$currentPoints / $leaderPoints PTS",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
                strokeCap = StrokeCap.Round
            )
        }
    }
}