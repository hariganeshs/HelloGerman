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
import com.hellogerman.app.ui.theme.*
import com.hellogerman.app.ui.viewmodel.DictionaryViewModel
import com.hellogerman.app.data.models.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DictionaryScreen(
    navController: NavController,
    dictionaryViewModel: DictionaryViewModel = viewModel()
) {
    val searchQuery by dictionaryViewModel.searchQuery.collectAsState()
    val searchResult by dictionaryViewModel.searchResult.collectAsState()
    val isLoading by dictionaryViewModel.isLoading.collectAsState()
    val errorMessage by dictionaryViewModel.errorMessage.collectAsState()
    val fromLanguage by dictionaryViewModel.fromLanguage.collectAsState()
    val toLanguage by dictionaryViewModel.toLanguage.collectAsState()
    val searchHistory by dictionaryViewModel.searchHistory.collectAsState()
    val selectedTab by dictionaryViewModel.selectedTab.collectAsState()
    val isTTSInitialized by dictionaryViewModel.isTTSInitialized.collectAsState()
    val isTTSPlaying by dictionaryViewModel.isTTSPlaying.collectAsState()
    
    val keyboardController = LocalSoftwareKeyboardController.current
    var showLanguageDialog by remember { mutableStateOf(false) }
    var isSelectingFromLanguage by remember { mutableStateOf(true) }
    
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
                if (searchQuery.isNotEmpty() && fromLanguage == "de" && isTTSInitialized) {
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
        
        // All Results in One Scrollable Screen (LEO-style)
        searchResult?.let { result ->
            if (result.hasResults) {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Word Overview Section
                    item { OverviewCard(result, dictionaryViewModel) }

                    // Definitions Section
                    if (result.definitions.isNotEmpty()) {
                        item {
                            SectionHeader("Definitions")
                        }
                        items(result.definitions) { definition ->
                            DefinitionCard(definition)
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
                    }

                    // Conjugations Section
                    result.conjugations?.let { conjugations ->
                        item {
                            SectionHeader("Conjugations")
                        }
                        item { ConjugationCard(conjugations) }
                    }

                    // Synonyms Section
                    if (result.synonyms.isNotEmpty()) {
                        item {
                            SectionHeader("Synonyms")
                        }
                        item { SynonymsCard(result.synonyms, dictionaryViewModel) }
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
                    }

                    // Add some bottom padding
                    item { Spacer(modifier = Modifier.height(32.dp)) }
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
private fun OverviewCard(result: DictionarySearchResult, viewModel: DictionaryViewModel) {
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
                    Text(
                        text = result.originalWord,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = AccentBlue
                    )
                    
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        result.wordType?.let { type ->
                            Text(
                                text = type,
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier
                                    .background(
                                        MaterialTheme.colorScheme.primaryContainer,
                                        RoundedCornerShape(8.dp)
                                    )
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                        
                        result.gender?.let { gender ->
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = gender,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.primary
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
            
            // Future tense
            if (conjugations.future.isNotEmpty()) {
                ConjugationSection("Future", conjugations.future)
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
                participle.present?.let { present ->
                    Text(
                        text = "Present: $present",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                participle.past?.let { past ->
                    Text(
                        text = "Past: $past",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
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
