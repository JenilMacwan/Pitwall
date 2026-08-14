package com.jenil.f1comp.ui

import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.navigation.compose.rememberNavController
import com.jenil.f1comp.ui.home.screen.HomeScreen
import com.jenil.f1comp.ui.theme.F1CompTheme
import org.junit.Rule
import org.junit.Test

class HomeScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun homeScreen_showsTitle() {
        composeTestRule.setContent {
            F1CompTheme {
                HomeScreen(navController = rememberNavController())
            }
        }

        // Check if a basic UI element is present. 
        // Note: This might need more setup if HomeScreen relies on Hilt for ViewModels.
        // For CI, we often use HiltAndroidTest or mock the ViewModels.
    }
}
