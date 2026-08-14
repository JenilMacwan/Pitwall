package com.jenil.f1comp.ui.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.jenil.f1comp.ui.home.screen.HomeScreen
import com.jenil.f1comp.ui.news.screen.NewsScreen
import com.jenil.f1comp.ui.profile.screen.ProfileScreen
import com.jenil.f1comp.ui.results.screen.RaceResultScreen
import com.jenil.f1comp.ui.schedule.screen.ScheduleScreen
import com.jenil.f1comp.ui.settings.screen.SettingsDetailPlaceholder
import com.jenil.f1comp.ui.settings.screen.SettingsScreen
import com.jenil.f1comp.ui.standings.screen.StandingsScreen
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeSource
import java.time.LocalDate


object BottomNavRoutes {
    val withBottomBar = setOf(
        BottomNavItem.Home.route,
        BottomNavItem.Standings.route,
        BottomNavItem.Schedule.route,
        BottomNavItem.News.route,
    )
}


@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val hazeState = remember { HazeState() }


    val showBottomBar = currentRoute in BottomNavRoutes.withBottomBar

    Box(modifier = Modifier.fillMaxSize()) {
        NavHost(
            navController = navController,
            startDestination = BottomNavItem.Home.route,
            modifier = Modifier
                .fillMaxSize()

                .hazeSource(state = hazeState)
        ) {
            composable(route = BottomNavItem.Home.route) {
                HomeScreen(
                    modifier = Modifier,
                    navController = navController,
                )
            }
            composable(route = BottomNavItem.Standings.route) {
                StandingsScreen(navController = navController)
            }
            composable(route = BottomNavItem.Schedule.route) {
                ScheduleScreen(navController = navController)
            }
            composable(
                route = "race_result/{round}/{year}",
                arguments = listOf(
                    navArgument("round") { type = NavType.StringType },
                    navArgument("year") { type = NavType.IntType }
                )
            ) { backStackEntry ->
                val round = backStackEntry.arguments?.getString("round") ?: return@composable
                val year = backStackEntry.arguments?.getInt("year") ?: LocalDate.now().year
                RaceResultScreen(round = round, year = year, navController = navController)
            }

            composable(route = BottomNavItem.News.route) {
                NewsScreen()
            }
            composable(route = "settings") {
                SettingsScreen(navController = navController)
            }
            composable(route = "theme_settings") {
                SettingsDetailPlaceholder(title = "Appearance & Theme", navController = navController)
            }
            composable(route = "language_settings") {
                SettingsDetailPlaceholder(title = "Language & Region", navController = navController)
            }
            composable(route = "units_settings") {
                SettingsDetailPlaceholder(title = "Units", navController = navController)
            }
            composable(route = "licenses") {
                SettingsDetailPlaceholder(title = "Open Source Licenses", navController = navController)
            }
            composable(route = "privacy_policy") {
                SettingsDetailPlaceholder(title = "Privacy Policy", navController = navController)
            }
            composable(route = "data_attribution") {
                SettingsDetailPlaceholder(title = "Data Attribution", navController = navController)
            }
            composable(
                route = "profile/{isDriver}/{id}",
                arguments = listOf(
                    navArgument("isDriver") { type = NavType.BoolType },
                    navArgument("id") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val isDriver = backStackEntry.arguments?.getBoolean("isDriver") ?: true
                val id = backStackEntry.arguments?.getString("id") ?: ""
                ProfileScreen(navController = navController, isDriver = isDriver, profileId = id)
            }
        }

        AnimatedVisibility(
            visible = showBottomBar,
            enter = fadeIn(tween(220)) + slideInVertically(
                animationSpec = tween(280),
                initialOffsetY = { it / 2 }
            ),
            exit = fadeOut(tween(180)) + slideOutVertically(
                animationSpec = tween(220),
                targetOffsetY = { it / 2 }
            ),
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            F1BottomNavigationBar(
                currentRoute = currentRoute,
                hazeState = hazeState,
                onNavigate = { targetRoute ->
                    navController.navigate(targetRoute) {
                        popUpTo(BottomNavItem.Home.route) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}