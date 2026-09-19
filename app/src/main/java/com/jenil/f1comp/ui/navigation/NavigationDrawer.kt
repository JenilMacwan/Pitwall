package com.jenil.f1comp.ui.navigation

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Chat
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material.icons.outlined.Equalizer
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Radio
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.test.calendar_add_on
import com.jenil.f1comp.BuildConfig
import com.jenil.f1comp.R
import com.jenil.f1comp.data.local.entity.NextRaceEntity
import com.jenil.f1comp.data.local.entity.ScheduleEntity
import com.jenil.f1comp.data.model.RaceCountdown
import com.jenil.f1comp.ui.chatbot.component.apexMark
import com.jenil.f1comp.ui.state.NextRaceUiState
import com.jenil.f1comp.ui.theme.F1CompTheme
import com.jenil.f1comp.util.FlagImage
import com.jenil.f1comp.util.ProfileUtils
import com.jenil.f1comp.util.syncRacesToCalendar
import com.jenil.f1comp.viewmodel.NextRaceViewModel
import com.jenil.f1comp.viewmodel.ScheduleViewModel
import com.jenil.f1comp.viewmodel.SettingsViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate

@Composable
fun PitwallDrawer(
    currentRoute: String?,
    onNavigate: (String) -> Unit,
    nextRaceViewModel: NextRaceViewModel = hiltViewModel(),
    scheduleViewModel: ScheduleViewModel = hiltViewModel(),
    settingsViewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by nextRaceViewModel.uiState.collectAsStateWithLifecycle()
    val raceSchedule by scheduleViewModel.schedule.collectAsStateWithLifecycle()
    val isDarkModePref by settingsViewModel.isDarkMode.collectAsStateWithLifecycle()
    val isDarkMode = isDarkModePref ?: isSystemInDarkTheme()



    PitwallDrawerContent(
        currentRoute = currentRoute,
        onNavigate = onNavigate,
        uiState = uiState,
        raceSchedule = raceSchedule,
        isDarkMode = isDarkMode,
        onToggleTheme = { newValue ->
            settingsViewModel.toggleDarkMode(newValue)
        }
    )
}

@Composable
fun PitwallDrawerContent(
    currentRoute: String?,
    onNavigate: (String) -> Unit,
    uiState: NextRaceUiState,
    raceSchedule: List<ScheduleEntity>,
    isDarkMode: Boolean,
    onToggleTheme: (Boolean) -> Unit
) {
    val context = LocalContext.current

    val allRaces = remember(raceSchedule) {
        raceSchedule.sortedBy { it.round.toIntOrNull() ?: 0 }
    }

    val totalRaces = remember(raceSchedule) {
        raceSchedule.size.takeIf { it > 0 } ?: 24
    }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val calendarPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val readGranted = permissions[Manifest.permission.READ_CALENDAR] ?: false
        val writeGranted = permissions[Manifest.permission.WRITE_CALENDAR] ?: false

        if (readGranted && writeGranted) {
            syncRacesToCalendar(context, allRaces) { message ->
                scope.launch { snackbarHostState.showSnackbar(message) }
            }
        } else {
            scope.launch {
                snackbarHostState.showSnackbar("Calendar permissions are required to sync.")
            }
        }
    }

    val containerSize = LocalWindowInfo.current.containerSize
    val density = LocalDensity.current
    val screenWidth = with(density) { containerSize.width.toDp() }
    val drawerWidth = (screenWidth - 56.dp).coerceAtMost(360.dp)

    ModalDrawerSheet(
        modifier = Modifier
            .fillMaxHeight()
            .widthIn(max = drawerWidth),
        drawerContainerColor = MaterialTheme.colorScheme.surface,
        drawerContentColor = MaterialTheme.colorScheme.onSurface
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .navigationBarsPadding()
            ) {
                // Pinned Header
                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(R.drawable.ic_app_icon),
                            contentDescription = null,
                            modifier = Modifier.size(44.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "PitWall",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Built for the Obsessed.",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                val nextRace = uiState.nextRace
                if (nextRace != null) {
                    Spacer(modifier = Modifier.height(14.dp))
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp),
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.22f)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                FlagImage(
                                    flagUrl = ProfileUtils.getFlagUrl(emoji = nextRace.flagEmoji),
                                    width = 20.dp,
                                    height = 14.dp
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = nextRace.raceName,
                                    style = MaterialTheme.typography.labelMedium,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    maxLines = 1
                                )
                            }
                            val countdown = nextRace.countdown
                            val countdownText = when {
                                countdown.days > 0 -> "${countdown.days}d ${countdown.hours}h"
                                countdown.hours > 0 -> "${countdown.hours}h ${countdown.minutes}m"
                                else -> "Live soon"
                            }
                            Text(
                                text = countdownText,
                                style = MaterialTheme.typography.labelSmall,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 24.dp),
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Scrollable Navigation Items (In Between Header & Footer)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                ) {
                    // Navigation Sections
                    DrawerSectionHeader("RACE HUB")

                DrawerNavItem(
                    icon = Icons.Outlined.Radio,
                    label = "Team Radio",
                    selected = currentRoute == "team_radio",
                    onClick = { onNavigate("team_radio") }
                )

                DrawerNavItem(
                    icon = Icons.Outlined.EmojiEvents,
                    label = "Race Results",
                    selected = currentRoute?.startsWith("race_result") == true,
                    onClick = { onNavigate("race_result/last/${LocalDate.now().year}") }
                )

                DrawerNavItem(
                    icon = apexMark,
                    label = "Apex AI Assistant",
                    selected = currentRoute == "chatbot",
                    onClick = { onNavigate("chatbot") }
                )

                DrawerNavItem(
                    icon = Icons.Outlined.Equalizer,
                    label = "Live Telemetry",
                    selected = currentRoute == "telemetry",
                    onClick = { onNavigate("telemetry") }
                )

                Spacer(modifier = Modifier.height(8.dp))
                DrawerSectionHeader("UTILITIES")

                DrawerNavItem(
                    icon = calendar_add_on,
                    label = "Sync Calendar",
                    info = totalRaces.toString(),
                    selected = false,
                    onClick = {
                        val hasReadPerm = ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.READ_CALENDAR
                        ) == PackageManager.PERMISSION_GRANTED
                        val hasWritePerm = ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.WRITE_CALENDAR
                        ) == PackageManager.PERMISSION_GRANTED

                        if (hasReadPerm && hasWritePerm) {
                            syncRacesToCalendar(context, allRaces) { message ->
                                scope.launch { snackbarHostState.showSnackbar(message) }
                            }
                        } else {
                            calendarPermissionLauncher.launch(
                                arrayOf(
                                    Manifest.permission.READ_CALENDAR,
                                    Manifest.permission.WRITE_CALENDAR
                                )
                            )
                        }
                    }
                )

                DrawerNavItem(
                    icon = Icons.Rounded.Share,
                    label = "Share Pitwall",
                    selected = false,
                    onClick = {
                        val uri =
                            "https://play.google.com/store/apps/details?id=${context.packageName}".toUri()
                        val intent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_SUBJECT, "PitWall")
                            putExtra(Intent.EXTRA_TEXT, uri.toString())
                        }
                        try {
                            context.startActivity(intent)
                        } catch (_: Exception) {
                        }
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))
                DrawerSectionHeader("PREFERENCES")

                DrawerNavItem(
                    icon = Icons.Outlined.Person,
                    label = "Paddock Profile",
                    selected = currentRoute == "user_profile",
                    onClick = { onNavigate("user_profile") }
                )

                DrawerNavItem(
                    icon = Icons.Outlined.Settings,
                    label = "Settings",
                    selected = currentRoute == "settings",
                    onClick = { onNavigate("settings") }
                )

                DrawerNavItem(
                    icon = Icons.Outlined.Description,
                    label = "Data Attribution",
                    selected = false,
                    onClick = {
                        onNavigate("data_attribution")
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))
            }

            // Pinned Footer
            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 24.dp),
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
            )
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Left side: Connection Status
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // Green Dot
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(
                                        color = Color(0xFF10B981),
                                        shape = CircleShape
                                    )
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "PitWall",
                                style = MaterialTheme.typography.labelSmall,
                                fontFamily = FontFamily.Monospace,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }


                        Text(
                            text = "v${BuildConfig.VERSION_NAME}",
                            style = MaterialTheme.typography.labelSmall,
                            fontFamily = FontFamily.Monospace,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))


                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Left side: Feedback Button
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable {
                                val intent = Intent(Intent.ACTION_SENDTO).apply {
                                    data = "mailto:".toUri()
                                    putExtra(Intent.EXTRA_EMAIL, arrayOf("support.pitwall@gmail.com"))
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
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Outlined.Chat,
                                contentDescription = "Feedback",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Feedback",
                                style = MaterialTheme.typography.labelSmall,
                                fontFamily = FontFamily.Monospace,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        // Right side: Executive Segmented Theme Toggle
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            border = BorderStroke(
                                1.dp,
                                MaterialTheme.colorScheme.outlineVariant
                            ),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.height(38.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(3.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                val haptic = LocalHapticFeedback.current

                                // Dark Segment
                                Surface(
                                    shape = RoundedCornerShape(16.dp),
                                    color = if (isDarkMode) MaterialTheme.colorScheme.primary else Color.Transparent,
                                    modifier = Modifier
                                        .fillMaxHeight()
                                        .width(38.dp)
                                        .clip(RoundedCornerShape(16.dp))
                                        .clickable {
                                            if (!isDarkMode) {
                                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                                onToggleTheme(true)
                                            }
                                        }
                                ) {
                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier = Modifier.fillMaxSize()
                                    ) {
                                        Icon(
                                            imageVector = Icons.Outlined.DarkMode,
                                            contentDescription = "Dark Mode",
                                            tint = if (isDarkMode) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(3.dp))

                                // Light Segment
                                Surface(
                                    shape = RoundedCornerShape(16.dp),
                                    color = if (!isDarkMode) MaterialTheme.colorScheme.primary else Color.Transparent,
                                    modifier = Modifier
                                        .fillMaxHeight()
                                        .width(38.dp)
                                        .clip(RoundedCornerShape(16.dp))
                                        .clickable {
                                            if (isDarkMode) {
                                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                                onToggleTheme(false)
                                            }
                                        }
                                ) {
                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier = Modifier.fillMaxSize()
                                    ) {
                                        Icon(
                                            imageVector = Icons.Outlined.LightMode,
                                            contentDescription = "Light Mode",
                                            tint = if (!isDarkMode) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}


@Composable
private fun DrawerSectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelSmall,
        fontWeight = FontWeight.Bold,
        fontFamily = FontFamily.Monospace,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(start = 24.dp, top = 10.dp, bottom = 4.dp)
    )
}

@Composable
private fun DrawerNavItem(
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    label: String,
    info: String? = null,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val haptic = LocalHapticFeedback.current

    val containerColor = if (selected) {
        MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
    } else {
        Color.Transparent
    }
    val contentColor = if (selected) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 3.dp),
        shape = RoundedCornerShape(50),
        color = containerColor,
        onClick = {
            if (!selected) haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            onClick()
        }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            if (icon != null) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(
                            color = if (selected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(
                                alpha = 0.5f
                            ),
                            shape = RoundedCornerShape(10.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = contentColor,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                color = contentColor
            )
            Spacer(modifier = Modifier.weight(1f))

            if (info != null) {
                Text(
                    text = "$info Races",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                    color = contentColor
                )
            }
        }
    }
}

@Preview
@Composable
fun PitwallDrawerPreview() {
    F1CompTheme {
        PitwallDrawerContent(
            currentRoute = "team_radio",
            onNavigate = {},
            uiState = NextRaceUiState(
                isLoading = false,
                nextRace = NextRaceEntity(
                    id = 1,
                    round = "1",
                    raceName = "Bahrain Grand Prix",
                    circuit = "Bahrain International Circuit",
                    country = "Bahrain",
                    weather = null,
                    countdown = RaceCountdown(days = 5, hours = 12, minutes = 30, seconds = 0),
                    sessionName = "Race",
                    ongoingSession = null,
                    sprint = false,
                    flagEmoji = "🇧🇭"
                ),
                error = null
            ),
            raceSchedule = emptyList(),
            isDarkMode = true,
            onToggleTheme = {}
        )
    }
}