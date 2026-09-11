package com.jenil.f1comp.ui.home.components

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.jenil.f1comp.data.local.entity.ConstructorStandingsEntity
import com.jenil.f1comp.data.local.entity.DriverStandingsEntity
import com.jenil.f1comp.ui.theme.F1CompTheme
import com.jenil.f1comp.util.TeamUtils
import com.jenil.f1comp.util.toConstructorLastNames


@Composable
fun Standing3Card(
    constructorStandings: List<ConstructorStandingsEntity> = emptyList(),
    modifier: Modifier = Modifier,
    navController: NavController? = null
) {
    val top3 = remember(constructorStandings) {
        constructorStandings.sortedBy { it.position }.take(3)
    }

    val p1 = top3.getOrNull(0)
    val p2 = top3.getOrNull(1)
    val p3 = top3.getOrNull(2)

    if (p1 == null) return

    val p1Drivers = remember(p1.drivers) {
        val formatted = p1.drivers.toConstructorLastNames()
        val list = formatted.split("·").map { it.trim() }
        Pair(list.getOrNull(0) ?: "", list.getOrNull(1) ?: "")
    }

    val p1Pts = p1.points.toInt()
    val p2Pts = p2?.points?.toInt() ?: 0
    val p3Pts = p3?.points?.toInt() ?: 0

    val p1Gap = (p1Pts - p2Pts).coerceAtLeast(0)

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 1. The Leader Card (P1)
        LeaderCard(
            teamName = p1.name,
            logoUrl = p1.constructorLogo,
            driver1 = p1Drivers.first,
            driver2 = p1Drivers.second,
            points = p1Pts,
            gapText = "+$p1Gap PTS GAP",
            teamColor = TeamUtils.getTeamColor(p1.name),
            onClick = { navController?.navigate("profile/false/${p1.name}") }
        )

        if (p2 != null) {
            Spacer(modifier = Modifier.height(12.dp))

            val p2Drivers = remember(p2.drivers) {
                val formatted = p2.drivers.toConstructorLastNames()
                val list = formatted.split("·").map { it.trim() }
                Pair(list.getOrNull(0) ?: "", list.getOrNull(1) ?: "")
            }
            val p2Gap = (p1Pts - p2Pts).coerceAtLeast(0)
            val p2IndexPct = if (p1Pts > 0) (p2Pts.toFloat() / p1Pts).coerceIn(0.05f, 1f) else 0.5f

            val p3Drivers = remember(p3?.drivers) {
                val formatted = (p3?.drivers ?: "").toConstructorLastNames()
                val list = formatted.split("·").map { it.trim() }
                Pair(list.getOrNull(0) ?: "", list.getOrNull(1) ?: "")
            }
            val p3Gap = if (p3 != null) (p1Pts - p3Pts).coerceAtLeast(0) else 0
            val p3IndexPct = if (p3 != null && p1Pts > 0) (p3Pts.toFloat() / p1Pts).coerceIn(0.05f, 1f) else 0.5f

            // 2. The Contenders Row (P2 & P3)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ContenderCard(
                    modifier = Modifier.weight(1f),
                    position = 2,
                    teamName = p2.name,
                    logoUrl = p2.constructorLogo,
                    driver1 = p2Drivers.first,
                    driver2 = p2Drivers.second,
                    points = p2Pts,
                    gapText = "-$p2Gap PTS",
                    indexPercentage = p2IndexPct,
                    teamColor = TeamUtils.getTeamColor(p2.name),
                    badgeColor = Color(0xFFB0B4B8),
                    onClick = { navController?.navigate("profile/false/${p2.name}") }
                )

                if (p3 != null) {
                    ContenderCard(
                        modifier = Modifier.weight(1f),
                        position = 3,
                        teamName = p3.name,
                        logoUrl = p3.constructorLogo,
                        driver1 = p3Drivers.first,
                        driver2 = p3Drivers.second,
                        points = p3Pts,
                        gapText = "-$p3Gap PTS",
                        indexPercentage = p3IndexPct,
                        teamColor = TeamUtils.getTeamColor(p3.name),
                        badgeColor = Color(0xFFCD7F32),
                        onClick = { navController?.navigate("profile/false/${p3.name}") }
                    )
                }
            }
        }
    }
}

@Composable
fun Driver3Card(
    driverStandings: List<DriverStandingsEntity> = emptyList(),
    modifier: Modifier = Modifier,
    navController: NavController? = null
) {
    val top3 = remember(driverStandings) {
        driverStandings.sortedBy { it.position }.take(3)
    }

    val p1 = top3.getOrNull(0)
    val p2 = top3.getOrNull(1)
    val p3 = top3.getOrNull(2)

    if (p1 == null) return

    val p1Pts = p1.points.toInt()
    val p2Pts = p2?.points?.toInt() ?: 0
    val p3Pts = p3?.points?.toInt() ?: 0

    val p1Gap = (p1Pts - p2Pts).coerceAtLeast(0)

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 1. Leader Card (Driver P1)
        LeaderDriverCard(
            driverName = p1.name,
            teamName = p1.team ?: "F1",
            imageUrl = p1.driverImage,
            points = p1Pts,
            gapText = "+$p1Gap PTS GAP",
            teamColor = TeamUtils.getTeamColor(p1.team),
            onClick = { navController?.navigate("profile/true/${p1.name}") }
        )

        if (p2 != null) {
            Spacer(modifier = Modifier.height(12.dp))

            val p2Gap = (p1Pts - p2Pts).coerceAtLeast(0)
            val p2IndexPct = if (p1Pts > 0) (p2Pts.toFloat() / p1Pts).coerceIn(0.05f, 1f) else 0.5f

            val p3Gap = if (p3 != null) (p1Pts - p3Pts).coerceAtLeast(0) else 0
            val p3IndexPct = if (p3 != null && p1Pts > 0) (p3Pts.toFloat() / p1Pts).coerceIn(0.05f, 1f) else 0.5f

            // 2. Contender Cards Row (P2 & P3)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ContenderDriverCard(
                    modifier = Modifier.weight(1f),
                    position = 2,
                    driverName = p2.name,
                    teamName = p2.team ?: "F1",
                    imageUrl = p2.driverImage,
                    points = p2Pts,
                    gapText = "-$p2Gap PTS",
                    indexPercentage = p2IndexPct,
                    teamColor = TeamUtils.getTeamColor(p2.team),
                    badgeColor = Color(0xFFB0B4B8),
                    onClick = { navController?.navigate("profile/true/${p2.name}") }
                )

                if (p3 != null) {
                    ContenderDriverCard(
                        modifier = Modifier.weight(1f),
                        position = 3,
                        driverName = p3.name,
                        teamName = p3.team ?: "F1",
                        imageUrl = p3.driverImage,
                        points = p3Pts,
                        gapText = "-$p3Gap PTS",
                        indexPercentage = p3IndexPct,
                        teamColor = TeamUtils.getTeamColor(p3.team),
                        badgeColor = Color(0xFFCD7F32),
                        onClick = { navController?.navigate("profile/true/${p3.name}") }
                    )
                }
            }
        }
    }
}

@Composable
fun LeaderDriverCard(
    modifier: Modifier = Modifier,
    driverName: String,
    teamName: String,
    imageUrl: String? = null,
    points: Int,
    gapText: String,
    teamColor: Color = MaterialTheme.colorScheme.primary,
    onClick: () -> Unit = {}
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.5.dp, teamColor.copy(alpha = 0.5f)),
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            // --- TOP HALF: Driver Info ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                // Left: Badges & Headshot
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.tertiary)
                            .padding(4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        DriverProfileCircle(
                            imageUrl = imageUrl,
                            driverName = driverName,
                            size = 42.dp
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Middle: Name & Team
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = driverName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        color = MaterialTheme.colorScheme.onSurface,
                        lineHeight = 18.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = teamName,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontFamily = FontFamily.Monospace
                    )
                }

                // Right: Points
                Column(horizontalAlignment = Alignment.End) {
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = points.toString(),
                            fontSize = 28.sp,
                            fontWeight = FontWeight.ExtraBold,
                            fontFamily = FontFamily.Monospace,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "PTS",
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // --- BOTTOM HALF: Telemetry Bar ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "CHAMPIONSHIP LEADER",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = teamColor,
                    fontFamily = FontFamily.Monospace
                )
                Text(
                    text = gapText,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = teamColor,
                    fontFamily = FontFamily.Monospace
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(50))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(1.0f)
                        .height(6.dp)
                        .clip(RoundedCornerShape(50))
                        .background(teamColor)
                )
            }
        }
    }
}

@Composable
fun ContenderDriverCard(
    modifier: Modifier = Modifier,
    position: Int,
    driverName: String,
    teamName: String,
    imageUrl: String? = null,
    points: Int,
    gapText: String,
    indexPercentage: Float,
    teamColor: Color,
    badgeColor: Color,
    onClick: () -> Unit = {}
) {
    val badgeBg = if (position == 2) {
        MaterialTheme.colorScheme.secondary
    } else {
        MaterialTheme.colorScheme.secondaryContainer
    }

    Surface(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, teamColor.copy(alpha = 0.5f)),
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            // Zone 1: Header (Badge & Gap Pill)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(badgeBg)
                        .padding(4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    DriverProfileCircle(
                        imageUrl = imageUrl,
                        driverName = driverName,
                        size = 38.dp
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = teamColor.copy(alpha = 0.10f)
                ) {
                    Text(
                        text = gapText,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = teamColor,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Zone 2: Driver Identity (Dot + Driver Name + Team)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(teamColor)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = driverName,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    fontFamily = FontFamily.Monospace,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1
                )
            }

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = teamName,
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                fontFamily = FontFamily.Monospace,
                maxLines = 1,
                modifier = Modifier.padding(start = 14.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            HorizontalDivider(
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
                thickness = 1.dp
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Zone 4: Points Row (TOTAL - 346 PTS)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "TOTAL",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                    fontFamily = FontFamily.Monospace
                )

                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = points.toString(),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        fontFamily = FontFamily.Monospace,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = "PTS",
                        fontSize = 9.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier.padding(bottom = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Progress bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(RoundedCornerShape(50))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(indexPercentage)
                        .height(4.dp)
                        .clip(RoundedCornerShape(50))
                        .background(teamColor)
                )
            }
        }
    }
}

@Composable
fun LeaderCard(
    modifier: Modifier = Modifier,
    teamName: String,
    logoUrl: String? = null,
    driver1: String,
    driver2: String,
    points: Int,
    gapText: String,
    teamColor: Color = MaterialTheme.colorScheme.primary,
    onClick: () -> Unit = {}
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.5.dp, teamColor.copy(alpha = 0.5f)),
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            // --- TOP HALF: Team Info ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                // Left: Badges
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // P1 Box
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.tertiary)
                            .padding(4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        TeamLogoCircle(
                            logoUrl = logoUrl,
                            teamName = teamName,
                            size = 42.dp
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Middle: Names
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = teamName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        color = MaterialTheme.colorScheme.onSurface,
                        lineHeight = 18.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = driver1,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontFamily = FontFamily.Monospace
                        )
                        if (driver2.isNotBlank()) {
                            Box(modifier = Modifier.size(3.dp).clip(CircleShape).background(MaterialTheme.colorScheme.onSurfaceVariant))
                            Text(
                                text = driver2,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }

                // Right: Points & Gap
                Column(horizontalAlignment = Alignment.End) {
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = points.toString(),
                            fontSize = 28.sp,
                            fontWeight = FontWeight.ExtraBold,
                            fontFamily = FontFamily.Monospace,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "PTS",
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // --- BOTTOM HALF: Telemetry Bar ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "CHAMPIONSHIP LEADER",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = teamColor,
                    fontFamily = FontFamily.Monospace
                )
                Text(
                    text = gapText,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = teamColor,
                    fontFamily = FontFamily.Monospace
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Progress bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(50))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(1.0f)
                        .height(6.dp)
                        .clip(RoundedCornerShape(50))
                        .background(teamColor)
                )
            }
        }
    }
}

@Composable
fun ContenderCard(
    modifier: Modifier = Modifier,
    position: Int,
    teamName: String,
    logoUrl: String? = null,
    driver1: String,
    driver2: String,
    points: Int,
    gapText: String,
    indexPercentage: Float,
    teamColor: Color,
    badgeColor: Color,
    onClick: () -> Unit = {}
) {
    val badgeBg = if (position == 2) {
        MaterialTheme.colorScheme.secondary
    } else {
        MaterialTheme.colorScheme.secondaryContainer
    }


    Surface(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, teamColor.copy(alpha = 0.5f)),
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            // Zone 1: Header (Badge & Gap Pill)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(badgeBg)
                        .padding( 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                   TeamLogoCircle(
                       logoUrl = logoUrl,
                       teamName = teamName,
                       size = 38.dp
                   )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = teamColor.copy(alpha = 0.10f)
                ) {
                    Text(
                        text = gapText,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = teamColor,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Zone 2: Team Identity (Dot + Name + Drivers)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(teamColor)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = teamName,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    fontFamily = FontFamily.Monospace,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1
                )
            }

            Spacer(modifier = Modifier.height(2.dp))

            if (driver1.isNotBlank()) {
                Text(
                    text = if (driver2.isNotBlank()) "$driver1\n$driver2" else driver1,
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                    fontFamily = FontFamily.Monospace,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(start = 14.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            HorizontalDivider(
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
                thickness = 1.dp
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Zone 4: Points Row (TOTAL - 346 PTS)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "TOTAL",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                    fontFamily = FontFamily.Monospace
                )

                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = points.toString(),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        fontFamily = FontFamily.Monospace,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = "PTS",
                        fontSize = 9.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier.padding(bottom = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Progress bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(RoundedCornerShape(50))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(indexPercentage)
                        .height(4.dp)
                        .clip(RoundedCornerShape(50))
                        .background(teamColor)
                )
            }
        }
    }
}

private val mockConstructors = listOf(
    ConstructorStandingsEntity(
        name = "Mercedes-AMG",
        position = 1,
        points = 468.0,
        nationality = "German",
        constructorLogo = null,
        drivers = "K. Antonelli, G. Russell"
    ),
    ConstructorStandingsEntity(
        name = "Scuderia Ferrari",
        position = 2,
        points = 346.0,
        nationality = "Italian",
        constructorLogo = null,
        drivers = "L. Hamilton, C. Leclerc"
    ),
    ConstructorStandingsEntity(
        name = "McLaren Formula 1",
        position = 3,
        points = 287.0,
        nationality = "British",
        constructorLogo = null,
        drivers = "L. Norris, O. Piastri"
    )
)

private val mockDrivers = listOf(
    DriverStandingsEntity(
        driverId = "verstappen",
        position = 1,
        points = 312.0,
        name = "Max Verstappen",
        nationality = "Dutch",
        driverImage = null,
        url = "",
        team = "Red Bull Racing"
    ),
    DriverStandingsEntity(
        driverId = "norris",
        position = 2,
        points = 245.0,
        name = "Lando Norris",
        nationality = "British",
        driverImage = null,
        url = "",
        team = "McLaren"
    ),
    DriverStandingsEntity(
        driverId = "leclerc",
        position = 3,
        points = 210.0,
        name = "Charles Leclerc",
        nationality = "Monegasque",
        driverImage = null,
        url = "",
        team = "Scuderia Ferrari"
    )
)

@Preview(name = "Constructor Standings Light", showBackground = true)
@Composable
fun ConstructorStandingsPreview() {
    F1CompTheme(darkTheme = false) {
        Box(modifier = Modifier.padding(16.dp)) {
            Standing3Card(constructorStandings = mockConstructors)
        }
    }
}

@Preview(name = "Constructor Standings Dark", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun ConstructorStandingsDarkPreview() {
    F1CompTheme(darkTheme = true) {
        Box(modifier = Modifier.padding(16.dp)) {
            Standing3Card(constructorStandings = mockConstructors)
        }
    }
}

@Preview(name = "Driver Standings Light", showBackground = true)
@Composable
fun DriverStandingsPreview() {
    F1CompTheme(darkTheme = false) {
        Box(modifier = Modifier.padding(16.dp)) {
            Driver3Card(driverStandings = mockDrivers)
        }
    }
}

@Preview(name = "Driver Standings Dark", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun DriverStandingsDarkPreview() {
    F1CompTheme(darkTheme = true) {
        Box(modifier = Modifier.padding(16.dp)) {
            Driver3Card(driverStandings = mockDrivers)
        }
    }
}
