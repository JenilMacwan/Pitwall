package com.jenil.f1comp.ui.home.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.jenil.f1comp.R
import com.jenil.f1comp.ui.F1ScreenPadding
import com.jenil.f1comp.ui.home.components.NextRaceCard
import com.jenil.f1comp.ui.home.components.RaceResultCard
import com.jenil.f1comp.ui.home.components.TopStandingsCard
import com.jenil.f1comp.ui.navigation.BottomNavItem
import com.jenil.f1comp.viewmodel.ConstructorStandingsViewModel
import com.jenil.f1comp.viewmodel.DriverStandingsViewModel
import com.jenil.f1comp.viewmodel.NextRaceViewModel
import com.jenil.f1comp.viewmodel.RaceResultViewModel

@Composable
fun HomeScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: NextRaceViewModel = hiltViewModel(),
    driverViewModel: DriverStandingsViewModel = hiltViewModel(),
    constructorViewModel: ConstructorStandingsViewModel = hiltViewModel(),
    raceResultViewModel: RaceResultViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val driverStandings by driverViewModel.driverStandings.collectAsStateWithLifecycle()
    val constructorStandings by constructorViewModel.constructorStandings.collectAsStateWithLifecycle()

    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(top = F1ScreenPadding.topPadding())
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(id = R.string.title_home),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            IconButton(
                onClick = {
                    navController.navigate("settings")
                },
            ) {
                Icon(
                    imageVector = Icons.Outlined.Settings,
                    contentDescription = "Settings",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }


        Box(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
        ) {
            when {
                uiState.isLoading && uiState.nextRace == null -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                uiState.error != null && uiState.nextRace == null -> {
                    Text(
                        text = "Error: ${uiState.error}",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                uiState.nextRace != null -> {
                    val nextRace = uiState.nextRace!!
                    val currentRound = nextRace.round.toIntOrNull() ?: 1
                    val lastRound = (currentRound - 1).coerceAtLeast(1)
                    val lastRaceId = lastRound.toString()

                    val raceResultsFlow = remember(lastRaceId) {
                        raceResultViewModel.raceResultsFlow(lastRaceId)
                    }
                    val raceResults by raceResultsFlow.collectAsStateWithLifecycle()

                    val podium = raceResults
                        .filter { it.position in listOf("1", "2", "3") }
                        .sortedBy { it.position }

                    LaunchedEffect(lastRaceId) {
                        raceResultViewModel.refreshRaceResult(raceId = lastRaceId, year = 2026)
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(scrollState)
                            .padding(bottom = F1ScreenPadding.bottomPadding())
                    ) {
                        NextRaceCard(
                            raceName = nextRace.raceName,
                            circuit = nextRace.circuit,
                            sessionName = nextRace.sessionName,
                            countdown = nextRace.countdown,
                            ongoingSession = nextRace.ongoingSession,
                            weather = nextRace.weather,
                            sprint = nextRace.sprint,
                            flagEmoji = nextRace.flagEmoji
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        RaceResultCard(
                            podium = podium,
                            onViewFullResult = {
                                navController.navigate("race_result/$lastRaceId/2026")
                            }
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        TopStandingsCard(
                            navController = navController,
                            driverStandings = driverStandings,
                            constructorStandings = constructorStandings,
                            onViewAllClicked = {
                                navController.navigate(BottomNavItem.Standings.route) {
                                    popUpTo(BottomNavItem.Home.route) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}