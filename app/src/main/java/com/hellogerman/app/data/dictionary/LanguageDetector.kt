package com.hellogerman.app.data.dictionary

/**
 * Language detection service for automatic identification of German vs English words
 * Provides intelligent language hints to guide unified dictionary searches
 */
class LanguageDetector {
    
    /**
     * Detect the most likely language of a given word
     * @param word The word to analyze
     * @return LanguageHint indicating the detected language and confidence level
     */
    fun detectLanguage(word: String): LanguageHint {
        if (word.isBlank()) return LanguageHint.UNKNOWN
        
        val cleanWord = word.trim().lowercase()
        
        // Priority 1: Check for known English words first (highest priority)
        if (hasEnglishPatterns(cleanWord)) {
            return LanguageHint.ENGLISH
        }
        
        // Priority 2: Strong German indicators
        if (hasGermanCharacters(cleanWord)) {
            return LanguageHint.GERMAN
        }
        
        // Priority 3: German word endings (but not if it's a known English word)
        if (hasGermanEndings(cleanWord)) {
            return LanguageHint.GERMAN
        }
        
        // Priority 4: German prefixes
        if (hasGermanPrefixes(cleanWord)) {
            return LanguageHint.GERMAN
        }
        
        // Priority 5: Ambiguous cases - could be either
        if (isAmbiguousWord(cleanWord)) {
            return LanguageHint.AMBIGUOUS
        }
        
        return LanguageHint.UNKNOWN
    }
    
    /**
     * Check if word contains German-specific characters
     */
    private fun hasGermanCharacters(word: String): Boolean {
        return word.contains(Regex("[äöüßÄÖÜ]"))
    }
    
    /**
     * Check for common German word endings
     */
    private fun hasGermanEndings(word: String): Boolean {
        // Strong German endings that are unlikely to be English
        val strongGermanEndings = listOf(
            "chen", "lein", "ung", "heit", "keit", "schaft", "tum", "nis",
            "lich", "bar", "sam", "haft"
        )
        
        if (strongGermanEndings.any { word.endsWith(it) && word.length > it.length + 1 }) {
            return true
        }
        
        // Weaker German endings that need more context
        val weakGermanEndings = listOf("er", "en", "el", "ig")
        
        // Only consider these if the word is longer and has German-like structure
        return weakGermanEndings.any { ending ->
            word.endsWith(ending) && 
            word.length > ending.length + 2 && 
            !isLikelyEnglishWord(word) // Additional check to avoid English words
        }
    }
    
    /**
     * Check if word is likely English based on common patterns
     */
    private fun isLikelyEnglishWord(word: String): Boolean {
        // Common English word patterns that might end with German-like endings
        val englishPatterns = listOf(
            "mother", "father", "brother", "sister", "water", "paper", "number", "winter", "summer",
            "center", "meter", "liter", "computer", "monster", "master", "disaster", "register",
            "minister", "semester", "character", "parameter", "thermometer", "barometer"
        )
        
        return englishPatterns.contains(word)
    }
    
    /**
     * Check for German prefixes
     */
    private fun hasGermanPrefixes(word: String): Boolean {
        val germanPrefixes = listOf(
            "ge", "be", "ver", "ent", "er", "zer", "un", "aus", "ein", "auf", "über", "unter"
        )
        
        return germanPrefixes.any { word.startsWith(it) && word.length > it.length + 1 }
    }
    
    /**
     * Check for English patterns
     */
    private fun hasEnglishPatterns(word: String): Boolean {
        // English suffixes
        val englishSuffixes = listOf(
            "ing", "tion", "sion", "ness", "ment", "able", "ible", "ful", "less", "ly"
        )
        
        if (englishSuffixes.any { word.endsWith(it) && word.length > it.length + 1 }) {
            return true
        }
        
        // English prefixes
        val englishPrefixes = listOf(
            "un", "re", "pre", "dis", "mis", "over", "under", "out", "up", "down"
        )
        
        if (englishPrefixes.any { word.startsWith(it) && word.length > it.length + 1 }) {
            return true
        }
        
        // Common English words
        val commonEnglishWords = setOf(
            "the", "and", "or", "but", "in", "on", "at", "to", "for", "of", "with", "by",
            "is", "are", "was", "were", "be", "been", "have", "has", "had", "do", "does", "did",
            "will", "would", "could", "should", "may", "might", "can", "must",
            // Common nouns
            "apple", "orange", "banana", "book", "car", "house", "water", "food", "time", "day",
            "night", "year", "month", "week", "hour", "minute", "person", "man", "woman", "child",
            "mother", "father", "brother", "sister", "friend", "family", "home", "school", "work",
            "money", "love", "life", "death", "health", "world", "country", "city", "street"
        )
        
        return commonEnglishWords.contains(word)
    }
    
    /**
     * Check if word is ambiguous (could be either language)
     */
    private fun isAmbiguousWord(word: String): Boolean {
        // Short words that exist in both languages
        val ambiguousWords = setOf(
            "der", "die", "das", "und", "oder", "aber", "ist", "sind", "hat", "hatte",
            "the", "and", "or", "but", "is", "are", "has", "had"
        )
        
        return ambiguousWords.contains(word)
    }
    
    /**
     * Get confidence level for language detection
     */
    fun getConfidence(hint: LanguageHint): SearchConfidence {
        return when (hint) {
            LanguageHint.GERMAN -> SearchConfidence.HIGH
            LanguageHint.ENGLISH -> SearchConfidence.HIGH
            LanguageHint.AMBIGUOUS -> SearchConfidence.MEDIUM
            LanguageHint.UNKNOWN -> SearchConfidence.LOW
        }
    }
}

/**
 * Language detection hints
 */
enum class LanguageHint {
    GERMAN,      // Clearly German word
    ENGLISH,     // Clearly English word
    AMBIGUOUS,   // Could be either language
    UNKNOWN      // Cannot determine language
}

/**
 * Search confidence levels
 */
enum class SearchConfidence {
    HIGH,    // Very confident in language detection
    MEDIUM,  // Somewhat confident, may need both directions
    LOW      // Low confidence, should search both directions
}
