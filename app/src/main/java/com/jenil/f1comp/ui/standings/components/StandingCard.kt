package com.jenil.f1comp.ui.standings.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.jenil.f1comp.data.local.entity.ConstructorStandingsEntity
import com.jenil.f1comp.data.local.entity.DriverStandingsEntity
import com.jenil.f1comp.util.toConstructorLastNames
import com.jenil.f1comp.util.toDriverName

@Composable
fun StandingsCard(
    modifier: Modifier = Modifier,
    navController: NavController,
    driverStandings: List<DriverStandingsEntity>,
    constructorStandings: List<ConstructorStandingsEntity>,
    isConstructorSelected: Boolean
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            if (isConstructorSelected) {
                StandingList(standings = constructorStandings) { constructor ->
                    StandingRowItem(
                        position = constructor.position,
                        name = constructor.name,
                        points = constructor.points.toInt(),
                        team = constructor.drivers.toConstructorLastNames(),
                        imageUrl = constructor.constructorLogo,
                        isConstructor = true,
                        onClick = { navController.navigate("profile/false/${constructor.name}") }
                    )
                }

            } else {
                StandingList(standings = driverStandings) { driver ->
                    StandingRowItem(
                        position = driver.position,
                        name = driver.name.toDriverName(),
                        team = driver.team,
                        points = driver.points.toInt(),
                        imageUrl = driver.driverImage,
                        isConstructor = false,
                        onClick = { navController.navigate("profile/true/${driver.name}") }
                    )
                }
            }
        }
    }
}

@Composable
private fun <T> StandingList(
    standings: List<T>,
    modifier: Modifier = Modifier,
    rowContent: @Composable (T) -> Unit
) {
    Column(modifier = modifier) {
        standings.forEachIndexed { index, item ->
            rowContent(item)
            if (index < standings.lastIndex) {
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 8.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    thickness = 1.dp
                )
            }
        }
    }
}