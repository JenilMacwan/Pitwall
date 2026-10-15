package com.jenil.f1comp.ui.telemetry.component

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jenil.f1comp.data.model.AppDataLine
import com.jenil.f1comp.data.model.DriverInfo
import com.jenil.f1comp.data.model.TimingLine
import com.jenil.f1comp.ui.theme.F1Red
import com.jenil.f1comp.ui.theme.FatestLap
import com.jenil.f1comp.ui.theme.PodiumGold
import com.jenil.f1comp.util.extractGap
import com.jenil.f1comp.util.parseTeamColor

@Composable
fun DriverRow(
    timingLine: TimingLine,
    driverInfo: DriverInfo?,
    appData: AppDataLine?,
    sessionType: String,
    showGaps: Boolean = true,
    showSectors: Boolean = true,
    showTyres: Boolean = true,
    modifier: Modifier = Modifier
) {
    var expanded by remember(timingLine.racingNumber) { mutableStateOf(false) }
    val isOut = timingLine.retired == true || timingLine.stopped == true || timingLine.knockedOut == true
    val rowAlpha = if (isOut) 0.5f else 1f

    val isPracticeOrQuali = sessionType.contains("Practice", ignoreCase = true) || sessionType.contains("Qualifying", ignoreCase = true)
    val isQuali = sessionType.contains("Qualifying", ignoreCase = true)

    val isLeader = timingLine.position == "1"
    val gap = if (isPracticeOrQuali) {
        timingLine.bestLapTime?.value ?: timingLine.lastLapTime?.value ?: "-"
    } else {
        if (isLeader) "LEADER" else extractGap(timingLine.gapToLeader)
    }
    
    val interval = if (isPracticeOrQuali) {
        if (isLeader) "" else timingLine.timeDiffToFastest ?: extractGap(timingLine.intervalToPositionAhead)
    } else {
        extractGap(timingLine.intervalToPositionAhead)
    }

    val currentStint = appData?.stints?.lastOrNull()
    val tyreCompound = currentStint?.compound?.uppercase() ?: "UNKNOWN"
    val tyreColor = when {
        tyreCompound.contains("SOFT") -> Color(0xFFFF3333)
        tyreCompound.contains("MEDIUM") -> Color(0xFFFFD600)
        tyreCompound.contains("HARD") -> Color.White
        tyreCompound.contains("INTER") -> Color(0xFF00E676)
        tyreCompound.contains("WET") -> Color(0xFF00B0FF)
        else -> Color.DarkGray
    }
    val tyreLetter = when {
        tyreCompound.contains("SOFT") -> "S"
        tyreCompound.contains("MEDIUM") -> "M"
        tyreCompound.contains("HARD") -> "H"
        tyreCompound.contains("INTER") -> "I"
        tyreCompound.contains("WET") -> "W"
        else -> tyreCompound.firstOrNull()?.toString() ?: "-"
    }

    val lastName = driverInfo?.lastName ?: ""
    val tla = if (lastName.isNotBlank()) lastName.take(3).uppercase() else "DRV"

    val teamColor = parseTeamColor(driverInfo?.teamColour)

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .alpha(rowAlpha)
            .clickable { expanded = !expanded }
            .animateContentSize(),
        shape = RoundedCornerShape(10.dp),
        color = if (expanded) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.62f)
        else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.28f),
        border = BorderStroke(
            1.dp,
            if (expanded) teamColor.copy(alpha = 0.45f)
            else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.22f)
        )
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
          Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(58.dp)
                .padding(end = 6.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            // 1. Team Color Indicator Bar
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .fillMaxHeight()
                    .background(teamColor)
            )

            Spacer(modifier = Modifier.width(6.dp))

            // 2. Position Number
            Text(
                text = timingLine.position ?: "-",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = if (isLeader) PodiumGold else MaterialTheme.colorScheme.onSurface,
                    fontSize = 14.sp
                ),
                modifier = Modifier.width(30.dp),
                maxLines = 1,
                softWrap = false
            )

            // 3. Driver Info Column
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = tla,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 13.sp
                        ),
                        maxLines = 1,
                        softWrap = false
                    )

                    if (isQuali && isLeader) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Surface(
                            color = PodiumGold.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(4.dp),
                            border = BorderStroke(1.dp, PodiumGold.copy(alpha = 0.5f))
                        ) {
                            Text(
                                text = "POLE",
                                color = PodiumGold,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 8.sp
                                ),
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                            )
                        }
                    }

                    if (timingLine.inPit == true) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Surface(
                            color = Color(0xFF00B0FF).copy(alpha = 0.15f),
                            shape = RoundedCornerShape(4.dp),
                            border = BorderStroke(1.dp, Color(0xFF00B0FF).copy(alpha = 0.5f))
                        ) {
                            Text(
                                text = "IN PIT",
                                color = Color(0xFF00B0FF),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 8.sp
                                ),
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
                Text(
                    text = driverInfo?.broadcastName ?: lastName,
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 10.sp
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            if (isOut) {
                // Retired / Stopped Pill
                Surface(
                    color = F1Red.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(6.dp),
                    border = BorderStroke(1.dp, F1Red.copy(alpha = 0.4f)),
                    modifier = Modifier.padding(end = 6.dp)
                ) {
                    Text(
                        text = when {
                            timingLine.knockedOut == true -> "OUT"
                            timingLine.stopped == true -> "STOPPED"
                            else -> "RETIRED"
                        },
                        color = F1Red,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp,
                            fontSize = 9.5.sp
                        ),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        maxLines = 1,
                        softWrap = false
                    )
                }
            } else {
                // 4. Gap & Interval Column
                if (showGaps) {
                Column(
                    modifier = Modifier.width(54.dp),
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = gap,
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = if (isLeader) Color(0xFF00E676) else MaterialTheme.colorScheme.onSurface,
                            fontSize = 11.sp
                        ),
                        maxLines = 1,
                        softWrap = false
                    )
                    if (!isLeader && interval.isNotBlank()) {
                        Text(
                            text = interval,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontFamily = FontFamily.Monospace,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 9.5.sp
                            ),
                            maxLines = 1,
                            softWrap = false
                        )
                    }
                }
                }

                if (showGaps && showSectors) Spacer(modifier = Modifier.width(6.dp))

                // 5. Sectors (S1, S2, S3) - Widened boxes (48.dp) with single line constraint to prevent wrapping
                if (showSectors) {
                Row(
                    modifier = Modifier.width(148.dp),
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    for (i in 0..2) {
                        val sector = timingLine.sectors?.getOrNull(i)
                        val sectorBgColor = when {
                            sector?.overallFastest == true -> FatestLap // Purple 0xFFB15EFF
                            sector?.personalFastest == true -> Color(0xFF00E676) // Green
                            else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f)
                        }
                        val sectorTextColor = when {
                            sector?.overallFastest == true -> Color.White
                            sector?.personalFastest == true -> Color.Black
                            else -> MaterialTheme.colorScheme.onSurface
                        }

                        Surface(
                            color = sectorBgColor,
                            shape = RoundedCornerShape(4.dp),
                            modifier = Modifier
                                .width(48.dp)
                                .height(22.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = sector?.value?.trim() ?: "-",
                                    color = sectorTextColor,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 9.5.sp
                                    ),
                                    maxLines = 1,
                                    softWrap = false
                                )
                            }
                        }
                    }
                }
                }

                if ((showGaps || showSectors) && showTyres) Spacer(modifier = Modifier.width(6.dp))

                // 6. Tyre Compound & Lap Age
                if (showTyres) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.width(32.dp)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = tyreColor,
                        modifier = Modifier.size(18.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = tyreLetter,
                                color = if (tyreCompound.contains("HARD")) Color.Black else Color.White,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Black,
                                    fontSize = 10.sp
                                )
                            )
                        }
                    }

                    val lapAge = currentStint?.totalLaps ?: currentStint?.newLaps
                    if (lapAge != null) {
                        Text(
                            text = "L$lapAge",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 9.sp,
                                fontFamily = FontFamily.Monospace
                            ),
                            maxLines = 1,
                            softWrap = false
                        )
                    }
                }
                }
            }
          }
          if (expanded) {
              val sectors = timingLine.sectors.orEmpty()
              val currentLap = timingLine.numberOfLaps?.plus(1)
              Column(
                  modifier = Modifier.fillMaxWidth().padding(start = 16.dp, end = 12.dp, bottom = 12.dp),
                  verticalArrangement = Arrangement.spacedBy(9.dp)
              ) {
                  HorizontalDivider(color = teamColor.copy(alpha = 0.45f))
                  LapDetailLine("CURRENT LAP${currentLap?.let { " · $it" } ?: ""}", sectors.takeIf { it.isNotEmpty() }?.mapIndexed { i, s -> "S${i + 1} ${s.value ?: "--"}" }?.joinToString("   ") ?: "Sector timing unavailable")
                  LapDetailLine("FASTEST LAP", when {
                      timingLine.lastLapTime?.overallFastest == true -> "${timingLine.lastLapTime.value ?: "--"} · overall fastest"
                      timingLine.lastLapTime?.personalFastest == true -> "${timingLine.lastLapTime.value ?: "--"} · personal best"
                      !timingLine.timeDiffToFastest.isNullOrBlank() -> "${timingLine.timeDiffToFastest} to fastest"
                      else -> "Fastest lap unavailable"
                  })
                  LapDetailLine("PREVIOUS LAP", timingLine.lastLapTime?.value ?: "Lap time unavailable")
              }
          }
        }
    }
}

@Composable
private fun LapDetailLine(label: String, value: String) {
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(label, style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 9.sp))
        Text(value, style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Medium), maxLines = 2, softWrap = true)
    }
}
