package com.jenil.f1comp.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp


data class ThemeSwatchOption(
    val id: String,
    val label: String,
    val color: Color,
    val style: F1ThemeStyle? = null,   // set for curated presets
    val teamColor: Color? = null       // set for "favorite team" options
)

private val presetSwatches = listOf(
    ThemeSwatchOption("classic_red", "Classic", ClassicRedPrimary, style = F1ThemeStyle.CLASSIC_RED),
    ThemeSwatchOption("midnight_teal", "Midnight", MidnightTealPrimary, style = F1ThemeStyle.MIDNIGHT_TEAL),
    ThemeSwatchOption("paddock_orange", "Paddock", PaddockOrangePrimary, style = F1ThemeStyle.PADDOCK_ORANGE),
    ThemeSwatchOption("racing_blue", "Racing", RacingBluePrimary, style = F1ThemeStyle.RACING_BLUE),
)

private val teamSwatches = listOf(
    ThemeSwatchOption("team_redbull", "Red Bull", TeamRedBull, teamColor = TeamRedBull),
    ThemeSwatchOption("team_ferrari", "Ferrari", TeamFerrari, teamColor = TeamFerrari),
    ThemeSwatchOption("team_mercedes", "Mercedes", TeamMercedes, teamColor = TeamMercedes),
    ThemeSwatchOption("team_mclaren", "McLaren", TeamMcLaren, teamColor = TeamMcLaren),
    ThemeSwatchOption("team_astonmartin", "Aston Martin", TeamAstonMartin, teamColor = TeamAstonMartin),
    ThemeSwatchOption("team_alpine", "Alpine", TeamAlpine, teamColor = TeamAlpine),
    ThemeSwatchOption("team_williams", "Williams", TeamWilliams, teamColor = TeamWilliams),
    ThemeSwatchOption("team_rb", "RB", TeamRB, teamColor = TeamRB),
    ThemeSwatchOption("team_sauber", "Sauber", TeamAudi, teamColor = TeamAudi),
    ThemeSwatchOption("team_haas", "Haas", TeamHaas, teamColor = TeamHaas),
    ThemeSwatchOption("team_cadillac", "Cadillac", TeamCadillac, teamColor = TeamCadillac)
)
@Composable
fun ThemeSelectionSection(
    selectedId: String,
    onSelect: (ThemeSwatchOption) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "App Theme",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
        SwatchRow(
            options = presetSwatches,
            selectedId = selectedId,
            onSelect = onSelect
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Team Colors",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
        Text(
            text = "Theme the app around your favorite constructor",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        SwatchRow(
            options = teamSwatches,
            selectedId = selectedId,
            onSelect = onSelect
        )
    }
}

@Composable
private fun SwatchRow(
    options: List<ThemeSwatchOption>,
    selectedId: String,
    onSelect: (ThemeSwatchOption) -> Unit
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        items(options, key = { it.id }) { option ->
            ColorSwatch(
                option = option,
                isSelected = option.id == selectedId,
                onClick = { onSelect(option) }
            )
        }
    }
}

@Composable
private fun ColorSwatch(
    option: ThemeSwatchOption,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val checkTint = if (option.color.luminance() > 0.5f) Color.Black else Color.White

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(64.dp)
            .clickable(onClick = onClick)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(option.color)
                .border(
                    width = if (isSelected) 2.dp else 1.dp,
                    color = if (isSelected)
                        MaterialTheme.colorScheme.onBackground
                    else
                        MaterialTheme.colorScheme.outline,
                    shape = CircleShape
                )
        ) {
            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Selected",
                    tint = checkTint,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = option.label,
            style = MaterialTheme.typography.labelSmall,
            maxLines = 1,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThemeSettingsScreenExample() {
    var selectedId by remember { mutableStateOf("classic_red") }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Appearance") })
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            ThemeSelectionSection(
                selectedId = selectedId,
                onSelect = { option ->
                    selectedId = option.id
                    // option.style (preset) or option.teamColor (team mode)
                    // for use in F1CompTheme(themeStyle = ..., favoriteTeamColor = ...)
                }
            )
        }
    }
}