package com.jenil.f1comp.util

import androidx.compose.ui.graphics.Color
import com.jenil.f1comp.ui.theme.F1Red
import com.jenil.f1comp.ui.theme.TeamAlpine
import com.jenil.f1comp.ui.theme.TeamAstonMartin
import com.jenil.f1comp.ui.theme.TeamAudi
import com.jenil.f1comp.ui.theme.TeamCadillac
import com.jenil.f1comp.ui.theme.TeamFerrari
import com.jenil.f1comp.ui.theme.TeamHaas
import com.jenil.f1comp.ui.theme.TeamMcLaren
import com.jenil.f1comp.ui.theme.TeamMercedes
import com.jenil.f1comp.ui.theme.TeamRB
import com.jenil.f1comp.ui.theme.TeamRedBull
import com.jenil.f1comp.ui.theme.TeamWilliams

object TeamUtils {

    /**
     * Resolves official team color for any constructor or team name string.
     */
    fun getTeamColor(constructorName: String?): Color {
        if (constructorName.isNullOrBlank()) return F1Red
        val name = constructorName.lowercase().trim()
        return when {
            name.contains("red bull") || name.contains("redbull") || name.contains("oracle") -> TeamRedBull
            name.contains("ferrari") || name.contains("scuderia") -> TeamFerrari
            name.contains("mercedes") -> TeamMercedes
            name.contains("mclaren") -> TeamMcLaren
            name.contains("aston") || name.contains("martin") -> TeamAstonMartin
            name.contains("alpine") -> TeamAlpine
            name.contains("williams") -> TeamWilliams
            (name.contains("racing bulls") || name.contains("vcarb") || name.contains("visa") || name.contains("rb") || name.contains("RB F1 Team")) -> TeamRB
            name.contains("audi") || name.contains("sauber") || name.contains("kick") || name.contains("stake") -> TeamAudi
            name.contains("haas") || name.contains("tgr") -> TeamHaas
            name.contains("cadillac") || name.contains("gm") -> TeamCadillac
            else -> F1Red
        }
    }

    /**
     * Resolves short team code or acronym (e.g. "RBR", "SF", "MCL").
     */
    fun getTeamCode(constructorName: String?): String {
        if (constructorName.isNullOrBlank()) return "F1"
        val name = constructorName.lowercase().trim()
        return when {
            name.contains("red bull") || name.contains("redbull") -> "RBR"
            name.contains("ferrari") -> "SF"
            name.contains("mercedes") -> "MGP"
            name.contains("mclaren") -> "MCL"
            name.contains("aston") -> "AMR"
            name.contains("alpine") -> "ALP"
            name.contains("williams") -> "WIL"
            name.contains("racing bulls") || name.contains("vcarb") || name == "rb" -> "RB"
            name.contains("audi") || name.contains("sauber") -> "AUD"
            name.contains("haas") -> "HAS"
            name.contains("cadillac") -> "CAD"
            else -> constructorName.take(3).uppercase()
        }
    }

    /**
     * Calculates positions gained or lost between Grid and Finish position.
     * Returns null if position is invalid/DNS/DNF.
     */
    fun calculateGridDelta(gridStr: String?, positionStr: String?): Int? {
        val grid = gridStr?.toIntOrNull() ?: return null
        val position = positionStr?.toIntOrNull() ?: return null
        if (position <= 0) return null
        val effectiveGrid = if (grid <= 0) 20 else grid
        return effectiveGrid - position // Positive means positions gained, negative means lost, 0 = no change
    }
}
