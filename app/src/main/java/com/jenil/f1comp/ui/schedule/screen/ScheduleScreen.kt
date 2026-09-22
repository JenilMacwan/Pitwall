package com.jenil.f1comp.ui.schedule.screen

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.jenil.f1comp.R
import com.jenil.f1comp.data.local.entity.ScheduleEntity
import com.jenil.f1comp.ui.F1ScreenPadding
import com.jenil.f1comp.ui.schedule.components.ScheduleCard
import com.jenil.f1comp.ui.schedule.components.SectionLabel
import com.jenil.f1comp.ui.schedule.components.TabPill
import com.jenil.f1comp.ui.schedule.components.UpcomingRaceCard
import com.jenil.f1comp.viewmodel.ScheduleViewModel
import java.time.LocalDate

@Composable
fun ScheduleScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    onDrawerClick: () -> Unit,
    viewModel: ScheduleViewModel = hiltViewModel()
) {
    val raceSchedule by viewModel.schedule.collectAsStateWithLifecycle()
    val circuitsMap by viewModel.circuitsMap.collectAsStateWithLifecycle()

    var selectedTabIndex by remember { mutableIntStateOf(0) }

    val scrollState = rememberScrollState()

    val upcomingRaces = remember(raceSchedule) {
        raceSchedule.filter { !it.isCompleted }
            .sortedBy { it.round.toIntOrNull() ?: Int.MAX_VALUE }
    }
    val completedRaces = remember(raceSchedule) {
        raceSchedule.filter { it.isCompleted }
            .sortedByDescending { it.round.toIntOrNull() ?: 0 }
    }

    val allRaces = remember(raceSchedule) {
        raceSchedule.sortedBy { it.round.toIntOrNull() ?: 0 }
    }

    fun navigateToResults(race: ScheduleEntity) {
        val year = runCatching { LocalDate.parse(race.grandPrix).year }
            .getOrDefault(LocalDate.now().year)
        navController.navigate("race_result/${race.round}/$year")
    }

    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Color.Transparent
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 8.dp, end = 8.dp),
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
                        text = stringResource(R.string.schedule_title),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
            TabPill(
                selectedTabIndex = selectedTabIndex,
                onTabSelected = { selectedTabIndex = it },
                modifier = Modifier.padding(top = 20.dp, start = 16.dp, end = 16.dp)
            )
            Spacer(modifier = Modifier.height(20.dp))

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(bottom = F1ScreenPadding.bottomPadding()),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                when (selectedTabIndex) {
                    0 -> { // All Races
                        allRaces.forEach { race ->
                            ScheduleCard(
                                schedule = race, circuit = circuitsMap[race.circuitId],
                                isNextRace = false,
                                onResultsClick = { navigateToResults(race) }
                            )
                        }
                    }

                    1 -> { // Upcoming
                        upcomingRaces.forEachIndexed { index, race ->
                            if (index == 0) {
                                SectionLabel(text = "${stringResource(R.string.home_next_race)} — ${stringResource(R.string.schedule_round)} ${race.round}")
                                ScheduleCard(
                                    schedule = race,
                                    circuit = circuitsMap[race.circuitId],
                                    isNextRace = true
                                )
                            } else {
                                if (index == 1) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(
                                                start = 16.dp,
                                                end = 16.dp,
                                                top = 12.dp,
                                                bottom = 4.dp
                                            ),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = stringResource(R.string.schedule_upcoming),
                                            fontFamily = FontFamily.Monospace,
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Text(
                                            text = "${upcomingRaces.size - 1} ${stringResource(R.string.drawer_races)}",
                                            fontFamily = FontFamily.Monospace,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                                UpcomingRaceCard(
                                    schedule = race,
                                    circuit = circuitsMap[race.circuitId]
                                )
                            }
                        }
                    }

                    2 -> { // Completed
                        completedRaces.forEach { race ->
                            ScheduleCard(
                                schedule = race, circuit = circuitsMap[race.circuitId],
                                isNextRace = false,
                                onResultsClick = { navigateToResults(race) }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}
