package com.hellogerman.app.data.models

import com.hellogerman.app.data.dictionary.LanguageHint
import com.hellogerman.app.data.dictionary.SearchConfidence

/**
 * Unified search result combining information from both German-to-English and English-to-German dictionaries
 * Provides comprehensive translation data with automatic language detection
 */
data class UnifiedSearchResult(
    val originalWord: String,
    val detectedLanguage: LanguageHint,
    val confidence: SearchConfidence,
    val germanToEnglish: DictionarySearchResult?,
    val englishToGerman: DictionarySearchResult?,
    val combinedTranslations: List<TranslationGroup>,
    val hasResults: Boolean,
    val searchStrategy: SearchStrategy
) {
    
    /**
     * Get the primary translation group (most relevant result)
     */
    val primaryTranslation: TranslationGroup?
        get() = combinedTranslations.firstOrNull()
    
    /**
     * Get all German words with their genders
     */
    val germanWordsWithGender: List<GermanWordWithGender>
        get() = combinedTranslations.mapNotNull { group ->
            group.gender?.let { gender ->
                GermanWordWithGender(
                    word = group.germanWord,
                    gender = gender,
                    englishTranslations = group.englishTranslations,
                    wordType = group.wordType
                )
            }
        }
    
    /**
     * Check if this is a cross-reference result (found in both directions)
     */
    val isCrossReference: Boolean
        get() = germanToEnglish != null && englishToGerman != null
    
    companion object {
        /**
         * Create a unified result from individual dictionary results
         */
        fun combine(
            originalWord: String,
            detectedLanguage: LanguageHint,
            confidence: SearchConfidence,
            deResult: DictionarySearchResult?,
            enResult: DictionarySearchResult?,
            searchStrategy: SearchStrategy
        ): UnifiedSearchResult {
            
            val combinedTranslations = mutableListOf<TranslationGroup>()
            
            // Add German-to-English translations
            deResult?.let { result ->
                if (result.hasResults) {
                    val germanWord = result.originalWord
                    val englishTranslations = result.translations
                    val gender = result.gender
                    val wordType = result.wordType
                    val examples: List<String> = result.examples.map { it.sentence }
                    
                    combinedTranslations.add(
                        TranslationGroup(
                            germanWord = germanWord,
                            englishTranslations = englishTranslations,
                            gender = gender,
                            wordType = wordType,
                            examples = examples
                        )
                    )
                }
            }
            
            // Add English-to-German translations
            enResult?.let { result ->
                if (result.hasResults) {
                    // For EN->DE, we need to extract German words from translations
                    val englishWord = result.originalWord
                    val germanTranslations = result.translations.filter { 
                        it.contains(Regex("[äöüßÄÖÜ]")) || 
                        it.endsWith("en") || it.endsWith("er") || it.endsWith("chen") || it.endsWith("lein")
                    }
                    
                    germanTranslations.forEach { germanWord ->
                        val cleanGermanWord = germanWord.replace(Regex("^(der|die|das)\\s+"), "").trim()
                        combinedTranslations.add(
                            TranslationGroup(
                                germanWord = cleanGermanWord,
                                englishTranslations = listOf(englishWord),
                                gender = result.gender,
                                wordType = result.wordType,
                                examples = result.examples.map { it.sentence }
                            )
                        )
                    }
                }
            }
            
            // Remove duplicates and merge similar translations
            val deduplicatedTranslations = deduplicateTranslations(combinedTranslations)
            
            return UnifiedSearchResult(
                originalWord = originalWord,
                detectedLanguage = detectedLanguage,
                confidence = confidence,
                germanToEnglish = deResult,
                englishToGerman = enResult,
                combinedTranslations = deduplicatedTranslations,
                hasResults = deduplicatedTranslations.isNotEmpty(),
                searchStrategy = searchStrategy
            )
        }
        
        /**
         * Remove duplicate translation groups and merge similar ones
         */
        private fun deduplicateTranslations(translations: List<TranslationGroup>): List<TranslationGroup> {
            val seen = mutableSetOf<String>()
            val result = mutableListOf<TranslationGroup>()
            
            translations.forEach { group ->
                val key = group.germanWord.lowercase()
                if (seen.add(key)) {
                    result.add(group)
                } else {
                    // Merge with existing group
                    val existingIndex = result.indexOfFirst { it.germanWord.lowercase() == key }
                    if (existingIndex >= 0) {
                        val existing = result[existingIndex]
                        val merged = existing.copy(
                            englishTranslations = (existing.englishTranslations + group.englishTranslations).distinct(),
                            examples = (existing.examples + group.examples).distinct()
                        )
                        result[existingIndex] = merged
                    }
                }
            }
            
            return result
        }
    }
}

/**
 * A group of translations for a German word
 */
data class TranslationGroup(
    val germanWord: String,
    val englishTranslations: List<String>,
    val gender: String?,
    val wordType: String?,
    val examples: List<String> = emptyList()
)

/**
 * German word with its grammatical gender and translations
 */
data class GermanWordWithGender(
    val word: String,
    val gender: String,
    val englishTranslations: List<String>,
    val wordType: String?
) {
    /**
     * Get the formatted German word with article
     */
    val formattedWord: String
        get() = when (gender.lowercase()) {
            "der", "masculine" -> "der $word"
            "die", "feminine" -> "die $word"
            "das", "neuter" -> "das $word"
            else -> word
        }
}

/**
 * Search strategy used to find results
 */
enum class SearchStrategy {
    GERMAN_ONLY,      // Searched only German-to-English dictionary
    ENGLISH_ONLY,     // Searched only English-to-German dictionary
    BOTH_DIRECTIONS,  // Searched both dictionaries
    FALLBACK          // Used fallback strategy
}
