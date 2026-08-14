package com.jenil.f1comp.ui.profile.screen

import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.jenil.f1comp.ui.F1ScreenPadding
import com.jenil.f1comp.ui.profile.components.DriverProfileCard
import com.jenil.f1comp.ui.profile.components.TeamProfileCard
import com.jenil.f1comp.util.ProfileUtils
import com.jenil.f1comp.viewmodel.ConstructorProfileViewModel
import com.jenil.f1comp.viewmodel.DriverProfileViewModel

@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    isDriver: Boolean,
    profileId: String,
    driverViewModel: DriverProfileViewModel = hiltViewModel(),
    constructorViewModel: ConstructorProfileViewModel = hiltViewModel()
) {
    val driverProfiles by driverViewModel.driverProfiles.collectAsStateWithLifecycle()
    val constructorProfiles by constructorViewModel.constructorProfiles.collectAsStateWithLifecycle()

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
        // Top bar (Static)
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
            Spacer(modifier = Modifier.width(5.dp))
            Text(
                text = if (isDriver) "Driver Profile" else "Team Profile",
                style = MaterialTheme.typography.titleLarge,
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
            Spacer(modifier = Modifier.height(16.dp))

            if (isDriver) {
                driverProfile?.let { profile ->
                    DriverProfileCard(
                        imageUrl = profile.image,
                        driverName = profile.fullName,
                        teamName = profile.team ?: "Unknown",
                        driverNumber = profile.number,
                        nationality = profile.nationality ?: "Unknown",
                        wins = profile.careerStats?.currentSeason?.wins?.toString() ?: "0",
                        podiums = profile.careerStats?.currentSeason?.podiums?.toString() ?: "0",
                        points = profile.careerStats?.currentSeason?.points ?: "0"
                    )
                } ?: Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = "Driver profile not found ($profileId)")
                }
            } else {
                constructorProfile?.let { profile ->
                    val leaderPoints = constructorProfiles
                        .mapNotNull { it.careerStats?.currentSeason?.points?.toDoubleOrNull()?.toInt() }
                        .maxOrNull() ?: 0

                    val teamDrivers = profile.drivers.mapNotNull { driverName ->
                        driverProfiles.find { it.fullName == driverName || it.lastName == driverName }
                    }

                    TeamProfileCard(
                        imageUrl = profile.logo,
                        carUrl = profile.car,
                        teamName = profile.name,
                        nationality = profile.nationality,
                        chassis = ProfileUtils.getChassis(profile.constructorId),
                        powerUnit = ProfileUtils.getPowerUnit(profile.constructorId),
                        teamBoss = ProfileUtils.getTeamPrincipal(profile.constructorId),
                        standing = profile.careerStats?.currentSeason?.position ?: "0",
                        points = profile.careerStats?.currentSeason?.points ?: "0",
                        podiums = profile.careerStats?.currentSeason?.podiums?.toString() ?: "0",
                        wdc = profile.careerStats?.driverChampionships?.toString() ?: "0",
                        wcc = profile.careerStats?.constructorChampionships?.toString() ?: "0",
                        leaderPoints = leaderPoints,
                        drivers = teamDrivers
                    )
                } ?: Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = "Team profile not found ($profileId)")
                }
            }
            Spacer(modifier = Modifier.height(F1ScreenPadding.bottomPadding()))
        }
    }
}
