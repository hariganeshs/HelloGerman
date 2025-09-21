package com.hellogerman.app.data.repository

import android.content.Context
import com.hellogerman.app.data.api.GermanGrammarApiService
import com.hellogerman.app.data.api.WiktionaryApiService
import com.hellogerman.app.data.dictionary.EnhancedLanguageDetector
import com.hellogerman.app.data.dictionary.FreedictReader
import com.hellogerman.app.data.dictionary.LanguageHint
import com.hellogerman.app.data.dictionary.SearchConfidence
import com.hellogerman.app.data.models.*
import com.hellogerman.app.data.parser.IpaExtractor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Unified dictionary repository that provides intelligent search across both German-to-English
 * and English-to-German dictionaries with automatic language detection
 */
class EnhancedUnifiedDictionaryRepository(
    private val context: Context,
    private val onlineRepository: DictionaryRepository,
    private val wiktionaryService: WiktionaryApiService?,
    private val grammarService: GermanGrammarApiService?
) {

    private val languageDetector = EnhancedLanguageDetector()
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
        if (confidence == SearchConfidence.HIGH) {
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
        
        // Priority 1.5: For medium confidence, prefer detection but consider user direction
        if (confidence == SearchConfidence.MEDIUM) {
            return when (detectedLanguage) {
                LanguageHint.GERMAN -> {
                    android.util.Log.d("UnifiedDictionaryRepository", "Medium confidence German detection, searching DE->EN")
                    SearchStrategy.GERMAN_ONLY
                }
                LanguageHint.ENGLISH -> {
                    android.util.Log.d("UnifiedDictionaryRepository", "Medium confidence English detection, searching EN->DE")
                    SearchStrategy.ENGLISH_ONLY
                }
                LanguageHint.AMBIGUOUS -> {
                    android.util.Log.d("UnifiedDictionaryRepository", "Ambiguous detection, searching both directions")
                    SearchStrategy.BOTH_DIRECTIONS
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
            LanguageHint.POSSIBLY_GERMAN -> SearchStrategy.GERMAN_ONLY
            LanguageHint.POSSIBLY_ENGLISH -> SearchStrategy.ENGLISH_ONLY
            LanguageHint.AMBIGUOUS, LanguageHint.UNKNOWN -> SearchStrategy.BOTH_DIRECTIONS
        }
    }
    
    /**
     * Enhanced unified search with pronunciation and grammar integration
     * @param word The word to search for
     * @param userFromLanguage The user's selected "from" language (optional)
     * @param userToLanguage The user's selected "to" language (optional)
     */
    suspend fun searchUnified(word: String, userFromLanguage: String? = null, userToLanguage: String? = null): Result<UnifiedSearchResult> {
        return withContext(Dispatchers.IO) {
            try {
                val cleanWord = word.trim()
                if (cleanWord.isEmpty()) {
                    return@withContext Result.failure(IllegalArgumentException("Empty search word"))
                }

                android.util.Log.d("EnhancedUnifiedDictionaryRepository", "Enhanced unified search for: '$cleanWord'")

                val detectedLanguage = languageDetector.detectLanguage(cleanWord)

                // Always search both directions for comprehensive results
                val deResult = searchGermanToEnglish(cleanWord)
                val enResult = searchEnglishToGerman(cleanWord)

                // Enhance results with additional data
                val enhancedDeResult = enhanceWithPronunciationAndGrammar(deResult)
                val enhancedEnResult = enhanceWithPronunciationAndGrammar(enResult)

                val result = UnifiedSearchResult.combine(
                    originalWord = cleanWord,
                    detectedLanguage = detectedLanguage,
                    confidence = calculateConfidence(deResult, enResult),
                    deResult = enhancedDeResult,
                    enResult = enhancedEnResult,
                    searchStrategy = SearchStrategy.BOTH_DIRECTIONS
                )

                android.util.Log.d("EnhancedUnifiedDictionaryRepository", "Enhanced search completed for '$cleanWord', hasResults: ${result.hasResults}")
                Result.success(result)

            } catch (e: Exception) {
                android.util.Log.e("EnhancedUnifiedDictionaryRepository", "Enhanced search failed for '$word'", e)
                Result.failure(e)
            }
        }
    }

    /**
     * Search for a word using both dictionaries simultaneously for maximum information
     * @param word The word to search for
     * @param userFromLanguage The user's selected "from" language (optional)
     * @param userToLanguage The user's selected "to" language (optional)
     */
    suspend fun searchWord(word: String, userFromLanguage: String? = null, userToLanguage: String? = null): Result<UnifiedSearchResult> {
        return searchUnified(word, userFromLanguage, userToLanguage)
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
            searchStrategy = SearchStrategy.GERMAN_ONLY,
            primaryResult = deResult
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
            searchStrategy = SearchStrategy.ENGLISH_ONLY,
            primaryResult = enResult
        )
    }
    
    /**
     * Comprehensive search using both dictionaries simultaneously for maximum information
     */
    private suspend fun searchBothDirectionsComprehensive(word: String): UnifiedSearchResult {
        android.util.Log.d("UnifiedDictionaryRepository", "Comprehensive search for: '$word'")
        
        // Detect language for display purposes only
        val languageHint = languageDetector.detectLanguage(word)
        val confidence = languageDetector.getConfidence(languageHint)
        
        // Search both directions in parallel for maximum coverage
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
        
        // Determine the best result to prioritize based on which direction found results
        val primaryResult = when {
            deResult?.hasResults == true && enResult?.hasResults == true -> {
                // Both found results - prioritize based on word characteristics
                if (word.contains(Regex("[äöüßÄÖÜ]")) || hasGermanCharacteristics(word)) {
                    android.util.Log.d("UnifiedDictionaryRepository", "Both directions found results, prioritizing German")
                    deResult
                } else {
                    android.util.Log.d("UnifiedDictionaryRepository", "Both directions found results, prioritizing English")
                    enResult
                }
            }
            deResult?.hasResults == true -> {
                android.util.Log.d("UnifiedDictionaryRepository", "Only German direction found results")
                deResult
            }
            enResult?.hasResults == true -> {
                android.util.Log.d("UnifiedDictionaryRepository", "Only English direction found results")
                enResult
            }
            else -> {
                android.util.Log.d("UnifiedDictionaryRepository", "No results found in either direction")
                null
            }
        }
        
        return UnifiedSearchResult.combine(
            originalWord = word,
            detectedLanguage = languageHint,
            confidence = confidence,
            deResult = deResult,
            enResult = enResult,
            searchStrategy = SearchStrategy.BOTH_DIRECTIONS,
            primaryResult = primaryResult
        )
    }
    
    /**
     * Check if word has German characteristics for prioritization
     */
    private fun hasGermanCharacteristics(word: String): Boolean {
        return word.contains(Regex("[äöüßÄÖÜ]")) ||
               word.endsWith("en") || word.endsWith("er") || word.endsWith("chen") || word.endsWith("lein") ||
               word.length > 8 // German compound words tend to be longer
    }
    
    /**
     * Search both directions and combine results (legacy method)
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
        
        // Determine primary result for legacy method
        val primaryResult = when {
            deResult?.hasResults == true && enResult?.hasResults == true -> {
                if (word.contains(Regex("[äöüßÄÖÜ]")) || hasGermanCharacteristics(word)) {
                    deResult
                } else {
                    enResult
                }
            }
            deResult?.hasResults == true -> deResult
            enResult?.hasResults == true -> enResult
            else -> null
        }
        
        return UnifiedSearchResult.combine(
            originalWord = word,
            detectedLanguage = languageHint,
            confidence = confidence,
            deResult = deResult,
            enResult = enResult,
            searchStrategy = SearchStrategy.BOTH_DIRECTIONS,
            primaryResult = primaryResult
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
                searchStrategy = SearchStrategy.FALLBACK,
                primaryResult = null
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

    /**
     * Search German to English direction
     */
    private suspend fun searchGermanToEnglish(word: String): DictionarySearchResult? {
        return try {
            deReader.initializeIfNeeded()
            searchOfflineFreedict(word, "de", "en")
        } catch (e: Exception) {
            android.util.Log.w("EnhancedUnifiedDictionaryRepository", "DE->EN search failed for '$word'", e)
            null
        }
    }

    /**
     * Search English to German direction
     */
    private suspend fun searchEnglishToGerman(word: String): DictionarySearchResult? {
        return try {
            enReader.initializeIfNeeded()
            searchOfflineFreedict(word, "en", "de")
        } catch (e: Exception) {
            android.util.Log.w("EnhancedUnifiedDictionaryRepository", "EN->DE search failed for '$word'", e)
            null
        }
    }

    /**
     * Calculate confidence based on search results
     */
    private fun calculateConfidence(deResult: DictionarySearchResult?, enResult: DictionarySearchResult?): SearchConfidence {
        val deHasResults = deResult?.hasResults == true
        val enHasResults = enResult?.hasResults == true

        return when {
            deHasResults && enHasResults -> SearchConfidence.HIGH
            deHasResults || enHasResults -> SearchConfidence.MEDIUM
            else -> SearchConfidence.LOW
        }
    }

    /**
     * Enhance dictionary results with pronunciation and grammar data
     */
    private suspend fun enhanceWithPronunciationAndGrammar(
        result: DictionarySearchResult?
    ): DictionarySearchResult? {
        if (result == null || !result.hasResults) return result

        // Add pronunciation data
        val pronunciation = try {
            wiktionaryService?.let { service ->
                val wikitext = service.getPageContent(page = result.originalWord).parse?.wikitext?.content
                wikitext?.let { IpaExtractor().extractPronunciationData(it) }
            }
        } catch (e: Exception) {
            android.util.Log.w("EnhancedUnifiedDictionaryRepository", "Failed to get pronunciation for '${result.originalWord}'", e)
            null
        }

        // Add grammar data
        val grammar = try {
            grammarService?.getGrammarInfo(result.originalWord)
        } catch (e: Exception) {
            android.util.Log.w("EnhancedUnifiedDictionaryRepository", "Failed to get grammar info for '${result.originalWord}'", e)
            null
        }

        return result.copy(
            pronunciation = pronunciation?.let { (ipa, audioUrl) ->
                Pronunciation(ipa, audioUrl)
            },
            pronunciationInfo = pronunciation?.let { (ipa, audioUrl) ->
                PronunciationInfo(ipa ?: "", audioUrl)
            }
        )
    }
}
