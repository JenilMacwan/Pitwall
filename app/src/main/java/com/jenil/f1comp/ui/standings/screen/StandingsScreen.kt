package com.jenil.f1comp.ui.standings.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jenil.f1comp.ui.F1ScreenPadding
import com.jenil.f1comp.ui.standings.components.StandingsCard
import com.jenil.f1comp.viewmodel.ConstructorStandingsViewModel
import com.jenil.f1comp.viewmodel.DriverStandingsViewModel


@Composable
fun StandingsScreen(
    modifier: Modifier = Modifier,
    driverViewModel: DriverStandingsViewModel = hiltViewModel(),
    constructorViewModel: ConstructorStandingsViewModel = hiltViewModel()
) {

    val driverStandings by driverViewModel.driverStandings.collectAsStateWithLifecycle()
    val constructorStandings by constructorViewModel.constructorStandings.collectAsStateWithLifecycle()

    val scrollState = rememberScrollState()

    Box(
        modifier = modifier.fillMaxSize()
    )
    {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(
                    top = F1ScreenPadding.topPadding(),
                    bottom = F1ScreenPadding.bottomPadding()
                )
        ) {
            Text(
                text = "Standings",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            StandingsCard(
                driverStandings = driverStandings,
                constructorStandings = constructorStandings
            )
        }
    }
}
