package com.jenil.f1comp.ui.profile.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.jenil.f1comp.ui.F1ScreenPadding
import com.jenil.f1comp.ui.profile.components.DriverProfileCard
import com.jenil.f1comp.ui.profile.components.ProfileLoadingState
import com.jenil.f1comp.ui.profile.components.ProfileNotFoundState
import com.jenil.f1comp.ui.profile.components.TeamProfileCard
import com.jenil.f1comp.util.ProfileUtils
import com.jenil.f1comp.viewmodel.ConstructorProfileViewModel
import com.jenil.f1comp.viewmodel.DriverProfileViewModel
import com.jenil.f1comp.viewmodel.TeammateHeadtoHeadViewModel

@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    isDriver: Boolean,
    profileId: String,
    driverViewModel: DriverProfileViewModel = hiltViewModel(),
    constructorViewModel: ConstructorProfileViewModel = hiltViewModel(),
    h2hViewModel: TeammateHeadtoHeadViewModel = hiltViewModel(),
) {
    val driverProfiles by driverViewModel.driverProfiles.collectAsStateWithLifecycle()
    val constructorProfiles by constructorViewModel.constructorProfiles.collectAsStateWithLifecycle()
    val h2hDataList by h2hViewModel.headToHeadData.collectAsStateWithLifecycle()

    val driverProfile = remember(driverProfiles, profileId) {
        driverProfiles.find { it.driverId == profileId || it.fullName == profileId || it.lastName == profileId }
    }
    val constructorProfile = remember(constructorProfiles, profileId) {
        constructorProfiles.find { it.constructorId == profileId || it.name == profileId }
    }

    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(top = F1ScreenPadding.topPadding())
    ) {
        // Dynamic Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
            Spacer(modifier = Modifier.width(4.dp))

            val topBarTitle = if (isDriver) {
                "Driver Profile"
            } else {
                "Team Profile"
            }

            Text(
                text = topBarTitle,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1
            )
        }

        // Scrollable Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            if (isDriver) {
                when {
                    driverProfiles.isEmpty() -> ProfileLoadingState()
                    driverProfile != null -> {
                        val h2hData = h2hDataList.find {
                            it.driverA.driverId == driverProfile.driverId || it.driverB.driverId == driverProfile.driverId
                        }
                        DriverProfileCard(
                            driverId = driverProfile.driverId,
                            imageUrl = driverProfile.image,
                            driverName = driverProfile.fullName,
                            teamName = driverProfile.team ?: "Unknown",
                            driverNumber = driverProfile.number,
                            nationality = driverProfile.nationality ?: "Unknown",
                            wins = driverProfile.careerStats?.currentSeason?.wins?.toString() ?: "0",
                            podiums = driverProfile.careerStats?.currentSeason?.podiums?.toString() ?: "0",
                            points = driverProfile.careerStats?.currentSeason?.points ?: "0",
                            pointsProgression = driverProfile.careerStats?.currentSeason?.pointsProgression ?: emptyList(),
                            h2hData = h2hData
                        )
                    }

                    else -> ProfileNotFoundState(profileId)
                }
            } else {
                when {
                    constructorProfiles.isEmpty() -> ProfileLoadingState()
                    constructorProfile != null -> {
                        val leaderPoints = constructorProfiles
                            .mapNotNull {
                                it.careerStats?.currentSeason?.points?.toDoubleOrNull()?.toInt()
                            }
                            .maxOrNull() ?: 0

                        val teamDrivers = constructorProfile.drivers.mapNotNull { driverName ->
                            driverProfiles.find { it.fullName == driverName || it.lastName == driverName }
                        }

                        TeamProfileCard(
                            imageUrl = constructorProfile.logo,
                            carUrl = constructorProfile.car,
                            teamName = constructorProfile.name,
                            constructorId = constructorProfile.constructorId,
                            nationality = constructorProfile.nationality,
                            chassis = ProfileUtils.getChassis(constructorProfile.constructorId),
                            powerUnit = ProfileUtils.getPowerUnit(constructorProfile.constructorId),
                            teamBoss = ProfileUtils.getTeamPrincipal(constructorProfile.constructorId),
                            standing = constructorProfile.careerStats?.currentSeason?.position ?: "0",
                            points = constructorProfile.careerStats?.currentSeason?.points ?: "0",
                            podiums = constructorProfile.careerStats?.currentSeason?.podiums?.toString() ?: "0",
                            wdc = constructorProfile.careerStats?.driverChampionships?.toString() ?: "0",
                            wcc = constructorProfile.careerStats?.constructorChampionships?.toString() ?: "0",
                            leaderPoints = leaderPoints,
                            drivers = teamDrivers,
                            onDriverClick = { driverId ->
                                navController.navigate("profile/true/$driverId")
                            }
                        )
                    }

                    else -> ProfileNotFoundState(profileId)
                }
            }
            Spacer(modifier = Modifier.height(F1ScreenPadding.bottomPadding()))
        }
    }
}