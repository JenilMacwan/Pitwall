package com.jenil.f1comp.ui.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.jenil.f1comp.ui.auth.screen.LoginScreen
import com.jenil.f1comp.ui.chatbot.screen.ChatbotScreen
import com.jenil.f1comp.ui.home.screen.HomeScreen
import com.jenil.f1comp.ui.news.screen.NewsScreen
import com.jenil.f1comp.ui.profile.screen.ProfileScreen
import com.jenil.f1comp.ui.profile.screen.UserProfileScreen
import com.jenil.f1comp.ui.radio.screen.TeamRadioScreen
import com.jenil.f1comp.ui.results.screen.RaceResultScreen
import com.jenil.f1comp.ui.schedule.screen.ScheduleScreen
import com.jenil.f1comp.ui.settings.screen.DataAttributionScreen
import com.jenil.f1comp.ui.settings.screen.LanguageSettingsScreen
import com.jenil.f1comp.ui.settings.screen.LicensesScreen
import com.jenil.f1comp.ui.settings.screen.PrivacyPolicyScreen
import com.jenil.f1comp.ui.settings.screen.SettingsDetailPlaceholder
import com.jenil.f1comp.ui.settings.screen.SettingsScreen
import com.jenil.f1comp.ui.settings.screen.ThemeSettingsScreen
import com.jenil.f1comp.ui.standings.screen.StandingsScreen
import com.jenil.f1comp.viewmodel.SettingsViewModel
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeSource
import kotlinx.coroutines.launch
import java.time.LocalDate

object BottomNavRoutes {
    val withBottomBar = setOf(
        BottomNavItem.Home.route,
        BottomNavItem.Standings.route,
        BottomNavItem.Schedule.route,
        BottomNavItem.News.route,
    )
}
private const val DEEP_LINK_BASE = "f1comp://"

@Composable
fun AppNavigation(
    settingsViewModel: SettingsViewModel = hiltViewModel()
) {
    val isFirstLaunch by settingsViewModel.isFirstLaunch.collectAsStateWithLifecycle()

    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val hazeState = remember { HazeState() }

    val showBottomBar = currentRoute in BottomNavRoutes.withBottomBar
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    if (isFirstLaunch == null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF0F0F0F)),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        }
        return
    }

    val initialDestination = if (isFirstLaunch == true) "login" else BottomNavItem.Home.route

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            PitwallDrawer(
                currentRoute = currentRoute,
                onNavigate = { route ->
                    scope.launch{ drawerState.close() }
                    navController.navigate(route) {
                        launchSingleTop = true
                    }
                }
            )
        }
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            NavHost(
                navController = navController,
                startDestination = initialDestination,
                modifier = Modifier
                    .fillMaxSize()
                    .hazeSource(state = hazeState)
            ) {
                composable(
                    route = BottomNavItem.Home.route,
                    deepLinks = listOf(
                        navDeepLink { uriPattern = "${DEEP_LINK_BASE}home" }
                    )
                ) {
                    HomeScreen(
                        modifier = Modifier,
                        navController = navController,
                        onDrawerClick = {
                            scope.launch {
                                drawerState.open()
                            }
                        },
                        onDetailClick = {
                            navController.navigate(BottomNavItem.Schedule.route) {
                                popUpTo(BottomNavItem.Home.route) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }

                composable(
                    route = BottomNavItem.Standings.route,
                    deepLinks = listOf(
                        navDeepLink { uriPattern = "${DEEP_LINK_BASE}standings" }
                    )
                ) {
                    StandingsScreen(
                        navController = navController,
                        onDrawerClick = {
                            scope.launch {
                                drawerState.open()
                            }
                        }
                    )
                }

                composable(route = BottomNavItem.Schedule.route) {
                    ScheduleScreen(
                        navController = navController,
                        onDrawerClick = {
                            scope.launch {
                                drawerState.open()
                            }
                        }
                    )
                }

                composable(
                    route = "race_result/{round}/{year}",
                    arguments = listOf(
                        navArgument("round") { type = NavType.StringType },
                        navArgument("year") { type = NavType.IntType }
                    ),
                    deepLinks = listOf(
                        navDeepLink { uriPattern = "${DEEP_LINK_BASE}race_result/{round}/{year}" }
                    )
                ) { backStackEntry ->
                    val round = backStackEntry.arguments?.getString("round") ?: return@composable
                    val year = backStackEntry.arguments?.getInt("year") ?: LocalDate.now().year
                    RaceResultScreen(round = round, year = year, navController = navController)
                }

                composable(
                    route = BottomNavItem.News.route,
                    deepLinks = listOf(
                        navDeepLink { uriPattern = "${DEEP_LINK_BASE}news" },
                        navDeepLink { uriPattern = "${DEEP_LINK_BASE}news?url={url}" }
                    )
                ) {
                    NewsScreen(
                        onDrawerClick = {
                            scope.launch {
                                drawerState.open()
                            }
                        }
                    )
                }

                composable(route = "settings") {
                    SettingsScreen(navController = navController)
                }
                composable(route = "theme_settings") {
                    ThemeSettingsScreen(navController = navController)
                }
                composable(route = "language_settings") {
                    LanguageSettingsScreen(navController = navController)
                }
                composable(route = "licenses") {
                    LicensesScreen(navController = navController)
                }
                composable(route = "privacy_policy") {
                    PrivacyPolicyScreen(navController = navController)
                }
                composable(route = "data_attribution") {
                    DataAttributionScreen(navController = navController)
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
                    ProfileScreen(
                        navController = navController,
                        isDriver = isDriver,
                        profileId = id
                    )
                }
                composable(route = "chatbot") {
                    ChatbotScreen(
                        navController = navController
                    )
                }
                composable(route = "team_radio") {
                    TeamRadioScreen(
                        navController = navController
                    )
                }
                composable(route = "login") {
                    LoginScreen(
                        navController = navController,
                        initialTab = "Sign-In"
                    )
                }
                composable(route = "register") {
                    LoginScreen(
                        navController = navController,
                        initialTab = "Register"
                    )
                }
                composable(route = "user_profile") {
                    UserProfileScreen(
                        navController = navController
                    )
                }
                composable(route = "telemetry") {
                    SettingsDetailPlaceholder(
                        title = "Live Telemetry",
                        navController = navController
                    )
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

//            AnimatedVisibility(
//                visible = showBottomBar,
//                enter = fadeIn(tween(220)) + scaleIn(tween(280)),
//                exit = fadeOut(tween(180)) + scaleOut(tween(220)),
//                modifier = Modifier
//                    .align(Alignment.BottomEnd)
//                    .navigationBarsPadding()
//                    .padding(end = 24.dp, bottom = 96.dp)
//            ) {
//                ApexFab(
//                    onClick = { navController.navigate("chatbot") }
//                )
//            }
        }
    }
}