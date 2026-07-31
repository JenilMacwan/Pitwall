package com.jenil.f1comp.ui.settings.screen

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
import androidx.compose.material.icons.outlined.Code
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Palette
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.jenil.f1comp.R
import com.jenil.f1comp.ui.F1ScreenPadding
import com.jenil.f1comp.ui.settings.component.SettingsItem
import com.jenil.f1comp.ui.settings.component.SettingsSection
import com.jenil.f1comp.ui.settings.component.SettingsSwitchItem

@Composable
fun SettingsScreen(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    var isDarkMode by remember { mutableStateOf(false) }

    val dynamicSubtitle = if (isDarkMode) {
        "Dark theme is currently active"
    }else{
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
                    title =stringResource(id = R.string.title_theme),
                    subtitle = stringResource(id = R.string.sub_title_theme),
                    onClick = { /* TODO */ }
                )
                SettingsSwitchItem(
                    icon = Icons.Outlined.DarkMode,
                    title =stringResource(id = R.string.title_mode),
                    subtitle = dynamicSubtitle,
                    isChecked = isDarkMode,
                    onCheckedChange = { newValue ->
                        isDarkMode = newValue
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