package com.jenil.f1comp.ui.news.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.jenil.f1comp.R
import com.jenil.f1comp.data.local.entity.NewsEntity
import com.jenil.f1comp.util.DateParserUtil
import androidx.compose.ui.platform.LocalUriHandler
@Composable
fun ExpandedNewsContent(news: NewsEntity, isFirstItem: Boolean) {

    val timeAgo = remember(news.published) {
        DateParserUtil.getTimeAgo(news.published)
    }

    val uriHandler = LocalUriHandler.current
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        // Image Placeholder (Replace with Coil AsyncImage)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = news.image,
                contentDescription = "News Image",
                modifier = Modifier.fillMaxWidth(),
                contentScale = ContentScale.Crop,
                placeholder = painterResource(R.drawable.ic_news_placeholder),
                error = painterResource(R.drawable.ic_error_outline)
            )
        }

        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (isFirstItem) {
                    Text(
                        text = "FEATURED",
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                } else {
                    Spacer(modifier = Modifier.width(1.dp)) // Spacer to push date to right if not featured
                }

                Text(
                    text = "$timeAgo · ${news.source}",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 12.sp
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = news.title,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 26.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = news.description,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 14.sp,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Add a clear call to action at the bottom
            TextButton(
                onClick = {

                    if (news.link.isNotEmpty()) {
                        uriHandler.openUri(news.link)
                    }
                },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text(
                    text = "Read Full Article",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}