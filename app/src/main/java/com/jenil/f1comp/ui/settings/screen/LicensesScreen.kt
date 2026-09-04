package com.jenil.f1comp.ui.settings.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.outlined.Code
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.jenil.f1comp.ui.F1ScreenPadding
import com.jenil.f1comp.ui.theme.F1CompTheme
import kotlinx.coroutines.delay

private data class LicenseItem(
    val libraryName: String,
    val developer: String,
    val licenseType: String,
    val description: String
)

@Composable
fun LicensesScreen(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val licenses = listOf(
        LicenseItem(
            libraryName = "Jetpack Compose & Material 3",
            developer = "Google / Android Open Source Project",
            licenseType = "Apache License 2.0",
            description = "Android's modern toolkit for building native UI."
        ),
        LicenseItem(
            libraryName = "Hilt Dependency Injection",
            developer = "Google",
            licenseType = "Apache License 2.0",
            description = "Dependency injection library for Android built on top of Dagger."
        ),
        LicenseItem(
            libraryName = "Kotlin & Coroutines",
            developer = "JetBrains / Google",
            licenseType = "Apache License 2.0",
            description = "Modern programming language and asynchronous programming framework."
        ),
        LicenseItem(
            libraryName = "Retrofit & OkHttp",
            developer = "Square, Inc.",
            licenseType = "Apache License 2.0",
            description = "Type-safe HTTP client and logging networking library for Android."
        ),
        LicenseItem(
            libraryName = "Room Persistence Library",
            developer = "Google",
            licenseType = "Apache License 2.0",
            description = "Abstraction layer over SQLite for robust offline database access."
        ),
        LicenseItem(
            libraryName = "Coil Compose",
            developer = "Coil Contributors",
            licenseType = "Apache License 2.0",
            description = "Image loading library for Android backed by Kotlin Coroutines."
        ),
        LicenseItem(
            libraryName = "Haze Glassmorphism",
            developer = "Chris Banes",
            licenseType = "Apache License 2.0",
            description = "Glassmorphism background blur effects library for Jetpack Compose."
        ),
        LicenseItem(
            libraryName = "Firebase Cloud Messaging & Analytics",
            developer = "Google / Firebase",
            licenseType = "Apache License 2.0",
            description = "Cross-platform messaging solution and app analytics."
        ),
        LicenseItem(
            libraryName = "Jsoup HTML Parser",
            developer = "Jonathan Hedley",
            licenseType = "MIT License",
            description = "Java HTML parser for extracting and parsing F1 news content."
        ),
        LicenseItem(
            libraryName = "Turbine & MockK",
            developer = "Cash App / MockK Contributors",
            licenseType = "Apache License 2.0 / MIT License",
            description = "Testing utilities for Kotlin StateFlows and unit mocking."
        )
    )

    // Staggered animation state
    val visibleItems = remember { mutableStateListOf<Int>() }
    LaunchedEffect(Unit) {
        // Header card (index -1 maps to the intro section)
        for (i in -1 until licenses.size) {
            delay(60L)
            visibleItems.add(i)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(top = F1ScreenPadding.topPadding())
    ) {
        // ── Top Bar ──
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
            Text(
                text = "Open Source Licenses",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .navigationBarsPadding(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // ── Intro Header Card ──
            item {
                AnimatedVisibility(
                    visible = visibleItems.contains(-1),
                    enter = fadeIn(tween(400)) + slideInVertically(
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioLowBouncy,
                            stiffness = Spring.StiffnessMediumLow
                        ),
                        initialOffsetY = { it / 3 }
                    )
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(20.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Code,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier
                                    .size(28.dp)
                                    .padding(top = 2.dp)
                            )
                            Spacer(modifier = Modifier.width(14.dp))
                            Column {
                                Text(
                                    text = "Built with Open Source",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "PitWall is built using open-source software and open APIs. We are grateful to the open-source community for making this app possible.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    lineHeight = 20.sp
                                )
                            }
                        }
                    }
                }
            }

            // ── License Cards ──
            itemsIndexed(licenses) { index, item ->
                AnimatedVisibility(
                    visible = visibleItems.contains(index),
                    enter = fadeIn(tween(350)) + slideInVertically(
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioLowBouncy,
                            stiffness = Spring.StiffnessMediumLow
                        ),
                        initialOffsetY = { it / 3 }
                    )
                ) {
                    LicenseCard(item = item)
                }
            }

            // ── Footer ──
            item {
                AnimatedVisibility(
                    visible = visibleItems.contains(licenses.size - 1),
                    enter = fadeIn(tween(500))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 20.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Made with",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Outlined.Favorite,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "for the F1 community",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LicenseCard(
    item: LicenseItem,
    modifier: Modifier = Modifier
) {
    val accentColor = if (item.licenseType.contains("MIT")) {
        MaterialTheme.colorScheme.tertiary
    } else {
        MaterialTheme.colorScheme.primary
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
        ) {
            // Accent stripe
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(topStart = 14.dp, bottomStart = 14.dp))
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                accentColor,
                                accentColor.copy(alpha = 0.4f)
                            )
                        )
                    )
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp, vertical = 14.dp)
            ) {
                // Library name + badge row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = item.libraryName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Surface(
                        color = accentColor.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = item.licenseType,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 10.sp,
                                letterSpacing = 0.5.sp
                            ),
                            color = accentColor,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Developer
                Text(
                    text = item.developer,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.85f),
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Description
                Text(
                    text = item.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 18.sp
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LicenseScreenPreview() {
    F1CompTheme {
        LicensesScreen(
            navController = rememberNavController()
        )
    }
}
