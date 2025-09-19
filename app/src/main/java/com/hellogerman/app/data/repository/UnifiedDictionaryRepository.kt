package com.hellogerman.app.data.repository

import android.content.Context
import com.hellogerman.app.data.dictionary.FreedictReader
import com.hellogerman.app.data.dictionary.LanguageDetector
import com.hellogerman.app.data.dictionary.LanguageHint
import com.hellogerman.app.data.dictionary.SearchConfidence
import com.hellogerman.app.data.models.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Unified dictionary repository that provides intelligent search across both German-to-English
 * and English-to-German dictionaries with automatic language detection
 */
class UnifiedDictionaryRepository(
    private val context: Context,
    private val onlineRepository: DictionaryRepository
) {
    
    private val languageDetector = LanguageDetector()
    private val deReader: FreedictReader = FreedictReader.buildGermanToEnglish(context)
    private val enReader: FreedictReader = FreedictReader.buildEnglishToGerman(context)
    
    /**
     * Initialize the unified dictionary repository
     */
    suspend fun initialize() {
        withContext(Dispatchers.IO) {
            try {
                // Lazy initialization - readers will initialize on first use
                android.util.Log.d("UnifiedDictionaryRepository", "Unified dictionary repository initialized")
            } catch (e: Exception) {
                android.util.Log.e("UnifiedDictionaryRepository", "Failed to initialize unified dictionary", e)
                throw e
            }
        }
    }
    
    /**
     * Determine the best search strategy based on language detection and user preferences
     */
    private fun determineSearchStrategy(
        detectedLanguage: LanguageHint, 
        confidence: SearchConfidence, 
        userFromLanguage: String?, 
        userToLanguage: String?
    ): SearchStrategy {
        // Priority 1: Always respect language detection for clear cases
        // This ensures "mother" (English) searches English->German regardless of UI direction
        if (confidence == SearchConfidence.HIGH || confidence == SearchConfidence.MEDIUM) {
            return when (detectedLanguage) {
                LanguageHint.GERMAN -> {
                    android.util.Log.d("UnifiedDictionaryRepository", "High confidence German detection, searching DE->EN")
                    SearchStrategy.GERMAN_ONLY
                }
                LanguageHint.ENGLISH -> {
                    android.util.Log.d("UnifiedDictionaryRepository", "High confidence English detection, searching EN->DE")
                    SearchStrategy.ENGLISH_ONLY
                }
                else -> SearchStrategy.BOTH_DIRECTIONS
            }
        }
        
        // Priority 2: For low confidence or ambiguous cases, consider user direction
        if (userFromLanguage != null && userToLanguage != null) {
            val userFromLang = userFromLanguage.lowercase()
            val userToLang = userToLanguage.lowercase()
            
            // If user direction matches detected language, use that
            if ((userFromLang in listOf("de", "german") && detectedLanguage == LanguageHint.GERMAN) ||
                (userFromLang in listOf("en", "english") && detectedLanguage == LanguageHint.ENGLISH)) {
                return when (detectedLanguage) {
                    LanguageHint.GERMAN -> SearchStrategy.GERMAN_ONLY
                    LanguageHint.ENGLISH -> SearchStrategy.ENGLISH_ONLY
                    else -> SearchStrategy.BOTH_DIRECTIONS
                }
            }
            
            // If user direction conflicts with detection, prefer detection for clear cases
            if (detectedLanguage == LanguageHint.GERMAN && userFromLang in listOf("en", "english")) {
                android.util.Log.d("UnifiedDictionaryRepository", "Detected German but user wants EN->DE, preferring German detection")
                return SearchStrategy.GERMAN_ONLY
            }
            
            if (detectedLanguage == LanguageHint.ENGLISH && userFromLang in listOf("de", "german")) {
                android.util.Log.d("UnifiedDictionaryRepository", "Detected English but user wants DE->EN, preferring English detection")
                return SearchStrategy.ENGLISH_ONLY
            }
        }
        
        // Fall back to automatic detection
        return when (detectedLanguage) {
            LanguageHint.GERMAN -> SearchStrategy.GERMAN_ONLY
            LanguageHint.ENGLISH -> SearchStrategy.ENGLISH_ONLY
            LanguageHint.AMBIGUOUS, LanguageHint.UNKNOWN -> SearchStrategy.BOTH_DIRECTIONS
        }
    }
    
    /**
     * Search for a word using intelligent language detection and unified results
     * @param word The word to search for
     * @param userFromLanguage The user's selected "from" language (optional)
     * @param userToLanguage The user's selected "to" language (optional)
     */
    suspend fun searchWord(word: String, userFromLanguage: String? = null, userToLanguage: String? = null): Result<UnifiedSearchResult> {
        return withContext(Dispatchers.IO) {
            try {
                val cleanWord = word.trim()
                if (cleanWord.isEmpty()) {
                    return@withContext Result.failure(IllegalArgumentException("Empty search word"))
                }
                
                android.util.Log.d("UnifiedDictionaryRepository", "Unified search for: '$cleanWord'")
                
                // Detect language
                val languageHint = languageDetector.detectLanguage(cleanWord)
                val confidence = languageDetector.getConfidence(languageHint)
                
                android.util.Log.d("UnifiedDictionaryRepository", "Detected language: $languageHint, confidence: $confidence")
                android.util.Log.d("UnifiedDictionaryRepository", "User direction: $userFromLanguage -> $userToLanguage")
                
                // Determine search strategy considering both detection and user preference
                val searchStrategy = determineSearchStrategy(languageHint, confidence, userFromLanguage, userToLanguage)
                
                // Perform search based on strategy
                val result = when (searchStrategy) {
                    SearchStrategy.GERMAN_ONLY -> searchGermanWord(cleanWord, languageHint, confidence)
                    SearchStrategy.ENGLISH_ONLY -> searchEnglishWord(cleanWord, languageHint, confidence)
                    SearchStrategy.BOTH_DIRECTIONS -> searchBothDirections(cleanWord, languageHint, confidence)
                    SearchStrategy.FALLBACK -> searchFallback(cleanWord, languageHint, confidence)
                }
                
                android.util.Log.d("UnifiedDictionaryRepository", "Search completed for '$cleanWord', hasResults: ${result.hasResults}")
                Result.success(result)
                
            } catch (e: Exception) {
                android.util.Log.e("UnifiedDictionaryRepository", "Search failed for '$word'", e)
                Result.failure(e)
            }
        }
    }
    
    /**
     * Search using German-to-English dictionary
     */
    private suspend fun searchGermanWord(
        word: String, 
        languageHint: LanguageHint, 
        confidence: SearchConfidence
    ): UnifiedSearchResult {
        android.util.Log.d("UnifiedDictionaryRepository", "Searching German word: '$word'")
        
        deReader.initializeIfNeeded()
        val deResult = searchOfflineFreedict(word, "de", "en")
        
        return UnifiedSearchResult.combine(
            originalWord = word,
            detectedLanguage = languageHint,
            confidence = confidence,
            deResult = deResult,
            enResult = null,
            searchStrategy = SearchStrategy.GERMAN_ONLY
        )
    }
    
    /**
     * Search using English-to-German dictionary
     */
    private suspend fun searchEnglishWord(
        word: String, 
        languageHint: LanguageHint, 
        confidence: SearchConfidence
    ): UnifiedSearchResult {
        android.util.Log.d("UnifiedDictionaryRepository", "Searching English word: '$word'")
        
        enReader.initializeIfNeeded()
        val enResult = searchOfflineFreedict(word, "en", "de")
        
        return UnifiedSearchResult.combine(
            originalWord = word,
            detectedLanguage = languageHint,
            confidence = confidence,
            deResult = null,
            enResult = enResult,
            searchStrategy = SearchStrategy.ENGLISH_ONLY
        )
    }
    
    /**
     * Search both directions and combine results
     */
    private suspend fun searchBothDirections(
        word: String, 
        languageHint: LanguageHint, 
        confidence: SearchConfidence
    ): UnifiedSearchResult {
        android.util.Log.d("UnifiedDictionaryRepository", "Searching both directions for: '$word'")
        
        // Search both directions in parallel
        val deResult = try {
            deReader.initializeIfNeeded()
            searchOfflineFreedict(word, "de", "en")
        } catch (e: Exception) {
            android.util.Log.w("UnifiedDictionaryRepository", "DE->EN search failed for '$word'", e)
            null
        }
        
        val enResult = try {
            enReader.initializeIfNeeded()
            searchOfflineFreedict(word, "en", "de")
        } catch (e: Exception) {
            android.util.Log.w("UnifiedDictionaryRepository", "EN->DE search failed for '$word'", e)
            null
        }
        
        return UnifiedSearchResult.combine(
            originalWord = word,
            detectedLanguage = languageHint,
            confidence = confidence,
            deResult = deResult,
            enResult = enResult,
            searchStrategy = SearchStrategy.BOTH_DIRECTIONS
        )
    }
    
    /**
     * Fallback search strategy when primary search fails
     */
    private suspend fun searchFallback(
        word: String, 
        languageHint: LanguageHint, 
        confidence: SearchConfidence
    ): UnifiedSearchResult {
        android.util.Log.d("UnifiedDictionaryRepository", "Using fallback strategy for: '$word'")
        
        // Try the opposite direction if primary failed
        val oppositeHint = when (languageHint) {
            LanguageHint.GERMAN -> LanguageHint.ENGLISH
            LanguageHint.ENGLISH -> LanguageHint.GERMAN
            else -> languageHint
        }
        
        return when (oppositeHint) {
            LanguageHint.GERMAN -> searchGermanWord(word, oppositeHint, SearchConfidence.LOW)
            LanguageHint.ENGLISH -> searchEnglishWord(word, oppositeHint, SearchConfidence.LOW)
            else -> UnifiedSearchResult.combine(
                originalWord = word,
                detectedLanguage = languageHint,
                confidence = SearchConfidence.LOW,
                deResult = null,
                enResult = null,
                searchStrategy = SearchStrategy.FALLBACK
            )
        }
    }
    
    /**
     * Search using offline FreeDict (reused from OfflineDictionaryRepository)
     */
    private suspend fun searchOfflineFreedict(word: String, fromLang: String, toLang: String): DictionarySearchResult {
        val isGerman = fromLang.lowercase() in listOf("de", "german")
        val reader = if (isGerman) deReader else enReader
        
        var entry = reader.lookupExact(word)
        
        // If EN->DE search fails but word looks German, try DE->EN reader
        if (entry == null && !isGerman && (word.contains(Regex("[äöüßÄÖÜ]")) || word.endsWith("en") || word.endsWith("er") || word.endsWith("chen") || word.endsWith("lein"))) {
            android.util.Log.d("UnifiedDictionaryRepository", "EN->DE lookup failed for '$word', but word looks German. Trying DE->EN reader.")
            entry = deReader.lookupExact(word)
            
            // If we found it in the DE->EN reader, we should treat it as a German word
            if (entry != null) {
                android.util.Log.d("UnifiedDictionaryRepository", "Found '$word' in DE->EN reader. Adjusting result for EN->DE context.")
                val originalGermanWord = word
                val englishTranslations = entry.translations
                val extractedGender = entry.gender
                val inferredWordType = if (extractedGender != null) "noun" else if (originalGermanWord.length > 3 && originalGermanWord.endsWith("en")) "verb" else null
                
                return DictionarySearchResult(
                    originalWord = originalGermanWord,
                    translations = englishTranslations,
                    fromLanguage = "de",
                    toLanguage = "en",
                    hasResults = englishTranslations.isNotEmpty(),
                    definitions = englishTranslations.map { Definition(meaning = it) },
                    gender = extractedGender,
                    wordType = inferredWordType
                )
            }
        }
        
        if (entry == null) {
            return DictionarySearchResult(
                originalWord = word,
                fromLanguage = fromLang,
                toLanguage = toLang,
                hasResults = false
            )
        }
        
        val defs = entry.translations.map { t -> Definition(meaning = t, partOfSpeech = null, level = null) }
        val explicitGender = entry.gender
        
        val heuristicGender = if (!isGerman && explicitGender == null) {
            entry.translations.firstOrNull()?.let { t ->
                when {
                    Regex("\\bder\\b", RegexOption.IGNORE_CASE).containsMatchIn(t) -> "der"
                    Regex("\\bdie\\b", RegexOption.IGNORE_CASE).containsMatchIn(t) -> "die"
                    Regex("\\bdas\\b", RegexOption.IGNORE_CASE).containsMatchIn(t) -> "das"
                    else -> null
                }
            }
        } else null
        
        val inferredWordType: String? = when {
            (explicitGender ?: heuristicGender) != null -> "noun"
            isGerman && word.length > 3 && word.endsWith("en") -> "verb"
            else -> null
        }
        
        return DictionarySearchResult(
            originalWord = word,
            translations = entry.translations,
            fromLanguage = fromLang,
            toLanguage = toLang,
            hasResults = entry.translations.isNotEmpty(),
            definitions = defs,
            gender = explicitGender ?: heuristicGender,
            wordType = inferredWordType
        )
    }
    
    /**
     * Get suggestions for a partial word
     */
    suspend fun getSuggestions(partialWord: String, maxSuggestions: Int = 10): List<String> {
        return withContext(Dispatchers.IO) {
            try {
                val languageHint = languageDetector.detectLanguage(partialWord)
                
                when (languageHint) {
                    LanguageHint.GERMAN -> {
                        deReader.initializeIfNeeded()
                        deReader.suggest(partialWord, maxSuggestions)
                    }
                    LanguageHint.ENGLISH -> {
                        enReader.initializeIfNeeded()
                        enReader.suggest(partialWord, maxSuggestions)
                    }
                    else -> {
                        // Try both and combine
                        val deSuggestions = try {
                            deReader.initializeIfNeeded()
                            deReader.suggest(partialWord, maxSuggestions / 2)
                        } catch (e: Exception) {
                            emptyList()
                        }
                        
                        val enSuggestions = try {
                            enReader.initializeIfNeeded()
                            enReader.suggest(partialWord, maxSuggestions / 2)
                        } catch (e: Exception) {
                            emptyList()
                        }
                        
                        (deSuggestions + enSuggestions).distinct().take(maxSuggestions)
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e("UnifiedDictionaryRepository", "Failed to get suggestions for '$partialWord'", e)
                emptyList()
            }
        }
    }
}
