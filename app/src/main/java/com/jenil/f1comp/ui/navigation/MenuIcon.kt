package com.jenil.f1comp.ui.navigation

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val menu: ImageVector
    get() {
        if (_menu != null) {
            return _menu!!
        }
        _menu =
            ImageVector.Builder(
                name = "menu",
                defaultWidth = 24.dp,
                defaultHeight = 24.dp,
                viewportWidth = 24f,
                viewportHeight = 24f,
            )
                .apply {
                    // Top line: M4 6L20 6
                    path(
                        stroke = SolidColor(Color.Unspecified),
                        strokeLineWidth = 2.4f,
                        strokeLineCap = StrokeCap.Round
                    ) {
                        moveTo(4f, 6f)
                        lineTo(20f, 6f)
                    }

                    // Middle line: M4 12L16 12 (Red Accent)
                    path(
                        stroke = SolidColor(Color(0xFFFF1801)),
                        strokeLineWidth = 2.4f,
                        strokeLineCap = StrokeCap.Round
                    ) {
                        moveTo(4f, 12f)
                        lineTo(16f, 12f)
                    }

                    // Bottom line: M4 18H10
                    path(
                        stroke = SolidColor(Color.Unspecified),
                        strokeLineWidth = 2.4f,
                        strokeLineCap = StrokeCap.Round
                    ) {
                        moveTo(4f, 18f)
                        lineTo(10f, 18f)
                    }
                }
                .build()
        return _menu!!
    }

private var _menu: ImageVector? = null
