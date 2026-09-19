package com.jenil.f1comp.ui.results.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.ChevronLeft
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.ErrorOutline
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.jenil.f1comp.data.local.entity.QualifyingResultEntity
import com.jenil.f1comp.data.local.entity.RaceResultEntity
import com.jenil.f1comp.data.local.entity.ScheduleEntity
import com.jenil.f1comp.data.local.entity.SprintQualifyingResultEntity
import com.jenil.f1comp.data.local.entity.SprintResultEntity
import com.jenil.f1comp.ui.F1ScreenPadding
import com.jenil.f1comp.ui.home.components.PodiumDriverData
import com.jenil.f1comp.ui.results.component.QualifyingResultRow
import com.jenil.f1comp.ui.results.component.RaceChip
import com.jenil.f1comp.ui.results.component.RaceResultRow
import com.jenil.f1comp.util.FlagImage
import com.jenil.f1comp.util.ProfileUtils
import com.jenil.f1comp.viewmodel.QualifyingViewModel
import com.jenil.f1comp.viewmodel.RaceResultViewModel
import com.jenil.f1comp.viewmodel.ScheduleViewModel
import com.jenil.f1comp.viewmodel.SprintQualifyingViewModel
import com.jenil.f1comp.viewmodel.SprintRaceViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate

enum class ResultSessionType(val label: String) {
    RACE("Race Result"),
    QUALIFYING("Quali Result"),
    SPRINT("Sprint Result"),
    SPRINT_QUALIFYING("Sprint Quali")
}

@Composable
fun RaceResultScreen(
    round: String,
    year: Int,
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: RaceResultViewModel = hiltViewModel(),
    qualifyingViewModel: QualifyingViewModel = hiltViewModel(),
    sprintQualifyingViewModel: SprintQualifyingViewModel = hiltViewModel(),
    sprintResultViewModel: SprintRaceViewModel = hiltViewModel(),
    scheduleViewModel: ScheduleViewModel = hiltViewModel()
) {
    val raceSchedule by scheduleViewModel.schedule.collectAsStateWithLifecycle()

    val completedRaces = remember(raceSchedule) {
        raceSchedule
            .filter { it.isCompleted }
            .sortedBy { it.round.toIntOrNull() ?: Int.MAX_VALUE }
    }

    var currentRound by remember { mutableStateOf(if(round == "last" || round =="latest") "1" else round )}
    var currentYear by remember { mutableStateOf(year) }
    var selectedSession by remember { mutableStateOf(ResultSessionType.RACE) }

    LaunchedEffect(completedRaces) {
        if ((round == "last" || round == "latest") && completedRaces.isNotEmpty()) {
            completedRaces.lastOrNull()?.round?.let {
                currentRound = it
            }
        }
    }

    val currentIndex = remember(completedRaces, currentRound) {
        completedRaces.indexOfFirst { it.round == currentRound }
    }
    val currentScheduleEntry = completedRaces.getOrNull(currentIndex)

    fun switchTo(race: ScheduleEntity) {
        currentRound = race.round
        currentYear = runCatching { LocalDate.parse(race.grandPrix).year }
            .getOrDefault(currentYear)
    }

    val raceResultsFlow = remember(currentRound) { viewModel.raceResultsFlow(currentRound) }
    val qualiResultsFlow = remember(currentRound) { qualifyingViewModel.qualifyingResultsFlow(currentRound) }
    val sprintQualiResultsFlow = remember(currentRound) { sprintQualifyingViewModel.sprintQualifyingResultsFlow(currentRound) }
    val sprintResultsFlow = remember(currentRound) { sprintResultViewModel.sprintResultsFlow(currentRound) }

    val raceResults by raceResultsFlow.collectAsStateWithLifecycle()
    val qualifyingResults by qualiResultsFlow.collectAsStateWithLifecycle()
    val sprintQualifyingResults by sprintQualiResultsFlow.collectAsStateWithLifecycle()
    val sprintResults by sprintResultsFlow.collectAsStateWithLifecycle()

    val isRaceLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val isQualiLoading by qualifyingViewModel.isLoading.collectAsStateWithLifecycle()
    val isSprintQualiLoading by sprintQualifyingViewModel.isLoading.collectAsStateWithLifecycle()
    val isSprintLoading by sprintResultViewModel.isLoading.collectAsStateWithLifecycle()

    val raceError by viewModel.error.collectAsStateWithLifecycle()
    val qualiError by qualifyingViewModel.error.collectAsStateWithLifecycle()
    val sprintQualiError by sprintQualifyingViewModel.error.collectAsStateWithLifecycle()
    val sprintError by sprintResultViewModel.error.collectAsStateWithLifecycle()

    LaunchedEffect(currentRound, currentYear) {
        launch { viewModel.refreshRaceResult(currentRound, currentYear) }
        launch { qualifyingViewModel.refreshQualifyingResults(currentRound, currentYear) }
        launch { sprintQualifyingViewModel.refreshSprintQualifyingResults(currentRound, currentYear) }
        launch { sprintResultViewModel.refreshSprintResults(currentRound, currentYear) }
    }

    val hasSprint = remember(currentScheduleEntry, sprintResults, sprintQualifyingResults) {
        (currentScheduleEntry?.sprint != null || currentScheduleEntry?.sprintQualifying != null)
                || sprintResults.isNotEmpty()
                || sprintQualifyingResults.isNotEmpty()
    }

    LaunchedEffect(hasSprint) {
        if (!hasSprint && (selectedSession == ResultSessionType.SPRINT || selectedSession == ResultSessionType.SPRINT_QUALIFYING)) {
            selectedSession = ResultSessionType.RACE
        }
    }

    val chipListState = rememberLazyListState()
    LaunchedEffect(currentIndex) {
        if (currentIndex >= 0) {
            chipListState.animateScrollToItem(maxOf(0, currentIndex - 1))
        }
    }

    val rawRaceName = currentScheduleEntry?.raceName
        ?: raceResults.firstOrNull()?.raceName
        ?: qualifyingResults.firstOrNull()?.raceName
        ?: "Race Result"

    val displayRaceName = rawRaceName
        .replace("Grand Prix", "GP")
        .replace("GrandPrix", "GP")

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(top = F1ScreenPadding.topPadding())
    ) {
        // --- Top Bar Header ---
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
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 6.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "$displayRaceName ",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    FlagImage(
                        flagUrl = ProfileUtils.getFlagUrl(
                            nationality = currentScheduleEntry?.circuitCountry,
                            emoji = currentScheduleEntry?.flag
                        ),
                        width = 24.dp,
                        height = 16.dp
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Round $currentRound · $currentYear ${currentScheduleEntry?.circuitLocation?.let { "• $it" } ?: ""}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            IconButton(
                onClick = {
                    completedRaces.getOrNull(currentIndex - 1)?.let { switchTo(it) }
                },
                enabled = currentIndex > 0
            ) {
                Icon(
                    imageVector = Icons.Rounded.ChevronLeft,
                    contentDescription = "Previous race",
                    tint = if (currentIndex > 0)
                        MaterialTheme.colorScheme.onSurface
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
                )
            }
            IconButton(
                onClick = {
                    completedRaces.getOrNull(currentIndex + 1)?.let { switchTo(it) }
                },
                enabled = currentIndex in 0 until completedRaces.lastIndex
            ) {
                Icon(
                    imageVector = Icons.Rounded.ChevronRight,
                    contentDescription = "Next race",
                    tint = if (currentIndex in 0 until completedRaces.lastIndex)
                        MaterialTheme.colorScheme.onSurface
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // --- Grand Prix Round Selector ---
        if (completedRaces.size > 1) {
            LazyRow(
                state = chipListState,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(horizontal = 16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(completedRaces, key = { it.round }) { race ->
                    RaceChip(
                        label = "R${race.round}",
                        isSelected = race.round == currentRound,
                        onClick = { switchTo(race) }
                    )
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
        }

        // --- Session Filter Segmented Toggles ---
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            item {
                RaceChip(
                    label = ResultSessionType.RACE.label,
                    isSelected = selectedSession == ResultSessionType.RACE,
                    onClick = { selectedSession = ResultSessionType.RACE }
                )
            }
            item {
                RaceChip(
                    label = ResultSessionType.QUALIFYING.label,
                    isSelected = selectedSession == ResultSessionType.QUALIFYING,
                    onClick = { selectedSession = ResultSessionType.QUALIFYING }
                )
            }
            if (hasSprint) {
                item {
                    RaceChip(
                        label = ResultSessionType.SPRINT.label,
                        isSelected = selectedSession == ResultSessionType.SPRINT,
                        onClick = { selectedSession = ResultSessionType.SPRINT }
                    )
                }
                item {
                    RaceChip(
                        label = ResultSessionType.SPRINT_QUALIFYING.label,
                        isSelected = selectedSession == ResultSessionType.SPRINT_QUALIFYING,
                        onClick = { selectedSession = ResultSessionType.SPRINT_QUALIFYING }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        val isCurrentSessionLoading = when (selectedSession) {
            ResultSessionType.RACE -> isRaceLoading
            ResultSessionType.QUALIFYING -> isQualiLoading
            ResultSessionType.SPRINT -> isSprintLoading
            ResultSessionType.SPRINT_QUALIFYING -> isSprintQualiLoading
        }

        val currentSessionError = when (selectedSession) {
            ResultSessionType.RACE -> raceError
            ResultSessionType.QUALIFYING -> qualiError
            ResultSessionType.SPRINT -> sprintError
            ResultSessionType.SPRINT_QUALIFYING -> sprintQualiError
        }

        val sortedRaceResults = remember(raceResults) {
            raceResults.sortedBy { it.position.toIntOrNull() ?: Int.MAX_VALUE }
        }
        val sortedQualiResults = remember(qualifyingResults) {
            qualifyingResults.sortedBy { it.position.toIntOrNull() ?: Int.MAX_VALUE }
        }
        val sortedSprintResults = remember(sprintResults) {
            sprintResults.sortedBy { it.position.toIntOrNull() ?: Int.MAX_VALUE }
        }
        val sortedSprintQualiResults = remember(sprintQualifyingResults) {
            sprintQualifyingResults.sortedBy { it.position.toIntOrNull() ?: Int.MAX_VALUE }
        }

        val isCurrentResultsEmpty = when (selectedSession) {
            ResultSessionType.RACE -> sortedRaceResults.isEmpty()
            ResultSessionType.QUALIFYING -> sortedQualiResults.isEmpty()
            ResultSessionType.SPRINT -> sortedSprintResults.isEmpty()
            ResultSessionType.SPRINT_QUALIFYING -> sortedSprintQualiResults.isEmpty()
        }

        when {
            isCurrentSessionLoading && isCurrentResultsEmpty -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Loading ${selectedSession.label}...",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            currentSessionError != null && isCurrentResultsEmpty -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Rounded.ErrorOutline,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = currentSessionError,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 32.dp)
                        )
                    }
                }
            }

            isCurrentResultsEmpty -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = "No ${selectedSession.label.lowercase()} available",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // --- Items : Full Driver Results List ---
                    when (selectedSession) {
                        ResultSessionType.RACE -> {
                            items(sortedRaceResults, key = { "race_${it.driver}" }) { result ->
                                RaceResultRow(
                                    result = result,
                                    onDriverClick = { navController.navigate("profile/true/${result.driver}") },
                                    onConstructorClick = { navController.navigate("profile/false/${result.constructor}") }
                                )
                            }
                        }
                        ResultSessionType.QUALIFYING -> {
                            items(sortedQualiResults, key = { "quali_${it.driver}" }) { result ->
                                QualifyingResultRow(
                                    result = result,
                                    onDriverClick = { navController.navigate("profile/true/${result.driver}") },
                                    onConstructorClick = { navController.navigate("profile/false/${result.constructor}") }
                                )
                            }
                        }
                        ResultSessionType.SPRINT -> {
                            items(sortedSprintResults, key = { "sprint_${it.driver}" }) { result ->
                                RaceResultRow(
                                    result = result,
                                    onDriverClick = { navController.navigate("profile/true/${result.driver}") },
                                    onConstructorClick = { navController.navigate("profile/false/${result.constructor}") }
                                )
                            }
                        }
                        ResultSessionType.SPRINT_QUALIFYING -> {
                            items(sortedSprintQualiResults, key = { "sprint_quali_${it.driver}" }) { result ->
                                QualifyingResultRow(
                                    result = result,
                                    onDriverClick = { navController.navigate("profile/true/${result.driver}") },
                                    onConstructorClick = { navController.navigate("profile/false/${result.constructor}") }
                                )
                            }
                        }
                    }

                    item { Spacer(modifier = Modifier.height(F1ScreenPadding.bottomPadding())) }
                }
            }
        }
    }
}

// --- Helper Functions to Extract Podium Data ---

private fun extractRacePodium(results: List<RaceResultEntity>): Triple<PodiumDriverData?, PodiumDriverData?, PodiumDriverData?> {
    val p1 = results.firstOrNull { it.position == "1" }?.let {
        PodiumDriverData("1", it.driver, it.driverImage, it.constructor, "${it.points} pts")
    }
    val p2 = results.firstOrNull { it.position == "2" }?.let {
        PodiumDriverData("2", it.driver, it.driverImage, it.constructor, if (it.time.isNotBlank()) it.time else "${it.points} pts")
    }
    val p3 = results.firstOrNull { it.position == "3" }?.let {
        PodiumDriverData("3", it.driver, it.driverImage, it.constructor, if (it.time.isNotBlank()) it.time else "${it.points} pts")
    }
    return Triple(p1, p2, p3)
}

private fun extractSprintPodium(results: List<SprintResultEntity>): Triple<PodiumDriverData?, PodiumDriverData?, PodiumDriverData?> {
    val p1 = results.firstOrNull { it.position == "1" }?.let {
        PodiumDriverData("1", it.driver, it.driverImage, it.constructor, "${it.points} pts")
    }
    val p2 = results.firstOrNull { it.position == "2" }?.let {
        PodiumDriverData("2", it.driver, it.driverImage, it.constructor, if (it.time.isNotBlank()) it.time else "${it.points} pts")
    }
    val p3 = results.firstOrNull { it.position == "3" }?.let {
        PodiumDriverData("3", it.driver, it.driverImage, it.constructor, if (it.time.isNotBlank()) it.time else "${it.points} pts")
    }
    return Triple(p1, p2, p3)
}

private fun extractQualiPodium(results: List<QualifyingResultEntity>): Triple<PodiumDriverData?, PodiumDriverData?, PodiumDriverData?> {
    val p1 = results.firstOrNull { it.position == "1" }?.let {
        PodiumDriverData("1", it.driver, it.driverImage, it.constructor, it.q3 ?: it.q2 ?: it.q1 ?: "POLE")
    }
    val p2 = results.firstOrNull { it.position == "2" }?.let {
        PodiumDriverData("2", it.driver, it.driverImage, it.constructor, it.q3 ?: it.q2 ?: it.q1 ?: "")
    }
    val p3 = results.firstOrNull { it.position == "3" }?.let {
        PodiumDriverData("3", it.driver, it.driverImage, it.constructor, it.q3 ?: it.q2 ?: it.q1 ?: "")
    }
    return Triple(p1, p2, p3)
}

private fun extractSprintQualiPodium(results: List<SprintQualifyingResultEntity>): Triple<PodiumDriverData?, PodiumDriverData?, PodiumDriverData?> {
    val p1 = results.firstOrNull { it.position == "1" }?.let {
        PodiumDriverData("1", it.driver, it.driverImage, it.constructor, it.q3 ?: it.q2 ?: it.q1 ?: "SQ POLE")
    }
    val p2 = results.firstOrNull { it.position == "2" }?.let {
        PodiumDriverData("2", it.driver, it.driverImage, it.constructor, it.q3 ?: it.q2 ?: it.q1 ?: "")
    }
    val p3 = results.firstOrNull { it.position == "3" }?.let {
        PodiumDriverData("3", it.driver, it.driverImage, it.constructor, it.q3 ?: it.q2 ?: it.q1 ?: "")
    }
    return Triple(p1, p2, p3)
}
