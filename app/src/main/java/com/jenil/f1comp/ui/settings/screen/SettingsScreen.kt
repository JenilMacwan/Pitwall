package com.jenil.f1comp.ui.settings.screen

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Code
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.NewReleases
import androidx.compose.material.icons.outlined.Palette
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
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.jenil.f1comp.BuildConfig
import com.jenil.f1comp.R
import com.jenil.f1comp.notification.RaceNotificationCoordinator
import com.jenil.f1comp.ui.F1ScreenPadding
import com.jenil.f1comp.ui.settings.component.SettingsItem
import com.jenil.f1comp.ui.settings.component.SettingsSection
import com.jenil.f1comp.ui.settings.component.SettingsSwitchItem
import com.jenil.f1comp.util.LegalConstants
import com.jenil.f1comp.util.openWebUrl
import com.jenil.f1comp.util.removeRacesFromCalendar
import com.jenil.f1comp.util.syncRacesToCalendar
import com.jenil.f1comp.viewmodel.AuthUiState
import com.jenil.f1comp.viewmodel.AuthViewModel
import com.jenil.f1comp.viewmodel.ScheduleViewModel
import com.jenil.f1comp.viewmodel.SettingsViewModel
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: ScheduleViewModel = hiltViewModel(),
    settingsViewModel: SettingsViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val scrollState = rememberScrollState()
    val isDarkModePref by settingsViewModel.isDarkMode.collectAsStateWithLifecycle()
    val isDarkMode = isDarkModePref ?: isSystemInDarkTheme()
    val context = LocalContext.current
    val raceSchedule by viewModel.schedule.collectAsStateWithLifecycle()
    val haptic = LocalHapticFeedback.current

    val currentUser by authViewModel.currentUser.collectAsStateWithLifecycle()
    val authUiState by authViewModel.authUiState.collectAsStateWithLifecycle()
    var showAuthDialog by rememberSaveable { mutableStateOf(false) }
    var showDeleteAccountConfirm by rememberSaveable { mutableStateOf(false) }

    var pendingAction by remember { mutableStateOf<(() -> Unit)?>(null) }
    var showClearConfirm by rememberSaveable { mutableStateOf(false) }
    var isSyncing by remember { mutableStateOf(false) }
    var calendarStatus by rememberSaveable { mutableStateOf<String?>(null) }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val raceReminders by settingsViewModel.isRaceRemindersEnabled.collectAsStateWithLifecycle()
    val sessionReminders by settingsViewModel.isSessionRemindersEnabled.collectAsStateWithLifecycle()
    val breakingNews by settingsViewModel.isBreakingNewsEnabled.collectAsStateWithLifecycle()
    val liveRaceEvents by settingsViewModel.isLiveRaceEventsEnabled.collectAsStateWithLifecycle()
    val standingsUpdates by settingsViewModel.isStandingsUpdatesEnabled.collectAsStateWithLifecycle()
    val favorites by settingsViewModel.isFavoritesUpdatesEnabled.collectAsStateWithLifecycle()
    var notificationPermissionGranted by remember {
        mutableStateOf(
            Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
        )
    }
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        notificationPermissionGranted = Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
    }

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
        notificationPermissionGranted = isGranted
        if (isGranted) {
            pendingAction?.invoke()
        } else {
            scope.launch {
                snackbarHostState.showSnackbar("Notifications are off. Allow them in Android settings to receive race alerts.")
            }
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
        val hasReadPerm = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.READ_CALENDAR
        ) == PackageManager.PERMISSION_GRANTED
        val hasWritePerm = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.WRITE_CALENDAR
        ) == PackageManager.PERMISSION_GRANTED

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
        stringResource(R.string.settings_dark_active)
    } else {
        stringResource(R.string.settings_light_active)
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
            title = { Text(stringResource(R.string.settings_clear_calendar_dialog_title)) },
            text = { Text(stringResource(R.string.settings_clear_calendar_dialog_body)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showClearConfirm = false
                        runWithPermission {
                            isSyncing = true
                            removeRacesFromCalendar(context) { message ->
                                isSyncing = false
                                calendarStatus = message
                                scope.launch { snackbarHostState.showSnackbar(message) }
                            }
                        }
                    }
                ) {
                    Text(stringResource(R.string.common_clear), color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearConfirm = false }) {
                    Text(stringResource(R.string.profile_cancel))
                }
            }
        )
    }

    LaunchedEffect(authUiState) {
        when (val state = authUiState) {
            is AuthUiState.Error -> {
                scope.launch {
                    snackbarHostState.showSnackbar(
                        message = state.message,
                        duration = SnackbarDuration.Short
                    )
                }
                authViewModel.resetAuthUiState()
            }
            is AuthUiState.Success -> {
                scope.launch {
                    snackbarHostState.showSnackbar(
                        message = "Account deleted successfully.",
                        duration = SnackbarDuration.Short
                    )
                }
                authViewModel.resetAuthUiState()
            }
            else -> {}
        }
    }

    if (showDeleteAccountConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteAccountConfirm = false },
            containerColor = MaterialTheme.colorScheme.surface,
            icon = {
                Icon(
                    imageVector = Icons.Rounded.WarningAmber,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error
                )
            },
            title = {
                Text(
                    text = stringResource(R.string.settings_delete_account_dialog_title),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            },
            text = {
                Text(
                    text = stringResource(R.string.settings_delete_account_dialog_msg),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteAccountConfirm = false
                        authViewModel.deleteAccount()
                        scope.launch {
                            snackbarHostState.showSnackbar("Deleting account...")
                        }
                    }
                ) {
                    Text(
                        text = stringResource(R.string.settings_delete_account_confirm),
                        color = MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteAccountConfirm = false }) {
                    Text(
                        text = stringResource(android.R.string.cancel),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        )
    }

    if (showAuthDialog) {
        AlertDialog(
            onDismissRequest = { showAuthDialog = false },
            icon = {
                Icon(
                    imageVector = Icons.Rounded.WarningAmber,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            title = {
                Text(
                    text = stringResource(R.string.auth_required_title),
                    style = MaterialTheme.typography.titleLarge
                )
            },
            text = {
                Text(
                    text = stringResource(R.string.auth_required_favorites_msg),
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showAuthDialog = false
                        navController.navigate("login")
                    }
                ) {
                    Text(stringResource(R.string.auth_sign_in_register))
                }
            },
            dismissButton = {
                TextButton(onClick = { showAuthDialog = false }) {
                    Text(stringResource(android.R.string.cancel))
                }
            }
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Color.Transparent
    ) { innerPadding ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .widthIn(max = 720.dp),
                horizontalAlignment = Alignment.CenterHorizontally
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
                            contentDescription = stringResource(R.string.common_back),
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
                    // Appearance & Theme
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
                        SettingsItem(
                            icon = Icons.Outlined.Palette,
                            title = stringResource(R.string.title_theme),
                            subtitle = stringResource(R.string.settings_theme_subtitle),
                            onClick = { navController.navigate("theme_settings") }
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Telemetry Settings
                    SettingsSection(title = stringResource(R.string.title_telemetry)) {
                        SettingsItem(
                            icon = Icons.Outlined.Dashboard,
                            title = stringResource(R.string.title_telemetry_settings),
                            subtitle = stringResource(R.string.settings_header_style_subtitle),
                            onClick = { navController.navigate("telemetry_settings") }
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    SettingsSection(title = stringResource(R.string.settings_calendar_sync)) {
                        Text(
                            text = calendarStatus
                                ?: "Race times are added in your device time zone to your primary calendar.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
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
                                    isSyncing = true
                                    syncRacesToCalendar(context, raceSchedule) { message ->
                                        isSyncing = false
                                        calendarStatus = message
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
                                    isSyncing = true
                                    syncRacesToCalendar(context, upcoming) { message ->
                                        isSyncing = false
                                        calendarStatus = message
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
                        Text(
                            text = "Race and session reminders are scheduled 15 minutes before start, using your device time zone.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                        )
                        if (!notificationPermissionGranted) {
                            Text(
                                text = "Android notifications are off. Turning on an alert will ask for permission; you can change it later in Android settings.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                            )
                        }
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
                                if (it) {
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
                                if (it) {
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
                                if (it) {
                                    runWithNotificationPermission {
                                        settingsViewModel.setStandingsUpdatesEnabled(true)
                                    }
                                } else {
                                    settingsViewModel.setStandingsUpdatesEnabled(false)
                                }

                            }
                        )
                        SettingsSwitchItem(
                            icon = Icons.Outlined.FavoriteBorder,
                            title = stringResource(R.string.settings_favorites),
                            subtitle = stringResource(R.string.settings_favorites_sub),
                            isChecked = favorites ?: false,
                            onCheckedChange = { isChecked ->
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                val isGuest =
                                    currentUser == null || currentUser?.isAnonymous == true
                                if (isChecked && isGuest) {
                                    showAuthDialog = true
                                } else if (isChecked) {
                                    runWithNotificationPermission {
                                        settingsViewModel.setFavoritesUpdatesEnabled(true)
                                    }
                                } else {
                                    settingsViewModel.setFavoritesUpdatesEnabled(false)
                                }
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // About
                    SettingsSection(title = stringResource(R.string.settings_about)) {
                        SettingsItem(
                            icon = Icons.Outlined.StarRate,
                            title = stringResource(R.string.settings_rate_app),
                            subtitle = stringResource(R.string.settings_rate_app_sub),
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
                            subtitle = stringResource(R.string.settings_licenses_sub),
                            onClick = { navController.navigate("licenses") }
                        )
                        SettingsItem(
                            icon = Icons.Outlined.Policy,
                            title = stringResource(R.string.title_privacy),
                            subtitle = stringResource(R.string.settings_privacy_sub),
                            onClick = { openWebUrl(context, LegalConstants.PRIVACY_POLICY_URL) }
                        )
                        SettingsItem(
                            icon = Icons.Outlined.Description,
                            title = stringResource(R.string.title_terms),
                            subtitle = stringResource(R.string.settings_terms_sub),
                            onClick = { openWebUrl(context, LegalConstants.TERMS_CONDITIONS_URL) }
                        )
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
                    }

                    // Account & Privacy (Delete Account)
                    if (currentUser != null) {
                        Spacer(modifier = Modifier.height(24.dp))

                        SettingsSection(title = stringResource(R.string.settings_account_section)) {
                            SettingsItem(
                                icon = Icons.Outlined.DeleteOutline,
                                title = stringResource(R.string.settings_delete_account),
                                subtitle = stringResource(R.string.settings_delete_account_sub),
                                textColor = MaterialTheme.colorScheme.error,
                                iconTint = MaterialTheme.colorScheme.error,
                                onClick = { showDeleteAccountConfirm = true }
                            )
                        }
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
}
