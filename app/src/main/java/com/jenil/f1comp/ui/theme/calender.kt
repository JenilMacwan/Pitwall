package com.example.test

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

@Suppress("CheckReturnValue")
public val calendar_add_on: ImageVector
  get() {
    if (_calendar_add_on != null) {
      return _calendar_add_on!!
    }
    _calendar_add_on =
      ImageVector.Builder(
          name = "calendar_add_on",
          defaultWidth = 24.dp,
          defaultHeight = 24.dp,
          viewportWidth = 24f,
          viewportHeight = 24f,
        )
        .apply {
          path(
            fill = SolidColor(Color.Black),
            fillAlpha = 1f,
            stroke = null,
            strokeAlpha = 1f,
            strokeLineWidth = 1f,
            strokeLineCap = StrokeCap.Butt,
            strokeLineJoin = StrokeJoin.Bevel,
            strokeLineMiter = 1f,
            pathFillType = PathFillType.Companion.NonZero,
          ) {
            moveTo(17f, 22f)
            verticalLineTo(19f)
            horizontalLineTo(14f)
            verticalLineTo(17f)
            horizontalLineToRelative(3f)
            verticalLineTo(14f)
            horizontalLineToRelative(2f)
            verticalLineToRelative(3f)
            horizontalLineToRelative(3f)
            verticalLineToRelative(2f)
            horizontalLineTo(19f)
            verticalLineToRelative(3f)
            horizontalLineTo(17f)
            close()
            moveTo(5f, 20f)
            quadTo(4.18f, 20f, 3.59f, 19.41f)
            reflectiveQuadTo(3f, 18f)
            verticalLineTo(6f)
            quadTo(3f, 5.18f, 3.59f, 4.59f)
            reflectiveQuadTo(5f, 4f)
            horizontalLineTo(6f)
            verticalLineTo(2f)
            horizontalLineTo(8f)
            verticalLineTo(4f)
            horizontalLineToRelative(6f)
            verticalLineTo(2f)
            horizontalLineToRelative(2f)
            verticalLineTo(4f)
            horizontalLineToRelative(1f)
            quadToRelative(0.82f, 0f, 1.41f, 0.59f)
            quadTo(19f, 5.18f, 19f, 6f)
            verticalLineToRelative(6.1f)
            quadTo(18.5f, 12.02f, 18f, 12.02f)
            reflectiveQuadTo(17f, 12.1f)
            verticalLineTo(10f)
            horizontalLineTo(5f)
            verticalLineToRelative(8f)
            horizontalLineToRelative(7f)
            quadToRelative(0f, 0.5f, 0.08f, 1f)
            reflectiveQuadToRelative(0.28f, 1f)
            horizontalLineTo(5f)
            close()
            moveTo(5f, 8f)
            horizontalLineTo(17f)
            verticalLineTo(6f)
            horizontalLineTo(5f)
            verticalLineTo(8f)
            close()
            moveTo(5f, 8f)
            verticalLineTo(6f)
            verticalLineTo(8f)
            close()
          }
        }
        .build()
    return _calendar_add_on!!
  }

private var _calendar_add_on: ImageVector? = null
