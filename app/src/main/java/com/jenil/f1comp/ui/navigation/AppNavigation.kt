package com.jenil.f1comp.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.jenil.f1comp.ui.home.screen.HomeScreen
import com.jenil.f1comp.ui.news.screen.NewsScreen
import com.jenil.f1comp.ui.schedule.screen.ScheduleScreen
import com.jenil.f1comp.ui.settings.screen.SettingsScreen
import com.jenil.f1comp.ui.standings.screen.StandingsScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Box(modifier = Modifier.fillMaxSize()) {
        NavHost(
            navController = navController,
            startDestination = BottomNavItem.Home.route,
            modifier = Modifier.fillMaxSize()
        ) {
            composable(route = BottomNavItem.Home.route) {
                HomeScreen(
                    modifier = Modifier,
                    navController = navController,
                )
            }
            composable(route = BottomNavItem.Standings.route) {
                StandingsScreen()
            }
            composable(route = BottomNavItem.Schedule.route) {
                ScheduleScreen()
            }
            composable(route = BottomNavItem.News.route) {
                NewsScreen()
            }
            composable(route = "settings") {
                SettingsScreen(navController = navController)
            }
        }

        F1BottomNavigationBar(
            currentRoute = currentRoute,
            onNavigate = { targetRoute ->
                navController.navigate(targetRoute) {
                    popUpTo(BottomNavItem.Home.route) { saveState = true }
                    launchSingleTop = true
                    restoreState = true
                }
            },
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}