package com.jenil.f1comp.ui.home.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.jenil.f1comp.data.local.entity.ConstructorStandingsEntity
import com.jenil.f1comp.data.local.entity.DriverStandingsEntity

@Composable
fun TopStandingsCard(
    modifier: Modifier = Modifier,
    driverStandings: List<DriverStandingsEntity>,
    constructorStandings: List<ConstructorStandingsEntity>,
    onViewAllClicked: () -> Unit
) {
    var isDriverSelected by remember { mutableStateOf(true) }

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
            // 2. HEADER ROW
            Column(
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    text = "Standings",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))
                StandingToggle(
                    isDriverSelected = isDriverSelected,
                    onToggle = { isDriverSelected = it }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (isDriverSelected) {
                StandingsList(standings = driverStandings) { driver ->
                    StandingRowItem(
                        position = driver.position,
                        name = driver.name,
                        team = driver.team,
                        nationality = driver.nationality,
                        points = driver.points.toInt(),
                        imageUrl = driver.driverImage
                    )
                }
            } else {
                StandingsList(standings = constructorStandings) { constructor ->
                    StandingRowItem(
                        position = constructor.position,
                        name = constructor.name,
                        nationality = constructor.nationality,
                        points = constructor.points.toInt(),
                        team = constructor.drivers,
                        imageUrl = constructor.constructorLogo
                    )
                }
            }
            // 4. THE FOOTER
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "View More →",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.clickable {
                        onViewAllClicked()
                    }
                )
            }
        }
    }
}

@Composable
private fun <T> StandingsList(
    standings: List<T>,
    modifier: Modifier = Modifier,

    rowContent: @Composable (T) -> Unit
) {
    Column(modifier = modifier) {
        standings.take(3).forEachIndexed { index, item ->

            rowContent(item)

            if (index < 2) {
                HorizontalDivider(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    thickness = 1.dp
                )
            }
        }
    }
}