package com.hellogerman.app.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.hellogerman.app.data.entities.DictionaryEntry
import com.hellogerman.app.data.entities.GermanGender
import com.hellogerman.app.data.entities.SearchLanguage
import com.hellogerman.app.data.entities.WordType
import com.hellogerman.app.ui.viewmodel.DictionaryViewModel

/**
 * Main dictionary screen with search and results
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DictionaryScreen(
    navController: NavController,
    viewModel: DictionaryViewModel = viewModel()
) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val searchResults by viewModel.searchResults.collectAsState()
    val searchLanguage by viewModel.searchLanguage.collectAsState()
    val isSearching by viewModel.isSearching.collectAsState()
    val isDictionaryImported by viewModel.isDictionaryImported.collectAsState()
    val isImporting by viewModel.isImporting.collectAsState()
    val importProgress by viewModel.importProgress.collectAsState()
    val statistics by viewModel.statistics.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    
    // Semantic search state
    val useSemanticSearch by viewModel.useSemanticSearch.collectAsState()
    val isSemanticSearchAvailable by viewModel.isSemanticSearchAvailable.collectAsState()
    val synonyms by viewModel.synonyms.collectAsState()
    val relatedWords by viewModel.relatedWords.collectAsState()
    
    var showImportDialog by remember { mutableStateOf(false) }
    var showStatisticsDialog by remember { mutableStateOf(false) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Dictionary") },
                actions = {
                    // Language toggle
                    IconButton(onClick = { viewModel.toggleSearchLanguage() }) {
                        Icon(
                            imageVector = Icons.Default.Translate,
                            contentDescription = "Toggle language",
                            tint = when (searchLanguage) {
                                SearchLanguage.ENGLISH -> MaterialTheme.colorScheme.primary
                                SearchLanguage.GERMAN -> MaterialTheme.colorScheme.secondary
                            }
                        )
                    }
                    
                    // Semantic search toggle (only show if available)
                    if (isSemanticSearchAvailable) {
                        IconButton(onClick = { viewModel.toggleSemanticSearch() }) {
                            Icon(
                                imageVector = if (useSemanticSearch) 
                                    Icons.Default.AutoAwesome 
                                else 
                                    Icons.Default.Search,
                                contentDescription = "Toggle semantic search",
                                tint = if (useSemanticSearch) 
                                    MaterialTheme.colorScheme.tertiary
                                else 
                                    MaterialTheme.colorScheme.outline
                            )
                        }
                    }
                    
                    // Statistics
                    if (isDictionaryImported) {
                        IconButton(onClick = { showStatisticsDialog = true }) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = "Statistics"
                            )
                        }
                    }
                    
                            // Debug Button (for testing)
                            IconButton(onClick = { viewModel.debugWord("apple") }) {
                                Icon(
                                    imageVector = Icons.Default.BugReport,
                                    contentDescription = "Debug apple",
                                    tint = MaterialTheme.colorScheme.secondary
                                )
                            }
                            
                            // Fix Search Issues (Full Import)
                            if (errorMessage?.contains("search quality") == true || errorMessage?.contains("partially imported") == true) {
                                IconButton(onClick = { viewModel.startFullImport() }) {
                                    Icon(
                                        imageVector = Icons.Default.Refresh,
                                        contentDescription = "Fix search issues",
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                    
                    // Import/Management
                    IconButton(onClick = { showImportDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Dictionary settings"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { paddingValues ->
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            
            // Error message
            errorMessage?.let { error ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = error,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(onClick = { viewModel.clearError() }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Dismiss error",
                                tint = MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                    }
                }
            }
            
            // Check if dictionary is imported
            if (!isDictionaryImported && !isImporting) {
                DictionaryNotImportedView(
                    onImportClick = { viewModel.startImport() }
                )
            } else if (isImporting) {
                DictionaryImportProgressView(importProgress = importProgress)
            } else {
                // Search bar
                SearchBar(
                    query = searchQuery,
                    onQueryChange = { viewModel.updateSearchQuery(it) },
                    onClearClick = { viewModel.clearSearch() },
                    isSearching = isSearching,
                    searchLanguage = searchLanguage
                )
                
                // Search results
                SearchResultsList(
                    results = searchResults,
                    onEntryClick = { entry ->
                        viewModel.selectEntry(entry)
                    },
                    onPlayAudio = { germanWord ->
                        viewModel.playPronunciation(germanWord)
                    },
                    isSearching = isSearching
                )
            }
        }
        
        // Dialogs
        if (showImportDialog) {
            DictionaryManagementDialog(
                isDictionaryImported = isDictionaryImported,
                isImporting = isImporting,
                onDismiss = { showImportDialog = false },
                onImportClick = { viewModel.startImport() },
                onClearClick = {
                    viewModel.clearDictionary()
                    showImportDialog = false
                }
            )
        }
        
        if (showStatisticsDialog) {
            StatisticsDialog(
                statistics = statistics,
                onDismiss = { showStatisticsDialog = false }
            )
        }
    }
}

@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onClearClick: () -> Unit,
    isSearching: Boolean,
    searchLanguage: SearchLanguage
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        placeholder = {
            Text(
                when (searchLanguage) {
                    SearchLanguage.ENGLISH -> "Search English words..."
                    SearchLanguage.GERMAN -> "Search German words..."
                }
            )
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search"
            )
        },
        trailingIcon = {
            Row {
                if (isSearching) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp
                    )
                }
                if (query.isNotEmpty()) {
                    IconButton(onClick = onClearClick) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Clear"
                        )
                    }
                }
            }
        },
        singleLine = true,
        shape = RoundedCornerShape(28.dp)
    )
}

@Composable
fun SearchResultsList(
    results: List<DictionaryEntry>,
    onEntryClick: (DictionaryEntry) -> Unit,
    onPlayAudio: (String) -> Unit,
    isSearching: Boolean
) {
    Box(modifier = Modifier.fillMaxSize()) {
        when {
            isSearching -> {
                // Loading state handled by search bar indicator
            }
            results.isEmpty() -> {
                // No results
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.outline
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "No results found",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }
            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(results, key = { it.id }) { entry ->
                        DictionaryEntryCard(
                            entry = entry,
                            onClick = { onEntryClick(entry) },
                            onPlayAudio = onPlayAudio
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DictionaryEntryCard(
    entry: DictionaryEntry,
    onClick: () -> Unit,
    onPlayAudio: (String) -> Unit = {}
) {
    var expanded by remember { mutableStateOf(false) }
    
    Card(
        onClick = { expanded = !expanded },
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // English word
            Text(
                text = entry.englishWord,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // German translation with gender and audio
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                // Gender article for nouns (larger and bolder)
                if (entry.wordType == WordType.NOUN && entry.gender != null) {
                    Text(
                        text = entry.gender.getArticle(),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = when (entry.gender) {
                            GermanGender.DER -> Color(0xFF2196F3) // Blue
                            GermanGender.DIE -> Color(0xFFE91E63) // Pink
                            GermanGender.DAS -> Color(0xFF9C27B0) // Purple
                        }
                    )
                }
                
                // German word
                Text(
                    text = entry.germanWord,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.weight(1f)
                )
                
                // Audio playback button
                IconButton(
                    onClick = { onPlayAudio(entry.germanWord) },
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.VolumeUp,
                        contentDescription = "Play pronunciation",
                        tint = MaterialTheme.colorScheme.secondary
                    )
                }
            }
            
            // Word type
            if (entry.wordType != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = entry.wordType.name.lowercase().replaceFirstChar { it.uppercase() },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }
            
            // Expandable details
            AnimatedVisibility(visible = expanded) {
                Column(
                    modifier = Modifier.padding(top = 12.dp)
                ) {
                    Divider()
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    // Additional grammar info
                    if (entry.pluralForm != null) {
                        DetailRow(label = "Plural", value = entry.pluralForm)
                    }
                    if (entry.auxiliaryVerb != null) {
                        DetailRow(label = "Auxiliary", value = entry.auxiliaryVerb)
                    }
                    if (entry.comparative != null) {
                        DetailRow(label = "Comparative", value = entry.comparative)
                    }
                    if (entry.superlative != null) {
                        DetailRow(label = "Superlative", value = entry.superlative)
                    }
                    
                    // Examples
                    if (entry.examples.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Examples:",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold
                        )
                        entry.examples.take(3).forEach { example ->
                            Text(
                                text = "• ${example.german}",
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(start = 8.dp, top = 4.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun GenderChip(gender: GermanGender) {
    val (color, text) = when (gender) {
        GermanGender.DER -> Color(0xFF2196F3) to "der"
        GermanGender.DIE -> Color(0xFFE91E63) to "die"
        GermanGender.DAS -> Color(0xFF9C27B0) to "das"
    }
    
    Surface(
        shape = RoundedCornerShape(4.dp),
        color = color,
        modifier = Modifier.padding(end = 4.dp)
    ) {
        Text(
            text = text,
            color = Color.White,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "$label:",
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
fun DictionaryNotImportedView(onImportClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.MenuBook,
            contentDescription = null,
            modifier = Modifier.size(96.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            text = "Dictionary Not Imported",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "Import the FreeDict English-German dictionary to start using offline search",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.outline,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Button(
            onClick = onImportClick,
            modifier = Modifier.fillMaxWidth(0.6f)
        ) {
            Icon(
                imageVector = Icons.Default.Download,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Import Dictionary")
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "This will take 30-60 minutes and use ~100MB storage",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.outline
        )
    }
}

@Composable
fun DictionaryImportProgressView(
    importProgress: com.hellogerman.app.data.dictionary.DictionaryImporter.ImportProgress?
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(64.dp),
            progress = (importProgress?.progressPercentage?.toFloat() ?: 0f) / 100f
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            text = "Importing Dictionary",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = importProgress?.message ?: "Starting import...",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        if (importProgress != null) {
            Text(
                text = "${importProgress.progressPercentage}% - ${importProgress.successfulEntries} / ${importProgress.totalEntries} entries",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.outline
            )
        }
    }
}

@Composable
fun DictionaryManagementDialog(
    isDictionaryImported: Boolean,
    isImporting: Boolean,
    onDismiss: () -> Unit,
    onImportClick: () -> Unit,
    onClearClick: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Dictionary Management") },
        text = {
            Column {
                Text("Manage your offline dictionary")
                Spacer(modifier = Modifier.height(16.dp))
                if (isDictionaryImported) {
                    Text(
                        text = "✓ Dictionary imported",
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        },
        confirmButton = {
            if (!isDictionaryImported && !isImporting) {
                TextButton(onClick = {
                    onImportClick()
                    onDismiss()
                }) {
                    Text("Import")
                }
            }
        },
        dismissButton = {
            Row {
                if (isDictionaryImported) {
                    TextButton(
                        onClick = {
                            onClearClick()
                            onDismiss()
                        },
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Text("Clear")
                    }
                }
                TextButton(onClick = onDismiss) {
                    Text("Close")
                }
            }
        }
    )
}

@Composable
fun StatisticsDialog(
    statistics: com.hellogerman.app.data.dictionary.DictionaryImporter.DictionaryStatistics?,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Dictionary Statistics") },
        text = {
            if (statistics != null) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    StatRow("Total Entries", statistics.totalEntries.toString())
                    Divider()
                    StatRow("Nouns", statistics.nouns.toString())
                    StatRow("  • Masculine (der)", statistics.masculineNouns.toString())
                    StatRow("  • Feminine (die)", statistics.feminineNouns.toString())
                    StatRow("  • Neuter (das)", statistics.neuterNouns.toString())
                    StatRow("Verbs", statistics.verbs.toString())
                    StatRow("Adjectives", statistics.adjectives.toString())
                    Divider()
                    StatRow("With Examples", statistics.entriesWithExamples.toString())
                    StatRow("Database Size", "${statistics.databaseSizeMB} MB")
                }
            } else {
                Text("No statistics available")
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}

@Composable
fun StatRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium)
        Text(value, fontWeight = FontWeight.Bold)
    }
}

