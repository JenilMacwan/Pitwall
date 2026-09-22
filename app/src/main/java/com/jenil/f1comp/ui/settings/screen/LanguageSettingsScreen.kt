package com.jenil.f1comp.ui.settings.screen

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.os.LocaleListCompat
import androidx.navigation.NavController
import com.jenil.f1comp.R
import com.jenil.f1comp.ui.theme.F1Red
import com.jenil.f1comp.util.FlagImage
import com.jenil.f1comp.util.ProfileUtils
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale

data class RegionFormat(
    val countryCode: String,
    val countryName: String,
    val languageName: String,
    val dateFormat: String,
    val numberFormat: String,
    val flagEmoji: String
)

data class LanguageOption(
    val code: String,
    val displayName: String,
    val nativeName: String,
    val subtitle: String? = null,
    val flagEmoji: String,
    val isSuggested: Boolean = false,
    val isActive: Boolean = false
)

private val regionFormatsMap = mapOf(
    "en" to RegionFormat("GB", "United Kingdom", "English (UK)", "24/10/2026 (DD/MM/YYYY)", "1,234,567.89", "🇬🇧"),
    "en-US" to RegionFormat("US", "United States", "English (US)", "10/24/2026 (MM/DD/YYYY)", "1,234,567.89", "🇺🇸"),
    "es" to RegionFormat("ES", "España", "Español", "24/10/2026 (DD/MM/YYYY)", "1.234.567,89", "🇪🇸"),
    "fr" to RegionFormat("FR", "France", "Français", "24/10/2026 (JJ/MM/AAAA)", "1 234 567,89", "🇫🇷"),
    "de" to RegionFormat("DE", "Deutschland", "Deutsch", "24.10.2026 (TT.MM.JJJJ)", "1.234.567,89", "🇩🇪"),
    "it" to RegionFormat("IT", "Italia", "Italiano", "24/10/2026 (GG/MM/AAAA)", "1.234.567,89", "🇮🇹"),
    "nl" to RegionFormat("NL", "Nederland", "Nederlands", "24-10-2026 (DD-MM-JJJJ)", "1.234.567,89", "🇳🇱")
)

@Composable
fun LanguageSettingsScreen(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var currentLanguageCode by remember { mutableStateOf(getAppLanguage(context)) }
    var selectedLanguageCode by remember { mutableStateOf(currentLanguageCode) }
    var searchQuery by remember { mutableStateOf("") }
    var isSwitchingLanguage by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    val suggestedLanguages = remember(selectedLanguageCode) {
        listOf(
            LanguageOption(
                code = "en",
                displayName = "English",
                nativeName = "English (UK)",
                subtitle = "App Default • Primary",
                flagEmoji = "🇬🇧",
                isSuggested = true,
                isActive = selectedLanguageCode.startsWith("en") && !selectedLanguageCode.contains("US")
            ),
            LanguageOption(
                code = "en-US",
                displayName = "English",
                nativeName = "English (US)",
                subtitle = "English (United States)",
                flagEmoji = "🇺🇸",
                isSuggested = true,
                isActive = selectedLanguageCode == "en-US"
            )
        )
    }

    val allLanguages = remember(selectedLanguageCode) {
        listOf(
            LanguageOption("de", "German", "Deutsch", "German", "🇩🇪", isActive = selectedLanguageCode == "de"),
            LanguageOption("es", "Spanish", "Español", "Spanish (Spain & LatAm)", "🇪🇸", isActive = selectedLanguageCode == "es"),
            LanguageOption("fr", "French", "Français", "French", "🇫🇷", isActive = selectedLanguageCode == "fr"),
            LanguageOption("it", "Italian", "Italiano", "Italian", "🇮🇹", isActive = selectedLanguageCode == "it"),
            LanguageOption("nl", "Dutch", "Nederlands", "Dutch", "🇳🇱", isActive = selectedLanguageCode == "nl")
        )
    }

    val filteredAllLanguages = remember(searchQuery, allLanguages) {
        if (searchQuery.isBlank()) {
            allLanguages
        } else {
            allLanguages.filter {
                it.nativeName.contains(searchQuery, ignoreCase = true) ||
                it.displayName.contains(searchQuery, ignoreCase = true) ||
                (it.subtitle?.contains(searchQuery, ignoreCase = true) == true)
            }
        }
    }

    val activeRegionFormat = remember(selectedLanguageCode) {
        regionFormatsMap[selectedLanguageCode]
            ?: regionFormatsMap[selectedLanguageCode.take(2)]
            ?: regionFormatsMap["en"]!!
    }

    val hasUnsavedChanges = selectedLanguageCode != currentLanguageCode

    fun applyLanguageChanges() {
        if (!hasUnsavedChanges) return
        isSwitchingLanguage = true
        scope.launch {
            delay(300)
            currentLanguageCode = selectedLanguageCode
            setAppLanguage(context, selectedLanguageCode)
            isSwitchingLanguage = false
        }
    }

    if (isSwitchingLanguage) {
        Dialog(
            onDismissRequest = {},
            properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
        ) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(1.dp, F1Red.copy(alpha = 0.5f)),
                shadowElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier
                        .padding(28.dp)
                        .widthIn(min = 220.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator(
                        color = F1Red,
                        strokeWidth = 3.dp,
                        modifier = Modifier.size(40.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "${activeRegionFormat.flagEmoji}  ${activeRegionFormat.languageName}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Applying language & regional settings...",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            if (hasUnsavedChanges) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    color = Color.Transparent
                ) {
                    Button(
                        onClick = { applyLanguageChanges() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.onSurface,
                            contentColor = MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Check,
                            contentDescription = "Save Changes",
                            tint = MaterialTheme.colorScheme.surface,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Save Changes",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        },
        containerColor = Color.Transparent
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // --- Centered Top Bar (No Reset Icon) ---
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 8.dp)
            ) {
                IconButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.align(Alignment.CenterStart)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.align(Alignment.Center)
                ) {
                    Text(
                        text = stringResource(R.string.lang_title),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center
                    )
                }
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // --- Search Field ---
                item {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = {
                            Text(
                                text = "Search language...",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Outlined.Search,
                                contentDescription = "Search",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        },
                        shape = RoundedCornerShape(24.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                        ),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // --- Region & Local Format Card ---
                item {
                    SectionHeader(
                        title = "REGION & LOCAL FORMAT",
                        badgeText = "ACTIVE"
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    RegionFormatCard(regionFormat = activeRegionFormat)
                }

                // --- Suggested Languages ---
                if (searchQuery.isBlank()) {
                    item {
                        SectionHeader(
                            title = "SUGGESTED LANGUAGES",
                            badgeText = "${suggestedLanguages.size} Available"
                        )
                    }

                    items(suggestedLanguages, key = { it.code }) { language ->
                        val isSelected = selectedLanguageCode == language.code
                        LanguageSelectionCard(
                            language = language,
                            isSelected = isSelected,
                            onSelect = { selectedLanguageCode = language.code }
                        )
                    }
                }

                // --- All Languages ---
                item {
                    SectionHeader(
                        title = "ALL LANGUAGES",
                        badgeText = "Alphabetical Directory"
                    )
                }

                items(filteredAllLanguages, key = { it.code }) { language ->
                    val isSelected = selectedLanguageCode == language.code
                    LanguageSelectionCard(
                        language = language,
                        isSelected = isSelected,
                        onSelect = { selectedLanguageCode = language.code }
                    )
                }
            }
        }
    }
}

@Composable
private fun SectionHeader(
    title: String,
    badgeText: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelSmall,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            letterSpacing = 1.2.sp
        )
        Surface(
            shape = RoundedCornerShape(50),
            color = if (badgeText == "ACTIVE") F1Red.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
            border = BorderStroke(1.dp, if (badgeText == "ACTIVE") F1Red.copy(alpha = 0.3f) else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
        ) {
            Text(
                text = badgeText,
                style = MaterialTheme.typography.labelSmall,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                color = if (badgeText == "ACTIVE") F1Red else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
            )
        }
    }
}

@Composable
private fun RegionFormatCard(
    regionFormat: RegionFormat
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Main Country Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.size(48.dp)
                ) {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                        FlagImage(
                            flagUrl = ProfileUtils.getFlagUrl(emoji = regionFormat.flagEmoji),
                            width = 32.dp,
                            height = 22.dp
                        )
                    }
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = regionFormat.countryName,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            Text(
                                text = regionFormat.countryCode,
                                style = MaterialTheme.typography.labelSmall,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = regionFormat.languageName,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(14.dp))

            // Sub Cards: Date Format & Number Format
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                FormatSubCard(
                    title = "DATE FORMAT",
                    value = regionFormat.dateFormat,
                    modifier = Modifier.weight(1f)
                )
                FormatSubCard(
                    title = "NUMBER FORMAT",
                    value = regionFormat.numberFormat,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun FormatSubCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 9.sp
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.bodySmall,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun LanguageSelectionCard(
    language: LanguageOption,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surface,
        border = BorderStroke(
            width = if (isSelected) 1.5.dp else 1.dp,
            color = if (isSelected) F1Red else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect() }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier.size(40.dp)
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    FlagImage(
                        flagUrl = ProfileUtils.getFlagUrl(emoji = language.flagEmoji),
                        width = 28.dp,
                        height = 20.dp
                    )
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = language.nativeName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) F1Red else MaterialTheme.colorScheme.onSurface
                    )
                    if (language.isActive) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = F1Red.copy(alpha = 0.15f),
                            border = BorderStroke(1.dp, F1Red.copy(alpha = 0.4f))
                        ) {
                            Text(
                                text = "Active",
                                style = MaterialTheme.typography.labelSmall,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                color = F1Red,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = language.subtitle ?: language.displayName,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Radio Button Indicator
            if (isSelected) {
                Surface(
                    shape = CircleShape,
                    color = F1Red,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Check,
                        contentDescription = "Selected",
                        tint = Color.White,
                        modifier = Modifier.padding(4.dp)
                    )
                }
            } else {
                Surface(
                    shape = CircleShape,
                    color = Color.Transparent,
                    border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)),
                    modifier = Modifier.size(24.dp)
                ) {}
            }
        }
    }
}

private fun setAppLanguage(context: Context, languageTag: String) {
    val localeList = LocaleListCompat.forLanguageTags(languageTag)
    AppCompatDelegate.setApplicationLocales(localeList)
}

private fun getAppLanguage(context: Context): String {
    val locales = AppCompatDelegate.getApplicationLocales()
    if (!locales.isEmpty) {
        locales.get(0)?.language?.let { return it }
    }
    return Locale.getDefault().language.ifEmpty { "en" }
}
