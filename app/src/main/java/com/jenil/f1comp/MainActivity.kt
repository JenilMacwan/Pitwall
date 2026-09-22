package com.jenil.f1comp

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.firebase.Firebase
import com.google.firebase.analytics.analytics
import com.jenil.f1comp.ui.navigation.AppNavigation
import com.jenil.f1comp.ui.theme.F1CompTheme
import com.jenil.f1comp.ui.theme.resolveThemePrimary
import com.jenil.f1comp.viewmodel.SettingsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val requestNotificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { _ ->
        // Permission result handled
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        logNotificationClick(intent)
        enableEdgeToEdge()

        requestNotificationPermissionOnFirstLaunch()

        setContent {
            val settingsViewModel: SettingsViewModel = hiltViewModel()
            val isDarkModePref by settingsViewModel.isDarkMode.collectAsStateWithLifecycle()
            val selectedThemeId by settingsViewModel.selectedThemeId.collectAsStateWithLifecycle()

            val isDarkMode = isDarkModePref ?: isSystemInDarkTheme()
            val primaryColor = resolveThemePrimary(selectedThemeId)

            F1CompTheme(darkTheme = isDarkMode, primaryColor = primaryColor) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation()
                }
            }
        }
    }

    private fun requestNotificationPermissionOnFirstLaunch() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val hasPermission = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED

            if (!hasPermission) {
                requestNotificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        logNotificationClick(intent)
    }

    private fun logNotificationClick(intent: Intent?) {
        intent?.getStringExtra("fcm_click_tracking")?.let { type ->
            val bundle = Bundle().apply {
                putString("notification_type", type)
            }
            Firebase.analytics.logEvent("fcm_notification_opened", bundle)
        }
    }
}
