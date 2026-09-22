package com.jenil.f1comp.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = F1Red,
    onPrimary = LightSurface,
    secondary = PodiumSilver,
    onSecondary = P2Text,
    tertiary = PodiumGold,
    onTertiary = P1Text,
    secondaryContainer = PodiumBronze,
    onSecondaryContainer = P3Text,
    background = DarkBackground,
    onBackground = DarkTextPrimary,
    surface = DarkSurface,
    onSurface = DarkTextPrimary,
    onSurfaceVariant = DarkTextSecondary,
    surfaceVariant = DarkSurfaceVariant,
    outline = DarkOutline,
    outlineVariant = DarkOutline,
    error = DarkError,
    onError = Color(0xFF000000),
)

private val LightColorScheme = lightColorScheme(
    primary = F1Red,
    onPrimary = LightSurface,
    secondary = PodiumSilver,
    onSecondary = P2Text,
    tertiary = PodiumGold,
    onTertiary = P1Text,
    secondaryContainer = PodiumBronze,
    onSecondaryContainer = P3Text,
    background = LightBackground,
    onBackground = LightTextPrimary,
    surface = LightSurface,
    onSurface = LightTextPrimary,
    onSurfaceVariant = LightTextSecondary,
    surfaceVariant = LightSurfaceVariant,
    outline = LightOutline,
    outlineVariant = LightOutline,
    error = LightError,
    onError = Color(0xFFFFFFFF),
)

enum class F1ThemeStyle {
    CLASSIC_RED,
    MIDNIGHT_TEAL,
    PADDOCK_ORANGE,
    RACING_BLUE
}

// --- Dynamic Color Scheme Builders ---

fun buildDarkColorScheme(primary: Color) = darkColorScheme(
    primary = primary,
    onPrimary = if (primary.luminance() > 0.5f) Color.Black else Color.White,
    secondary = PodiumSilver,
    onSecondary = P2Text,
    tertiary = PodiumGold,
    onTertiary = P1Text,
    secondaryContainer = PodiumBronze,
    onSecondaryContainer = P3Text,
    background = DarkBackground,
    onBackground = DarkTextPrimary,
    surface = DarkSurface,
    onSurface = DarkTextPrimary,
    onSurfaceVariant = DarkTextSecondary,
    surfaceVariant = DarkSurfaceVariant,
    outline = DarkOutline,
    outlineVariant = DarkOutline,
    error = DarkError,
    onError = Color.Black,
)

fun buildLightColorScheme(primary: Color) = lightColorScheme(
    primary = primary,
    onPrimary = if (primary.luminance() > 0.5f) Color.Black else Color.White,
    secondary = PodiumSilver,
    onSecondary = P2Text,
    tertiary = PodiumGold,
    onTertiary = P1Text,
    secondaryContainer = PodiumBronze,
    onSecondaryContainer = P3Text,
    background = LightBackground,
    onBackground = LightTextPrimary,
    surface = LightSurface,
    onSurface = LightTextPrimary,
    onSurfaceVariant = LightTextSecondary,
    surfaceVariant = LightSurfaceVariant,
    outline = LightOutline,
    outlineVariant = LightOutline,
    error = LightError,
    onError = Color.White,
)

// --- Swatch ID → Primary Color Resolver ---

fun resolveThemePrimary(swatchId: String?): Color = when (swatchId) {
    // Presets
    "classic_red"       -> ClassicRedPrimary
    "midnight_teal"     -> MidnightTealPrimary
    "paddock_orange"    -> PaddockOrangePrimary
    "racing_blue"       -> RacingBluePrimary
    // Teams
    "team_redbull"      -> TeamRedBull
    "team_ferrari"      -> TeamFerrari
    "team_mercedes"     -> TeamMercedes
    "team_mclaren"      -> TeamMcLaren
    "team_astonmartin"  -> TeamAstonMartin
    "team_alpine"       -> TeamAlpine
    "team_williams"     -> TeamWilliams
    "team_rb"           -> TeamRB
    "team_sauber"       -> TeamAudi
    "team_haas"         -> TeamHaas
    "team_cadillac"     -> TeamCadillac
    // Default
    else                -> F1Red
}

@Composable
fun F1CompTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    primaryColor: Color = F1Red,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        primaryColor == F1Red -> if (darkTheme) DarkColorScheme else LightColorScheme
        darkTheme -> buildDarkColorScheme(primaryColor)
        else -> buildLightColorScheme(primaryColor)
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window

            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
