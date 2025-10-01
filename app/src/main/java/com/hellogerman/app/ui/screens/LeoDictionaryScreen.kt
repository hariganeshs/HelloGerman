package com.hellogerman.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.hellogerman.app.ui.navigation.Screen
import com.hellogerman.app.ui.theme.*
import com.hellogerman.app.ui.viewmodel.LeoDictionaryViewModel
import com.hellogerman.app.ui.components.PronunciationPlayer
import com.hellogerman.app.data.models.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeoDictionaryScreen(
    navController: NavController,
    initialWord: String = "",
    leoDictionaryViewModel: LeoDictionaryViewModel = viewModel()
) {
    val searchQuery by leoDictionaryViewModel.searchQuery.collectAsState()
    val searchResult by leoDictionaryViewModel.searchResult.collectAsState()
    val isLoading by leoDictionaryViewModel.isLoading.collectAsState()
    val errorMessage by leoDictionaryViewModel.errorMessage.collectAsState()
    val searchHistory by leoDictionaryViewModel.searchHistory.collectAsState()
    val currentLanguage by leoDictionaryViewModel.currentLanguage.collectAsState()
    val isTTSInitialized by leoDictionaryViewModel.isTTSInitialized.collectAsState()
    val isTTSPlaying by leoDictionaryViewModel.isTTSPlaying.collectAsState()

    val keyboardController = LocalSoftwareKeyboardController.current

    // Handle initial word search
    LaunchedEffect(initialWord) {
        if (initialWord.isNotBlank()) {
            leoDictionaryViewModel.updateSearchQuery(initialWord)
            leoDictionaryViewModel.searchWord()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Leo Dictionary",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                // Language toggle
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (currentLanguage == "de") AccentBlue else MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "DE",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (currentLanguage == "de") Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.clickable { leoDictionaryViewModel.setLanguage("de") }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "EN",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (currentLanguage == "en") Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.clickable { leoDictionaryViewModel.setLanguage("en") }
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Close button
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        // Search Input
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { leoDictionaryViewModel.updateSearchQuery(it) },
            label = { Text("Search German or English word") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = {
                leoDictionaryViewModel.searchWord()
                keyboardController?.hide()
            }),
            trailingIcon = {
                IconButton(onClick = {
                    leoDictionaryViewModel.searchWord()
                    keyboardController?.hide()
                }) {
                    Icon(Icons.Default.Search, "Search")
                }
            },
            singleLine = true
        )

        // Error Message
        errorMessage?.let { error ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Error,
                        "Error",
                        tint = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = { leoDictionaryViewModel.clearError() }) {
                        Icon(
                            Icons.Default.Close,
                            "Close",
                            tint = MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }

        // Loading Indicator
        if (isLoading) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                        color = AccentBlue
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("Searching...", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }

        // Search Results
        searchResult?.let { result ->
            if (result.hasResults) {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(result.entries) { entry ->
                        LeoDictionaryEntryCard(entry, leoDictionaryViewModel, isTTSInitialized)
                    }
                }
            } else {
                EmptyStateCard("No results found for \"${result.originalWord}\"")
            }
        } ?: run {
            // Show search history when no results
            if (searchHistory.isNotEmpty() && !isLoading) {
                SearchHistorySection(
                    history = searchHistory,
                    onWordSelected = { leoDictionaryViewModel.selectFromHistory(it) },
                    onClearHistory = { leoDictionaryViewModel.clearHistory() }
                )
            }
        }
    }
}

/**
 * Leo-style dictionary entry card with gender display and comprehensive grammar
 */
@Composable
private fun LeoDictionaryEntryCard(
    entry: LeoDictionaryEntry,
    viewModel: LeoDictionaryViewModel,
    isTTSInitialized: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Word header with gender article
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    // Display word with gender article (Leo-style)
                    val displayWord = entry.gender?.let { gender ->
                        "${gender.getArticle()} ${entry.germanWord}"
                    } ?: entry.germanWord

                    Text(
                        text = displayWord,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = AccentBlue
                    )

                    // Word type and additional info
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        GrammarChip(
                            text = entry.wordType.name.lowercase(),
                            backgroundColor = MaterialTheme.colorScheme.primaryContainer,
                            textColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )

                        entry.gender?.let { gender ->
                            GrammarChip(
                                text = gender.getArticle(),
                                backgroundColor = MaterialTheme.colorScheme.secondaryContainer,
                                textColor = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }

                        entry.plural?.let { plural ->
                            GrammarChip(
                                text = "Plural: $plural",
                                backgroundColor = MaterialTheme.colorScheme.tertiaryContainer,
                                textColor = MaterialTheme.colorScheme.onTertiaryContainer
                            )
                        }
                    }
                }

                // Pronunciation
                entry.pronunciation?.let { pronunciation ->
                    PronunciationPlayer(
                        ipaNotation = pronunciation.ipa ?: "",
                        audioUrl = pronunciation.audioUrl,
                        onPlayAudio = {
                            // Use TTS for pronunciation playback
                            viewModel.playPronunciation(entry.germanWord)
                        }
                    )
                } ?: run {
                    // Fallback: Show TTS button for words without Wiktionary pronunciation
                    if (isTTSInitialized) {
                        PronunciationPlayer(
                            ipaNotation = "",
                            audioUrl = null,
                            onPlayAudio = {
                                viewModel.playPronunciation(entry.germanWord)
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // English translations
            if (entry.englishTranslations.isNotEmpty()) {
                SectionHeader("Translations")
                entry.englishTranslations.forEach { translation ->
                    Text(
                        text = "• $translation",
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(vertical = 2.dp)
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Grammar information based on word type
            when (entry.wordType) {
                WordType.NOUN -> {
                    entry.declension?.let { declension ->
                        NounDeclensionSection(declension)
                    }
                }
                WordType.VERB -> {
                    entry.conjugation?.let { conjugation ->
                        VerbConjugationSection(conjugation)
                    }
                }
                WordType.ADJECTIVE -> {
                    entry.adjectiveDeclension?.let { declension ->
                        AdjectiveDeclensionSection(declension)
                    }
                }
                else -> {}
            }

            // Examples
            if (entry.examples.isNotEmpty()) {
                SectionHeader("Examples")
                entry.examples.take(3).forEach { example ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Text(
                            text = "\"${example.sentence}\"",
                            fontSize = 16.sp,
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }
            }

            // Source attribution
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Source: ${entry.source}",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
            )
        }
    }
}

/**
 * Noun declension section
 */
@Composable
private fun NounDeclensionSection(declension: NounDeclensionTable) {
    SectionHeader("Declension")

    // Simple declension table
    Column {
        DeclensionRow("Nominative", declension.nominative.singular, declension.nominative.plural)
        DeclensionRow("Genitive", declension.genitive.singular, declension.genitive.plural)
        DeclensionRow("Dative", declension.dative.singular, declension.dative.plural)
        DeclensionRow("Accusative", declension.accusative.singular, declension.accusative.plural)
    }
}

/**
 * Verb conjugation section
 */
@Composable
private fun VerbConjugationSection(conjugation: VerbConjugationTable) {
    SectionHeader("Conjugation")

    // Present tense
    ConjugationTable("Present", conjugation.present)

    Spacer(modifier = Modifier.height(8.dp))

    // Past tense
    ConjugationTable("Past", conjugation.past)
}

/**
 * Adjective declension section
 */
@Composable
private fun AdjectiveDeclensionSection(declension: AdjectiveDeclensionTable) {
    SectionHeader("Declension")

    // Simple adjective forms
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        GrammarChip("Positive: ${declension.positive.masculine}", MaterialTheme.colorScheme.primaryContainer, MaterialTheme.colorScheme.onPrimaryContainer)
        declension.comparative.masculine.takeIf { it.isNotEmpty() }?.let {
            GrammarChip("Comparative: $it", MaterialTheme.colorScheme.secondaryContainer, MaterialTheme.colorScheme.onSecondaryContainer)
        }
        declension.superlative.masculine.takeIf { it.isNotEmpty() }?.let {
            GrammarChip("Superlative: $it", MaterialTheme.colorScheme.tertiaryContainer, MaterialTheme.colorScheme.onTertiaryContainer)
        }
    }
}

/**
 * Declension table row
 */
@Composable
private fun DeclensionRow(case: String, singular: String, plural: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = case,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = singular,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center
        )
        Text(
            text = plural,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.End
        )
    }
}

/**
 * Conjugation table
 */
@Composable
private fun ConjugationTable(title: String, forms: PersonForms) {
    Text(
        text = title,
        fontSize = 16.sp,
        fontWeight = FontWeight.Medium,
        color = MaterialTheme.colorScheme.onSurface
    )

    Spacer(modifier = Modifier.height(4.dp))

    Column {
        ConjugationRow("ich", forms.ich)
        ConjugationRow("du", forms.du)
        ConjugationRow("er/sie/es", forms.erSieEs)
        ConjugationRow("wir", forms.wir)
        ConjugationRow("ihr", forms.ihr)
        ConjugationRow("sie/Sie", forms.sieSie)
    }
}

/**
 * Conjugation row
 */
@Composable
private fun ConjugationRow(person: String, form: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = person,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = form,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Medium
        )
    }
}

/**
 * Grammar chip component
 */
@Composable
private fun GrammarChip(text: String, backgroundColor: Color, textColor: Color) {
    Text(
        text = text,
        fontSize = 12.sp,
        fontWeight = FontWeight.Medium,
        color = textColor,
        modifier = Modifier
            .background(backgroundColor, RoundedCornerShape(12.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    )
}

/**
 * Section header
 */
@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        fontSize = 18.sp,
        fontWeight = FontWeight.Medium,
        color = MaterialTheme.colorScheme.onSurface
    )
    Spacer(modifier = Modifier.height(8.dp))
}

/**
 * Search history section
 */
@Composable
private fun SearchHistorySection(
    history: List<String>,
    onWordSelected: (String) -> Unit,
    onClearHistory: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Recent Searches",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )

                TextButton(onClick = onClearHistory) {
                    Text("Clear")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            history.forEach { word ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onWordSelected(word) }
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.History,
                        "History",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = word,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

/**
 * Empty state card
 */
@Composable
private fun EmptyStateCard(message: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    Icons.Default.Info,
                    null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = message,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}