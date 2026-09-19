package com.jenil.f1comp.ui.schedule.components

import androidx.compose.animation.animateContentSize
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLocale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.jenil.f1comp.data.local.entity.CircuitInfoEntity
import com.jenil.f1comp.data.local.entity.ScheduleEntity
import com.jenil.f1comp.ui.theme.F1Red
import com.jenil.f1comp.util.FlagImage
import com.jenil.f1comp.util.ProfileUtils
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Composable
fun ScheduleCard(
    modifier: Modifier = Modifier,
    schedule: ScheduleEntity,
    circuit: CircuitInfoEntity? = null,
    isNextRace: Boolean = false,
    onResultsClick: () -> Unit = {}
) {
    var isExpanded by remember { mutableStateOf(false) }
    val isCompleted = schedule.isCompleted

    val parsedDate = LocalDate.parse(schedule.grandPrix)
    val formatter = DateTimeFormatter.ofPattern("EEE, MMM dd, yyyy", LocalLocale.current.platformLocale)
    val date = parsedDate.format(formatter)

    val parsedTime = LocalTime.parse(schedule.time.removeSuffix("Z"))
    val timeFormatter = DateTimeFormatter.ofPattern("hh:mm a", LocalLocale.current.platformLocale)
    val time = parsedTime.format(timeFormatter)
    val laps = circuit?.laps?.toString()?: "N/A"
    val type = circuit?.type?.let { "$it CIRCUIT" } ?: "N/A"

    Card(
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize()
            .then(
                if (!isCompleted) Modifier.clickable { isExpanded = !isExpanded } else Modifier
            )
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(
            1.dp,
            if (isNextRace) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        Column {
            if (isNextRace) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.primary)
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "NEXT RACE",
                        fontFamily = FontFamily.Monospace,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }

            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.weight(1f)) {

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            FlagImage(
                                flagUrl = ProfileUtils.getFlagUrl(emoji = schedule.flag),
                                width = 22.dp,
                                height = 15.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = schedule.raceName,
                                fontFamily = FontFamily.Monospace,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.LocationOn,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = "${schedule.circuitName} · ${schedule.circuitLocation}",
                                fontFamily = FontFamily.Monospace,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircuitTypeBadge(circuitType = type)

                        if (isCompleted) {
                            StatusPill(
                                text = "Done",
                                backgroundColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        } else if (schedule.sprint != null) {
                            StatusPill(
                                text = "Sprint",
                                backgroundColor = MaterialTheme.colorScheme.tertiary,
                                textColor = MaterialTheme.colorScheme.onTertiary
                            )
                        }
                    }
                }

                if (isCompleted) {
                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        thickness = 1.dp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "ROUND",
                                fontFamily = FontFamily.Monospace,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = schedule.round,
                                fontFamily = FontFamily.Monospace,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Column {
                            Text(
                                text = "RACE DATE",
                                fontFamily = FontFamily.Monospace,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = date,
                                fontFamily = FontFamily.Monospace,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        OutlinedButton(
                            onClick = onResultsClick,
                            shape = RoundedCornerShape(50),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                        ) {
                            Text(
                                text = "Results →",
                                fontFamily = FontFamily.Monospace,
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                } else {
                    Spacer(modifier = Modifier.height(8.dp))
                    // Basic Info
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp, bottom = 4.dp)
                            .background(
                                shape = RoundedCornerShape(14.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant
                            )
                            .padding(horizontal = 12.dp, vertical = 12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            // LAPS
                            Column(
                                modifier = Modifier.weight(1f),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "LAPS",
                                    fontFamily = FontFamily.Monospace,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = laps,
                                    fontFamily = FontFamily.Monospace,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }

                            // Vertical Divider
                            Box(
                                modifier = Modifier
                                    .width(1.dp)
                                    .height(28.dp)
                                    .background(MaterialTheme.colorScheme.outline)
                            )

                            // CIRCUIT LENGTH
                            Column(
                                modifier = Modifier.weight(1.3f),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "CIRCUIT LENGTH",
                                    fontFamily = FontFamily.Monospace,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = circuit?.length ?: "N/A",
                                    fontFamily = FontFamily.Monospace,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }

                            // Vertical Divider
                            Box(
                                modifier = Modifier
                                    .width(1.dp)
                                    .height(28.dp)
                                    .background(MaterialTheme.colorScheme.outline)
                            )

                            // LAYOUT
                            Column(
                                modifier = Modifier.weight(1.1f),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "LAYOUT",
                                    fontFamily = FontFamily.Monospace,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                if (circuit?.circuitLayout != null) {
                                    AsyncImage(
                                        model = circuit.circuitLayout,
                                        contentDescription = "Circuit Layout",
                                        modifier = Modifier.size(35.dp),
                                        alignment = Alignment.Center
                                    )
                                } else {
                                    Text(
                                        text = "N/A",
                                        fontFamily = FontFamily.Monospace,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }


                    if (isExpanded) {
                        Spacer(modifier = Modifier.height(16.dp))
                        HorizontalDivider(
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            thickness = 1.dp
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 300.dp),
                            userScrollEnabled = false
                        ) {

                            schedule.firstPractice?.let {
                                item { SessionCard("Practice 1", it) }
                            }

                            schedule.secondPractice?.let {
                                item { SessionCard("Practice 2", it) }
                            }

                            schedule.thirdPractice?.let {
                                item { SessionCard("Practice 3", it) }
                            }

                            schedule.sprintQualifying?.let {
                                item { SessionCard("Sprint Quali", it) }
                            }

                            schedule.sprint?.let {
                                item { SessionCard("Sprint", it) }
                            }

                            schedule.qualifying?.let {
                                item { SessionCard("Qualifying", it) }
                            }
                        }
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp, bottom = 4.dp)
                                .background(
                                    shape = RoundedCornerShape(14.dp),
                                    color = Color.Black
                                )
                                .padding(horizontal = 12.dp, vertical = 10.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Red Squircle with Trophy Icon
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .background(
                                            shape = RoundedCornerShape(10.dp),
                                            color = F1Red
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.EmojiEvents,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(10.dp))

                                // Title + Date/Time
                                Column(
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "GRAND PRIX",
                                            fontFamily = FontFamily.Monospace,
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold,
                                            style = MaterialTheme.typography.titleSmall
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(2.dp))

                                    Text(
                                        text = "$date • $time",
                                        fontFamily = FontFamily.Monospace,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color(0xFFAAAAAA)
                                    )
                                }
                            }
                        }
                    } else {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Tap for session times",
                            fontFamily = FontFamily.Monospace,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CircuitTypeBadge(
    modifier: Modifier = Modifier,
    circuitType: String = "Street Circuit"
) {
    val lines = if (circuitType.equals("Street Circuit", ignoreCase = true)) {
        listOf("STREET", "CIRCUIT")
    } else {
        circuitType.uppercase().split(" ")
    }

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.tertiaryContainer,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            lines.forEach { line ->
                Text(
                    text = line,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 9.sp,
                    lineHeight = 10.sp,
                    color = MaterialTheme.colorScheme.onTertiaryContainer
                )
            }
        }
    }
}

