package com.jenil.f1comp.ui.news.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.jenil.f1comp.data.local.entity.NewsEntity

@Composable
fun NewsCard(
    news: NewsEntity,
    isFirstItem: Boolean,
    modifier: Modifier = Modifier
) {
    // The first item starts expanded. Others start collapsed.
    var isExpanded by remember { mutableStateOf(isFirstItem) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clickable {
                if (!isFirstItem) {
                    isExpanded = !isExpanded
                }
            }
            .animateContentSize(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        if (isExpanded) {
            ExpandedNewsContent(news, isFirstItem)
        } else {
            CollapsedNewsContent(news)
        }
    }
}



