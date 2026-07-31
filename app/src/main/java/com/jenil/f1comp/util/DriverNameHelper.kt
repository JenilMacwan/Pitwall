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