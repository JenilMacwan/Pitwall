package com.jenil.f1comp.ui.settings.screen

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
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
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material.icons.outlined.Flag
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.MailOutline
import androidx.compose.material.icons.outlined.NewReleases
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.Policy
import androidx.compose.material.icons.outlined.RadioButtonChecked
import androidx.compose.material.icons.outlined.Speed
import androidx.compose.material.icons.outlined.StarRate
import androidx.compose.material.icons.outlined.Sync
import androidx.compose.material.icons.outlined.Timeline
import androidx.compose.material.icons.rounded.WarningAmber
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.jenil.f1comp.ui.F1ScreenPadding
import com.jenil.f1comp.ui.settings.component.SettingsItem
import com.jenil.f1comp.ui.settings.component.SettingsSection
import com.jenil.f1comp.ui.settings.component.SettingsSwitchItem
import com.jenil.f1comp.util.removeRacesFromCalendar
import com.jenil.f1comp.util.syncRacesToCalendar
import com.jenil.f1comp.viewmodel.ScheduleViewModel
import com.jenil.f1comp.viewmodel.SettingsViewModel


@Composable
fun SettingsScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: ScheduleViewModel = hiltViewModel(),
    settingsViewModel: SettingsViewModel = hiltViewModel()
) {
    val scrollState = rememberScrollState()
    val isDarkMode by settingsViewModel.isDarkMode.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val raceSchedule by viewModel.schedule.collectAsStateWithLifecycle()
    val haptic = LocalHapticFeedback.current

    var pendingAction by remember { mutableStateOf<(() -> Unit)?>(null) }
    var showClearConfirm by remember { mutableStateOf(false) }
    var isSyncing by remember { mutableStateOf(false) }

    // --- Placeholder notification toggle states ---
    var raceReminders by remember { mutableStateOf(true) }
    var sessionReminders by remember { mutableStateOf(false) }
    var breakingNews by remember { mutableStateOf(true) }
    var liveRaceEvents by remember { mutableStateOf(false) }
    var standingsUpdates by remember { mutableStateOf(true) }

    val calendarPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val readGranted = permissions[Manifest.permission.READ_CALENDAR] ?: false
        val writeGranted = permissions[Manifest.permission.WRITE_CALENDAR] ?: false

        if (readGranted && writeGranted) {
            pendingAction?.invoke()
        }
        pendingAction = null
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

    val dynamicSubtitle = if (isDarkMode == true) {
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
                            removeRacesFromCalendar(context)
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
            // Appearance
            SettingsSection(title = "Appearance") {
                SettingsItem(
                    icon = Icons.Outlined.Palette,
                    title = stringResource(id = R.string.title_theme),
                    subtitle = stringResource(id = R.string.sub_title_theme),
                    onClick = { navController.navigate("theme_settings") }
                )
                SettingsSwitchItem(
                    icon = Icons.Outlined.DarkMode,
                    title = stringResource(id = R.string.title_mode),
                    subtitle = dynamicSubtitle,
                    isChecked = isDarkMode ?: false,
                    onCheckedChange = { newValue ->
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        settingsViewModel.toggleDarkMode(newValue)
                    }
                )
                SettingsItem(
                    icon = Icons.Outlined.Flag,
                    title = "Favorite Team",
                    subtitle = "Theme the app around your constructor",
                    onClick = { navController.navigate("theme_settings") }
                )
                SettingsItem(
                    icon = Icons.Outlined.Language,
                    title = "Language & Region",
                    subtitle = "Date format, driver name display",
                    onClick = { navController.navigate("language_settings") }
                )
                SettingsItem(
                    icon = Icons.Outlined.Speed,
                    title = "Units",
                    subtitle = "Speed and distance format (km/h, mph)",
                    onClick = { navController.navigate("units_settings") }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            SettingsSection(title = "Calendar Sync") {
                SettingsItem(
                    icon = Icons.Outlined.Sync,
                    title = "Sync All Races",
                    subtitle = "Add all season races to your calendar",
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
                    onClick = { showClearConfirm = true }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Notifications
            SettingsSection(title = "Notifications") {
                SettingsSwitchItem(
                    icon = Icons.Outlined.CalendarMonth,
                    title = "Race Start Reminders",
                    subtitle = "Get notified before lights out",
                    isChecked = raceReminders,
                    onCheckedChange = {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        raceReminders = it

                    }
                )
                SettingsSwitchItem(
                    icon = Icons.Outlined.Timeline,
                    title = "Practice & Qualifying Reminders",
                    subtitle = "Alerts before FP1-3 and Qualifying",
                    isChecked = sessionReminders,
                    onCheckedChange = {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        sessionReminders = it
                    }
                )
                SettingsSwitchItem(
                    icon = Icons.Outlined.NewReleases,
                    title = "Breaking News",
                    subtitle = "Driver transfers, penalties, big stories",
                    isChecked = breakingNews,
                    onCheckedChange = {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        breakingNews = it

                    }
                )
                SettingsSwitchItem(
                    icon = Icons.Outlined.RadioButtonChecked,
                    title = "Live Race Events",
                    subtitle = "Safety car, red flags, fastest lap — during races",
                    isChecked = liveRaceEvents,
                    onCheckedChange = {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        liveRaceEvents = it

                    }
                )
                SettingsSwitchItem(
                    icon = Icons.Outlined.EmojiEvents,
                    title = "Standings Updates",
                    subtitle = "When the Drivers' or Constructors' standings change",
                    isChecked = standingsUpdates,
                    onCheckedChange = {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        standingsUpdates = it

                    }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // About
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
                SettingsItem(
                    icon = Icons.Outlined.StarRate,
                    title = "Rate the App",
                    subtitle = "Enjoying F1Companion? Leave a review",
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
                    icon = Icons.Outlined.MailOutline,
                    title = "Send Feedback",
                    subtitle = "Report a bug or suggest a feature",
                    onClick = {
                        val intent = Intent(Intent.ACTION_SENDTO).apply {
                            data = "mailto:".toUri()
                            putExtra(Intent.EXTRA_EMAIL, arrayOf("jenilmacwan29@gmail.com"))
                            putExtra(
                                Intent.EXTRA_SUBJECT,
                                "F1Companion Feedback (v${BuildConfig.VERSION_NAME})"
                            )
                        }
                        try {
                            context.startActivity(intent)
                        } catch (_: Exception) {
                        }
                    }
                )
                SettingsItem(
                    icon = Icons.Outlined.Description,
                    title = "Open Source Licenses",
                    subtitle = "Third-party libraries used in this app",
                    onClick = { navController.navigate("licenses") }
                )
                SettingsItem(
                    icon = Icons.Outlined.Policy,
                    title = "Privacy Policy",
                    subtitle = "How your data is handled",
                    onClick = { navController.navigate("privacy_policy") }
                )
                SettingsItem(
                    icon = Icons.Outlined.Description,
                    title = "Data Attribution",
                    subtitle = "Race data via Jolpica & OpenF1 APIs",
                    onClick = { navController.navigate("data_attribution") }
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
                    text = "F1Companion v${BuildConfig.VERSION_NAME} • Made for Fans",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                )
            }

            Spacer(modifier = Modifier.height(F1ScreenPadding.bottomPadding()))
        }
    }
}
