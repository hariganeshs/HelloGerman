# Leo Dictionary-Style Implementation Plan for HelloGerman

## Executive Summary

This document outlines a comprehensive plan to transform the HelloGerman dictionary into a Leo dictionary-style experience, focusing on unified search, comprehensive grammar information, pronunciation, and enhanced user experience. The plan leverages the existing FreeDict dictionaries as the primary source while adding essential features for German language learning.

## Current State Analysis

### Existing Infrastructure ✅
- **FreeDict Integration**: Both German-to-English (`deu-eng`) and English-to-German (`eng-deu`) dictionaries are available
- **Unified Search System**: Basic unified search with language detection is already implemented
- **Dictionary Models**: Comprehensive data models for search results and translations
- **UI Framework**: Dictionary screen with search functionality and result display
- **Caching System**: Room database caching for offline access

### Current Limitations ❌
1. **Gender Information**: Limited gender extraction from FreeDict entries
2. **Pronunciation**: No IPA or audio pronunciation data
3. **Grammar Elements**: Missing comprehensive grammar information (declensions, conjugations)
4. **Search Experience**: Not fully unified - still requires some language direction awareness
5. **Data Completeness**: FreeDict entries lack rich grammatical metadata

## Requirements Analysis

### 1. Gender Display Requirements
- **Always display noun gender** before German words (der/die/das)
- **Example**: "die Mutter" instead of just "Mutter"
- **Implementation**: Enhanced gender extraction from FreeDict + fallback rules

### 2. Pronunciation Requirements
- **IPA transcription** for every German word
- **Audio pronunciation** when available
- **Implementation**: Integrate with Wiktionary API for IPA data

### 3. Grammar Elements Requirements
- **Noun declensions** (all cases: nominative, genitive, dative, accusative)
- **Verb conjugations** (present, past, perfect, future tenses)
- **Adjective declensions** and comparative forms
- **Plural forms** for nouns
- **Implementation**: Enhanced parsing + external grammar APIs

### 4. Unified Search Requirements
- **Single search interface** for both English and German words
- **Automatic language detection** and bidirectional search
- **Cross-referencing** between language directions
- **Implementation**: Enhance existing unified search system

## Implementation Plan

### Phase 1: Enhanced Gender and Basic Grammar (Week 1-2)

#### 1.1 Improved Gender Extraction
**Files to Modify:**
- `app/src/main/java/com/hellogerman/app/data/dictionary/FreedictReader.kt`
- `app/src/main/java/com/hellogerman/app/data/models/DictionaryModels.kt`

**Implementation:**
```kotlin
// Enhanced gender extraction with fallback rules
private fun extractGenderFromRaw(raw: String, word: String): String? {
    // 1. Check explicit tags in FreeDict entry
    val explicitGender = extractExplicitGender(raw)
    if (explicitGender != null) return explicitGender
    
    // 2. Apply German gender rules as fallback
    return applyGenderRules(word)
}

private fun applyGenderRules(word: String): String? {
    val lowerWord = word.lowercase()
    return when {
        // Masculine endings
        lowerWord.endsWith("er") && !lowerWord.endsWith("chen") -> "der"
        lowerWord.endsWith("ling") -> "der"
        lowerWord.endsWith("ig") -> "der"
        
        // Feminine endings
        lowerWord.endsWith("ung") -> "die"
        lowerWord.endsWith("heit") -> "die"
        lowerWord.endsWith("keit") -> "die"
        lowerWord.endsWith("schaft") -> "die"
        lowerWord.endsWith("tion") -> "die"
        lowerWord.endsWith("sion") -> "die"
        lowerWord.endsWith("nis") -> "die"
        
        // Neuter endings
        lowerWord.endsWith("chen") -> "das"
        lowerWord.endsWith("lein") -> "das"
        lowerWord.endsWith("ment") -> "das"
        lowerWord.endsWith("um") -> "das"
        
        else -> null
    }
}
```

#### 1.2 Enhanced UI Display
**Files to Modify:**
- `app/src/main/java/com/hellogerman/app/ui/screens/DictionaryScreen.kt`
- `app/src/main/java/com/hellogerman/app/ui/screens/UnifiedResultsCard.kt`

**Implementation:**
```kotlin
@Composable
fun GermanWordWithGender(
    word: String,
    gender: String?,
    translations: List<String>
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Gender chip
        gender?.let { g ->
            GenderChip(gender = g)
        }
        
        // Word with proper formatting
        Text(
            text = formatGermanWord(word, gender),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun GenderChip(gender: String) {
    val (color, text) = when (gender.lowercase()) {
        "der" -> MaterialTheme.colorScheme.primary to "der"
        "die" -> MaterialTheme.colorScheme.secondary to "die"
        "das" -> MaterialTheme.colorScheme.tertiary to "das"
        else -> MaterialTheme.colorScheme.surface to gender
    }
    
    Surface(
        color = color,
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            color = color.onColor,
            fontWeight = FontWeight.Bold
        )
    }
}
```

### Phase 2: Pronunciation Integration (Week 2-3)

#### 2.1 IPA Data Integration
**New Files:**
- `app/src/main/java/com/hellogerman/app/data/api/WiktionaryApiService.kt`
- `app/src/main/java/com/hellogerman/app/data/parser/IpaExtractor.kt`

**Implementation:**
```kotlin
// Wiktionary API service for IPA data
interface WiktionaryApiService {
    @GET("w/api.php")
    suspend fun getPageContent(
        @Query("action") action: String = "parse",
        @Query("format") format: String = "json",
        @Query("prop") prop: String = "wikitext",
        @Query("page") page: String,
        @Query("disableeditsection") disableeditsection: Boolean = true
    ): WiktionaryResponse
}

// IPA extraction from Wiktionary content
class IpaExtractor {
    fun extractIpa(wikitext: String): String? {
        // Extract IPA from {{IPA|de|/ˈmʊtɐ/}} templates
        val ipaPattern = Regex("\\{\\{IPA\\|de\\|([^}]+)\\}\\}")
        val match = ipaPattern.find(wikitext)
        return match?.groupValues?.get(1)?.removeSurrounding("/")
    }
    
    fun extractAudioUrl(wikitext: String): String? {
        // Extract audio from {{Audio|De-Mutter.ogg}} templates
        val audioPattern = Regex("\\{\\{Audio\\|([^}]+)\\.ogg\\}\\}")
        val match = audioPattern.find(wikitext)
        return match?.groupValues?.get(1)?.let { filename ->
            "https://upload.wikimedia.org/wikipedia/commons/$filename.ogg"
        }
    }
}
```

#### 2.2 Enhanced Dictionary Models
**Files to Modify:**
- `app/src/main/java/com/hellogerman/app/data/models/DictionaryModels.kt`

**Implementation:**
```kotlin
data class DictionarySearchResult(
    // ... existing fields ...
    val ipa: String? = null,
    val audioUrl: String? = null,
    val pronunciation: PronunciationInfo? = null
)

data class PronunciationInfo(
    val ipa: String,
    val audioUrl: String? = null,
    val isAvailable: Boolean = true
)
```

### Phase 3: Comprehensive Grammar Integration (Week 3-4)

#### 3.1 Grammar Data Sources
**New Files:**
- `app/src/main/java/com/hellogerman/app/data/api/GermanGrammarApiService.kt`
- `app/src/main/java/com/hellogerman/app/data/models/GrammarModels.kt`

**Implementation:**
```kotlin
// Grammar data models
data class NounDeclension(
    val nominative: String,
    val genitive: String,
    val dative: String,
    val accusative: String,
    val plural: String
)

data class VerbConjugation(
    val infinitive: String,
    val present: Map<String, String>,
    val past: Map<String, String>,
    val perfect: Map<String, String>,
    val future: Map<String, String>,
    val participle: String,
    val auxiliary: String // haben or sein
)

data class AdjectiveDeclension(
    val positive: String,
    val comparative: String,
    val superlative: String,
    val declensionTable: Map<String, Map<String, String>>
)

// Grammar API service
interface GermanGrammarApiService {
    @GET("api/verbs/{verb}")
    suspend fun getVerbConjugation(@Path("verb") verb: String): VerbConjugationResponse
    
    @GET("api/nouns/{noun}")
    suspend fun getNounDeclension(@Path("noun") noun: String): NounDeclensionResponse
}
```

#### 3.2 Grammar UI Components
**New Files:**
- `app/src/main/java/com/hellogerman/app/ui/components/GrammarComponents.kt`

**Implementation:**
```kotlin
@Composable
fun NounDeclensionTable(declension: NounDeclension) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Declension",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Declension table
            LazyColumn {
                items(listOf(
                    "Nominative" to declension.nominative,
                    "Genitive" to declension.genitive,
                    "Dative" to declension.dative,
                    "Accusative" to declension.accusative,
                    "Plural" to declension.plural
                )) { (case, form) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = case,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = form,
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.End
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun VerbConjugationTable(conjugation: VerbConjugation) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Conjugation",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Present tense
            Text(
                text = "Present Tense",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Medium
            )
            
            conjugation.present.forEach { (pronoun, form) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = pronoun,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = form,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.End
                    )
                }
            }
        }
    }
}
```

### Phase 4: Enhanced Unified Search (Week 4-5)

#### 4.1 Improved Language Detection
**Files to Modify:**
- `app/src/main/java/com/hellogerman/app/data/dictionary/LanguageDetector.kt`

**Implementation:**
```kotlin
class EnhancedLanguageDetector {
    fun detectLanguage(word: String): LanguageHint {
        val cleanWord = word.trim().lowercase()
        
        // Strong German indicators
        if (containsGermanCharacters(cleanWord)) return LanguageHint.GERMAN
        if (hasGermanEndings(cleanWord)) return LanguageHint.POSSIBLY_GERMAN
        if (hasGermanPrefixes(cleanWord)) return LanguageHint.POSSIBLY_GERMAN
        
        // Strong English indicators
        if (hasEnglishEndings(cleanWord)) return LanguageHint.POSSIBLY_ENGLISH
        if (hasEnglishPatterns(cleanWord)) return LanguageHint.POSSIBLY_ENGLISH
        
        return LanguageHint.UNKNOWN
    }
    
    private fun containsGermanCharacters(word: String): Boolean {
        return word.contains(Regex("[äöüßÄÖÜ]"))
    }
    
    private fun hasGermanEndings(word: String): Boolean {
        val germanEndings = listOf(
            "chen", "lein", "ung", "heit", "keit", "schaft", 
            "tion", "sion", "ment", "ling", "ig"
        )
        return germanEndings.any { word.endsWith(it) }
    }
    
    private fun hasEnglishEndings(word: String): Boolean {
        val englishEndings = listOf(
            "ing", "tion", "sion", "ness", "ment", "able", "ible"
        )
        return englishEndings.any { word.endsWith(it) }
    }
}
```

#### 4.2 Bidirectional Search Enhancement
**Files to Modify:**
- `app/src/main/java/com/hellogerman/app/data/repository/UnifiedDictionaryRepository.kt`

**Implementation:**
```kotlin
class EnhancedUnifiedDictionaryRepository(
    private val deReader: FreedictReader,
    private val enReader: FreedictReader,
    private val wiktionaryService: WiktionaryApiService,
    private val grammarService: GermanGrammarApiService
) {
    suspend fun searchUnified(word: String): UnifiedSearchResult {
        val detectedLanguage = languageDetector.detectLanguage(word)
        
        // Always search both directions for comprehensive results
        val deResult = searchGermanToEnglish(word)
        val enResult = searchEnglishToGerman(word)
        
        // Enhance results with additional data
        val enhancedDeResult = enhanceWithPronunciationAndGrammar(deResult)
        val enhancedEnResult = enhanceWithPronunciationAndGrammar(enResult)
        
        return UnifiedSearchResult.combine(
            originalWord = word,
            detectedLanguage = detectedLanguage,
            confidence = calculateConfidence(deResult, enResult),
            deResult = enhancedDeResult,
            enResult = enhancedEnResult,
            searchStrategy = SearchStrategy.BOTH_DIRECTIONS
        )
    }
    
    private suspend fun enhanceWithPronunciationAndGrammar(
        result: DictionarySearchResult?
    ): DictionarySearchResult? {
        if (result == null || !result.hasResults) return result
        
        // Add pronunciation data
        val pronunciation = wiktionaryService.getPronunciation(result.originalWord)
        
        // Add grammar data
        val grammar = grammarService.getGrammarInfo(result.originalWord)
        
        return result.copy(
            ipa = pronunciation?.ipa,
            audioUrl = pronunciation?.audioUrl,
            grammar = grammar
        )
    }
}
```

### Phase 5: UI/UX Enhancements (Week 5-6)

#### 5.1 Leo-Style Layout
**Files to Modify:**
- `app/src/main/java/com/hellogerman/app/ui/screens/DictionaryScreen.kt`

**Implementation:**
```kotlin
@Composable
fun LeoStyleDictionaryLayout(
    searchResult: UnifiedSearchResult,
    onPlayAudio: (String) -> Unit,
    onAddToVocabulary: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header with word, gender, and pronunciation
        item {
            WordHeader(
                word = searchResult.primaryTranslation?.germanWord ?: searchResult.originalWord,
                gender = searchResult.primaryTranslation?.gender,
                ipa = searchResult.primaryTranslation?.ipa,
                audioUrl = searchResult.primaryTranslation?.audioUrl,
                onPlayAudio = onPlayAudio
            )
        }
        
        // Quick actions
        item {
            QuickActions(
                word = searchResult.originalWord,
                onAddToVocabulary = onAddToVocabulary,
                onCopy = { /* copy to clipboard */ },
                onShare = { /* share functionality */ }
            )
        }
        
        // Translations
        item {
            TranslationsSection(
                translations = searchResult.combinedTranslations
            )
        }
        
        // Grammar information
        searchResult.grammar?.let { grammar ->
            item {
                GrammarSection(grammar = grammar)
            }
        }
        
        // Examples
        item {
            ExamplesSection(
                examples = searchResult.combinedTranslations.flatMap { it.examples }
            )
        }
        
        // Cross-references
        if (searchResult.isCrossReference) {
            item {
                CrossReferenceSection(
                    germanToEnglish = searchResult.germanToEnglish,
                    englishToGerman = searchResult.englishToGerman
                )
            }
        }
    }
}
```

#### 5.2 Enhanced Search Experience
**Implementation:**
```kotlin
@Composable
fun EnhancedSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    suggestions: List<String>,
    onSuggestionClick: (String) -> Unit
) {
    Column {
        OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            label = { Text("Search German or English") },
            trailingIcon = {
                IconButton(onClick = onSearch) {
                    Icon(Icons.Default.Search, contentDescription = "Search")
                }
            },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = { onSearch() })
        )
        
        // Show suggestions
        if (suggestions.isNotEmpty()) {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                items(suggestions) { suggestion ->
                    SuggestionChip(
                        onClick = { onSuggestionClick(suggestion) },
                        label = { Text(suggestion) }
                    )
                }
            }
        }
    }
}
```

## Data Sources and APIs

### Primary Sources
1. **FreeDict Dictionaries** (Current)
   - German-to-English: `freedict-deu-eng-1.9-fd1.dictd`
   - English-to-German: `freedict-eng-deu-1.9-fd1.dictd`
   - **Usage**: Primary translation source, enhanced with gender extraction

2. **Wiktionary API** (New)
   - **Endpoint**: `https://de.wiktionary.org/w/api.php`
   - **Usage**: IPA pronunciation, audio files, additional definitions
   - **Rate Limit**: Polite usage (1 request per second)

3. **German Verb API** (Existing)
   - **Endpoint**: `https://german-verbs-api.onrender.com/api/verbs/{verb}`
   - **Usage**: Verb conjugations
   - **Rate Limit**: Community API, use with fallback

### Secondary Sources
1. **OpenThesaurus** (Existing)
   - **Usage**: Synonyms and related words
   - **Rate Limit**: ~60 requests per minute

2. **Tatoeba API** (Existing)
   - **Usage**: Example sentences
   - **Rate Limit**: Respectful usage

## Technical Implementation Details

### Database Schema Updates
```sql
-- Enhanced dictionary cache table
CREATE TABLE dictionary_entries_enhanced (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    word TEXT NOT NULL,
    language TEXT NOT NULL,
    gender TEXT,
    ipa TEXT,
    audio_url TEXT,
    grammar_data TEXT, -- JSON
    pronunciation_data TEXT, -- JSON
    created_at INTEGER,
    expires_at INTEGER
);

-- Grammar data table
CREATE TABLE grammar_data (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    word TEXT NOT NULL,
    word_type TEXT NOT NULL, -- noun, verb, adjective
    declension_data TEXT, -- JSON
    conjugation_data TEXT, -- JSON
    created_at INTEGER
);
```

### Performance Considerations
1. **Caching Strategy**
   - Cache pronunciation data for 7 days
   - Cache grammar data for 30 days
   - Cache search results for 24 hours

2. **Offline Support**
   - Essential grammar rules as fallback
   - Basic gender rules for offline operation
   - Cached pronunciation for common words

3. **API Rate Limiting**
   - Implement exponential backoff
   - Use circuit breakers for unreliable APIs
   - Prioritize FreeDict for offline operation

## Testing Strategy

### Unit Tests
- Gender extraction accuracy
- Language detection precision
- Grammar rule application
- IPA extraction from Wiktionary

### Integration Tests
- API service reliability
- Caching behavior
- Offline fallback functionality

### UI Tests
- Search functionality
- Result display accuracy
- Audio playback
- Grammar table rendering

## Success Metrics

### User Experience
- **Search Success Rate**: >95% for common words
- **Gender Accuracy**: >90% for nouns
- **Pronunciation Coverage**: >80% for German words
- **Grammar Completeness**: >70% for verbs and nouns

### Performance
- **Search Response Time**: <500ms for cached results
- **Offline Functionality**: 100% for basic features
- **API Reliability**: >95% uptime with fallbacks

## Timeline and Milestones

### Week 1-2: Foundation
- ✅ Enhanced gender extraction
- ✅ Basic grammar UI components
- ✅ Improved search interface

### Week 3-4: Pronunciation & Grammar
- ✅ IPA integration
- ✅ Audio pronunciation
- ✅ Comprehensive grammar tables

### Week 5-6: Polish & Testing
- ✅ Leo-style UI layout
- ✅ Performance optimization
- ✅ Comprehensive testing

## Risk Mitigation

### Technical Risks
1. **API Reliability**: Implement multiple fallbacks and offline rules
2. **Performance**: Use aggressive caching and lazy loading
3. **Data Quality**: Validate and clean all external data sources

### User Experience Risks
1. **Complexity**: Keep UI simple and intuitive
2. **Offline Usage**: Ensure core functionality works without internet
3. **Learning Curve**: Provide clear visual indicators and help text

## Conclusion

This plan transforms the HelloGerman dictionary into a comprehensive, Leo-style learning tool that provides:

1. **Unified Search**: Single interface for both German and English words
2. **Complete Grammar**: Gender, declensions, conjugations, and pronunciation
3. **Rich Data**: IPA, audio, examples, and cross-references
4. **Offline Support**: Core functionality without internet connection
5. **Learning Focus**: Designed specifically for German language learners

The implementation leverages existing infrastructure while adding essential features that make the dictionary a powerful learning companion, similar to Leo dictionary but optimized for the HelloGerman app's learning-focused approach.
