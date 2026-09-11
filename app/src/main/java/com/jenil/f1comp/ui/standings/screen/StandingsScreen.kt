package com.jenil.f1comp.ui.standings.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.jenil.f1comp.R
import com.jenil.f1comp.ui.F1ScreenPadding
import com.jenil.f1comp.ui.standings.components.StandingsCard
import com.jenil.f1comp.viewmodel.ConstructorStandingsViewModel
import com.jenil.f1comp.viewmodel.DriverStandingsViewModel
import com.jenil.f1comp.viewmodel.NextRaceViewModel
import com.jenil.f1comp.viewmodel.ScheduleViewModel

@Composable
fun StandingsScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    onDrawerClick: () -> Unit,
    driverViewModel: DriverStandingsViewModel = hiltViewModel(),
    constructorViewModel: ConstructorStandingsViewModel = hiltViewModel(),
    scheduleViewModel: ScheduleViewModel = hiltViewModel(),
    nextRaceViewModel: NextRaceViewModel = hiltViewModel()
) {
    val driverStandings by driverViewModel.driverStandings.collectAsStateWithLifecycle()
    val constructorStandings by constructorViewModel.constructorStandings.collectAsStateWithLifecycle()
    val raceSchedule by scheduleViewModel.schedule.collectAsStateWithLifecycle()
    val nextRaceUiState by nextRaceViewModel.uiState.collectAsStateWithLifecycle()

    val totalRaces = remember(raceSchedule) {
        raceSchedule.size.takeIf { it > 0 } ?: 24
    }
    val currentRound = remember(raceSchedule, nextRaceUiState) {
        nextRaceUiState.nextRace?.round
            ?: raceSchedule.count { it.isCompleted }.let { if (it > 0) it.toString() else "1" }
    }

    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(top = F1ScreenPadding.topPadding())
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp, end = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape),
                    color = MaterialTheme.colorScheme.surface
                ) {
                    IconButton(onClick = onDrawerClick) {
                        Icon(
                            painter = painterResource(R.drawable.ic_menu),
                            contentDescription = "Menu",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Standings",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            // Round Progress Pill (e.g. R14 / 24)
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.8f),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF00E676))
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "R$currentRound / $totalRaces",
                        style = MaterialTheme.typography.labelSmall,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(bottom = F1ScreenPadding.bottomPadding())
            ) {
                StandingsCard(
                    navController = navController,
                    driverStandings = driverStandings,
                    constructorStandings = constructorStandings,
                )
            }
        }
    }
}
