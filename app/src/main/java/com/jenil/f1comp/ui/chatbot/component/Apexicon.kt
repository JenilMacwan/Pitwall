package com.jenil.f1comp.ui.chatbot.component

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

@Suppress("CheckReturnValue")
public val apexMark: ImageVector
    get() {
        if (_apexMark != null) {
            return _apexMark!!
        }
        _apexMark = ImageVector.Builder(
            name = "apex_mark",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f,
        )
            .apply {
                // Bold chevron — the "apex" of a racing line
                path(
                    fill = SolidColor(Color.Black),
                    fillAlpha = 1f,
                    stroke = null,
                    strokeAlpha = 1f,
                    strokeLineWidth = 1f,
                    pathFillType = PathFillType.NonZero,
                ) {
                    moveTo(12f, 3.2f)
                    lineTo(20.5f, 15.5f)
                    lineTo(16.6f, 15.5f)
                    lineTo(12f, 8.6f)
                    lineTo(7.4f, 15.5f)
                    lineTo(3.5f, 15.5f)
                    close()
                }
                // Small diamond accent beneath the chevron
                path(
                    fill = SolidColor(Color.Black),
                    fillAlpha = 1f,
                    stroke = null,
                    strokeAlpha = 1f,
                    strokeLineWidth = 1f,
                    pathFillType = PathFillType.NonZero,
                ) {
                    moveTo(12f, 18f)
                    lineTo(13.5f, 19.6f)
                    lineTo(12f, 21.2f)
                    lineTo(10.5f, 19.6f)
                    close()
                }
            }
            .build()
        return _apexMark!!
    }

private var _apexMark: ImageVector? = null


