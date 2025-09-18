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
import androidx.compose.material.icons.automirrored.filled.*
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
import com.hellogerman.app.ui.viewmodel.DictionaryViewModel
import com.hellogerman.app.data.models.*
import com.hellogerman.app.data.dictionary.LanguageHint

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DictionaryScreen(
    navController: NavController,
    initialWord: String = "",
    dictionaryViewModel: DictionaryViewModel = viewModel()
) {
    val searchQuery by dictionaryViewModel.searchQuery.collectAsState()
    val searchResult by dictionaryViewModel.searchResult.collectAsState()
    val unifiedSearchResult by dictionaryViewModel.unifiedSearchResult.collectAsState()
    val detectedLanguage by dictionaryViewModel.detectedLanguage.collectAsState()
    val isLoading by dictionaryViewModel.isLoading.collectAsState()
    val errorMessage by dictionaryViewModel.errorMessage.collectAsState()
    val fromLanguage by dictionaryViewModel.fromLanguage.collectAsState()
    val toLanguage by dictionaryViewModel.toLanguage.collectAsState()
    val searchHistory by dictionaryViewModel.searchHistory.collectAsState()
    val selectedTab by dictionaryViewModel.selectedTab.collectAsState()
    val isTTSInitialized by dictionaryViewModel.isTTSInitialized.collectAsState()
    val isTTSPlaying by dictionaryViewModel.isTTSPlaying.collectAsState()
    val isWordInVocabulary by dictionaryViewModel.isWordInVocabulary.collectAsState()
    val vocabularyMessage by dictionaryViewModel.vocabularyMessage.collectAsState()
    
    val keyboardController = LocalSoftwareKeyboardController.current
    var showLanguageDialog by remember { mutableStateOf(false) }
    var isSelectingFromLanguage by remember { mutableStateOf(true) }

    // Handle initial word search from lesson content
    LaunchedEffect(initialWord) {
        if (initialWord.isNotBlank()) {
            dictionaryViewModel.setInitialSearchQuery(initialWord)
        }
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header with TTS controls
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Dictionary",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Close button to navigate back
                IconButton(
                    onClick = { navController.popBackStack() }
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close Dictionary",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Home/Dashboard button
                IconButton(
                    onClick = {
                        navController.popBackStack(Screen.Dashboard.route, inclusive = false)
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Home,
                        contentDescription = "Go to Dashboard",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Dictionary Settings Menu
                var showSettingsMenu by remember { mutableStateOf(false) }
                IconButton(onClick = { showSettingsMenu = true }) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Dictionary Settings",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }

                DropdownMenu(
                    expanded = showSettingsMenu,
                    onDismissRequest = { showSettingsMenu = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Reset Dictionary Database") },
                        onClick = {
                            showSettingsMenu = false
                            dictionaryViewModel.resetDictionaryDatabase()
                        },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Refresh,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    )
                }

                // TTS Controls
                if (searchQuery.isNotEmpty() && (fromLanguage == "de" || fromLanguage == "en") && isTTSInitialized) {
                    IconButton(
                        onClick = { 
                            if (isTTSPlaying) {
                                dictionaryViewModel.stopTTS()
                            } else {
                                dictionaryViewModel.speakWordSlowly()
                            }
                        }
                    ) {
                        Icon(
                            imageVector = if (isTTSPlaying) Icons.Default.Stop else Icons.AutoMirrored.Filled.VolumeUp,
                            contentDescription = if (isTTSPlaying) "Stop" else "Speak slowly",
                            tint = AccentBlue
                        )
                    }
                    
                    IconButton(
                        onClick = { dictionaryViewModel.speakWord() }
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Speak normal",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                
                IconButton(
                    onClick = { dictionaryViewModel.clearResults() }
                ) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "Clear",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
        
        // Language Selection Row
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // From Language
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            isSelectingFromLanguage = true
                            showLanguageDialog = true
                        }
                ) {
                    Text(
                        text = "From",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = dictionaryViewModel.getLanguageName(fromLanguage),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                
                // Swap Button
                IconButton(
                    onClick = { dictionaryViewModel.swapLanguages() }
                ) {
                    Icon(
                        imageVector = Icons.Default.SwapHoriz,
                        contentDescription = "Swap Languages",
                        tint = AccentBlue
                    )
                }
                
                // To Language
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            isSelectingFromLanguage = false
                            showLanguageDialog = true
                        }
                ) {
                    Text(
                        text = "To",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = dictionaryViewModel.getLanguageName(toLanguage),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
        
        // Search Input
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { dictionaryViewModel.updateSearchQuery(it) },
            label = { Text("Enter word to translate") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(
                onSearch = {
                    dictionaryViewModel.searchWord()
                    keyboardController?.hide()
                }
            ),
            trailingIcon = {
                IconButton(
                    onClick = {
                        dictionaryViewModel.searchWord()
                        keyboardController?.hide()
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search"
                    )
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
                        imageVector = Icons.Default.Error,
                        contentDescription = "Error",
                        tint = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(
                        onClick = { dictionaryViewModel.clearError() }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
        
        // Vocabulary Message
        vocabularyMessage?.let { message ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (isWordInVocabulary) Icons.Default.BookmarkRemove else Icons.Default.BookmarkAdd,
                        contentDescription = "Vocabulary",
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = message,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(
                        onClick = { dictionaryViewModel.clearVocabularyMessage() }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
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
                    Text(
                        text = "Searching...",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
        
        // Unified Results Display (LEO-style)
        unifiedSearchResult?.let { unifiedResult ->
            if (unifiedResult.hasResults) {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Unified Results Card
                    item { 
                        UnifiedResultsCard(
                            result = unifiedResult,
                            viewModel = dictionaryViewModel
                        ) 
                    }
                    
                    // Legacy Overview Card and additional sections for backward compatibility
                    searchResult?.let { result ->
                        item { OverviewCard(result, dictionaryViewModel, isWordInVocabulary) }

                        // Definitions Section
                        if (result.definitions.isNotEmpty()) {
                            item {
                                SectionHeader("Definitions")
                            }
                            item {
                                SenseGroupedDefinitionsCard(result.definitions)
                            }
                        }

                        // Examples Section
                        if (result.examples.isNotEmpty()) {
                            item {
                                SectionHeader("Examples")
                            }
                            items(result.examples) { example ->
                                ExampleCard(example, dictionaryViewModel)
                            }
                            item {
                                AttributionCard(
                                    sources = result.examples.mapNotNull { it.source }.distinct(),
                                    section = "Examples"
                                )
                            }
                        }

                        // Conjugations Section
                        result.conjugations?.let { conjugations ->
                            item {
                                SectionHeader("Conjugations")
                            }
                            item { ConjugationCard(conjugations) }
                        }

                        // Declensions Section (for nouns)
                        result.wikidataLexemeData?.let { lexemeData ->
                            if (lexemeData.lexicalCategory == "noun" && lexemeData.declensions.isNotEmpty()) {
                                item {
                                    SectionHeader("Declensions")
                                }
                                item { DeclensionCard(lexemeData) }
                            }
                        }

                        // Synonyms Section
                        if (result.synonyms.isNotEmpty()) {
                            item {
                                SectionHeader("Synonyms")
                            }
                            item { SynonymsCard(result.synonyms, dictionaryViewModel) }
                            item {
                                AttributionCard(
                                    sources = listOf("OpenThesaurus"),
                                    section = "Synonyms"
                                )
                            }
                        }

                        // Translations Section
                        if (result.translations.isNotEmpty()) {
                            item {
                                SectionHeader("Translations")
                            }
                            item {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(16.dp)
                                    ) {
                                        result.translations.forEach { translation ->
                                            Text(
                                                text = "• $translation",
                                                fontSize = 16.sp,
                                                color = MaterialTheme.colorScheme.onSurface,
                                                modifier = Modifier.padding(vertical = 2.dp)
                                            )
                                        }
                                    }
                                }
                            }
                            item {
                                AttributionCard(
                                    sources = listOf("MyMemory", "LibreTranslate"),
                                    section = "Translations"
                                )
                            }
                        }

                        // Etymology Section (if available)
                        result.etymology?.let { etymology ->
                            item {
                                SectionHeader("Etymology")
                            }
                            item {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                                ) {
                                    Text(
                                        text = etymology,
                                        fontSize = 16.sp,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.padding(16.dp)
                                    )
                                }
                            }
                            item {
                                AttributionCard(
                                    sources = listOf("Wiktionary"),
                                    section = "Etymology"
                                )
                            }
                        }

                        // Comprehensive Attribution Footer
                        item {
                            AttributionFooter(result)
                        }
                    }
                }
            }
        } ?: run {
            // Show search history when no results
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (searchHistory.isNotEmpty() && !isLoading) {
                    item {
                        SearchHistorySection(
                            history = searchHistory,
                            onWordSelected = { dictionaryViewModel.selectFromHistory(it) },
                            onClearHistory = { dictionaryViewModel.clearHistory() }
                        )
                    }
                }
            }
        }
    }
    
    // Language Selection Dialog
    if (showLanguageDialog) {
        LanguageSelectionDialog(
            languages = dictionaryViewModel.getSupportedLanguages(),
            onLanguageSelected = { code ->
                if (isSelectingFromLanguage) {
                    dictionaryViewModel.setFromLanguage(code)
                } else {
                    dictionaryViewModel.setToLanguage(code)
                }
                showLanguageDialog = false
            },
            onDismiss = { showLanguageDialog = false }
        )
    }
}

// Enhanced Dictionary UI Components

@Composable
private fun OverviewCard(
    result: DictionarySearchResult, 
    viewModel: DictionaryViewModel,
    isWordInVocabulary: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Word header with pronunciation
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    // Prefer showing the first German translation when searching EN→DE
                    val headerWord = if (result.fromLanguage.lowercase() in listOf("en", "english") && result.translations.isNotEmpty()) {
                        // Build a concise lemma-like header: join up to first 2 single-word nouns with separate articles
                        val parts = result.translations
                            .map { it.replace(Regex("^(?i)(der|die|das)\\s+"), "").trim() }
                            .filter { it.isNotEmpty() }
                        val singles = parts.filter { !it.contains(" ") }
                        if (singles.isNotEmpty()) {
                            val article = result.gender?.let {
                                when (it.lowercase()) {
                                    "masculine" -> "der"
                                    "feminine" -> "die"
                                    "neuter" -> "das"
                                    "der", "die", "das" -> it
                                    else -> ""
                                }
                            } ?: ""
                            if (article.isNotEmpty()) {
                                singles.take(2).joinToString(", ") { "$article $it" }
                            } else {
                                singles.take(2).joinToString(", ")
                            }
                        } else parts.first()
                    } else {
                        // For DE→EN, show original word with article
                        val articlePrefix = result.gender?.let {
                            when (it.lowercase()) {
                                "masculine" -> "der "
                                "feminine" -> "die "
                                "neuter" -> "das "
                                "der", "die", "das" -> "$it "
                                else -> ""
                            }
                        } ?: ""
                        articlePrefix + result.originalWord
                    }
                    Text(
                        text = headerWord,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = AccentBlue
                    )
                    
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        result.wordType?.let { type ->
                            GrammarChip(
                                text = type,
                                backgroundColor = MaterialTheme.colorScheme.primaryContainer,
                                textColor = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                        
                        result.gender?.let { gender ->
                            val article = when (gender.lowercase()) {
                                "masculine" -> "der"
                                "feminine" -> "die"
                                "neuter" -> "das"
                                else -> gender
                            }
                            GrammarChip(
                                text = article,
                                backgroundColor = MaterialTheme.colorScheme.secondaryContainer,
                                textColor = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                        
                        result.wikidataLexemeData?.plural?.let { plural ->
                            GrammarChip(
                                text = "Plural: $plural",
                                backgroundColor = MaterialTheme.colorScheme.tertiaryContainer,
                                textColor = MaterialTheme.colorScheme.onTertiaryContainer
                            )
                        }
                    }
                }
                
                result.pronunciation?.ipa?.let { ipa ->
                    Text(
                        text = "/$ipa/",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Quick Actions Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                QuickActionButton(
                    icon = Icons.Default.ContentCopy,
                    label = "Copy",
                    onClick = { /* TODO: Implement copy to clipboard */ }
                )
                
                QuickActionButton(
                    icon = Icons.Default.Share,
                    label = "Share",
                    onClick = { /* TODO: Implement share */ }
                )
                
                QuickActionButton(
                    icon = if (isWordInVocabulary) Icons.Default.BookmarkRemove else Icons.Default.BookmarkAdd,
                    label = if (isWordInVocabulary) "Remove from Vocab" else "Add to Vocab",
                    onClick = { 
                        if (isWordInVocabulary) {
                            viewModel.removeWordFromVocabulary()
                        } else {
                            viewModel.addWordToVocabulary()
                        }
                    }
                )
                
                Spacer(modifier = Modifier.weight(1f))
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Quick overview sections
            if (result.translations.isNotEmpty()) {
                SectionHeader("Translations")
                result.translations.take(3).forEachIndexed { index, translation ->
                    Text(
                        text = "• $translation",
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(vertical = 2.dp)
                    )
                }
                if (result.translations.size > 3) {
                    Text(
                        text = "... and ${result.translations.size - 3} more",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
            }
            
            if (result.definitions.isNotEmpty()) {
                SectionHeader("Top Definition")
                Text(
                    text = result.definitions.first().meaning,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
            
            if (result.examples.isNotEmpty()) {
                SectionHeader("Example")
                Text(
                    text = "\"${result.examples.first().sentence}\"",
                    fontSize = 16.sp,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
private fun DefinitionCard(definition: Definition) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                definition.partOfSpeech?.let { pos ->
                    Text(
                        text = pos,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier
                            .background(
                                MaterialTheme.colorScheme.secondaryContainer,
                                RoundedCornerShape(4.dp)
                            )
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
                
                definition.level?.let { level ->
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = level,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = definition.meaning,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            definition.context?.let { context ->
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Context: $context",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                )
            }
        }
    }
}

@Composable
private fun SenseGroupedDefinitionsCard(definitions: List<Definition>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Group definitions by part of speech
            val groupedDefinitions = definitions.groupBy { it.partOfSpeech ?: "other" }
            
            groupedDefinitions.forEach { (pos, defs) ->
                if (pos != "other") {
                    Text(
                        text = pos.replaceFirstChar { it.uppercase() },
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
                
                defs.forEachIndexed { index, definition ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.Top
                    ) {
                        Text(
                            text = "${index + 1}.",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(end = 8.dp, top = 2.dp)
                        )
                        
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = definition.meaning,
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            
                            definition.context?.let { context ->
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Context: $context",
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                                )
                            }
                            
                            definition.level?.let { level ->
                                Spacer(modifier = Modifier.height(4.dp))
                                GrammarChip(
                                    text = level,
                                    backgroundColor = MaterialTheme.colorScheme.primaryContainer,
                                    textColor = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }
                    }
                    
                    if (index < defs.size - 1) {
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
                
                if (pos != groupedDefinitions.keys.last()) {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
private fun ExampleCard(example: Example, viewModel: DictionaryViewModel) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "\"${example.sentence}\"",
                        fontSize = 16.sp,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    
                    example.translation?.let { translation ->
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "→ $translation",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    
                    example.source?.let { source ->
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Source: $source",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                
                IconButton(
                    onClick = { viewModel.speakExample(example.sentence) }
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Speak example",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun ConjugationCard(conjugations: VerbConjugations) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            SectionHeader("Verb Conjugations")
            
            // Verb info chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                conjugations.infinitive?.let { infinitive ->
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    ) {
                        Text(
                            text = "Infinitive: $infinitive",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(6.dp)
                        )
                    }
                }
                
                conjugations.auxiliary?.let { auxiliary ->
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer
                        )
                    ) {
                        Text(
                            text = "Auxiliary: $auxiliary",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier.padding(6.dp)
                        )
                    }
                }
                
                if (conjugations.isSeparable) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.tertiaryContainer
                        )
                    ) {
                        Text(
                            text = "Separable",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onTertiaryContainer,
                            modifier = Modifier.padding(6.dp)
                        )
                    }
                }
                
                if (conjugations.isIrregular) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Text(
                            text = "Irregular",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier.padding(6.dp)
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Present tense
            if (conjugations.present.isNotEmpty()) {
                ConjugationSection("Present", conjugations.present)
                Spacer(modifier = Modifier.height(12.dp))
            }
            
            // Past tense
            if (conjugations.past.isNotEmpty()) {
                ConjugationSection("Past", conjugations.past)
                Spacer(modifier = Modifier.height(12.dp))
            }
            
            // Perfect tense
            if (conjugations.perfect.isNotEmpty()) {
                ConjugationSection("Perfect", conjugations.perfect)
                Spacer(modifier = Modifier.height(12.dp))
            }
            
            // Future tense
            if (conjugations.future.isNotEmpty()) {
                ConjugationSection("Future", conjugations.future)
                Spacer(modifier = Modifier.height(12.dp))
            }
            
            // Imperative
            if (conjugations.imperative.isNotEmpty()) {
                ConjugationSection("Imperative", conjugations.imperative)
                Spacer(modifier = Modifier.height(12.dp))
            }
            
            // Participles
            conjugations.participle?.let { participle ->
                Text(
                    text = "Participles",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))
                
                participle.present?.let { present ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Present:",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = present,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
                participle.past?.let { past ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Past:",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = past,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ConjugationSection(title: String, conjugations: Map<String, String>) {
    Text(
        text = title,
        fontSize = 16.sp,
        fontWeight = FontWeight.Medium,
        color = MaterialTheme.colorScheme.onSurface
    )
    
    conjugations.forEach { (person, conjugation) ->
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
                text = conjugation,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun DeclensionCard(lexemeData: WikidataLexemeData) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            SectionHeader("Noun Declensions")
            
            // Gender and Plural info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                lexemeData.gender?.let { gender ->
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    ) {
                        Text(
                            text = "Gender: $gender",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }
                
                lexemeData.plural?.let { plural ->
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer
                        )
                    ) {
                        Text(
                            text = "Plural: $plural",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Declension table
            if (lexemeData.declensions.isNotEmpty()) {
                Text(
                    text = "Case Declensions",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Create a table-like layout
                lexemeData.declensions.forEach { (case, form) ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = case.replaceFirstChar { it.uppercase() },
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = form,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.End
                        )
                    }
                }
            }
            
            // Additional forms if available
            if (lexemeData.forms.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "Additional Forms",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                lexemeData.forms.take(5).forEach { form ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = form.representation,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Medium
                        )
                        if (form.grammaticalFeatures.isNotEmpty()) {
                            Text(
                                text = form.grammaticalFeatures.joinToString(", "),
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SynonymsCard(synonyms: List<String>, viewModel: DictionaryViewModel) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            SectionHeader("Synonyms")
            
            val chunkedSynonyms = synonyms.chunked(3)
            chunkedSynonyms.forEach { row ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    row.forEach { synonym ->
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { 
                                    viewModel.updateSearchQuery(synonym)
                                    viewModel.searchWord()
                                },
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer
                            )
                        ) {
                            Text(
                                text = synonym,
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.padding(12.dp),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                    // Fill empty slots if row is not complete
                    repeat(3 - row.size) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

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
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
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
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
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
                        imageVector = Icons.Default.History,
                        contentDescription = "History",
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

@Composable
private fun LanguageSelectionDialog(
    languages: List<Pair<String, String>>,
    onLanguageSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Select Language")
        },
        text = {
            LazyColumn {
                items(languages) { (code, name) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onLanguageSelected(code) }
                            .padding(vertical = 12.dp, horizontal = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = name,
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun GrammarChip(
    text: String,
    backgroundColor: Color,
    textColor: Color,
    onClick: (() -> Unit)? = null
) {
    val modifier = if (onClick != null) {
        Modifier.clickable { onClick() }
    } else {
        Modifier
    }
    
    Text(
        text = text,
        fontSize = 12.sp,
        fontWeight = FontWeight.Medium,
        color = textColor,
        modifier = modifier
            .background(
                backgroundColor,
                RoundedCornerShape(12.dp)
            )
            .padding(horizontal = 8.dp, vertical = 4.dp)
    )
}

@Composable
private fun QuickActionButton(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = label,
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun AttributionCard(
    sources: List<String>,
    section: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = "Source",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "$section data from: ${sources.joinToString(", ")}",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
            )
        }
    }
}

@Composable
private fun AttributionFooter(result: DictionarySearchResult) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Data Sources & Licensing",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            val allSources = mutableSetOf<String>()
            if (result.definitions.isNotEmpty()) allSources.add("Wiktionary")
            if (result.examples.isNotEmpty()) {
                result.examples.forEach { it.source?.let { source -> allSources.add(source) } }
            }
            if (result.synonyms.isNotEmpty()) allSources.add("OpenThesaurus")
            if (result.translations.isNotEmpty()) allSources.addAll(listOf("MyMemory", "LibreTranslate"))
            if (result.conjugations != null) allSources.add("German Verb API")
            if (result.wikidataLexemeData != null) allSources.add("Wikidata")
            
            allSources.forEach { source ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "• $source:",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.width(100.dp)
                    )
                    Text(
                        text = getSourceLicense(source),
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "This dictionary aggregates data from multiple free and open sources. Please respect the licensing terms of each source.",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
            )
        }
    }
}

private fun getSourceLicense(source: String): String {
    return when (source) {
        "Wiktionary" -> "CC BY-SA 3.0"
        "Tatoeba" -> "CC BY 2.0"
        "OpenThesaurus" -> "GPL 2.0"
        "MyMemory" -> "Free for non-commercial use"
        "LibreTranslate" -> "MIT License"
        "German Verb API" -> "Open source"
        "Wikidata" -> "CC0 1.0"
        "Reverso Context" -> "Terms of service apply"
        else -> "Please check source website"
    }
}
