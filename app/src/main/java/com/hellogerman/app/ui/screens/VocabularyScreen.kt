package com.hellogerman.app.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.hellogerman.app.data.entities.UserVocabulary
import com.hellogerman.app.ui.viewmodel.VocabularyViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VocabularyScreen(
    navController: NavController,
    vocabularyViewModel: VocabularyViewModel = viewModel()
) {
    val vocabulary by vocabularyViewModel.vocabulary.collectAsState()
    val isLoading by vocabularyViewModel.isLoading.collectAsState()
    val selectedFilter by vocabularyViewModel.selectedFilter.collectAsState()
    
    LaunchedEffect(Unit) {
        vocabularyViewModel.loadVocabulary()
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Vocabulary") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { vocabularyViewModel.toggleFavoriteFilter() }) {
                        Icon(
                            Icons.Default.Favorite,
                            contentDescription = "Favorites",
                            tint = if (selectedFilter == "favorites") 
                                MaterialTheme.colorScheme.primary 
                            else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            // Filter chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = selectedFilter == "all",
                    onClick = { vocabularyViewModel.setFilter("all") },
                    label = { Text("All") }
                )
                FilterChip(
                    selected = selectedFilter == "favorites",
                    onClick = { vocabularyViewModel.setFilter("favorites") },
                    label = { Text("Favorites") }
                )
                FilterChip(
                    selected = selectedFilter == "recent",
                    onClick = { vocabularyViewModel.setFilter("recent") },
                    label = { Text("Recent") }
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Vocabulary count
            Text(
                text = "${vocabulary.size} words in vocabulary",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Vocabulary list
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (vocabulary.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.BookmarkBorder,
                            contentDescription = "Empty",
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No words in vocabulary yet",
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Add words from the dictionary to get started",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(vocabulary) { vocab ->
                        VocabularyCard(
                            vocabulary = vocab,
                            onToggleFavorite = { vocabularyViewModel.toggleFavorite(vocab.word) },
                            onDelete = { vocabularyViewModel.deleteVocabulary(vocab.word) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun VocabularyCard(
    vocabulary: UserVocabulary,
    onToggleFavorite: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = vocabulary.word,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = vocabulary.translation,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                Row {
                    IconButton(onClick = onToggleFavorite) {
                        Icon(
                            Icons.Default.Favorite,
                            contentDescription = "Toggle Favorite",
                            tint = if (vocabulary.isFavorite) 
                                MaterialTheme.colorScheme.primary 
                            else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(onClick = onDelete) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
            
            // Additional info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                vocabulary.gender?.let { gender ->
                    Text(
                        text = gender,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .background(
                                MaterialTheme.colorScheme.primaryContainer,
                                RoundedCornerShape(4.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
                
                vocabulary.level?.let { level ->
                    Text(
                        text = level,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier
                            .background(
                                MaterialTheme.colorScheme.secondaryContainer,
                                RoundedCornerShape(4.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
                
                Spacer(modifier = Modifier.weight(1f))
                
                Text(
                    text = "Added ${formatDate(vocabulary.addedAt)}",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

private fun formatDate(timestamp: Long): String {
    val now = System.currentTimeMillis()
    val diff = now - timestamp
    val days = diff / (24 * 60 * 60 * 1000)
    
    return when {
        days == 0L -> "today"
        days == 1L -> "yesterday"
        days < 7L -> "$days days ago"
        days < 30L -> "${days / 7} weeks ago"
        else -> "${days / 30} months ago"
    }
}
