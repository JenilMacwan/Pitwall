package com.jenil.f1comp.util

import androidx.compose.ui.graphics.Color
import com.jenil.f1comp.data.local.entity.TeamRadioEntity
import com.jenil.f1comp.ui.radio.components.RadioClip
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

fun mapDriverDetails(code: String, driverNumber: Int): Triple<String, String, Color> {
    val cleanCode = code.uppercase().trim()
    return when {
        cleanCode == "VER" || driverNumber == 3 -> Triple("Max Verstappen", "Red Bull Racing", TeamRedBull)
        cleanCode == "NOR" || driverNumber == 4 || driverNumber == 1 -> Triple("Lando Norris", "McLaren", TeamMcLaren)
        cleanCode == "PIA" || driverNumber == 81 -> Triple("Oscar Piastri", "McLaren", TeamMcLaren)
        cleanCode == "LEC" || driverNumber == 16 -> Triple("Charles Leclerc", "Ferrari", TeamFerrari)
        cleanCode == "HAM" || driverNumber == 44 -> Triple("Lewis Hamilton", "Ferrari", TeamFerrari)
        cleanCode == "RUS" || driverNumber == 63 -> Triple("George Russell", "Mercedes", TeamMercedes)
        cleanCode == "ANT" || driverNumber == 12 -> Triple("Kimi Antonelli", "Mercedes", TeamMercedes)
        cleanCode == "ALO" || driverNumber == 14 -> Triple("Fernando Alonso", "Aston Martin", TeamAstonMartin)
        cleanCode == "STR" || driverNumber == 18 -> Triple("Lance Stroll", "Aston Martin", TeamAstonMartin)
        cleanCode == "GAS" || driverNumber == 10 -> Triple("Pierre Gasly", "Alpine", TeamAlpine)
        cleanCode == "COL" || driverNumber == 43 -> Triple("Franco Colapinto", "Alpine", TeamAlpine)
        cleanCode == "BOR" || driverNumber == 5 -> Triple("Gabriel Bortoleto", "Audi", TeamAudi)
        cleanCode == "HUL" || driverNumber == 27 -> Triple("Nico Hülkenberg", "Audi", TeamAudi)
        cleanCode == "BOT" || driverNumber == 77 -> Triple("Valtteri Bottas", "Cadillac", TeamCadillac)
        cleanCode == "PER" || driverNumber == 11 -> Triple("Sergio Pérez", "Cadillac", TeamCadillac)
        cleanCode == "BEA" || driverNumber == 87 -> Triple("Oliver Bearman", "Haas", TeamHaas)
        cleanCode == "OCO" || driverNumber == 31 -> Triple("Esteban Ocon", "Haas", TeamHaas)
        cleanCode == "LIN" || driverNumber == 41 -> Triple("Arvid Lindblad", "RB", TeamRB)
        cleanCode == "LAW" || driverNumber == 30 -> Triple("Liam Lawson", "RB", TeamRB)
        cleanCode == "HAD" || driverNumber == 6 -> Triple("Isack Hadjar", "Red Bull", TeamRedBull)
        cleanCode == "ALB" || driverNumber == 23 -> Triple("Alex Albon", "Williams", TeamWilliams)
        cleanCode == "SAI" || driverNumber == 55 -> Triple("Carlos Sainz", "Williams", TeamWilliams)
        else -> Triple("Driver #$driverNumber", "F1 Team", TeamRedBull)
    }
}

fun formatTimestampToTime(timestamp: String): String {
    return try {
        if (timestamp.contains("T")) {
            val timePart = timestamp.substringAfter("T").substringBefore(".")
            val parts = timePart.split(":")
            if (parts.size >= 2) "${parts[0]}:${parts[1]}" else timePart
        } else {
            timestamp
        }
    } catch (_: Exception) {
        "Live"
    }
}
fun TeamRadioEntity.toRadioClip(): RadioClip {
    val (driverName, teamName, teamColor) = mapDriverDetails(driverCode, driverNumber)
    val timeFormatted = formatTimestampToTime(timestamp)
    val sessionLabel = sessionName ?: eventName ?: "Race Session"

    return RadioClip(
        id = radioUrl,
        driverCode = driverCode.ifEmpty { "P$driverNumber" },
        driverName = driverName,
        teamName = teamName,
        teamColor = teamColor,
        lapLabel = "$sessionLabel · $timeFormatted",
        quote = "Team Radio Transmission ($driverCode #$driverNumber)",
        durationLabel = "Radio",
        audioUrl = radioUrl
    )
}