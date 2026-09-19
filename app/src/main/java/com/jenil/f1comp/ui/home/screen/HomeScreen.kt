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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.imageLoader
import coil.request.ImageRequest
import com.jenil.f1comp.R
import com.jenil.f1comp.ui.F1ScreenPadding
import com.jenil.f1comp.ui.home.components.NextRaceCard
import com.jenil.f1comp.ui.home.components.PodiumDriverData
import com.jenil.f1comp.ui.home.components.PodiumHeroCard
import com.jenil.f1comp.ui.home.components.TopStandingsCard
import com.jenil.f1comp.ui.navigation.BottomNavItem
import com.jenil.f1comp.ui.results.screen.ResultSessionType
import com.jenil.f1comp.viewmodel.ConstructorStandingsViewModel
import com.jenil.f1comp.viewmodel.DriverStandingsViewModel
import com.jenil.f1comp.viewmodel.NextRaceViewModel
import com.jenil.f1comp.viewmodel.RaceResultViewModel
import com.jenil.f1comp.viewmodel.ScheduleViewModel
import java.time.LocalDate

@Composable
fun HomeScreen(
    navController: NavController,
    onDrawerClick: () -> Unit,
    onDetailClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: NextRaceViewModel = hiltViewModel(),
    driverViewModel: DriverStandingsViewModel = hiltViewModel(),
    constructorViewModel: ConstructorStandingsViewModel = hiltViewModel(),
    raceResultViewModel: RaceResultViewModel = hiltViewModel(),
    scheduleViewModel: ScheduleViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val driverStandings by driverViewModel.driverStandings.collectAsStateWithLifecycle()
    val constructorStandings by constructorViewModel.constructorStandings.collectAsStateWithLifecycle()
    val raceSchedule by scheduleViewModel.schedule.collectAsStateWithLifecycle()

    val context = LocalContext.current

    // Pre-fetch driver images for faster loading in other screens
    LaunchedEffect(driverStandings) {
        if (driverStandings.isNotEmpty()) {
            driverStandings.take(10).forEach { driver ->
                driver.driverImage?.let { url ->
                    val request = ImageRequest.Builder(context)
                        .data(url)
                        .build()
                    context.imageLoader.enqueue(request)
                }
            }
        }
    }

    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(top = F1ScreenPadding.topPadding())
    ) {
        // --- Top Bar Header ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp, end = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
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
                    text = stringResource(id = R.string.title_home),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.weight(1f))
                IconButton(
                    onClick = { navController.navigate("user_profile") }
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_profile),
                        contentDescription = "Profile",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
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

                    val podium = remember(raceResults) {
                        raceResults
                            .filter { it.position in listOf("1", "2", "3") }
                            .sortedBy { it.position.toIntOrNull() ?: Int.MAX_VALUE }
                    }

                    val p1 = remember(podium) {
                        podium.firstOrNull { it.position == "1" }?.let {
                            PodiumDriverData("1", it.driver, it.driverImage, it.constructor, "${it.points} pts")
                        }
                    }
                    val p2 = remember(podium) {
                        podium.firstOrNull { it.position == "2" }?.let {
                            PodiumDriverData("2", it.driver, it.driverImage, it.constructor, if (it.time.isNotBlank()) it.time else "${it.points} pts")
                        }
                    }
                    val p3 = remember(podium) {
                        podium.firstOrNull { it.position == "3" }?.let {
                            PodiumDriverData("3", it.driver, it.driverImage, it.constructor, if (it.time.isNotBlank()) it.time else "${it.points} pts")
                        }
                    }

                    LaunchedEffect(lastRaceId) {
                        raceResultViewModel.refreshRaceResult(raceId = lastRaceId, year = 2026)
                    }

                    val currentScheduleEntity = remember(raceSchedule, nextRace.round) {
                        raceSchedule.firstOrNull { it.round == nextRace.round }
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(scrollState)
                            .padding(bottom = F1ScreenPadding.bottomPadding())
                    ) {
                        NextRaceCard(
                            round = nextRace.round,
                            raceName = nextRace.raceName,
                            circuit = nextRace.circuit,
                            country = nextRace.country,
                            sessionName = nextRace.sessionName,
                            countdown = nextRace.countdown,
                            ongoingSession = nextRace.ongoingSession,
                            weather = nextRace.weather,
                            sprint = nextRace.sprint,
                            flagEmoji = nextRace.flagEmoji,
                            schedule = currentScheduleEntity,
                            onDetailClick = {
                                onDetailClick()
                            }
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        if (p1 != null) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp)
                            ) {
                                PodiumHeroCard(
                                    p1 = p1,
                                    p2 = p2,
                                    p3 = p3,
                                    raceResult = raceResults,
                                    selectedSession = ResultSessionType.RACE,
                                    onDriverClick = { driverId -> navController.navigate("profile/true/$driverId") },
                                    onConstructorClick = { teamName -> navController.navigate("profile/false/$teamName") },
                                    onResultClick = {
                                        navController.navigate("race_result/$lastRaceId/${LocalDate.now().year}")
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))
                        }

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
                        Spacer(modifier = Modifier.height(22.dp))
                    }
                }
            }
        }
    }
}
