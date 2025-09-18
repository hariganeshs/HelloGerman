package com.hellogerman.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hellogerman.app.data.models.UnifiedSearchResult
import com.hellogerman.app.data.models.TranslationGroup
import com.hellogerman.app.data.models.GermanWordWithGender
import com.hellogerman.app.data.dictionary.LanguageHint
import com.hellogerman.app.data.dictionary.SearchConfidence
import com.hellogerman.app.ui.theme.AccentBlue
import com.hellogerman.app.ui.viewmodel.DictionaryViewModel

/**
 * Unified results card displaying comprehensive dictionary information
 * Shows translations from both German-to-English and English-to-German dictionaries
 */
@Composable
fun UnifiedResultsCard(
    result: UnifiedSearchResult,
    viewModel: DictionaryViewModel,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header with word and language detection
            WordHeader(
                word = result.originalWord,
                detectedLanguage = result.detectedLanguage,
                confidence = result.confidence,
                viewModel = viewModel,
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Translation groups
            if (result.combinedTranslations.isNotEmpty()) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    result.combinedTranslations.forEach { translationGroup ->
                        TranslationGroupCard(
                            translationGroup = translationGroup,
                            viewModel = viewModel
                        )
                    }
                }
            }
            
            // Cross-reference indicator
            if (result.isCrossReference) {
                Spacer(modifier = Modifier.height(12.dp))
                CrossReferenceIndicator()
            }
        }
    }
}

@Composable
private fun WordHeader(
    word: String,
    detectedLanguage: LanguageHint,
    confidence: SearchConfidence,
    viewModel: DictionaryViewModel,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(
                text = word,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = AccentBlue
            )
            
            // Language detection indicator
            LanguageIndicator(
                detectedLanguage = detectedLanguage,
                confidence = confidence
            )
        }
        
        // Action buttons
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            IconButton(
                onClick = { viewModel.speakWord() }
            ) {
                Icon(
                    imageVector = Icons.Default.VolumeUp,
                    contentDescription = "Speak word",
                    tint = AccentBlue
                )
            }
            
            IconButton(
                onClick = { viewModel.speakWordSlowly() }
            ) {
                Icon(
                    imageVector = Icons.Default.SlowMotionVideo,
                    contentDescription = "Speak slowly",
                    tint = AccentBlue
                )
            }
        }
    }
}

@Composable
private fun LanguageIndicator(
    detectedLanguage: LanguageHint,
    confidence: SearchConfidence
) {
    val (text, color) = when (detectedLanguage) {
        LanguageHint.GERMAN -> "German" to Color(0xFF4CAF50)
        LanguageHint.ENGLISH -> "English" to Color(0xFF2196F3)
        LanguageHint.AMBIGUOUS -> "Both languages" to Color(0xFFFF9800)
        LanguageHint.UNKNOWN -> "Unknown" to Color(0xFF9E9E9E)
    }
    
    val confidenceText = when (confidence) {
        SearchConfidence.HIGH -> "High confidence"
        SearchConfidence.MEDIUM -> "Medium confidence"
        SearchConfidence.LOW -> "Low confidence"
    }
    
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Surface(
            color = color.copy(alpha = 0.1f),
            shape = MaterialTheme.shapes.small
        ) {
            Text(
                text = text,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = color,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }
        
        Text(
            text = confidenceText,
            fontSize = 10.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun TranslationGroupCard(
    translationGroup: TranslationGroup,
    viewModel: DictionaryViewModel
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            // German word with gender
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val formattedGermanWord = translationGroup.gender?.let { gender ->
                    val article = when (gender.lowercase()) {
                        "der", "masculine" -> "der"
                        "die", "feminine" -> "die"
                        "das", "neuter" -> "das"
                        else -> gender
                    }
                    "$article ${translationGroup.germanWord}"
                } ?: translationGroup.germanWord
                
                Text(
                    text = formattedGermanWord,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                // Word type chip
                translationGroup.wordType?.let { wordType ->
                    Surface(
                        color = AccentBlue.copy(alpha = 0.1f),
                        shape = MaterialTheme.shapes.small
                    ) {
                        Text(
                            text = wordType,
                            fontSize = 10.sp,
                            color = AccentBlue,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // English translations
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                translationGroup.englishTranslations.forEach { translation ->
                    Text(
                        text = "• $translation",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }
            
            // Examples
            if (translationGroup.examples.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Examples:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                translationGroup.examples.take(2).forEach { example ->
                    Text(
                        text = "• $example",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun CrossReferenceIndicator() {
    Surface(
        color = AccentBlue.copy(alpha = 0.1f),
        shape = MaterialTheme.shapes.small
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.SwapHoriz,
                contentDescription = null,
                tint = AccentBlue,
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = "Found in both dictionaries",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = AccentBlue
            )
        }
    }
}

/**
 * Simplified search input for unified dictionary
 */
@Composable
fun UnifiedSearchInput(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        placeholder = {
            Text(
                text = "Search in German or English...",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        trailingIcon = {
            IconButton(onClick = onSearch) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = AccentBlue
                )
            }
        },
        modifier = modifier.fillMaxWidth(),
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = AccentBlue,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline
        )
    )
}
