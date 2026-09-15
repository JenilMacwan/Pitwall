package com.jenil.f1comp.ui.standings.components

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jenil.f1comp.ui.home.components.DriverProfileCircle
import com.jenil.f1comp.ui.home.components.StandingPositionCircle
import com.jenil.f1comp.ui.home.components.TeamLogoCircle
import com.jenil.f1comp.util.TeamUtils

@Composable
fun StandingRowItem(
    modifier: Modifier = Modifier,
    position: Int,
    name: String,
    team: String?,
    points: Int,
    imageUrl: String? = null,
    isConstructor: Boolean = false,
    onClick: () -> Unit = {},
) {
    val teamNameForColor = if (isConstructor) name else team
    val teamColor = TeamUtils.getTeamColor(teamNameForColor)
    val driverCode = TeamUtils.getDriverCode(name)
    val driverCodeColor = TeamUtils.getDriverCodeColor(name)

    val driverBorderColor = when (position) {
        1 -> MaterialTheme.colorScheme.tertiary
        2 -> MaterialTheme.colorScheme.secondary
        3 -> MaterialTheme.colorScheme.secondaryContainer
        else -> MaterialTheme.colorScheme.surfaceVariant
    }
    val isLeader = position == 1
    val leaderModifier = if (isLeader && !isConstructor) {
        Modifier
            .border(
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.tertiary.copy(alpha = 0.5f)),
                shape = RoundedCornerShape(12.dp)
            )
            .background(
                color = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.1f),
                shape = RoundedCornerShape(12.dp)
            )
    } else {
        Modifier
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .then(leaderModifier)
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(vertical = 12.dp, horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        StandingPositionCircle(position)

        Spacer(modifier = Modifier.width(12.dp))

        if (isConstructor) {
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(teamColor)
                    .padding(2.dp),
                contentAlignment = Alignment.Center
            ) {
                TeamLogoCircle(
                    logoUrl = imageUrl,
                    teamName = name,
                    size = 32.dp
                )
            }
        } else {
            Box {
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(driverBorderColor)
                        .padding(2.dp),
                    contentAlignment = Alignment.Center
                ) {
                    DriverProfileCircle(
                        imageUrl = imageUrl,
                        driverName = name,
                        size = 40.dp
                    )
                }

                Surface(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(start = 20.dp, top = 20.dp),
                    shape = CircleShape,
                    color = teamColor,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.surface)
                ) {
                    Text(
                        text = driverCode,
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = driverCodeColor,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                    )
                }
            }

            Log.d("StandingRowItem", "Image URL: $imageUrl , Driver Name: $name")
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Middle Column
        Column(modifier = Modifier.weight(1f)) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = name,
                    style = MaterialTheme.typography.titleMedium,
                    fontFamily = FontFamily.Monospace,
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (isLeader && !isConstructor) {
                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = MaterialTheme.colorScheme.tertiary,
                        border = BorderStroke(
                            1.dp,
                            MaterialTheme.colorScheme.tertiary.copy(alpha = 0.5f)
                        )
                    ) {
                        Text(
                            text = "LEADER",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onTertiary,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                        )
                    }
                }
            }
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                if( !isConstructor ) {
                    Box(
                        modifier = Modifier
                            .size(5.dp)
                            .clip(CircleShape)
                            .background(teamColor)
                            .padding(2.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(
                    text = team ?: "N/A",
                    style = MaterialTheme.typography.bodySmall,
                    fontFamily = FontFamily.Monospace,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Right Column (Points)
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = points.toString(),
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