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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.jenil.f1comp.data.local.entity.ScheduleEntity
import com.jenil.f1comp.ui.F1ScreenPadding
import com.jenil.f1comp.ui.results.component.RaceChip
import com.jenil.f1comp.ui.results.component.RaceResultRow
import com.jenil.f1comp.viewmodel.RaceResultViewModel
import com.jenil.f1comp.viewmodel.ScheduleViewModel
import java.time.LocalDate


@Composable
fun RaceResultScreen(
    round: String,
    year: Int,
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: RaceResultViewModel = hiltViewModel(),
    scheduleViewModel: ScheduleViewModel = hiltViewModel()
) {
    val raceSchedule by scheduleViewModel.schedule.collectAsStateWithLifecycle()

    val completedRaces = remember(raceSchedule) {
        raceSchedule
            .filter { it.isCompleted }
            .sortedBy { it.round.toIntOrNull() ?: Int.MAX_VALUE }
    }

    var currentRound by remember { mutableStateOf(round) }
    var currentYear by remember { mutableStateOf(year) }

    val currentIndex = remember(completedRaces, currentRound) {
        completedRaces.indexOfFirst { it.round == currentRound }
    }
    val currentScheduleEntry = completedRaces.getOrNull(currentIndex)

    fun switchTo(race: ScheduleEntity) {
        currentRound = race.round
        currentYear = runCatching { LocalDate.parse(race.grandPrix).year }
            .getOrDefault(currentYear)
    }

    val resultsFlow = remember(currentRound) { viewModel.raceResultsFlow(currentRound) }
    val results by resultsFlow.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val error by viewModel.error.collectAsStateWithLifecycle()

    LaunchedEffect(currentRound, currentYear) {
        viewModel.refreshRaceResult(currentRound, currentYear)
    }

    val sortedResults = remember(results) {
        results.sortedBy { it.position.toIntOrNull() ?: Int.MAX_VALUE }
    }

    val chipListState = rememberLazyListState()
    LaunchedEffect(currentIndex) {
        if (currentIndex >= 0) {
            chipListState.animateScrollToItem(maxOf(0, currentIndex - 1))
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(top = F1ScreenPadding.topPadding())
    ) {
        // Top bar with prev/next arrows for quick race switching
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
                    .padding(start = 8.dp)
            ) {
                Text(
                    text = (currentScheduleEntry?.raceName
                        ?: sortedResults.firstOrNull()?.raceName
                        ?: "Race Result")
                        .replace("Grand Prix", "GP")
                        .replace("GrandPrix", "GP"),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Round $currentRound · $currentYear",
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

        Spacer(modifier = Modifier.height(12.dp))


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
            Spacer(modifier = Modifier.height(12.dp))
        }

        when {
            isLoading && sortedResults.isEmpty() -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            error != null && sortedResults.isEmpty() -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Rounded.ErrorOutline,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = error ?: "Couldn't load results",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 32.dp)
                        )
                    }
                }
            }

            sortedResults.isEmpty() -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = "No results available yet",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(sortedResults, key = { it.driver }) { result ->
                        RaceResultRow(
                            result = result,
                            onDriverClick = { navController.navigate("profile/true/${result.driver}") },
                            onConstructorClick = { navController.navigate("profile/false/${result.constructor}") }
                        )
                    }
                    item { Spacer(modifier = Modifier.height(F1ScreenPadding.bottomPadding())) }
                }
            }
        }
    }
}


