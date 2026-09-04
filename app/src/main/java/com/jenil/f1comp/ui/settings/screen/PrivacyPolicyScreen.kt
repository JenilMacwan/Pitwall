package com.jenil.f1comp.ui.settings.screen

import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.OpenInNew
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.outlined.AdminPanelSettings
import androidx.compose.material.icons.outlined.Analytics
import androidx.compose.material.icons.outlined.Cloud
import androidx.compose.material.icons.outlined.Gavel
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Mail
import androidx.compose.material.icons.outlined.SmartToy
import androidx.compose.material.icons.outlined.Storage
import androidx.compose.material.icons.outlined.VerifiedUser
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.jenil.f1comp.ui.F1ScreenPadding
import com.jenil.f1comp.ui.theme.F1CompTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds


private data class PolicySection(
    val shortLabel: String,
    val title: String,
    val content: String,
    val icon: ImageVector
)

private val policySections = listOf(
    PolicySection(
        shortLabel = "Storage",
        title = "Data Storage & Local Preferences",
        icon = Icons.Outlined.Storage,
        content = "User choices — including dark mode, favorite team theme accents, units preferences, and notification toggles — are stored locally on your device using Jetpack DataStore and Room Database. This data never leaves your device."
    ),
    PolicySection(
        shortLabel = "Permissions",
        title = "Permissions Used",
        icon = Icons.Outlined.AdminPanelSettings,
        content = "• Calendar Access: Used solely to sync Formula 1 Grand Prix schedules to your device's system calendar upon your explicit request.\n\n• Notifications & Exact Alarms: Used to trigger local reminders before race sessions begin and deliver breaking news updates."
    ),
    PolicySection(
        shortLabel = "Third-Party",
        title = "Third-Party Services & APIs",
        icon = Icons.Outlined.Cloud,
        content = "• Firebase Cloud Messaging (FCM): Used to deliver optional push notifications for breaking F1 news.\n\n• Public F1 APIs: Fetches public Formula 1 session schedules, standings, driver statistics, and team radio audio files."
    ),
    PolicySection(
        shortLabel = "Apex AI",
        title = "Apex AI & Data Processing",
        icon = Icons.Outlined.SmartToy,
        content = "PitWall features 'Apex', an AI chatbot powered by Google Gemini. When you interact with Apex, your text queries are sent to our secure backend API. We temporarily cache chat sessions for up to 1 hour to maintain conversational context. We do not use your queries to train our AI models, nor do we sell this conversational data."
    ),
    PolicySection(
        shortLabel = "Analytics",
        title = "App Analytics & Stability",
        icon = Icons.Outlined.Analytics,
        content = "To improve PitWall, we may collect anonymous crash reports and basic usage telemetry (e.g., device model, OS version). This data contains no personally identifiable information and is used strictly to fix bugs and optimize app performance."
    ),
    PolicySection(
        shortLabel = "Your Rights",
        title = "Your Choices & Control",
        icon = Icons.Outlined.Gavel,
        content = "You are always in control. Calendar sync can be reversed at any time from Settings → Clear F1 Events. Notification categories can be toggled individually. Revoking a permission in your device settings simply disables the related feature — the rest of PitWall keeps working normally."
    )
)

private data class TrustHighlight(val icon: ImageVector, val text: String)

private val trustHighlights = listOf(
    TrustHighlight(Icons.Outlined.Lock, "No Account Needed"),
    TrustHighlight(Icons.Outlined.VerifiedUser, "100% Local First"),
    TrustHighlight(Icons.Outlined.Cloud, "Zero Data Selling")
)


private const val ITEMS_BEFORE_SECTIONS = 3

@Composable
fun PrivacyPolicyScreen(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    val visibleItems = remember { mutableStateListOf<Int>() }
    LaunchedEffect(Unit) {
        for (i in -3 until policySections.size + 1) {
            delay(45L.milliseconds)
            visibleItems.add(i)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(top = F1ScreenPadding.topPadding())
    ) {
        DocumentTopBar(
            onBack = { navController.popBackStack() },
            onShare = {
                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_SUBJECT, "PitWall — Privacy Policy")
                    putExtra(Intent.EXTRA_TEXT, "Read PitWall's Privacy Policy: https://jenil.dev/pitwall-privacy")
                }
                context.startActivity(Intent.createChooser(shareIntent, "Share Privacy Policy"))
            }
        )

        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .navigationBarsPadding(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            item {
                RevealIn(visible = visibleItems.contains(-3)) {
                    DocumentLetterhead()
                }
            }


            item {
                RevealIn(visible = visibleItems.contains(-2)) {
                    OverviewCallout()
                }
            }


            item {
                RevealIn(visible = visibleItems.contains(-1)) {
                    TableOfContents(
                        sections = policySections,
                        onSectionSelected = { index ->
                            coroutineScope.launch {
                                listState.animateScrollToItem(ITEMS_BEFORE_SECTIONS + index)
                            }
                        }
                    )
                }
            }


            itemsIndexed(policySections) { index, section ->
                RevealIn(visible = visibleItems.contains(index)) {
                    PolicySectionCard(index = index + 1, section = section)
                }
            }


            item {
                RevealIn(visible = visibleItems.contains(policySections.size)) {
                    DocumentFooter(context = context)
                }
            }
        }
    }
}


@Composable
private fun DocumentTopBar(
    onBack: () -> Unit,
    onShare: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
            Text(
                text = "Privacy Policy",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
        IconButton(onClick = onShare) {
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.OpenInNew,
                contentDescription = "Share policy",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun RevealIn(visible: Boolean, content: @Composable () -> Unit) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(tween(380)) + slideInVertically(
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioLowBouncy,
                stiffness = Spring.StiffnessMediumLow
            ),
            initialOffsetY = { it / 3 }
        )
    ) {
        content()
    }
}


@Composable
private fun DocumentLetterhead() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column {
                        Text(
                            text = "PITWALL LEGAL DOCUMENT",
                            style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.5.sp),
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Privacy Policy",
                            style = MaterialTheme.typography.headlineLarge.copy(fontSize = 24.sp),
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Doc. PW-PRIV · Rev. 1.0",
                            style = MaterialTheme.typography.labelSmall,
                            fontFamily = FontFamily.Monospace,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(20.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                ) {
                    Text(
                        text = "Sep 2026",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                        fontFamily = FontFamily.Monospace,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            DottedRule()
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                trustHighlights.forEach { badge ->
                    TrustBadge(icon = badge.icon, text = badge.text, modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun DottedRule() {
    Row(modifier = Modifier.fillMaxWidth()) {
        HorizontalDivider(
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f),
            thickness = 1.dp
        )
    }
}


@Composable
private fun OverviewCallout() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.22f),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.25f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Overview",
                style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.5.sp),
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "PitWall is designed with privacy as a core principle. The app runs locally on your device without requiring an account, a login, or personal identifying information such as your name, email, or phone number.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                lineHeight = 22.sp
            )
        }
    }
}


@Composable
private fun TableOfContents(
    sections: List<PolicySection>,
    onSectionSelected: (Int) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "IN THIS DOCUMENT",
            style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.2.sp),
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(start = 2.dp, bottom = 8.dp)
        )
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            itemsIndexed(sections) { index, section ->
                Surface(
                    shape = RoundedCornerShape(50),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)),
                    onClick = { onSectionSelected(index) }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = section.icon,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "${index + 1}. ${section.shortLabel}",
                            style = MaterialTheme.typography.labelMedium,
                            fontFamily = FontFamily.Monospace,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}


@Composable
private fun TrustBadge(
    icon: ImageVector,
    text: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
    ) {
        Column(
            modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                maxLines = 2
            )
        }
    }
}


@Composable
private fun PolicySectionCard(
    index: Int,
    section: PolicySection,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            // Left accent stripe — gives the "annotated legal document" feel
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.5f))
            )

            Column(modifier = Modifier.padding(18.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                        shape = CircleShape
                    ) {
                        Box(
                            modifier = Modifier.size(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = section.icon,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "SECTION ${String.format("%02d", index)}",
                            style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.sp),
                            fontFamily = FontFamily.Monospace,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = section.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = section.content,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 22.sp
                )
            }
        }
    }
}


@Composable
private fun DocumentFooter(context: android.content.Context) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Legal Disclosures & Inquiries",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = "This policy may be updated as PitWall evolves. Material changes will be reflected here with a new revision date. For the complete web disclosure or a privacy inquiry, use the options below.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            OutlinedButton(
                onClick = {
                    val intent = Intent(Intent.ACTION_VIEW, "https://jenil.dev/pitwall-privacy".toUri())
                    context.startActivity(intent)
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.OpenInNew,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Read Full Web Policy")
            }

            TextButton(
                onClick = {
                    val intent = Intent(Intent.ACTION_SENDTO).apply {
                        data = "mailto:jenilmacwan29@gmail.com?subject=PitWall Privacy Inquiry".toUri()
                    }
                    context.startActivity(intent)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Outlined.Mail,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Contact Data Protection")
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "PitWall · Doc. PW-PRIV · Rev. 1.0 · Sep 2026",
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                fontFamily = FontFamily.Monospace,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PrivacyPolicyScreenPreview() {
    F1CompTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            PrivacyPolicyScreen(navController = rememberNavController())
        }
    }
}