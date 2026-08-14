package com.jenil.f1comp.util

object ProfileUtils {

    fun getFlagEmoji(nationality: String?): String {
        return when (nationality?.lowercase()) {
            "british" -> "🇬🇧"
            "dutch" -> "🇳🇱"
            "monégasque", "monegasque" -> "🇲🇨"
            "spanish" -> "🇪🇸"
            "mexican" -> "🇲🇽"
            "australian" -> "🇦🇺"
            "german" -> "🇩🇪"
            "french" -> "🇫🇷"
            "canadian" -> "🇨🇦"
            "japanese" -> "🇯🇵"
            "thai" -> "🇹🇭"
            "danish" -> "🇩🇰"
            "chinese" -> "🇨🇳"
            "finnish" -> "🇫🇮"
            "brazilian" -> "🇧🇷"
            "italian" -> "🇮🇹"
            "american" -> "🇺🇸"
            "austrian" -> "🇦🇹"
            "swiss" -> "🇨🇭"
            "new zealander" -> "🇳🇿"
            "argentine" -> "🇦🇷"
            "belgian" -> "🇧🇪"
            else -> "🏁"
        }
    }

    fun getChassis(constructorId: String?): String {
        return when (constructorId?.lowercase()) {
            "mercedes" -> "F1 W17"
            "red_bull", "redbull" -> "RB22"
            "ferrari" -> "SF-26"
            "mclaren" -> "MCL40"
            "aston_martin" -> "AMR26"
            "alpine" -> "A526"
            "williams" -> "FW48"
            "rb", "racing_bulls" -> "VCARB 03"
            "audi", "Audi" -> "R26"
            "haas" -> "VF-26"
            "cadillac" -> "MAC26"
            else -> "TBD"
        }
    }

    fun getPowerUnit(constructorId: String?): String {
        return when (constructorId?.lowercase()) {
            "mercedes", "williams", "mclaren", "aston_martin", "alpine" -> "Mercedes-AMG"
            "red_bull", "redbull", "rb", "racing_bulls" -> "Red Bull Ford"
            "ferrari", "haas", "cadillac" -> "Ferrari"
            "audi", "Audi" -> "Audi"
            else -> "TBD"
        }
    }

    fun getTeamPrincipal(constructorId: String?): String {
        return when (constructorId?.lowercase()) {
            "mercedes" -> "Toto Wolff"
            "red_bull", "redbull" -> "Laurent Mekies"
            "ferrari" -> "Frédéric Vasseur"
            "mclaren" -> "Andrea Stella"
            "aston_martin" -> "Adrain Newey"
            "alpine" -> "Flavio Briatore"
            "williams" -> "James Vowles"
            "rb", "racing_bulls" -> "Alan Permane"
            "audi" -> "Jonathan Wheatley"
            "haas" -> "Ayao Komatsu"
            "cadillac" -> "Graeme Lowdon"
            else -> "TBD"
        }
    }
}
