package com.jenil.f1comp.ui.telemetry.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Equalizer
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jenil.f1comp.data.model.AppDataLine
import com.jenil.f1comp.data.model.DriverInfo
import com.jenil.f1comp.data.model.TimingLine
import com.jenil.f1comp.ui.theme.F1CompTheme
import com.jenil.f1comp.ui.theme.PodiumGold

@Composable
fun TimingTowerCard(
    modifier: Modifier = Modifier,
    timingLines: Map<String, TimingLine>? = null,
    driverList: Map<String, DriverInfo>? = null,
    appDataLines: Map<String, AppDataLine>? = null,
    sessionType: String = "Race",
    showGaps: Boolean = true,
    showSectors: Boolean = true,
    showTyres: Boolean = true
) {
    val sortedDrivers = remember(timingLines) {
        timingLines?.values?.sortedBy { it.position?.toIntOrNull() ?: 999 } ?: emptyList()
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
        ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header Row: Title & Session Type
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.Equalizer,
                        contentDescription = "Timing Tower",
                        tint = PodiumGold,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "TIMING TOWER",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = PodiumGold,
                            fontSize = 14.sp,
                            letterSpacing = 1.sp
                        )
                    )
                }

                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                    shape = RoundedCornerShape(6.dp),
                    border = BorderStroke(
                        1.dp,
                        MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
                    )
                ) {
                    Text(
                        text = sessionType.uppercase(),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                            fontSize = 10.sp,
                            letterSpacing = 0.8.sp
                        )
                    )
                }
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 12.dp),
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.25f)
            )

            if (sortedDrivers.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "NO TIMING DATA AVAILABLE",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                            fontSize = 12.sp,
                            letterSpacing = 0.8.sp
                        )
                    )
                }
            } else {
                // Table Header Titles Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 6.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "POS",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontFamily = FontFamily.Monospace,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.width(30.dp)
                    )
                    Text(
                        text = "DRIVER",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontFamily = FontFamily.Monospace,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.weight(1f)
                    )
                    if (showGaps) Text(
                        text = "GAP",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontFamily = FontFamily.Monospace,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.width(54.dp)
                    )
                    if (showGaps && showSectors) Spacer(modifier = Modifier.width(6.dp))
                    if (showSectors) Text(
                        text = "SECTORS",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontFamily = FontFamily.Monospace,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.width(148.dp)
                    )
                    if (showSectors && showTyres) Spacer(modifier = Modifier.width(6.dp))
                    if (showTyres) Text(
                        text = "TYRE",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontFamily = FontFamily.Monospace,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.width(32.dp)
                    )
                }

                Spacer(modifier = Modifier.padding(bottom = 6.dp))

                // Drivers Column
                Column(
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    sortedDrivers.forEach { timingLine ->
                        val racingNumber = timingLine.racingNumber ?: return@forEach
                        val driverInfo = driverList?.get(racingNumber)
                        val appData = appDataLines?.get(racingNumber)

                        DriverRow(
                            timingLine = timingLine,
                            driverInfo = driverInfo,
                            appData = appData,
                            sessionType = sessionType,
                            showGaps = showGaps,
                            showSectors = showSectors,
                            showTyres = showTyres
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun TimingTowerCardPreview() {
    F1CompTheme {
        Column(modifier = Modifier.padding(8.dp)) {
            TimingTowerCard()
        }
    }
}
