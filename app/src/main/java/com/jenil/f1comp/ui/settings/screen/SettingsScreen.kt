package com.jenil.f1comp.ui.settings.screen

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Code
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.Sync
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.jenil.f1comp.R
import com.jenil.f1comp.ui.F1ScreenPadding
import com.jenil.f1comp.ui.settings.component.SettingsItem
import com.jenil.f1comp.ui.settings.component.SettingsSection
import com.jenil.f1comp.ui.settings.component.SettingsSwitchItem
import com.jenil.f1comp.util.removeRacesFromCalendar
import com.jenil.f1comp.util.syncRacesToCalendar
import com.jenil.f1comp.viewmodel.ScheduleViewModel

@Composable
fun SettingsScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: ScheduleViewModel = hiltViewModel()
) {
    val scrollState = rememberScrollState()
    var isDarkMode by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val raceSchedule by viewModel.schedule.collectAsStateWithLifecycle()

    var pendingAction by remember { mutableStateOf<(() -> Unit)?>(null) }

    val calendarPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val readGranted = permissions[Manifest.permission.READ_CALENDAR] ?: false
        val writeGranted = permissions[Manifest.permission.WRITE_CALENDAR] ?: false

        if (readGranted && writeGranted) {
            pendingAction?.invoke()
        } else {
            Toast.makeText(context, "Calendar permissions are required.", Toast.LENGTH_SHORT).show()
        }
        pendingAction = null
    }

    val runWithPermission = { action: () -> Unit ->
        val hasReadPerm = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CALENDAR) == PackageManager.PERMISSION_GRANTED
        val hasWritePerm = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_CALENDAR) == PackageManager.PERMISSION_GRANTED

        if (hasReadPerm && hasWritePerm) {
            action()
        } else {
            pendingAction = action
            calendarPermissionLauncher.launch(
                arrayOf(Manifest.permission.READ_CALENDAR, Manifest.permission.WRITE_CALENDAR)
            )
        }
    }

    val dynamicSubtitle = if (isDarkMode) {
        "Dark theme is currently active"
    } else {
        "Light theme is currently active"
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(top = F1ScreenPadding.topPadding())
    ) {
        // Top Bar
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
                text = stringResource(id = R.string.title_settings),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 16.dp)
        ) {
            SettingsSection(title = "Appearance") {
                SettingsItem(
                    icon = Icons.Outlined.Palette,
                    title = stringResource(id = R.string.title_theme),
                    subtitle = stringResource(id = R.string.sub_title_theme),
                    onClick = { /* TODO */ }
                )
                SettingsSwitchItem(
                    icon = Icons.Outlined.DarkMode,
                    title = stringResource(id = R.string.title_mode),
                    subtitle = dynamicSubtitle,
                    isChecked = isDarkMode,
                    onCheckedChange = { newValue ->
                        isDarkMode = newValue
                    }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            SettingsSection(title = "Calendar Sync") {
                SettingsItem(
                    icon = Icons.Outlined.Sync,
                    title = "Sync All Races",
                    subtitle = "Add all season races to your calendar",
                    onClick = {
                        runWithPermission {
                            syncRacesToCalendar(context, raceSchedule)
                        }
                    }
                )
                SettingsItem(
                    icon = Icons.Outlined.CalendarMonth,
                    title = "Sync Upcoming Races",
                    subtitle = "Add only future races to your calendar",
                    onClick = {
                        runWithPermission {
                            val upcoming = raceSchedule.filter { !it.isCompleted }
                            syncRacesToCalendar(context, upcoming)
                        }
                    }
                )
                SettingsItem(
                    icon = Icons.Outlined.DeleteOutline,
                    title = "Clear F1 Events",
                    subtitle = "Remove all F1 races from your calendar",
                    onClick = {
                        runWithPermission {
                            removeRacesFromCalendar(context)
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            SettingsSection(title = "Notifications") {
                SettingsItem(
                    icon = Icons.Outlined.Notifications,
                    title = stringResource(id = R.string.title_alerts),
                    subtitle = stringResource(id = R.string.sub_title_alerts),
                    onClick = { /* TODO */ }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            SettingsSection(title = "About") {
                SettingsItem(
                    icon = Icons.Outlined.Info,
                    title = stringResource(id = R.string.title_version),
                    subtitle = stringResource(id = R.string.sub_title_version),
                    showChevron = false,
                    onClick = { }
                )
                SettingsItem(
                    icon = Icons.Outlined.Code,
                    title = stringResource(id = R.string.title_developer),
                    subtitle = stringResource(id = R.string.sub_title_developer),
                    showChevron = false,
                    onClick = { }
                )
            }

            Spacer(modifier = Modifier.height(F1ScreenPadding.bottomPadding()))
        }
    }
}
