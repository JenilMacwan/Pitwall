package com.jenil.f1comp.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val Typography = Typography(

    headlineLarge = TextStyle(
        fontWeight = FontWeight.Bold,
        fontFamily = FontFamily.Monospace,
        fontSize = 32.sp,
        lineHeight = 38.sp,
        letterSpacing = 0.sp
    ),

    titleLarge = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontFamily = FontFamily.Monospace,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),

    titleMedium = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 18.sp,
        fontFamily = FontFamily.Monospace,
        lineHeight = 24.sp,
        letterSpacing = 0.15.sp
    ),

    bodyMedium = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        fontFamily = FontFamily.Monospace,
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp
    ),

    labelSmall = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        fontFamily = FontFamily.Monospace,
        lineHeight = 14.sp,
        letterSpacing = 0.3.sp
    )
)
