package com.jenil.f1comp.ui.theme

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jenil.f1comp.data.local.entity.ConstructorProfileEntity
import com.jenil.f1comp.ui.home.components.TeamLogoCircle

data class ThemeSwatchOption(
    val id: String,
    val label: String,
    val color: Color,
    val logoUrl: String? = null,
    val style: F1ThemeStyle? = null,
    val teamColor: Color? = null
)

private val presetSwatches = listOf(
    ThemeSwatchOption("classic_red", "Classic Red", ClassicRedPrimary, style = F1ThemeStyle.CLASSIC_RED),
    ThemeSwatchOption("racing_blue", "Racing Blue", RacingBluePrimary, style = F1ThemeStyle.RACING_BLUE),
    ThemeSwatchOption("paddock_orange", "Amber", PaddockOrangePrimary, style = F1ThemeStyle.PADDOCK_ORANGE),
    ThemeSwatchOption("midnight_teal", "Midnight", MidnightTealPrimary, style = F1ThemeStyle.MIDNIGHT_TEAL),
)

private val teamSwatches = listOf(
    ThemeSwatchOption(
        id = "team_ferrari",
        label = "Scuderia Ferrari",
        color = TeamFerrari,
        logoUrl = "https://media.formula1.com/content/dam/fom-website/teams/2025/ferrari-logo.png",
        teamColor = TeamFerrari
    ),
    ThemeSwatchOption(
        id = "team_mercedes",
        label = "Mercedes-AMG PETRONAS",
        color = TeamMercedes,
        logoUrl = "https://media.formula1.com/content/dam/fom-website/teams/2025/mercedes-logo.png",
        teamColor = TeamMercedes
    ),
    ThemeSwatchOption(
        id = "team_mclaren",
        label = "McLaren Formula 1",
        color = TeamMcLaren,
        logoUrl = "https://media.formula1.com/content/dam/fom-website/teams/2025/mclaren-logo.png",
        teamColor = TeamMcLaren
    ),
    ThemeSwatchOption(
        id = "team_redbull",
        label = "Oracle Red Bull Racing",
        color = TeamRedBull,
        logoUrl = "https://media.formula1.com/content/dam/fom-website/teams/2025/red-bull-racing-logo.png",
        teamColor = TeamRedBull
    ),
    ThemeSwatchOption(
        id = "team_astonmartin",
        label = "Aston Martin Aramco",
        color = TeamAstonMartin,
        logoUrl = "https://media.formula1.com/content/dam/fom-website/teams/2025/aston-martin-logo.png",
        teamColor = TeamAstonMartin
    ),
    ThemeSwatchOption(
        id = "team_alpine",
        label = "BWT Alpine F1 Team",
        color = TeamAlpine,
        logoUrl = "https://media.formula1.com/content/dam/fom-website/teams/2025/alpine-logo.png",
        teamColor = TeamAlpine
    ),
    ThemeSwatchOption(
        id = "team_williams",
        label = "Williams Racing",
        color = TeamWilliams,
        logoUrl = "https://media.formula1.com/content/dam/fom-website/teams/2025/williams-logo.png",
        teamColor = TeamWilliams
    ),
    ThemeSwatchOption(
        id = "team_rb",
        label = "Visa Cash App RB",
        color = TeamRB,
        logoUrl = "https://media.formula1.com/content/dam/fom-website/teams/2025/rb-logo.png",
        teamColor = TeamRB
    ),
    ThemeSwatchOption(
        id = "team_sauber",
        label = "Audi F1 Team",
        color = TeamAudi,
        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/9/92/Audi-Logo_2016.svg/500px-Audi-Logo_2016.svg.png",
        teamColor = TeamAudi
    ),
    ThemeSwatchOption(
        id = "team_haas",
        label = "MoneyGram Haas F1 Team",
        color = TeamHaas,
        logoUrl = "https://media.formula1.com/content/dam/fom-website/teams/2025/haas-logo.png",
        teamColor = TeamHaas
    ),
    ThemeSwatchOption(
        id = "team_cadillac",
        label = "Cadillac F1 Team",
        color = TeamCadillac,
        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/2/29/Cadillac_logo.svg/500px-Cadillac_logo.svg.png",
        teamColor = TeamCadillac
    )
)

@Composable
fun ThemeSelectionSection(
    selectedId: String,
    onSelect: (ThemeSwatchOption) -> Unit,
    modifier: Modifier = Modifier,
    constructorProfiles: List<ConstructorProfileEntity> = emptyList()
) {
    Column(modifier = modifier.fillMaxWidth()) {

        // ── Preset swatches ──
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(18.dp),
            contentPadding = PaddingValues(horizontal = 16.dp)
        ) {
            items(presetSwatches, key = { it.id }) { option ->
                PresetSwatch(
                    option = option,
                    isSelected = option.id == selectedId,
                    onClick = { onSelect(option) }
                )
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // ── Constructor team themes ──
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Text(
                text = "CONSTRUCTOR TEAM THEMES",
                style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.sp),
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.weight(1f))
            Surface(
                shape = RoundedCornerShape(50),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
            ) {
                Text(
                    text = "2026 Grid",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // ── Scrollable Constructor Team Themes Collection ──
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(teamSwatches, key = { it.id }) { option ->
                val resolvedLogoUrl = resolveLogoUrl(option, constructorProfiles)
                TeamThemeCard(
                    option = option,
                    logoUrl = resolvedLogoUrl,
                    isSelected = option.id == selectedId,
                    onClick = { onSelect(option) }
                )
            }
        }
    }
}

// ── Preset circular swatch, with a "badge" check and Primary/hex subtitle ──

@Composable
private fun PresetSwatch(
    option: ThemeSwatchOption,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(68.dp)
            .clickable(onClick = onClick)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(52.dp)
                .clip(CircleShape)
                .background(option.color)
        ) {
            if (isSelected) {
                Box(
                    modifier = Modifier
                        .size(22.dp)
                        .clip(CircleShape)
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Selected",
                        tint = option.color,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = option.label,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = if (isSelected) "Primary" else option.color.toHexString(),
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            maxLines = 1,
            color = if (isSelected) option.color else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

private fun resolveLogoUrl(option: ThemeSwatchOption, profiles: List<ConstructorProfileEntity>): String? {
    if (profiles.isNotEmpty()) {
        val matchedProfile = profiles.firstOrNull { profile ->
            val name = profile.name.lowercase()
            val id = profile.constructorId.lowercase()
            when (option.id) {
                "team_rb" -> id.contains("rb") || name.contains("rb") || name.contains("visa") || name.contains("vcarb")
                "team_sauber" -> id.contains("sauber") || id.contains("audi") || name.contains("audi") || name.contains("sauber") || name.contains("kick")
                "team_cadillac" -> id.contains("cadillac") || name.contains("cadillac") || name.contains("gm")
                "team_ferrari" -> id.contains("ferrari") || name.contains("ferrari")
                "team_mercedes" -> id.contains("mercedes") || name.contains("mercedes")
                "team_mclaren" -> id.contains("mclaren") || name.contains("mclaren")
                "team_redbull" -> id.contains("red") || name.contains("red bull")
                "team_astonmartin" -> id.contains("aston") || name.contains("aston")
                "team_alpine" -> id.contains("alpine") || name.contains("alpine")
                "team_williams" -> id.contains("williams") || name.contains("williams")
                "team_haas" -> id.contains("haas") || name.contains("haas")
                else -> false
            }
        }
        if (matchedProfile != null && matchedProfile.logo.isNotBlank()) {
            return matchedProfile.logo
        }
    }
    return option.logoUrl
}

// ── Team theme row with Team Logo Circle, selected pill, and livery color subtitle ──

@Composable
private fun TeamThemeCard(
    option: ThemeSwatchOption,
    logoUrl: String?,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val checkTint = if (option.color.luminance() > 0.5f) Color.Black else Color.White

    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        color = if (isSelected) option.color.copy(alpha = 0.08f) else MaterialTheme.colorScheme.surface,
        border = BorderStroke(
            width = if (isSelected) 1.5.dp else 1.dp,
            color = if (isSelected) option.color.copy(alpha = 0.45f)
            else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Team Logo Circle / Badge
            TeamLogoCircle(
                logoUrl = logoUrl,
                teamName = option.label,
                size = 44.dp
            )

            Spacer(modifier = Modifier.width(14.dp))

            // Team name, selected pill, subtitle
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = option.label,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    if (isSelected) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = option.color.copy(alpha = 0.15f),
                            border = BorderStroke(1.dp, option.color.copy(alpha = 0.5f))
                        ) {
                            Text(
                                text = "Selected",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                                fontWeight = FontWeight.Bold,
                                color = option.color,
                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = teamSubtitle(option.id),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Radio indicator
            if (isSelected) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(option.color),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Selected",
                        tint = checkTint,
                        modifier = Modifier.size(14.dp)
                    )
                }
            } else {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .border(
                            width = 1.5.dp,
                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f),
                            shape = CircleShape
                        )
                )
            }
        }
    }
}

private fun teamSubtitle(id: String): String = when (id) {
    "team_ferrari" -> "Rosso Corsa • Giallo Modena"
    "team_mercedes" -> "Petronas Teal • Obsidian"
    "team_mclaren" -> "Papaya Orange • Anthracite"
    "team_redbull" -> "Matte Navy • Crimson Accent"
    "team_astonmartin" -> "Racing Green • Lime"
    "team_alpine" -> "French Blue • BWT Pink"
    "team_williams" -> "Heritage Blue • Navy"
    "team_rb" -> "Royal Blue • White"
    "team_sauber" -> "Titanium Silver • Audi Red"
    "team_haas" -> "Racing Red • Carbon Black"
    "team_cadillac" -> "Carbon Black • Metallic Gold"
    else -> ""
}

private fun Color.toHexString(): String {
    val argb = this.toArgb()
    return "#" + String.format("%06X", 0xFFFFFF and argb)
}