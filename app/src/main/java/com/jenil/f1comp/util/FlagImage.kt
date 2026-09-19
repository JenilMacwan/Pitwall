package com.jenil.f1comp.util

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

@Composable
fun FlagImage(
    flagUrl: String,
    modifier: Modifier = Modifier,
    contentDescription: String? = "Flag",
    width: Dp = 22.dp,
    height: Dp = 15.dp,
    cornerRadius: Dp = 3.dp
) {
    val shape = RoundedCornerShape(cornerRadius)
    AsyncImage(
        model = flagUrl,
        contentDescription = contentDescription,
        contentScale = ContentScale.Crop,
        modifier = modifier
            .width(width)
            .height(height)
            .clip(shape)
            .border(
                BorderStroke(
                    0.5.dp,
                    MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                ),
                shape = shape
            )
    )
}
