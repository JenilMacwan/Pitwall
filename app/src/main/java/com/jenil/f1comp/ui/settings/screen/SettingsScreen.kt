package com.jenil.f1comp.ui.settings.screen

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.NewReleases
import androidx.compose.material.icons.outlined.Policy
import androidx.compose.material.icons.outlined.RadioButtonChecked
import androidx.compose.material.icons.outlined.StarRate
import androidx.compose.material.icons.outlined.Sync
import androidx.compose.material.icons.outlined.Timeline
import androidx.compose.material.icons.rounded.WarningAmber
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.jenil.f1comp.BuildConfig
import com.jenil.f1comp.R
import com.jenil.f1comp.notification.RaceNotificationCoordinator
import com.jenil.f1comp.ui.F1ScreenPadding
import com.jenil.f1comp.ui.settings.component.SettingsItem
import com.jenil.f1comp.ui.settings.component.SettingsSection
import com.jenil.f1comp.ui.settings.component.SettingsSwitchItem
import com.jenil.f1comp.util.removeRacesFromCalendar
import com.jenil.f1comp.util.syncRacesToCalendar
import com.jenil.f1comp.viewmodel.ScheduleViewModel
import com.jenil.f1comp.viewmodel.SettingsViewModel
import kotlinx.coroutines.launch


@Composable
fun SettingsScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: ScheduleViewModel = hiltViewModel(),
    settingsViewModel: SettingsViewModel = hiltViewModel()
) {
    val scrollState = rememberScrollState()
    val isDarkModePref by settingsViewModel.isDarkMode.collectAsStateWithLifecycle()
    val isDarkMode = isDarkModePref ?: isSystemInDarkTheme()
    val context = LocalContext.current
    val raceSchedule by viewModel.schedule.collectAsStateWithLifecycle()
    val haptic = LocalHapticFeedback.current

    var pendingAction by remember { mutableStateOf<(() -> Unit)?>(null) }
    var showClearConfirm by remember { mutableStateOf(false) }
    var isSyncing by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val raceReminders by settingsViewModel.isRaceRemindersEnabled.collectAsStateWithLifecycle()
    val sessionReminders by settingsViewModel.isSessionRemindersEnabled.collectAsStateWithLifecycle()
    val breakingNews by settingsViewModel.isBreakingNewsEnabled.collectAsStateWithLifecycle()
    val liveRaceEvents by settingsViewModel.isLiveRaceEventsEnabled.collectAsStateWithLifecycle()
    val standingsUpdates by settingsViewModel.isStandingsUpdatesEnabled.collectAsStateWithLifecycle()

    val calendarPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val readGranted = permissions[Manifest.permission.READ_CALENDAR] ?: false
        val writeGranted = permissions[Manifest.permission.WRITE_CALENDAR] ?: false

        if (readGranted && writeGranted) {
            pendingAction?.invoke()
        } else {
            scope.launch {
                snackbarHostState.showSnackbar("Calendar permissions are required to sync.")
            }
        }
        pendingAction = null
    }

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            pendingAction?.invoke()
        }
        pendingAction = null
    }

    val runWithNotificationPermission = { action: () -> Unit ->
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val hasPermission = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED

            if (hasPermission) {
                action()
            } else {
                pendingAction = action
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        } else {
            action()
        }
    }


    val runWithPermission = { action: () -> Unit ->
        val hasReadPerm = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CALENDAR) == PackageManager.PERMISSION_GRANTED
        val hasWritePerm = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_CALENDAR) == PackageManager.PERMISSION_GRANTED

        val wrappedAction = {
            isSyncing = true
            action()
            isSyncing = false
        }

        if (hasReadPerm && hasWritePerm) {
            wrappedAction()
        } else {
            pendingAction = wrappedAction
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

    if (showClearConfirm) {
        AlertDialog(
            onDismissRequest = { showClearConfirm = false },
            icon = {
                Icon(
                    imageVector = Icons.Rounded.WarningAmber,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error
                )
            },
            title = { Text("Clear all F1 events?") },
            text = { Text("This removes every F1 race added to your calendar. This can't be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showClearConfirm = false
                        runWithPermission {
                            removeRacesFromCalendar(context) { message ->
                                scope.launch { snackbarHostState.showSnackbar(message) }
                            }
                        }
                    }
                ) {
                    Text("Clear", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearConfirm = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Color.Transparent
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
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
            // Appearance
            SettingsSection(title = stringResource(R.string.settings_appearance)) {
                SettingsSwitchItem(
                    icon = Icons.Outlined.DarkMode,
                    title = stringResource(R.string.settings_dark_mode),
                    subtitle = dynamicSubtitle,
                    isChecked = isDarkMode,
                    onCheckedChange = { newValue ->
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        settingsViewModel.toggleDarkMode(newValue)
                    }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            SettingsSection(title = stringResource(R.string.settings_calendar_sync)) {
                SettingsItem(
                    icon = Icons.Outlined.Sync,
                    title = stringResource(R.string.settings_sync_all),
                    subtitle = stringResource(R.string.settings_sync_all_sub),
                    trailing = if (isSyncing) {
                        {
                            CircularProgressIndicator(
                                modifier = Modifier.height(18.dp),
                                strokeWidth = 2.dp
                            )
                        }
                    } else null,
                    onClick = {
                        runWithPermission {
                            syncRacesToCalendar(context, raceSchedule) { message ->
                                scope.launch { snackbarHostState.showSnackbar(message) }
                            }
                        }
                    }
                )
                SettingsItem(
                    icon = Icons.Outlined.CalendarMonth,
                    title = stringResource(R.string.settings_sync_upcoming),
                    subtitle = stringResource(R.string.settings_sync_upcoming_sub),
                    onClick = {
                        runWithPermission {
                            val upcoming = raceSchedule.filter { !it.isCompleted }
                            syncRacesToCalendar(context, upcoming) { message ->
                                scope.launch { snackbarHostState.showSnackbar(message) }
                            }
                        }
                    }
                )
                SettingsItem(
                    icon = Icons.Outlined.DeleteOutline,
                    title = stringResource(R.string.settings_clear_calendar),
                    subtitle = stringResource(R.string.settings_clear_calendar_sub),
                    onClick = { showClearConfirm = true }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Notifications
            SettingsSection(title = stringResource(R.string.settings_notifications)) {
                SettingsSwitchItem(
                    icon = Icons.Outlined.CalendarMonth,
                    title = stringResource(R.string.settings_race_reminders),
                    subtitle = stringResource(R.string.settings_race_reminders_sub),
                    isChecked = raceReminders ?: false,
                    onCheckedChange = { newValue ->
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        if (newValue) {
                            runWithNotificationPermission {
                                settingsViewModel.setRaceRemindersEnabled(true)
                                RaceNotificationCoordinator.syncAllAlarms(
                                    context = context,
                                    schedule = raceSchedule,
                                    isRaceRemindersEnabled = true,
                                    isSessionRemindersEnabled = sessionReminders ?: false
                                )
                            }
                        } else {
                            settingsViewModel.setRaceRemindersEnabled(false)
                            RaceNotificationCoordinator.syncAllAlarms(
                                context = context,
                                schedule = raceSchedule,
                                isRaceRemindersEnabled = false,
                                isSessionRemindersEnabled = sessionReminders ?: false
                            )
                        }
                    }
                )
                SettingsSwitchItem(
                    icon = Icons.Outlined.Timeline,
                    title = stringResource(R.string.settings_session_reminders),
                    subtitle = stringResource(R.string.settings_session_reminders_sub),
                    isChecked = sessionReminders ?: false,
                    onCheckedChange = { newValue ->
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        if (newValue) {
                            runWithNotificationPermission {
                                settingsViewModel.setSessionRemindersEnabled(true)
                                RaceNotificationCoordinator.syncAllAlarms(
                                    context = context,
                                    schedule = raceSchedule,
                                    isRaceRemindersEnabled = raceReminders ?: false,
                                    isSessionRemindersEnabled = true
                                )
                            }
                        } else {
                            settingsViewModel.setSessionRemindersEnabled(false)
                            RaceNotificationCoordinator.syncAllAlarms(
                                context = context,
                                schedule = raceSchedule,
                                isRaceRemindersEnabled = raceReminders ?: false,
                                isSessionRemindersEnabled = false
                            )
                        }
                    }
                )
                SettingsSwitchItem(
                    icon = Icons.Outlined.NewReleases,
                    title = stringResource(R.string.settings_breaking_news),
                    subtitle = stringResource(R.string.settings_breaking_news_sub),
                    isChecked = breakingNews ?: false,
                    onCheckedChange = {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        if(it) {
                            runWithNotificationPermission {
                                settingsViewModel.setBreakingNewsEnabled(true)
                            }
                        } else {
                            settingsViewModel.setBreakingNewsEnabled(false)
                        }
                    }
                )
                SettingsSwitchItem(
                    icon = Icons.Outlined.RadioButtonChecked,
                    title = stringResource(R.string.settings_live_events),
                    subtitle = stringResource(R.string.settings_live_events_sub),
                    isChecked = liveRaceEvents ?: false,
                    onCheckedChange = {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        if(it) {
                            runWithNotificationPermission {
                                settingsViewModel.setLiveRaceEventsEnabled(true)
                            }
                        } else {
                            settingsViewModel.setLiveRaceEventsEnabled(false)
                        }

                    }
                )
                SettingsSwitchItem(
                    icon = Icons.Outlined.EmojiEvents,
                    title = stringResource(R.string.settings_standings_updates),
                    subtitle = stringResource(R.string.settings_standings_updates_sub),
                    isChecked = standingsUpdates ?: false,
                    onCheckedChange = {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        if(it) {
                            runWithNotificationPermission {
                                settingsViewModel.setStandingsUpdatesEnabled(true)
                            }
                        } else {
                            settingsViewModel.setStandingsUpdatesEnabled(false)
                        }

                    }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // About
            SettingsSection(title = stringResource(R.string.settings_about)) {
                SettingsItem(
                    icon = Icons.Outlined.Info,
                    title = stringResource(id = R.string.title_version),
                    subtitle = "v${BuildConfig.VERSION_NAME}",
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
                SettingsItem(
                    icon = Icons.Outlined.StarRate,
                    title = stringResource(R.string.settings_rate_app),
                    subtitle = "Enjoying Pitwall? Leave a review",
                    onClick = {
                        val uri = "market://details?id=${context.packageName}".toUri()
                        val intent = Intent(Intent.ACTION_VIEW, uri).apply {
                            setPackage("com.android.vending")
                        }
                        try {
                            context.startActivity(intent)
                        } catch (_: Exception) {
                            context.startActivity(
                                Intent(
                                    Intent.ACTION_VIEW,
                                    "https://play.google.com/store/apps/details?id=${context.packageName}".toUri()
                                )
                            )
                        }
                    }
                )
                SettingsItem(
                    icon = Icons.Outlined.Description,
                    title = stringResource(R.string.title_licenses),
                    subtitle = "Third-party libraries used in this app",
                    onClick = { navController.navigate("licenses") }
                )
                SettingsItem(
                    icon = Icons.Outlined.Policy,
                    title = stringResource(R.string.title_privacy),
                    subtitle = "How your data is handled",
                    onClick = { navController.navigate("privacy_policy") }
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Lights out and away we go!",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Pitwall v${BuildConfig.VERSION_NAME} • Made for Fans",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                )
            }

            Spacer(modifier = Modifier.height(F1ScreenPadding.bottomPadding()))
        }
    }
}
}
