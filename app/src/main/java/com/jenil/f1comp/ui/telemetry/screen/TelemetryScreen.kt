package com.jenil.f1comp.ui.telemetry.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.outlined.CloudOff
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.jenil.f1comp.ui.F1ScreenPadding
import com.jenil.f1comp.ui.telemetry.component.CompactTelemetryHeader
import com.jenil.f1comp.ui.telemetry.component.RaceConditionBanner
import com.jenil.f1comp.ui.telemetry.component.RaceControlCard
import com.jenil.f1comp.ui.telemetry.component.TelemetryHeader
import com.jenil.f1comp.ui.telemetry.component.TimingTowerCard
import com.jenil.f1comp.ui.theme.F1Red
import com.jenil.f1comp.viewmodel.LiveTelemetryViewModel
import com.jenil.f1comp.viewmodel.SettingsViewModel
import com.jenil.f1comp.viewmodel.TelemetryUiState

@Composable
fun TelemetryScreen(
    modifier: Modifier = Modifier,
    title: String = "Live Telemetry",
    navController: NavController,
    telemetryViewModel: LiveTelemetryViewModel = hiltViewModel(),
    settingsViewModel: SettingsViewModel = hiltViewModel()
) {
    val telemetryData by telemetryViewModel.telemetryData.collectAsState()
    val telemetryUiState by telemetryViewModel.telemetryUiState.collectAsState()
    val rawHeaderStyle by settingsViewModel.telemetryHeaderStyle.collectAsStateWithLifecycle()
    val headerStyle = rawHeaderStyle ?: "DEFAULT"
    val showTowerGaps by settingsViewModel.telemetryTowerShowGaps.collectAsStateWithLifecycle()
    val showTowerSectors by settingsViewModel.telemetryTowerShowSectors.collectAsStateWithLifecycle()
    val showTowerTyres by settingsViewModel.telemetryTowerShowTyres.collectAsStateWithLifecycle()

    val scrollState = rememberScrollState()

    val currentData = telemetryData
    val isLiveSessionActive = remember(currentData) {
        currentData != null && (
            (!currentData.sessionInfo?.name.isNullOrBlank() && currentData.sessionInfo.name != "UNKNOWN") ||
            !currentData.timingData?.lines.isNullOrEmpty() ||
            (currentData.weatherData != null && !currentData.weatherData.airTemp.isNullOrBlank()) ||
            currentData.raceControlMessages.isNotEmpty()
        )
    }

    val currentLapString = remember(currentData) {
        val lapFromTiming = currentData?.timingData?.lines?.values?.firstOrNull { it.numberOfLaps != null }?.numberOfLaps
        lapFromTiming?.toString() ?: "--"
    }

    val bannerColor = MaterialTheme.colorScheme.primary

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(top = F1ScreenPadding.topPadding())
            .navigationBarsPadding()
    ) {
        // Top Navigation Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(start = 8.dp)
            )
            Spacer(modifier = Modifier.weight(1f))
            IconButton(onClick = { navController.navigate("telemetry_settings") }) {
                Icon(
                    imageVector = Icons.Outlined.Settings,
                    contentDescription = "Telemetry Settings",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // State handling container
        when {
            // Loading state with no existing data
            (telemetryUiState is TelemetryUiState.Loading || telemetryUiState is TelemetryUiState.Idle) && currentData == null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator(
                            color = F1Red,
                            modifier = Modifier.size(44.dp),
                            strokeWidth = 3.dp
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "CONNECTING TO PITWALL STREAM...",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                letterSpacing = 1.sp
                            )
                        )
                    }
                }
            }

            // Error state with no existing data
            telemetryUiState is TelemetryUiState.Error && currentData == null -> {
                val errorMessage = (telemetryUiState as? TelemetryUiState.Error)?.message
                    ?: "Unable to establish live telemetry connection."
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, F1Red.copy(alpha = 0.4f)),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.CloudOff,
                                contentDescription = "Connection Error",
                                tint = F1Red,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "STREAM CONNECTION ERROR",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    color = F1Red,
                                    letterSpacing = 1.sp
                                )
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = errorMessage,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontFamily = FontFamily.Monospace,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 13.sp
                                )
                            )
                            Spacer(modifier = Modifier.height(20.dp))
                            Button(
                                onClick = { telemetryViewModel.retryTelemetry() },
                                colors = ButtonDefaults.buttonColors(containerColor = F1Red),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Refresh,
                                    contentDescription = "Retry",
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "RECONNECT FEED",
                                    style = MaterialTheme.typography.labelLarge.copy(
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 1.sp
                                    )
                                )
                            }
                        }
                    }
                }
            }

            // Display telemetry cards (with banner if stream interrupted or no live session active)
            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                ) {
                    // Banner 1: Stream interrupted error banner
                    if (telemetryUiState is TelemetryUiState.Error) {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 4.dp),
                            color = F1Red.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, F1Red.copy(alpha = 0.4f))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.CloudOff,
                                        contentDescription = "Error",
                                        tint = F1Red,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "STREAM INTERRUPTED • SHOWING RECENT FEED",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontFamily = FontFamily.Monospace,
                                            fontWeight = FontWeight.Bold,
                                            color = F1Red,
                                            fontSize = 11.sp
                                        )
                                    )
                                }
                                IconButton(
                                    onClick = { telemetryViewModel.retryTelemetry() },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.Refresh,
                                        contentDescription = "Retry",
                                        tint = F1Red,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    } else if (!isLiveSessionActive) {
                        // Banner 2: No active live session banner in theme primary color
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 4.dp),
                            color = bannerColor.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, bannerColor.copy(alpha = 0.5f))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.Info,
                                        contentDescription = "No Live Session",
                                        tint = bannerColor,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "NO LIVE SESSION ONGOING • FEED IN STANDBY",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontFamily = FontFamily.Monospace,
                                            fontWeight = FontWeight.Bold,
                                            color = bannerColor,
                                            fontSize = 11.sp
                                        )
                                    )
                                }
                                IconButton(
                                    onClick = { telemetryViewModel.retryTelemetry() },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.Refresh,
                                        contentDescription = "Refresh",
                                        tint = bannerColor,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }

                    // Render selected header layout (COMPACT vs DEFAULT)
                    val trackCondition = currentData?.trackStatus?.message
                        ?: currentData?.trackStatus?.status
                        ?: ""
                    val latestRaceControlMessage = currentData?.raceControlMessages?.lastOrNull()
                    RaceConditionBanner(
                        trackCondition = trackCondition,
                        latestMessage = latestRaceControlMessage?.message,
                        latestCategory = latestRaceControlMessage?.category,
                        latestFlag = latestRaceControlMessage?.flag
                    )

                    if (headerStyle == "COMPACT") {
                        CompactTelemetryHeader(
                            sessionType = currentData?.sessionInfo?.type ?: "N/A",
                            sessionPart = currentData?.timingData?.sessionPart,
                            trackStatus = currentData?.trackStatus?.message ?: currentData?.trackStatus?.status ?: "N/A",
                            trackTemp = currentData?.weatherData?.trackTemp ?: "--°C",
                            currentLap = currentLapString,
                            remainingTime = currentData?.extrapolatedClock?.remaining ?: "--:--",
                            isExtrapolating = currentData?.extrapolatedClock?.extrapolating == true
                        )
                    } else {
                        TelemetryHeader(
                            sessionName = currentData?.sessionInfo?.meeting?.name ?: "UNKNOWN",
                            sessionType = currentData?.sessionInfo?.type ?: "N/A",
                            sessionPart = currentData?.timingData?.sessionPart,
                            trackStatus = currentData?.trackStatus?.message ?: currentData?.trackStatus?.status ?: "N/A",
                            currentLap = currentLapString,
                            remainingTime = currentData?.extrapolatedClock?.remaining ?: "--:--",
                            isExtrapolating = currentData?.extrapolatedClock?.extrapolating == true,
                            airTemp = currentData?.weatherData?.airTemp ?: "--°C",
                            trackTemp = currentData?.weatherData?.trackTemp ?: "--°C",
                            humidity = currentData?.weatherData?.humidity ?: "--%",
                            wind = "${currentData?.weatherData?.windSpeed ?: "--"} ${currentData?.weatherData?.windDirection ?: ""}".trim()
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    TimingTowerCard(
                        timingLines = currentData?.timingData?.lines,
                        driverList = currentData?.driverList,
                        appDataLines = currentData?.timingAppData?.lines,
                        sessionType = currentData?.sessionInfo?.type ?: "N/A",
                        showGaps = showTowerGaps,
                        showSectors = showTowerSectors,
                        showTyres = showTowerTyres
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    RaceControlCard(
                        messages = currentData?.raceControlMessages ?: emptyList()
                    )
                }
            }
        }
    }
}
