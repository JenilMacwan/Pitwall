package com.jenil.f1comp.ui.settings.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.Sync
import androidx.compose.material.icons.outlined.ViewStream
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.jenil.f1comp.R
import com.jenil.f1comp.ui.F1ScreenPadding
import com.jenil.f1comp.ui.settings.component.SettingsSection
import com.jenil.f1comp.ui.settings.component.SettingsSwitchItem
import com.jenil.f1comp.ui.telemetry.component.CompactTelemetryHeader
import com.jenil.f1comp.ui.telemetry.component.TelemetryHeader
import com.jenil.f1comp.ui.theme.F1CompTheme
import com.jenil.f1comp.viewmodel.SettingsViewModel

@Composable
fun TelemetrySettingsScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    settingsViewModel: SettingsViewModel = hiltViewModel()
) {
    val currentHeaderStyle by settingsViewModel.telemetryHeaderStyle.collectAsStateWithLifecycle()
    val isSyncDelayedPref by settingsViewModel.isTelemetrySyncDelayed.collectAsStateWithLifecycle()
    val activeStyle = currentHeaderStyle ?: "DEFAULT"
    val isSyncDelayed = isSyncDelayedPref ?: false

    val haptic = LocalHapticFeedback.current
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(top = F1ScreenPadding.topPadding())
    ) {
        // Top Navigation Bar
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
                text = stringResource(R.string.title_telemetry_settings),
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
            // Section 1: Header Layout Style
            Text(
                text = stringResource(R.string.telemetry_settings_header_style_label),
                style = MaterialTheme.typography.labelMedium.copy(
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    letterSpacing = 1.sp
                )
            )
            Spacer(modifier = Modifier.height(4.dp))
            SettingsSection(title = stringResource(R.string.telemetry_settings_header_style_desc)) {

                // Option 1: Default Header Option Card
                HeaderStyleCard(
                    title = stringResource(R.string.telemetry_settings_default_header_title),
                    subtitle = stringResource(R.string.telemetry_settings_default_header_subtitle),
                    isSelected = activeStyle == "DEFAULT",
                    icon = Icons.Outlined.Dashboard,
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        settingsViewModel.setTelemetryHeaderStyle("DEFAULT")
                    }
                ) {
                    TelemetryHeader(
                        sessionName = "Singapore GP",
                        sessionType = "Race",
                        trackStatus = "All Clear",
                        currentLap = "34",
                        remainingTime = "48:22",
                        airTemp = "29.4°C",
                        trackTemp = "37.2°C",
                        humidity = "78%",
                        wind = "12 NE"
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Option 2: Compact Header Option Card
                HeaderStyleCard(
                    title = stringResource(R.string.telemetry_settings_compact_header_title),
                    subtitle = stringResource(R.string.telemetry_settings_compact_header_subtitle),
                    isSelected = activeStyle == "COMPACT",
                    icon = Icons.Outlined.ViewStream,
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        settingsViewModel.setTelemetryHeaderStyle("COMPACT")
                    }
                ) {
                    CompactTelemetryHeader(
                        sessionType = "Race",
                        trackStatus = "All Clear",
                        trackTemp = "37.2°C",
                        currentLap = "34",
                        remainingTime = "48:22"
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Section 2: Broadcast Stream Sync Toggle
            SettingsSection(title = stringResource(R.string.telemetry_settings_stream_sync_label)) {
                SettingsSwitchItem(
                    icon = Icons.Outlined.Sync,
                    title = stringResource(R.string.telemetry_settings_sync_title),
                    subtitle = stringResource(R.string.telemetry_settings_sync_subtitle),
                    isChecked = isSyncDelayed,
                    onCheckedChange = { newValue ->
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        settingsViewModel.setTelemetrySyncDelayed(newValue)
                    }
                )
            }

            Spacer(modifier = Modifier.height(F1ScreenPadding.bottomPadding()))
        }
    }
}

@Composable
private fun HeaderStyleCard(
    title: String,
    subtitle: String,
    isSelected: Boolean,
    icon: ImageVector,
    onClick: () -> Unit,
    previewContent: @Composable () -> Unit
) {
    val borderColor = if (isSelected) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
    }

    val containerColor = if (isSelected) {
        MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
    } else {
        MaterialTheme.colorScheme.surface
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(if (isSelected) 2.dp else 1.dp, borderColor),
        colors = CardDefaults.cardColors(containerColor = containerColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = isSelected,
                    onClick = onClick,
                    colors = RadioButtonDefaults.colors(
                        selectedColor = MaterialTheme.colorScheme.primary
                    )
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(start = 48.dp, bottom = 12.dp)
            )

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 8.dp),
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
            )

            // Live Preview of Header
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
            ) {
                previewContent()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TelemetrySettingsScreenPreview() {
    F1CompTheme {
        Column(modifier = Modifier.padding(8.dp)) {
            // Preview placeholder
        }
    }
}
