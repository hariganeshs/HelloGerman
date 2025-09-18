# Unified Dictionary Implementation Plan

## Overview
Merge English-to-German and German-to-English dictionaries into a single unified session, similar to Leo dictionary, where users can search both English and German words in the same interface without manual language switching.

## Current State Analysis

### Current Architecture
- **DictionaryViewModel**: Manages language switching with `fromLanguage` and `toLanguage` states
- **OfflineDictionaryRepository**: Uses separate `deReader` (DE→EN) and `enReader` (EN→DE) 
- **DictionaryScreen**: Shows language selection UI with swap functionality
- **Search Logic**: Requires explicit language direction selection

### Current Issues
1. Users must manually select language direction before searching
2. No automatic language detection
3. Separate search sessions for each language direction
4. Limited cross-referencing between dictionaries

## Implementation Plan

### Phase 1: Automatic Language Detection & Unified Search

#### 1.1 Language Detection Service
**File**: `app/src/main/java/com/hellogerman/app/data/dictionary/LanguageDetector.kt`

```kotlin
class LanguageDetector {
    fun detectLanguage(word: String): LanguageHint {
        return when {
            // German character patterns
            word.contains(Regex("[äöüßÄÖÜ]")) -> LanguageHint.GERMAN
            word.endsWith("en") && word.length > 3 -> LanguageHint.POSSIBLY_GERMAN
            word.endsWith("er") && word.length > 3 -> LanguageHint.POSSIBLY_GERMAN
            word.endsWith("chen") -> LanguageHint.GERMAN
            word.endsWith("lein") -> LanguageHint.GERMAN
            
            // English patterns
            word.contains(Regex("[^a-zA-ZäöüßÄÖÜ]")) -> LanguageHint.ENGLISH
            word.endsWith("ing") -> LanguageHint.POSSIBLY_ENGLISH
            word.endsWith("tion") -> LanguageHint.POSSIBLY_ENGLISH
            
            else -> LanguageHint.UNKNOWN
        }
    }
}

enum class LanguageHint {
    GERMAN, ENGLISH, POSSIBLY_GERMAN, POSSIBLY_ENGLISH, UNKNOWN
}
```

#### 1.2 Unified Search Strategy
**File**: `app/src/main/java/com/hellogerman/app/data/repository/UnifiedDictionaryRepository.kt`

```kotlin
class UnifiedDictionaryRepository {
    private val deReader: FreedictReader // German to English
    private val enReader: FreedictReader // English to German
    private val languageDetector = LanguageDetector()
    
    suspend fun searchWord(word: String): UnifiedSearchResult {
        val languageHint = languageDetector.detectLanguage(word)
        
        return when (languageHint) {
            LanguageHint.GERMAN -> searchGermanWord(word)
            LanguageHint.ENGLISH -> searchEnglishWord(word)
            LanguageHint.POSSIBLY_GERMAN -> searchBothDirections(word, preferGerman = true)
            LanguageHint.POSSIBLY_ENGLISH -> searchBothDirections(word, preferEnglish = true)
            LanguageHint.UNKNOWN -> searchBothDirections(word)
        }
    }
    
    private suspend fun searchBothDirections(word: String): UnifiedSearchResult {
        // Try both directions and combine results
        val deResult = searchGermanWord(word)
        val enResult = searchEnglishWord(word)
        
        return UnifiedSearchResult.combine(deResult, enResult)
    }
}
```

#### 1.3 Enhanced Search Result Model
**File**: `app/src/main/java/com/hellogerman/app/data/models/UnifiedSearchResult.kt`

```kotlin
data class UnifiedSearchResult(
    val originalWord: String,
    val detectedLanguage: LanguageHint,
    val germanToEnglish: DictionarySearchResult?,
    val englishToGerman: DictionarySearchResult?,
    val combinedTranslations: List<TranslationGroup>,
    val hasResults: Boolean,
    val confidence: SearchConfidence
) {
    data class TranslationGroup(
        val germanWord: String,
        val englishTranslations: List<String>,
        val gender: String?,
        val wordType: String?,
        val examples: List<String>
    )
}

enum class SearchConfidence {
    HIGH, MEDIUM, LOW
}
```

### Phase 2: UI/UX Improvements

#### 2.1 Simplified Dictionary Screen
**File**: `app/src/main/java/com/hellogerman/app/ui/screens/DictionaryScreen.kt`

**Changes**:
- Remove language selection UI (From/To language picker)
- Remove swap button
- Add single search input with auto-detection
- Show detected language as a subtle indicator
- Display comprehensive results from both directions

```kotlin
@Composable
fun DictionaryScreen(viewModel: DictionaryViewModel = viewModel()) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val searchResult by viewModel.unifiedSearchResult.collectAsState()
    val detectedLanguage by viewModel.detectedLanguage.collectAsState()
    
    Column {
        // Single search input
        SearchInput(
            query = searchQuery,
            onQueryChange = viewModel::updateSearchQuery,
            placeholder = "Search in German or English..."
        )
        
        // Language detection indicator
        if (detectedLanguage != LanguageHint.UNKNOWN) {
            LanguageIndicator(detectedLanguage)
        }
        
        // Unified results
        searchResult?.let { result ->
            UnifiedResultsCard(result)
        }
    }
}
```

#### 2.2 Enhanced Results Display
**File**: `app/src/main/java/com/hellogerman/app/ui/screens/UnifiedResultsCard.kt`

```kotlin
@Composable
fun UnifiedResultsCard(result: UnifiedSearchResult) {
    Card {
        Column {
            // Main word with detected language
            WordHeader(
                word = result.originalWord,
                language = result.detectedLanguage,
                confidence = result.confidence
            )
            
            // Translation groups
            result.combinedTranslations.forEach { group ->
                TranslationGroupCard(group)
            }
            
            // Cross-references
            if (result.germanToEnglish != null && result.englishToGerman != null) {
                CrossReferenceCard(result)
            }
        }
    }
}

@Composable
fun TranslationGroupCard(group: TranslationGroup) {
    Card {
        Column {
            // German word with gender
            Text(
                text = "${group.gender ?: ""} ${group.germanWord}".trim(),
                style = MaterialTheme.typography.headlineSmall
            )
            
            // English translations
            group.englishTranslations.forEach { translation ->
                Text(text = translation)
            }
            
            // Examples
            group.examples.take(2).forEach { example ->
                Text(
                    text = example,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
```

### Phase 3: Enhanced Data Integration

#### 3.1 Cross-Reference System
**Enhancement**: When searching "father", show:
- Primary: "der Vater" (German word with gender)
- Secondary: "der Begründer" (alternative German translation)
- Cross-reference: "Vater" → "father" (reverse lookup)
- Examples from both directions

#### 3.2 Maximum Information Display
**Features**:
- Combine definitions from both dictionaries
- Merge examples from both directions
- Show conjugations for verbs
- Display declensions for nouns
- Include synonyms and related words
- Preserve gender information for all German words

#### 3.3 Smart Result Prioritization
**Logic**:
1. **High Confidence**: Clear language detection → Show primary results
2. **Medium Confidence**: Ambiguous word → Show both directions with indicators
3. **Low Confidence**: Unknown word → Show all possible matches

### Phase 4: Performance Optimizations

#### 4.1 Lazy Loading
- Initialize only the required dictionary reader
- Load cross-references on demand
- Cache frequently searched words

#### 4.2 Memory Management
- Keep only active dictionary in memory
- Implement smart caching for cross-references
- Optimize for mobile memory constraints

## Implementation Steps

### Step 1: Create Language Detection
1. Create `LanguageDetector.kt`
2. Add language detection tests
3. Integrate with existing search flow

### Step 2: Enhance Repository
1. Create `UnifiedDictionaryRepository.kt`
2. Implement dual-direction search
3. Add result combination logic

### Step 3: Update ViewModel
1. Modify `DictionaryViewModel.kt` to use unified search
2. Add unified result state management
3. Remove language switching logic

### Step 4: Redesign UI
1. Simplify `DictionaryScreen.kt`
2. Create `UnifiedResultsCard.kt`
3. Add language detection indicators

### Step 5: Testing & Refinement
1. Test with various word types
2. Verify gender preservation
3. Optimize performance
4. Update documentation

## Expected Benefits

### User Experience
- **Simplified Interface**: Single search input, no language switching
- **Automatic Detection**: Smart language recognition
- **Comprehensive Results**: Maximum information from both dictionaries
- **Leo-like Experience**: Familiar unified dictionary interface

### Technical Benefits
- **Reduced Complexity**: Single search flow
- **Better Performance**: Optimized dual-direction searches
- **Enhanced Data**: Combined information from both dictionaries
- **Maintainable Code**: Cleaner architecture

## Risk Mitigation

### Potential Issues
1. **Language Detection Accuracy**: Implement fallback to manual selection
2. **Performance Impact**: Use lazy loading and caching
3. **Memory Usage**: Optimize dictionary loading
4. **User Confusion**: Clear indicators for detected language

### Fallback Strategy
- If automatic detection fails, show both directions
- Provide manual language override option
- Maintain current functionality as backup

## Success Metrics

### Functional Requirements
- ✅ Search English words without language selection
- ✅ Search German words without language selection
- ✅ Display maximum information from both dictionaries
- ✅ Preserve gender information for all German words
- ✅ Maintain current performance levels

### User Experience Goals
- Reduce search steps from 2 (select language + search) to 1 (search)
- Increase information density per search
- Improve discoverability of cross-references
- Maintain familiar Leo-like interface

## Timeline

- **Week 1**: Language detection and unified repository
- **Week 2**: UI redesign and unified results display
- **Week 3**: Testing, optimization, and refinement
- **Week 4**: Documentation and final polish

## Files to Modify

### New Files
- `LanguageDetector.kt`
- `UnifiedDictionaryRepository.kt`
- `UnifiedSearchResult.kt`
- `UnifiedResultsCard.kt`

### Modified Files
- `DictionaryViewModel.kt` - Remove language switching, add unified search
- `DictionaryScreen.kt` - Simplify UI, remove language selection
- `OfflineDictionaryRepository.kt` - Add unified search methods
- `BUG_LOG.md` - Document implementation

### Configuration
- Update navigation if needed
- Modify any hardcoded language assumptions
- Update tests for new unified flow

---

This plan provides a comprehensive roadmap for implementing a unified dictionary experience that matches Leo's functionality while preserving all current features and improving the user experience.
