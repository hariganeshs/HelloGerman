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
            // Cross-reference indicator at the very top
            if (result.isCrossReference) {
                CrossReferenceIndicator()
                Spacer(modifier = Modifier.height(12.dp))
            }
            
            // Main German word with gender at the top
            if (result.combinedTranslations.isNotEmpty()) {
                val primaryTranslation = result.combinedTranslations.first()
                PrimaryWordDisplay(
                    translationGroup = primaryTranslation,
                    viewModel = viewModel
                )
                
                Spacer(modifier = Modifier.height(16.dp))
            }
            
            // Language detection indicator
            LanguageDetectionIndicator(
                detectedLanguage = result.detectedLanguage,
                confidence = result.confidence
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Translation groups (remaining translations)
            if (result.combinedTranslations.size > 1) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    result.combinedTranslations.drop(1).forEach { translationGroup ->
                        TranslationGroupCard(
                            translationGroup = translationGroup,
                            viewModel = viewModel
                        )
                    }
                }
            }
        }
    }
}

/**
 * Determine if a German article should be added to a word
 */
private fun shouldAddGermanArticle(word: String, gender: String?): Boolean {
    // Don't add articles to English phrases
    if (isEnglishPhrase(word)) {
        return false
    }
    
    // Don't add articles if no gender is specified
    if (gender == null) {
        return false
    }
    
    // Don't add articles to words that already have them
    if (word.startsWith("der ", ignoreCase = true) || 
        word.startsWith("die ", ignoreCase = true) || 
        word.startsWith("das ", ignoreCase = true)) {
        return false
    }
    
    // Add article for single German words with gender
    return word.split(" ").size == 1
}

/**
 * Check if a word/phrase is English (shouldn't get German articles)
 */
private fun isEnglishPhrase(word: String): Boolean {
    val lowerWord = word.lowercase()
    
    // Common English words that shouldn't get German articles
    val englishWords = setOf(
        "apple", "orange", "banana", "peel", "compare", "and", "or", "the", "a", "an",
        "to", "for", "with", "from", "by", "in", "on", "at", "of", "is", "are", "was", "were"
    )
    
    // Check if it contains English words
    val words = lowerWord.split(" ")
    val englishWordCount = words.count { it in englishWords }
    
    // If more than half the words are English, treat as English phrase
    if (words.size > 1 && englishWordCount > words.size / 2) {
        return true
    }
    
    // Check for common English phrase patterns
    val englishPhrasePatterns = listOf(
        "peel an", "compare.*and.*", "apples and oranges", "eat an", "buy a", "have a"
    )
    
    return englishPhrasePatterns.any { pattern ->
        Regex(pattern, RegexOption.IGNORE_CASE).containsMatchIn(lowerWord)
    }
}

/**
 * Primary word display showing the main German word with gender and pronunciation
 */
@Composable
private fun PrimaryWordDisplay(
    translationGroup: TranslationGroup,
    viewModel: DictionaryViewModel,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        // German word with gender
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val formattedGermanWord = if (shouldAddGermanArticle(translationGroup.germanWord, translationGroup.gender)) {
                val article = when (translationGroup.gender?.lowercase()) {
                    "der", "masculine" -> "der"
                    "die", "feminine" -> "die"
                    "das", "neuter" -> "das"
                    else -> "der" // Default fallback
                }
                "$article ${translationGroup.germanWord}"
            } else {
                translationGroup.germanWord
            }
            
            Text(
                text = formattedGermanWord,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = AccentBlue
            )
            
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
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Word type and gender chips
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            translationGroup.wordType?.let { wordType ->
                Surface(
                    color = AccentBlue.copy(alpha = 0.1f),
                    shape = MaterialTheme.shapes.small
                ) {
                    Text(
                        text = wordType,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = AccentBlue,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
            
            translationGroup.gender?.let { gender ->
                val article = when (gender.lowercase()) {
                    "der", "masculine" -> "der"
                    "die", "feminine" -> "die"
                    "das", "neuter" -> "das"
                    else -> gender
                }
                
                Surface(
                    color = AccentBlue.copy(alpha = 0.1f),
                    shape = MaterialTheme.shapes.small
                ) {
                    Text(
                        text = article,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = AccentBlue,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        // Action buttons row
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = { /* Copy functionality */ },
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = Icons.Default.ContentCopy,
                    contentDescription = "Copy",
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("Copy")
            }
            
            OutlinedButton(
                onClick = { /* Share functionality */ },
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = "Share",
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("Share")
            }
            
            OutlinedButton(
                onClick = { /* Add to vocab functionality */ },
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = Icons.Default.BookmarkAdd,
                    contentDescription = "Add to Vocab",
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("Add to Vocab")
            }
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        // Translations section
        Text(
            text = "Translations",
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
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
    }
}

/**
 * Language detection indicator component
 */
@Composable
private fun LanguageDetectionIndicator(
    detectedLanguage: LanguageHint,
    confidence: SearchConfidence,
    modifier: Modifier = Modifier
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
        modifier = modifier,
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
