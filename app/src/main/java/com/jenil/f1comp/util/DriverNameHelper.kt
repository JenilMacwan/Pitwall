package com.jenil.f1comp.util

fun String.toDriverName(): String {
    val parts = trim().split("\\s+".toRegex())

    return when {
        parts.size >= 3 -> {
            "${parts[parts.size - 2].first()}. ${parts.last()}"
        }
        parts.size == 2 -> {
            "${parts[0].first()}. ${parts[1]}"
        }
        else -> this
    }
}

fun String.toConstructorLastNames(): String {
    if (isBlank()) return ""
    val driverFullNames = split("·", ",", ";").map { it.trim() }.filter { it.isNotBlank() }

    fun extractLastName(fullName: String): String {
        val parts = fullName.trim().split("\\s+".toRegex())
        return parts.lastOrNull()?.trim() ?: fullName
    }

    return driverFullNames.joinToString(" · ") { extractLastName(it) }
}
