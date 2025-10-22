# Dictionary Quality Fix Plan

## Problem Analysis

Based on the FreeDict data analysis, the issues are **NOT** with the data source but with the **parsing and extraction logic**. FreeDict contains excellent data with:

- ✅ **Perfect gender information**: `<fem>`, `<masc>`, `<neut>` tags
- ✅ **Rich examples**: German sentences with English translations  
- ✅ **Complete pronunciation**: IPA for all words
- ✅ **Quality translations**: Multiple options per word

## Root Cause: Parser Issues

### Issue 1: Gender Extraction is Broken
**Current Problem**: The parser removes articles (`der`, `die`, `das`) and then tries to detect gender
**Solution**: Extract gender from explicit `<fem>`, `<masc>`, `<neut>` tags in FreeDict

### Issue 2: Translation Filtering is Too Loose  
**Current Problem**: Accepts any text as translation, including phrases and technical terms
**Solution**: Implement strict filtering for common vocabulary

### Issue 3: Example Extraction is Poor
**Current Problem**: Examples may be English or low-quality
**Solution**: Extract German sentences with English translations from FreeDict

### Issue 4: Wrong Data Source Priority
**Current Problem**: Uses English→German as primary source
**Solution**: Use German→English as primary (better gender data)

## Implementation Plan

### Step 1: Fix Gender Extraction ✅

**File**: `DictdDataParser.kt`

**Current broken code**:
```kotlin
// Line 129: REMOVES the gender article!
cleaned = cleaned.replace(Regex("^(der|die|das|ein|eine)\\s+", RegexOption.IGNORE_CASE), "")
```

**New approach**:
```kotlin
private fun extractGenderFromTags(text: String): GermanGender? {
    return when {
        text.contains("<fem>") -> GermanGender.DIE
        text.contains("<masc>") -> GermanGender.DER  
        text.contains("<neut>") -> GermanGender.DAS
        else -> null
    }
}

private fun extractWordFromTranslation(text: String): String {
    // Remove gender tags but keep the word
    return text.replace(Regex("<[^>]+>"), "").trim()
}
```

### Step 2: Implement Strict Translation Filtering ✅

**File**: `DictdDataParser.kt`

**Add validation methods**:
```kotlin
private fun isValidGermanWord(word: String): Boolean {
    // Must be 2-50 characters
    if (word.length !in 2..50) return false
    
    // Must contain German characters or be capitalized (German nouns)
    val hasGermanChars = word.contains(Regex("[äöüßÄÖÜ]"))
    val isCapitalized = word[0].isUpperCase()
    
    // Reject all-lowercase (likely English)
    if (word.matches(Regex("^[a-z]+$"))) return false
    
    // Reject multi-word phrases (>2 words)
    if (word.split(" ").size > 2) return false
    
    return hasGermanChars || isCapitalized
}

private fun isCommonVocabulary(word: String, domainLabels: List<String>): Boolean {
    // Prioritize common domains
    val commonDomains = listOf("soc.", "gen.", "fam.", "everyday")
    val technicalDomains = listOf("chem.", "tech.", "med.", "biol.", "phys.")
    
    return when {
        domainLabels.any { it in commonDomains } -> true
        domainLabels.any { it in technicalDomains } -> false
        domainLabels.isEmpty() -> true // No domain = likely common
        else -> false
    }
}
```

### Step 3: Fix Example Extraction ✅

**File**: `DictdDataParser.kt`

**Current problem**: Extracts any text as examples
**New approach**: Extract German sentences with English translations

```kotlin
private fun extractExamples(text: String): List<DictionaryExample> {
    val examples = mutableListOf<DictionaryExample>()
    val lines = text.split('\n')
    
    for (line in lines) {
        val trimmed = line.trim()
        
        // Look for pattern: "German sentence" - English translation
        val exampleMatch = Regex("\"([^\"]+)\"\\s*-\\s*(.+)").find(trimmed)
        if (exampleMatch != null) {
            val german = exampleMatch.groupValues[1]
            val english = exampleMatch.groupValues[2]
            
            // Validate German sentence
            if (isValidGermanExample(german)) {
                examples.add(DictionaryExample(
                    german = german,
                    english = english
                ))
            }
        }
    }
    
    return examples.take(3) // Limit to 3 best examples
}

private fun isValidGermanExample(text: String): Boolean {
    // Must be 10-200 characters
    if (text.length !in 10..200) return false
    
    // Must contain German characters or capitalized nouns
    val hasGermanIndicators = text.contains(Regex("[äöüßÄÖÜ]")) ||
                              text.split(" ").any { it[0].isUpperCase() && it.length > 1 }
    
    // Should not be all lowercase (likely English)
    if (text == text.lowercase()) return false
    
    return hasGermanIndicators
}
```

### Step 4: Use German→English as Primary Source ✅

**File**: `DictionaryImporter.kt`

**Current approach**: Import both directions equally
**New approach**: Use German→English as primary, derive English→German

```kotlin
// Phase 1: Import German → English (PRIMARY)
notifyProgress(listener, ImportPhase.IMPORTING_ENTRIES, totalEntries, 0, 0, 0, 0, totalBatches, "Importing German → English entries...")
for (indexEntry in deuEngIndex) {
    val entries = processDeuEngEntry(indexEntry)
    if (entries != null) {
        insertBatch(entries)
    }
}

// Phase 2: Import English → German (SECONDARY, filtered)
notifyProgress(listener, ImportPhase.IMPORTING_ENTRIES, totalEntries, processedCount, successfulEntries, failedEntries, batchNumber, totalBatches, "Importing English → German entries...")
for (indexEntry in engDeuIndex) {
    val entries = processEngDeuEntry(indexEntry)
    if (entries != null && isCommonWord(entries.first().englishWord)) {
        insertBatch(entries)
    }
}
```

### Step 5: Implement Smart Ranking ✅

**File**: `DictionaryDao.kt`

**Current ranking**: Basic exact match priority
**New ranking**: Multi-factor relevance scoring

```kotlin
@Query("""
    SELECT * FROM dictionary_entries 
    WHERE english_normalized = :word 
    ORDER BY 
        CASE WHEN word_type = 'NOUN' THEN 0 ELSE 1 END,
        CASE WHEN gender IS NOT NULL THEN 0 ELSE 1 END,
        CASE WHEN word_length <= 10 THEN 0 ELSE 1 END,
        CASE WHEN source = 'FreeDict-DeuEng' THEN 0 ELSE 1 END,
        word_length ASC,
        english_word ASC
    LIMIT :limit
""")
```

### Step 6: Add Common Words Filter ✅

**New File**: `CommonGermanWords.kt`

```kotlin
object CommonGermanWords {
    val COMMON_WORDS = setOf(
        "Mutter", "Vater", "Kind", "Haus", "Auto", "Buch", "Wasser", "Brot",
        "Apfel", "Katze", "Hund", "Baum", "Blume", "Sonne", "Mond", "Stern",
        // ... top 1000 common German words
    )
    
    fun isCommonWord(word: String): Boolean {
        return COMMON_WORDS.contains(word.capitalize())
    }
}
```

## Expected Results

### Before Fix:
- "mother" → "brutfarn", "monaster", "der mutter" ❌
- "Mutter" → "der Mutter" (wrong gender) ❌
- No examples ❌
- Unrelated results ❌

### After Fix:
- "mother" → "die Mutter" ✅
- "Mutter" → "mother" (gender: DIE) ✅  
- Examples: "allein erziehende Mutter - single mother" ✅
- Only relevant, common words ✅

## Files to Modify

1. **`DictdDataParser.kt`** - Fix gender extraction, add filtering, improve examples
2. **`DictionaryImporter.kt`** - Use German→English as primary source
3. **`DictionaryDao.kt`** - Improve ranking algorithm
4. **`CommonGermanWords.kt`** - NEW FILE - Common vocabulary filter

## Testing Strategy

### Test Cases:
1. **"mother"** → Should show ONLY "die Mutter"
2. **"father"** → Should show ONLY "der Vater"  
3. **"apple"** → Should show "der Apfel"
4. **"Mutter"** → Should show "mother" with gender DIE
5. **"Vater"** → Should show "father" with gender DER

### Success Criteria:
- ✅ Exact gender from FreeDict tags
- ✅ Common vocabulary prioritized
- ✅ Quality German examples
- ✅ No unrelated results
- ✅ No technical terms for common words

## Implementation Priority

1. **HIGH**: Fix gender extraction (use `<fem>`, `<masc>`, `<neut>` tags)
2. **HIGH**: Implement translation filtering (common words only)
3. **MEDIUM**: Improve example extraction (German sentences)
4. **MEDIUM**: Use German→English as primary source
5. **LOW**: Add smart ranking and common words filter

The FreeDict data is **perfect** - we just need to parse it correctly!
