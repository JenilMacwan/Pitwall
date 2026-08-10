package com.jenil.f1comp.ui.schedule.screen

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.test.calendar_add_on
import com.jenil.f1comp.ui.F1ScreenPadding
import com.jenil.f1comp.ui.schedule.components.ScheduleCard
import com.jenil.f1comp.ui.schedule.components.TabPill
import com.jenil.f1comp.util.syncRacesToCalendar
import com.jenil.f1comp.viewmodel.ScheduleViewModel

@Composable
fun ScheduleScreen(
    modifier: Modifier = Modifier,
    viewModel: ScheduleViewModel = hiltViewModel()
) {
    val raceSchedule by viewModel.schedule.collectAsStateWithLifecycle()

    var selectedTab by remember { mutableStateOf("All") }

    val scrollState = rememberScrollState()

    val context = LocalContext.current

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

    val calendarPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val readGranted = permissions[Manifest.permission.READ_CALENDAR] ?: false
        val writeGranted = permissions[Manifest.permission.WRITE_CALENDAR] ?: false

        if (readGranted && writeGranted) {
            syncRacesToCalendar(context, allRaces)
        } else {
            Toast.makeText(context, "Calendar permissions are required to sync.", Toast.LENGTH_SHORT).show()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(top = F1ScreenPadding.topPadding())
    ) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(end = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Schedule",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            IconButton(
                onClick = {
                    val hasReadPerm = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CALENDAR) == PackageManager.PERMISSION_GRANTED
                    val hasWritePerm = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_CALENDAR) == PackageManager.PERMISSION_GRANTED

                    if (hasReadPerm && hasWritePerm) {
                        // Permissions already exist, execute sync
                        syncRacesToCalendar(context, allRaces)
                    } else {
                        // Launch permission request
                        calendarPermissionLauncher.launch(
                            arrayOf(Manifest.permission.READ_CALENDAR, Manifest.permission.WRITE_CALENDAR)
                        )
                    }
                }
            ) {
                Icon(
                    imageVector = calendar_add_on,
                    contentDescription = "Sync calendar",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Row(
            modifier = Modifier
                .padding(top = 20.dp, start = 16.dp, end = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TabPill(
                text = "All Races",
                isSelected = selectedTab == "All",
                onClick = { selectedTab = "All" }
            )
            Spacer(modifier = Modifier.width(5.dp))
            TabPill(
                text = "Upcoming",
                isSelected = selectedTab == "Upcoming",
                onClick = { selectedTab = "Upcoming" }
            )
            Spacer(modifier = Modifier.width(5.dp))
            TabPill(
                text = "Completed",
                isSelected = selectedTab == "Completed",
                onClick = { selectedTab = "Completed" }
            )
        }
        Spacer(modifier = Modifier.height(20.dp))

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(bottom = F1ScreenPadding.bottomPadding()),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            when (selectedTab) {
                "All" -> {
                    allRaces.forEach { race ->
                        ScheduleCard(schedule = race, isNextRace = false)
                    }
                }

                "Upcoming" -> {
                    upcomingRaces.forEachIndexed { index, race ->
                        if (index == 0) {
                            SectionLabel(text = "NEXT UP — ROUND ${race.round}")
                            ScheduleCard(schedule = race, isNextRace = true)
                            Spacer(modifier = Modifier.height(20.dp))
                        } else {
                            ScheduleCard(schedule = race, isNextRace = false)
                        }
                    }
                }

                "Completed" -> {
                    completedRaces.forEach { race ->
                        ScheduleCard(schedule = race, isNextRace = false)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text,
        fontFamily = FontFamily.Monospace,
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    )
}