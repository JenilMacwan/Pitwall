package com.jenil.f1comp.ui.profile.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.jenil.f1comp.data.local.entity.DriverProfileEntity
import com.jenil.f1comp.data.local.entity.DriverStandingsEntity
import com.jenil.f1comp.data.model.DriverPointsProgression
import com.jenil.f1comp.data.model.TeamPointsProgression
import com.jenil.f1comp.ui.home.components.TeamLogoCircle
import com.jenil.f1comp.util.FlagImage
import com.jenil.f1comp.util.ProfileUtils
import com.jenil.f1comp.util.ProfileUtils.getTeamAbout
import com.jenil.f1comp.util.TeamUtils

@Composable
fun TeamProfileCard(
    teamName: String,
    constructorId: String,
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
    wins: String = "0",
    totalRaces: String = "0",
    wdc: String = "0",
    wcc: String = "0",
    leaderPoints: Int = 0,
    pointsProgression: List<TeamPointsProgression> = emptyList(),
    drivers: List<DriverProfileEntity> = emptyList(),
    driverStandings: List<DriverStandingsEntity> = emptyList(),
    onDriverClick: (String) -> Unit = {},
) {
    val teamColor = TeamUtils.getTeamColor(teamName)
    val positionText = "P$standing in Championship"

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
            colors = CardDefaults.cardColors(containerColor = Color.Transparent),
            border = BorderStroke(1.dp, teamColor.copy(alpha = 0.35f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(108.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                teamColor.copy(alpha = 0.28f),
                                teamColor.copy(alpha = 0.12f),
                                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.40f)
                            )
                        )
                    )
            ) {
                // Team Logo + Middle Info Column
                Row(
                    modifier = Modifier.fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TeamLogoBox(
                        imageUrl = imageUrl,
                        teamName = teamName,
                        modifier = Modifier
                            .width(80.dp)
                            .fillMaxHeight()
                    )

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(
                                start = 12.dp,
                                top = 12.dp,
                                bottom = 12.dp,
                                end = if (!carUrl.isNullOrBlank()) 105.dp else 12.dp
                            ),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = teamName,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "$nationality ",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            FlagImage(flagUrl = ProfileUtils.getFlagUrl(nationality = nationality))
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Surface(
                            color = teamColor.copy(alpha = 0.18f),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, teamColor.copy(alpha = 0.35f))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(teamColor)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = positionText,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }

                // Extreme Right: Polished F1 Car Image (Filling Top-to-Bottom, Nose Facing Left)
                if (!carUrl.isNullOrBlank()) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .fillMaxHeight()
                            .width(155.dp)
                            .offset(x = 10.dp),
                        contentAlignment = Alignment.CenterEnd
                    ) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(carUrl)
                                .crossfade(true)
                                .build(),
                            contentDescription = "F1 Car: $chassis",
                            contentScale = ContentScale.Fit,
                            alignment = Alignment.CenterEnd,
                            modifier = Modifier
                                .fillMaxHeight()
                                .fillMaxWidth()
                                .graphicsLayer(scaleX = -1f) // Flips horizontally so front nose points LEFT
                        )
                    }
                } else {
                    Column(
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .padding(end = 16.dp),
                        horizontalAlignment = Alignment.End,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "#$standing",
                            style = MaterialTheme.typography.headlineMedium,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "RANK",
                            fontFamily = FontFamily.Monospace,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // 2. SEASON OVERVIEW
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .height(13.dp)
                        .width(6.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(teamColor)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "SEASON OVERVIEW",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            SeasonOverviewCard(
                points = points,
                wins = wins,
                podiums = podiums,
                teamColor = teamColor
            )

            GapToP1Card(
                currentPoints = points.toDoubleOrNull()?.toInt() ?: 0,
                leaderPoints = leaderPoints
            )
        }

        // 3. POINTS PROGRESSION CHART
        if (pointsProgression.isNotEmpty()) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .height(13.dp)
                            .width(6.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(teamColor)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "POINTS PROGRESSION",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                ) {
                    val convertedProgression = remember(pointsProgression) {
                        pointsProgression.map {
                            DriverPointsProgression(
                                round = it.round,
                                raceName = it.raceName,
                                points = it.points,
                                cumulativePoints = it.cumulativePoints
                            )
                        }
                    }
                    PointsProgressionChart(
                        pointsProgression = convertedProgression,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }
        }

        // 4. TECHNICAL SPECIFICATIONS
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .height(13.dp)
                        .width(6.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(teamColor)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "TECHNICAL SPECIFICATIONS",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    SpecRow(label = "Chassis", value = chassis)
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                    SpecRow(label = "Power Unit", value = powerUnit)
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                    SpecRow(label = "Team Principal", value = teamBoss)
                }
            }
        }

        // 5. 2026 DRIVER LINEUP
        if (drivers.isNotEmpty()) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .height(13.dp)
                            .width(6.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(teamColor)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "2026 DRIVER LINEUP",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    drivers.forEach { driver ->
                        val standingInfo = driverStandings.find {
                            it.driverId.equals(driver.driverId, ignoreCase = true)
                        }
                        val posText = standingInfo?.let { "P${it.position} • ${it.points.toInt()} PTS" }
                            ?: driver.careerStats?.currentSeason?.let { "P${it.position} • ${it.points} PTS" }

                        DriverPairingCard(
                            modifier = Modifier.fillMaxWidth(),
                            imageUrl = driver.image,
                            driverName = driver.fullName,
                            driverNumber = driver.number,
                            nationality = driver.nationality,
                            positionText = posText,
                            teamColor = teamColor,
                            onClick = { onDriverClick(driver.driverId) }
                        )
                    }
                }
            }
        }

        // 6. CAR CHALLENGER CARD
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .height(13.dp)
                        .width(6.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(teamColor)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "2026 CHALLENGER",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            CarImageCard(
                carUrl = carUrl,
                chassisName = chassis,
                teamColor = teamColor
            )
        }

        // 7. CAREER HERITAGE
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .height(13.dp)
                        .width(6.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(teamColor)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "CONSTRUCTORS HERITAGE",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    StatsCard(modifier = Modifier.weight(1f), cardName = "Constructors (WCC)", cardStats = wcc)
                    StatsCard(modifier = Modifier.weight(1f), cardName = "Drivers (WDC)", cardStats = wdc)
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    StatsCard(modifier = Modifier.weight(1f), cardName = "Total Races", cardStats = totalRaces)
                    StatsCard(modifier = Modifier.weight(1f), cardName = "Total Wins", cardStats = wins)
                }
            }
        }

        // 8. BIOGRAPHY / TEAM INFO
        val teamAbout = getTeamAbout(constructorId)
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .height(13.dp)
                        .width(6.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(teamColor)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "BIOGRAPHY",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
                ),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(IntrinsicSize.Min),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .width(3.5.dp)
                                .fillMaxHeight()
                                .background(
                                    color = teamColor,
                                    shape = RoundedCornerShape(2.dp)
                                )
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = teamAbout.fullName,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = teamColor
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = teamAbout.about,
                                style = MaterialTheme.typography.bodyMedium,
                                fontFamily = FontFamily.Monospace,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                        thickness = 1.dp
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Base Location Chip
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .border(
                                    BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .background(
                                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .padding(horizontal = 12.dp, vertical = 10.dp)
                        ) {
                            Column {
                                Text(
                                    text = "Base:",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontFamily = FontFamily.Monospace,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = teamAbout.base,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    maxLines = 2
                                )
                            }
                        }

                        // Engine Chip
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .border(
                                    BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .background(
                                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .padding(horizontal = 12.dp, vertical = 10.dp)
                        ) {
                            Column {
                                Text(
                                    text = "Power Unit:",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontFamily = FontFamily.Monospace,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = powerUnit,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    maxLines = 2
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TeamLogoBox(
    imageUrl: String?,
    teamName: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)),
        contentAlignment = Alignment.Center
    ) {
        if (!imageUrl.isNullOrBlank()) {
            TeamLogoCircle(
                logoUrl = imageUrl,
                teamName = teamName,
                size = 56.dp
            )
        } else {
            Text(
                text = teamName.take(1).uppercase(),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
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
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}


