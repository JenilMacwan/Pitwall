package com.jenil.f1comp

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.firebase.Firebase
import com.google.firebase.analytics.analytics
import com.jenil.f1comp.ui.navigation.AppNavigation
import com.jenil.f1comp.ui.theme.F1CompTheme
import com.jenil.f1comp.viewmodel.SettingsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        logNotificationClick(intent)
        enableEdgeToEdge()
        setContent {
            val settingsViewModel: SettingsViewModel = hiltViewModel()
            val isDarkModePref by settingsViewModel.isDarkMode.collectAsStateWithLifecycle()

            val isDarkMode = isDarkModePref ?: isSystemInDarkTheme()

            F1CompTheme(darkTheme = isDarkMode) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation()
                }
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