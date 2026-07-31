package com.jenil.f1comp.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Newspaper
import androidx.compose.material.icons.rounded.BarChart
import androidx.compose.material.icons.rounded.Leaderboard
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem (
    val route: String,
    val title: String,
    val icon: ImageVector
){
    data object Home: BottomNavItem(
        route = "home",
        title = "Home",
        icon = Icons.Default.Home
    )

    data object Standings: BottomNavItem(
        route = "standings",
        title = "Standings",
        icon = Icons.Rounded.Leaderboard
    )

    data object Schedule: BottomNavItem(
        route = "schedule",
        title = "Schedule",
        icon = Icons.Default.DateRange
    )

    data object News: BottomNavItem(
        route = "news",
        title = "News",
        icon = Icons.Default.Newspaper
    )
    companion object{
        val items by lazy {
            listOf(
                Home,
                Standings,
                Schedule,
                News
            )
        }
    }
}