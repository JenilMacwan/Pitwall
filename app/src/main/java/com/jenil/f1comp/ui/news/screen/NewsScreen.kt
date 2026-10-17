package com.jenil.f1comp.ui.news.screen

import android.text.format.DateUtils
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Bookmark
import androidx.compose.material.icons.rounded.BookmarkBorder
import androidx.compose.material.icons.rounded.CloudOff
import androidx.compose.material.icons.rounded.Newspaper
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jenil.f1comp.R
import com.jenil.f1comp.data.local.SavedNewsStore
import com.jenil.f1comp.ui.F1ScreenPadding
import com.jenil.f1comp.ui.news.components.NewsCard
import com.jenil.f1comp.viewmodel.NewsViewModel

@Composable
fun NewsScreen(
    modifier: Modifier = Modifier,
    onDrawerClick: () -> Unit,
    viewModel: NewsViewModel = hiltViewModel()
) {
    val newsInfo by viewModel.news.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val error by viewModel.error.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val savedNewsStore = remember(context) { SavedNewsStore(context) }
    var savedArticles by remember { mutableStateOf(savedNewsStore.load()) }
    var showSavedOnly by rememberSaveable { mutableStateOf(false) }
    val displayedNews = if (showSavedOnly) savedArticles else newsInfo

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 720.dp)
                .padding(top = F1ScreenPadding.topPadding()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp, end = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape),
                    color = MaterialTheme.colorScheme.surface
                ) {
                    IconButton(onClick = onDrawerClick) {
                        Icon(
                            painter = painterResource(R.drawable.ic_menu),
                            contentDescription = stringResource(R.string.accessibility_menu),
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = stringResource(id = R.string.title_news),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { showSavedOnly = !showSavedOnly }) {
                    Icon(
                        imageVector = if (showSavedOnly) Icons.Rounded.Bookmark else Icons.Rounded.BookmarkBorder,
                        contentDescription = if (showSavedOnly) "Show latest news" else "Show saved stories",
                        tint = if (showSavedOnly) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                    )
                }
                Surface(
                    modifier = Modifier.size(38.dp).clip(CircleShape),
                    color = MaterialTheme.colorScheme.surface
                ) {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = MaterialTheme.colorScheme.primary,
                                strokeWidth = 2.dp
                            )
                        } else {
                            IconButton(onClick = { viewModel.refreshNews() }) {
                                Icon(
                                    imageVector = Icons.Rounded.Refresh,
                                    contentDescription = stringResource(R.string.accessibility_refresh_news),
                                    tint = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        when {
            isLoading && newsInfo.isEmpty() && !showSavedOnly -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(4) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(Modifier.size(72.dp).clip(RoundedCornerShape(8.dp)).background(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.55f)))
                            Spacer(Modifier.width(14.dp))
                            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Box(Modifier.fillMaxWidth(0.94f).height(16.dp).clip(CircleShape).background(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.55f)))
                                Box(Modifier.fillMaxWidth(0.72f).height(16.dp).clip(CircleShape).background(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)))
                                Box(Modifier.fillMaxWidth(0.4f).height(12.dp).clip(CircleShape).background(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f)))
                            }
                        }
                    }
                }
            }

            error != null && newsInfo.isEmpty() && !showSavedOnly -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Rounded.CloudOff,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Couldn't load news",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = error ?: "An unknown error occurred",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { viewModel.refreshNews() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Text(stringResource(R.string.chatbot_retry))
                        }
                    }
                }
            }

            showSavedOnly && savedArticles.isEmpty() -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(24.dp)) {
                        Icon(Icons.Rounded.BookmarkBorder, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(stringResource(R.string.news_saved_empty_title), style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(stringResource(R.string.news_saved_empty_body), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)
                    }
                }
            }

            newsInfo.isEmpty() && !showSavedOnly -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Rounded.Newspaper,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No news yet",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(
                        start = 16.dp,
                        end = 16.dp,
                        bottom = F1ScreenPadding.bottomPadding() + 16.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (!showSavedOnly && error != null && newsInfo.isNotEmpty()) {
                        item(key = "stale-news-state") {
                            Text(
                                text = "Couldn't refresh. Showing saved stories · updated ${DateUtils.getRelativeTimeSpanString(newsInfo.maxOf { it.cachedAt }, System.currentTimeMillis(), DateUtils.MINUTE_IN_MILLIS)}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                            )
                        }
                    }
                    itemsIndexed(displayedNews, key = { _, item -> item.link }) { index, newsItem ->
                        val isFirstItem = index == 0
                        NewsCard(
                            news = newsItem,
                            isFirstItem = isFirstItem,
                            isSaved = savedArticles.any { it.link == newsItem.link },
                            onSaveToggle = { savedArticles = savedNewsStore.toggle(newsItem) }
                        )
                    }
                }
            }
        }
    }
}
}
