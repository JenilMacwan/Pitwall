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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DividerDefaults
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.jenil.f1comp.R
import com.jenil.f1comp.data.local.entity.DriverStandingsEntity
import com.jenil.f1comp.data.local.entity.TeammateHeadtoHeadEntity
import com.jenil.f1comp.data.model.DriverPointsProgression
import com.jenil.f1comp.ui.home.components.DriverHeadshotCircle
import com.jenil.f1comp.util.FlagImage
import com.jenil.f1comp.util.ProfileUtils
import com.jenil.f1comp.util.TeamUtils
import java.time.LocalDate

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
    poles: String,
    about: String,
    born: String,
    debut: String,
    pointsProgression: List<DriverPointsProgression>,
    position: List<DriverStandingsEntity>,
    h2hData: TeammateHeadtoHeadEntity?,
    modifier: Modifier = Modifier
) {
    val teamColor = TeamUtils.getTeamColor(teamName)

    val currentStanding = remember(position, driverId) {
        position.find { it.driverId.equals(driverId, ignoreCase = true) }
    }
    val year = LocalDate.now().year
    val positionText = currentStanding?.let { "P${it.position} · $year Championship" } ?: "Standing unavailable"

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
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                teamColor.copy(alpha = 0.25f),
                                teamColor.copy(alpha = 0.10f),
                                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
                            )
                        )
                    )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(IntrinsicSize.Min),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    DriverHeadshot(
                        imageUrl = imageUrl,
                        driverName = driverName,
                        modifier = Modifier
                            .width(85.dp)
                            .fillMaxHeight()
                    )

                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = driverName,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "$teamName • $nationality ",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                FlagImage(flagUrl = ProfileUtils.getFlagUrl(nationality = nationality))
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Surface(
                                modifier = Modifier.padding(top = 4.dp),
                                color = teamColor.copy(alpha = 0.15f),
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
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }

                        Column(
                            horizontalAlignment = Alignment.End,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = driverNumber,
                                style = MaterialTheme.typography.headlineMedium,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = stringResource(R.string.profile_car_number).uppercase(),
                                fontFamily = FontFamily.Monospace,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        // 2. SEASON PERFORMANCE GRID
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .height(13.dp)
                        .width(6.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.primary)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "$year ${stringResource(R.string.profile_season_overview).uppercase()}",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            SeasonOverviewCard(
                points = points,
                wins = wins,
                podiums = podiums,
                poles = poles,
                teamColor = teamColor
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 3. POINTS PROGRESSION CHART
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            ) {
                PointsProgressionChart(
                    pointsProgression = pointsProgression,
                    modifier = Modifier.padding(12.dp)
                )
                }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 4. TEAMMATE HEAD-TO-HEAD
        h2hData?.let { data ->
            val isDriverA = data.driverA.driverId == driverId
            val teammate = if (isDriverA) data.driverB else data.driverA

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
                        text = stringResource(R.string.profile_teammate_comparison).uppercase(),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        text = "${data.season} season",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Teammate Header
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            DriverHeadshotCircle(
                                headshotUrl = teammate.headshotUrl,
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
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                text = stringResource(R.string.profile_teammate_battle),
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
                        Text(
                            text = stringResource(R.string.profile_completed_sessions_note),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        if (data.qualifying.totalCompleted > 0) {
                            ComparisonRow(
                                label = stringResource(R.string.profile_qualifying),
                                driverWins = if (isDriverA) data.qualifying.driverAWins else data.qualifying.driverBWins,
                                teammateWins = if (isDriverA) data.qualifying.driverBWins else data.qualifying.driverAWins,
                                completedSessions = data.qualifying.totalCompleted,
                                ties = data.qualifying.ties,
                                accentColor = teamColor
                            )
                        }

                        if (data.race.totalCompleted > 0) {
                            ComparisonRow(
                                label = stringResource(R.string.profile_races_ahead),
                                driverWins = if (isDriverA) data.race.driverAWins else data.race.driverBWins,
                                teammateWins = if (isDriverA) data.race.driverBWins else data.race.driverAWins,
                                completedSessions = data.race.totalCompleted,
                                ties = data.race.ties,
                                accentColor = teamColor
                            )
                        }
                        data.sprintQualifying?.totalCompleted?.let {
                            if (it > 0){
                                ComparisonRow(
                                    label = stringResource(R.string.profile_sprint_qualifying),
                                    driverWins = if (isDriverA) data.sprintQualifying.driverAWins else data.sprintQualifying.driverBWins,
                                    teammateWins = if (isDriverA) data.sprintQualifying.driverBWins else data.sprintQualifying.driverAWins,
                                    completedSessions = data.sprintQualifying.totalCompleted,
                                    ties = data.sprintQualifying.ties,
                                    accentColor = teamColor
                                )
                            }
                        }
                        data.sprintRace?.totalCompleted?.let {
                            if (it > 0){
                                ComparisonRow(
                                    label = stringResource(R.string.profile_sprint_race),
                                    driverWins = if (isDriverA) data.sprintRace.driverAWins else data.sprintRace.driverBWins,
                                    teammateWins = if (isDriverA) data.sprintRace.driverBWins else data.sprintRace.driverAWins,
                                    completedSessions = data.sprintRace.totalCompleted,
                                    ties = data.sprintRace.ties,
                                    accentColor = teamColor
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // 5. BIOGRAPHY / ABOUT CARD
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .height(13.dp)
                        .width(6.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.primary)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.profile_biography).uppercase(),
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
                    // Top Section: Vertical Accent Bar + Monospace Bio Text
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
                        Text(
                            text = about,
                            style = MaterialTheme.typography.bodyMedium,
                            fontFamily = FontFamily.Monospace,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // Horizontal Divider
                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                        thickness = 1.dp
                    )

                    // Bottom Section: Born & Debut Chips
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Born Chip
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
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = stringResource(R.string.profile_born),
                                    style = MaterialTheme.typography.labelMedium,
                                    fontFamily = FontFamily.Monospace,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = born,
                                    style = MaterialTheme.typography.labelMedium,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }

                        // Debut Chip
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
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = stringResource(R.string.profile_debut),
                                    style = MaterialTheme.typography.labelMedium,
                                    fontFamily = FontFamily.Monospace,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = debut,
                                    style = MaterialTheme.typography.labelMedium,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
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
fun DriverHeadshot(
    imageUrl: String?,
    driverName: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)),
        contentAlignment = Alignment.Center
    ) {
        if (!imageUrl.isNullOrBlank()) {
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(imageUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = driverName,
                contentScale = ContentScale.Crop,
                alignment = Alignment.TopCenter,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Text(
                text = driverName.take(1).uppercase(),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
