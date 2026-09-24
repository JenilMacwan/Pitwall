package com.jenil.f1comp.util

import android.content.Context
import androidx.compose.ui.graphics.Color
import com.jenil.f1comp.data.local.entity.ConstructorProfileEntity
import com.jenil.f1comp.data.local.entity.DriverProfileEntity
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
     * Resolves short code or acronym of the driver.
     */
    fun getDriverCode(driverName: String?): String {
        if (driverName.isNullOrBlank()) return "00"
        val name = driverName.lowercase().trim()
        return when {
            name.contains("norris") -> "1"
            name.contains("piastri") -> "81"
            name.contains("hamilton") -> "44"
            name.contains("leclerc") -> "16"
            name.contains("russell") -> "63"
            name.contains("antonelli") -> "12"
            name.contains("verstappen") -> "3"
            name.contains("hadjar") -> "6"
            name.contains("stroll") -> "18"
            name.contains("alonso") -> "14"
            name.contains("sainz") -> "55"
            name.contains("albon") -> "23"
            name.contains("gasly") -> "10"
            name.contains("colapinto") -> "43"
            name.contains("lawson") -> "30"
            name.contains("lindblad") -> "41"
            name.contains("ocon") -> "31"
            name.contains("bearman") -> "87"
            name.contains("hülkenberg") -> "27"
            name.contains("bortoleto") -> "5"
            name.contains("pérez") -> "11"
            name.contains("bottas") -> "77"
            name.contains("tsunoda") -> "22"
            else -> "00"
        }
    }

    fun getDriverCodeColor(driverName: String?): Color {
        if (driverName.isNullOrBlank()) return Color.White
        val name = driverName.lowercase().trim()
        return when {
            name.contains("russell") -> Color.Black
            name.contains("antonelli") ->  Color.Black
            name.contains("ocon") -> Color.Black
            name.contains("bearman") -> Color.Black
            else -> Color.White
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

    /**
     * Resolves official team logo URL from ConstructorProfileEntity or fallback static mapping.
     */
    fun getTeamLogoUrl(
        teamName: String?,
        constructorProfiles: List<ConstructorProfileEntity> = emptyList()
    ): String? {
        if (teamName.isNullOrBlank()) return null
        val cleanName = teamName.lowercase().trim()

        // 1. Try matching from loaded ConstructorProfileEntity list
        val matchedProfile = constructorProfiles.firstOrNull { profile ->
            val pName = profile.name.lowercase().trim()
            cleanName.contains(pName) || pName.contains(cleanName) ||
                    (cleanName.contains("ferrari") && pName.contains("ferrari")) ||
                    (cleanName.contains("red bull") && pName.contains("red bull")) ||
                    (cleanName.contains("mercedes") && pName.contains("mercedes")) ||
                    (cleanName.contains("mclaren") && pName.contains("mclaren")) ||
                    (cleanName.contains("aston") && pName.contains("aston")) ||
                    (cleanName.contains("alpine") && pName.contains("alpine")) ||
                    (cleanName.contains("williams") && pName.contains("williams")) ||
                    (cleanName.contains("racing bulls") && pName.contains("rb")) ||
                    (cleanName.contains("audi") && pName.contains("audi")) ||
                    (cleanName.contains("haas") && pName.contains("haas")) ||
                    (cleanName.contains("cadillac") && pName.contains("cadillac"))
        }
        if (matchedProfile != null && matchedProfile.logo.isNotBlank()) {
            return matchedProfile.logo
        }

        // 2. Fallback static URL mapping
        return when {
            cleanName.contains("ferrari") -> "https://media.formula1.com/content/dam/fom-website/teams/2025/ferrari-logo.png"
            cleanName.contains("red bull") || cleanName.contains("redbull") || cleanName.contains("oracle") -> "https://media.formula1.com/content/dam/fom-website/teams/2025/red-bull-racing-logo.png"
            cleanName.contains("mercedes") -> "https://media.formula1.com/content/dam/fom-website/teams/2025/mercedes-logo.png"
            cleanName.contains("mclaren") -> "https://media.formula1.com/content/dam/fom-website/teams/2025/mclaren-logo.png"
            cleanName.contains("aston") || cleanName.contains("martin") -> "https://media.formula1.com/content/dam/fom-website/teams/2025/aston-martin-logo.png"
            cleanName.contains("alpine") -> "https://media.formula1.com/content/dam/fom-website/teams/2025/alpine-logo.png"
            cleanName.contains("williams") -> "https://media.formula1.com/content/dam/fom-website/teams/2025/williams-logo.png"
            cleanName.contains("racing bulls") || cleanName.contains("vcarb") || cleanName.contains("visa") || cleanName.contains("rb") -> "https://media.formula1.com/content/dam/fom-website/teams/2025/rb-logo.png"
            cleanName.contains("audi") || cleanName.contains("sauber") || cleanName.contains("stake") || cleanName.contains("kick") -> "https://media.formula1.com/content/dam/fom-website/teams/2025/kick-sauber-logo.png"
            cleanName.contains("haas") || cleanName.contains("moneygram") || cleanName.contains("tgr") -> "https://media.formula1.com/content/dam/fom-website/teams/2025/haas-logo.png"
            cleanName.contains("cadillac") || cleanName.contains("gm") -> "https://media.formula1.com/content/dam/fom-website/teams/2025/cadillac-logo.png"
            else -> null
        }
    }

    /**
     * Resolves driver headshot / image URL from DriverProfileEntity or fallback static mapping.
     */
    fun getDriverImageUrl(
        driverName: String?,
        driverProfiles: List<DriverProfileEntity> = emptyList()
    ): String? {
        if (driverName.isNullOrBlank()) return null
        val cleanName = driverName.lowercase().trim()

        // 1. Try matching from loaded DriverProfileEntity list
        val matchedProfile = driverProfiles.firstOrNull { profile ->
            val fullName = profile.fullName.lowercase().trim()
            val lastName = profile.lastName.lowercase().trim()
            cleanName.contains(fullName) || cleanName.contains(lastName) || fullName.contains(cleanName)
        }
        val entityUrl = matchedProfile?.headshotUrl?.ifBlank { null } ?: matchedProfile?.image?.ifBlank { null }
        if (!entityUrl.isNullOrBlank()) {
            return entityUrl
        }

        // 2. Fallback static URL mapping
        return when {
            // McLaren
            cleanName.contains("norris") -> "https://media.formula1.com/d_driver_fallback_image.png/content/dam/fom-website/drivers/L/LANNOR01_Lando_Norris/lannor01.png"
            cleanName.contains("piastri") -> "https://media.formula1.com/d_driver_fallback_image.png/content/dam/fom-website/drivers/O/OSCPIA01_Oscar_Piastri/oscpia01.png"

            // Ferrari
            cleanName.contains("leclerc") -> "https://media.formula1.com/d_driver_fallback_image.png/content/dam/fom-website/drivers/C/CHALEC01_Charles_Leclerc/chalec01.png"
            cleanName.contains("hamilton") -> "https://media.formula1.com/d_driver_fallback_image.png/content/dam/fom-website/drivers/L/LEWHAM01_Lewis_Hamilton/lewham01.png"

            // Red Bull Racing
            cleanName.contains("verstappen") -> "https://media.formula1.com/d_driver_fallback_image.png/content/dam/fom-website/drivers/M/MAXVER01_Max_Verstappen/maxver01.png"
            cleanName.contains("hadjar") -> "https://media.formula1.com/d_driver_fallback_image.png/content/dam/fom-website/drivers/I/ISAHAD01_Isack_Hadjar/isahad01.png"

            // Mercedes
            cleanName.contains("russell") -> "https://media.formula1.com/d_driver_fallback_image.png/content/dam/fom-website/drivers/G/GEORUS01_George_Russell/georus01.png"
            cleanName.contains("antonelli") -> "https://media.formula1.com/d_driver_fallback_image.png/content/dam/fom-website/drivers/A/ANDANT01_Andrea_Kimi_Antonelli/andant01.png"

            // Aston Martin
            cleanName.contains("alonso") -> "https://media.formula1.com/d_driver_fallback_image.png/content/dam/fom-website/drivers/F/FERALO01_Fernando_Alonso/feralo01.png"
            cleanName.contains("stroll") -> "https://media.formula1.com/d_driver_fallback_image.png/content/dam/fom-website/drivers/L/LANSTR01_Lance_Stroll/lanstr01.png"

            // Williams
            cleanName.contains("albon") -> "https://media.formula1.com/d_driver_fallback_image.png/content/dam/fom-website/drivers/A/ALEALB01_Alexander_Albon/alealb01.png"
            cleanName.contains("sainz") -> "https://media.formula1.com/d_driver_fallback_image.png/content/dam/fom-website/drivers/C/CARSAI01_Carlos_Sainz/carsai01.png"

            // Alpine
            cleanName.contains("gasly") -> "https://media.formula1.com/d_driver_fallback_image.png/content/dam/fom-website/drivers/P/PIEGAS01_Pierre_Gasly/piegas01.png"
            cleanName.contains("colapinto") -> "https://media.formula1.com/d_driver_fallback_image.png/content/dam/fom-website/drivers/F/FRACOL01_Franco_Colapinto/fracol01.png"

            // Haas
            cleanName.contains("ocon") -> "https://media.formula1.com/d_driver_fallback_image.png/content/dam/fom-website/drivers/E/ESTOCO01_Esteban_Ocon/estoco01.png"
            cleanName.contains("bearman") -> "https://media.formula1.com/d_driver_fallback_image.png/content/dam/fom-website/drivers/O/OLIBEA01_Oliver_Bearman/olibea01.png"

            // Audi
            cleanName.contains("hulkenberg") || cleanName.contains("hülkenberg") -> "https://media.formula1.com/d_driver_fallback_image.png/content/dam/fom-website/drivers/N/NICHUL01_Nico_Hulkenberg/nichul01.png"
            cleanName.contains("bortoleto") -> "https://media.formula1.com/d_driver_fallback_image.png/content/dam/fom-website/drivers/G/GABBOR01_Gabriel_Bortoleto/gabbor01.png"

            // Racing Bulls
            cleanName.contains("lawson") -> "https://media.formula1.com/d_driver_fallback_image.png/content/dam/fom-website/drivers/L/LIALAW01_Liam_Lawson/lialaw01.png"
            cleanName.contains("lindblad") -> "https://media.formula1.com/d_driver_fallback_image.png/content/dam/fom-website/drivers/A/ARVLIN01_Arvid_Lindblad/arvlin01.png"

            // Cadillac
            cleanName.contains("pérez") || cleanName.contains("perez") -> "https://media.formula1.com/d_driver_fallback_image.png/content/dam/fom-website/drivers/S/SERPER01_Sergio_Perez/serper01.png"
            cleanName.contains("bottas") -> "https://media.formula1.com/d_driver_fallback_image.png/content/dam/fom-website/drivers/V/VALBOT01_Valtteri_Bottas/valbot01.png"

            // Reserve / Former Main Grid
            cleanName.contains("tsunoda") -> "https://media.formula1.com/d_driver_fallback_image.png/content/dam/fom-website/drivers/Y/YUKTSU01_Yuki_Tsunoda/yuktsu01.png"
            else -> null
        }
    }
    /**
     * Extracts or resolves racing number for a driver.
     */
    fun getDriverNumber(
        driverName: String?,
        driverProfiles: List<DriverProfileEntity> = emptyList()
    ): String {
        if (driverName.isNullOrBlank()) return ""
        if (driverName.contains("#")) {
            val numPart = driverName.substringAfter("#").trim()
            if (numPart.isNotBlank()) return numPart
        }

        val cleanName = driverName.lowercase().trim()
        val matchedProfile = driverProfiles.firstOrNull { profile ->
            val fullName = profile.fullName.lowercase().trim()
            val lastName = profile.lastName.lowercase().trim()
            cleanName.contains(fullName) || cleanName.contains(lastName) || fullName.contains(cleanName)
        }
        if (matchedProfile != null && matchedProfile.number.isNotBlank()) {
            return matchedProfile.number
        }

        return when {
            cleanName.contains("verstappen") -> "1"
            cleanName.contains("norris") -> "4"
            cleanName.contains("hadjar") -> "6"
            cleanName.contains("gasly") -> "10"
            cleanName.contains("perez") || cleanName.contains("pérez") -> "11"
            cleanName.contains("alonso") -> "14"
            cleanName.contains("leclerc") -> "16"
            cleanName.contains("stroll") -> "18"
            cleanName.contains("tsunoda") -> "22"
            cleanName.contains("albon") -> "23"
            cleanName.contains("lawson") -> "30"
            cleanName.contains("ocon") -> "31"
            cleanName.contains("hamilton") -> "44"
            cleanName.contains("sainz") -> "55"
            cleanName.contains("russell") -> "63"
            cleanName.contains("bottas") -> "77"
            cleanName.contains("piastri") -> "81"
            else -> ""
        }
    }

    /**
     * Resolves driver avatar/photo/number drawable resource ID from res/drawable
     * matching driver names (e.g. leclerc, hamilton, verstappen, norris, oscar, etc.)
     * or driver racing numbers (e.g. ic_number_16, driver_16, etc.).
     */
    fun getDriverDrawableRes(
        driverName: String?,
        context: Context,
        driverProfiles: List<DriverProfileEntity> = emptyList()
    ): Int? {
        if (driverName.isNullOrBlank()) return null
        val cleanName = driverName.lowercase().trim()

        // 1. Check direct name candidates matching res/drawable filenames
        val nameCandidates = when {
            cleanName.contains("leclerc") -> listOf("leclerc", "charles")
            cleanName.contains("hamilton") -> listOf("hamilton")
            cleanName.contains("verstappen") -> listOf("verstappen")
            cleanName.contains("norris") -> listOf("norris")
            cleanName.contains("piastri") -> listOf("oscar", "piastri")
            cleanName.contains("sainz") -> listOf("carlos", "sainz")
            cleanName.contains("russell") -> listOf("russell")
            cleanName.contains("alonso") -> listOf("alonso")
            cleanName.contains("stroll") -> listOf("stroll")
            cleanName.contains("albon") -> listOf("albon")
            cleanName.contains("gasly") -> listOf("gasly")
            cleanName.contains("bearman") -> listOf("bearman")
            cleanName.contains("ocon") -> listOf("ocon")
            cleanName.contains("perez") || cleanName.contains("pérez") -> listOf("perez")
            cleanName.contains("bottas") -> listOf("bottas")
            cleanName.contains("lawson") -> listOf("liam", "lawson")
            cleanName.contains("lindblad") || cleanName.contains("arvid") -> listOf("arvid", "lindblad")
            cleanName.contains("colapinto") || cleanName.contains("franco") -> listOf("franco", "colapinto")
            cleanName.contains("hulkenberg") || cleanName.contains("hülkenberg") || cleanName.contains("nico") -> listOf("nico", "hulkenberg")
            cleanName.contains("bortoleto") -> listOf("bortoleto")
            cleanName.contains("antonelli") || cleanName.contains("kimi") -> listOf("kimi", "antonelli")
            cleanName.contains("hadjar") -> listOf("hadjar")
            cleanName.contains("tsunoda") -> listOf("tsunoda")
            else -> emptyList()
        }

        for (candidate in nameCandidates) {
            val resId = context.resources.getIdentifier(candidate, "drawable", context.packageName)
            if (resId != 0) {
                return resId
            }
        }

        // 2. Check number-based candidates (e.g. ic_number_16, driver_16)
        val number = getDriverNumber(driverName, driverProfiles)
        if (number.isNotBlank()) {
            val numClean = number.trim()
            val numCandidates = listOf(
                "ic_number_$numClean",
                "ic_driver_$numClean",
                "driver_$numClean",
                "ic_$numClean",
                "number_$numClean",
                "num_$numClean"
            )
            for (candidate in numCandidates) {
                val resId = context.resources.getIdentifier(candidate, "drawable", context.packageName)
                if (resId != 0) {
                    return resId
                }
            }
        }

        return null
    }

    /**
     * Resolves team logo drawable resource ID if added in res/drawable (e.g. ferrari, mclaren, redbull, etc.).
     */
    fun getTeamLogoDrawableRes(
        teamName: String?,
        context: Context
    ): Int? {
        if (teamName.isNullOrBlank()) return null
        val cleanName = teamName.lowercase().trim()

        val candidates = when {
            cleanName.contains("ferrari") -> listOf("ferrari", "scuderia_ferrari", "ic_ferrari")
            cleanName.contains("red bull") || cleanName.contains("redbull") -> listOf("redbull", "red_bull", "oracle_red_bull", "ic_redbull")
            cleanName.contains("mercedes") -> listOf("mercedes", "mercedes_amg", "ic_mercedes")
            cleanName.contains("mclaren") -> listOf("mclaren", "ic_mclaren")
            cleanName.contains("aston") || cleanName.contains("martin") -> listOf("aston_martin", "astonmartin", "ic_aston")
            cleanName.contains("alpine") -> listOf("alpine", "ic_alpine")
            cleanName.contains("williams") -> listOf("williams", "ic_williams")
            cleanName.contains("racing bulls") || cleanName.contains("vcarb") || cleanName.contains("visa") || cleanName.contains("rb") -> listOf("rb", "racing_bulls", "vcarb", "ic_rb")
            cleanName.contains("audi") || cleanName.contains("sauber") || cleanName.contains("kick") || cleanName.contains("stake") -> listOf("audi", "sauber", "kick_sauber", "stake", "ic_sauber")
            cleanName.contains("haas") || cleanName.contains("tgr") -> listOf("haas", "moneygram_haas", "ic_haas")
            cleanName.contains("cadillac") || cleanName.contains("gm") -> listOf("cadillac", "ic_cadillac")
            else -> emptyList()
        }

        for (candidate in candidates) {
            val resId = context.resources.getIdentifier(candidate, "drawable", context.packageName)
            if (resId != 0) {
                return resId
            }
        }
        return null
    }
}
