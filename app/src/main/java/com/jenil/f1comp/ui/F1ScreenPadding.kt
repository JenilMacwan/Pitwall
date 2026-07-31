package com.jenil.f1comp.ui

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.statusBars
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

object F1ScreenPadding {
    @Composable
    fun topPadding(extra: Dp = 8.dp): Dp =
        WindowInsets.statusBars.asPaddingValues().calculateTopPadding() + extra

    @Composable
    fun bottomPadding(extra: Dp = 72.dp): Dp =
        WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding() + extra
}