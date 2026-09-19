package com.jenil.f1comp.ui.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material.icons.rounded.Timer
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jenil.f1comp.data.local.entity.RaceResultEntity
import com.jenil.f1comp.ui.results.screen.ResultSessionType
import com.jenil.f1comp.ui.theme.PodiumBronze
import com.jenil.f1comp.ui.theme.PodiumGold
import com.jenil.f1comp.ui.theme.PodiumSilver
import com.jenil.f1comp.util.TeamUtils

data class PodiumDriverData(
    val position: String,
    val driver: String,
    val driverImage: String?,
    val constructor: String,
    val detail: String,
)

@Composable
fun PodiumHeroCard(
    p1: PodiumDriverData?,
    p2: PodiumDriverData?,
    p3: PodiumDriverData?,
    raceResult: List<RaceResultEntity>,
    selectedSession: ResultSessionType,
    onDriverClick: (String) -> Unit,
    onConstructorClick: (String) -> Unit,
    onResultClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    if (p1 == null) return

    val isQualifyingSession = selectedSession == ResultSessionType.QUALIFYING || selectedSession == ResultSessionType.SPRINT_QUALIFYING

    val raceName = raceResult.firstOrNull()?.raceName ?: "Race Result"

    val headerTitle = when (selectedSession) {
        ResultSessionType.RACE -> "$raceName Result"
        ResultSessionType.SPRINT -> "$raceName SPRINT Result"
        ResultSessionType.QUALIFYING -> "$raceName QUALIFIERS"
        ResultSessionType.SPRINT_QUALIFYING -> "$raceName SPRINT QUALIFIERS"
    }

    val headerIcon = if (isQualifyingSession) Icons.Rounded.Timer else Icons.Rounded.EmojiEvents

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor =  MaterialTheme.colorScheme.surface
        ),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = Brush.linearGradient(
                colors = listOf(
                    PodiumGold.copy(alpha = 0.5f),
                    MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f),
                    PodiumSilver.copy(alpha = 0.4f)
                )
            )
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header Title
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = headerIcon,
                    contentDescription = headerTitle,
                    tint = PodiumGold,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = headerTitle,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    letterSpacing = 1.2.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 3 Podium Columns: P2 (Left), P1 (Center elevated), P3 (Right)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.Bottom
            ) {
                // P2 Column
                if (p2 != null) {
                    PodiumColumnItem(
                        data = p2,
                        badgeColor = PodiumSilver,
                        badgeText = "2",
                        avatarSize = 52.dp,
                        isWinner = false,
                        onDriverClick = onDriverClick,
                        onConstructorClick = onConstructorClick,
                        modifier = Modifier.weight(1f)
                    )
                } else {
                    Spacer(modifier = Modifier.weight(1f))
                }

                // P1 Column (Center, Elevated)
                PodiumColumnItem(
                    data = p1,
                    badgeColor = PodiumGold,
                    badgeText = "1",
                    avatarSize = 68.dp,
                    isWinner = true,
                    onDriverClick = onDriverClick,
                    onConstructorClick = onConstructorClick,
                    modifier = Modifier
                        .weight(1.1f)
                        .offset(y = (-8).dp)
                )

                // P3 Column
                if (p3 != null) {
                    PodiumColumnItem(
                        data = p3,
                        badgeColor = PodiumBronze,
                        badgeText = "3",
                        avatarSize = 52.dp,
                        isWinner = false,
                        onDriverClick = onDriverClick,
                        onConstructorClick = onConstructorClick,
                        modifier = Modifier.weight(1f)
                    )
                } else {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "View Full Race Result →",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable {
                    onResultClick()
                }
            )
        }
    }
}

@Composable
private fun PodiumColumnItem(
    data: PodiumDriverData,
    badgeColor: Color,
    badgeText: String,
    avatarSize: Dp,
    isWinner: Boolean,
    onDriverClick: (String) -> Unit,
    onConstructorClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val teamColor = TeamUtils.getTeamColor(data.constructor)

    Column(
        modifier = modifier
            .clickable { onDriverClick(data.driver) }
            .padding(vertical = 4.dp)
            .background(MaterialTheme.colorScheme.surface),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            contentAlignment = Alignment.BottomEnd
        ) {
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .border(
                        width = if (isWinner) 2.5.dp else 1.5.dp,
                        color = teamColor,
                        shape = CircleShape
                    )
            ) {
                DriverProfileCircle(
                    imageUrl = data.driverImage,
                    driverName = data.driver,
                    size = avatarSize
                )
            }

            // Position Badge Pill
            Box(
                modifier = Modifier
                    .offset(x = 4.dp, y = 4.dp)
                    .size(22.dp)
                    .clip(CircleShape)
                    .background(badgeColor)
                    .border(1.dp, MaterialTheme.colorScheme.surface, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = badgeText,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                    color = Color.Black
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Driver Name
        Text(
            text = data.driver,
            style = if (isWinner) MaterialTheme.typography.titleMedium else MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            color = MaterialTheme.colorScheme.onSurface
        )

        // Team Name Pill
        Box(
            modifier = Modifier
                .padding(top = 2.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(teamColor.copy(alpha = 0.18f))
                .clickable { onConstructorClick(data.constructor) }
                .padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
            Text(
                text = data.constructor,
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.SemiBold,
                color = teamColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Detail (e.g. Points or Time Gap)
        Text(
            text = data.detail,
            style = MaterialTheme.typography.labelSmall,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            color = if (isWinner) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
