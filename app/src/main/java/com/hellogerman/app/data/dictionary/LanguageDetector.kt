package com.hellogerman.app.data.dictionary

/**
 * Language detection service for automatic identification of German vs English words
 * Provides intelligent language hints to guide unified dictionary searches
 */
class EnhancedLanguageDetector {

    /**
     * Enhanced language detection with improved German/English detection
     * @param word The word to analyze
     * @return LanguageHint indicating the detected language and confidence level
     */
    fun detectLanguage(word: String): LanguageHint {
        val cleanWord = word.trim().lowercase()

        // Strong German indicators (highest priority)
        if (containsGermanCharacters(cleanWord)) return LanguageHint.GERMAN
        if (hasGermanEndings(cleanWord)) return LanguageHint.POSSIBLY_GERMAN
        if (hasGermanPrefixes(cleanWord)) return LanguageHint.POSSIBLY_GERMAN

        // Strong English indicators
        if (hasEnglishEndings(cleanWord)) return LanguageHint.POSSIBLY_ENGLISH
        if (hasEnglishPatterns(cleanWord)) return LanguageHint.POSSIBLY_ENGLISH

        // Use scoring system for ambiguous cases
        val germanScore = calculateGermanScore(cleanWord)
        val englishScore = calculateEnglishScore(cleanWord)

        return when {
            germanScore > englishScore && germanScore >= 2 -> LanguageHint.GERMAN
            englishScore > germanScore && englishScore >= 2 -> LanguageHint.ENGLISH
            germanScore == englishScore && germanScore >= 1 -> LanguageHint.AMBIGUOUS
            else -> LanguageHint.UNKNOWN
        }
    }
    
    /**
     * Check if word contains German-specific characters
     */
    private fun containsGermanCharacters(word: String): Boolean {
        return word.contains(Regex("[äöüßÄÖÜ]"))
    }

    /**
     * Check for common German word endings
     */
    private fun hasGermanEndings(word: String): Boolean {
        val germanEndings = listOf(
            "chen", "lein", "ung", "heit", "keit", "schaft",
            "tion", "sion", "ment", "ling", "ig"
        )
        return germanEndings.any { word.endsWith(it) }
    }

    /**
     * Check for English word endings
     */
    private fun hasEnglishEndings(word: String): Boolean {
        val englishEndings = listOf(
            "ing", "tion", "sion", "ness", "ment", "able", "ible"
        )
        return englishEndings.any { word.endsWith(it) }
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
     * Calculate German language score based on multiple indicators
     */
    private fun calculateGermanScore(word: String): Int {
        var score = 0
        
        // German characters (strong indicator)
        if (containsGermanCharacters(word)) score += 3
        
        // German endings (moderate indicator)
        if (hasGermanEndings(word)) score += 2
        
        // German prefixes (moderate indicator)
        if (hasGermanPrefixes(word)) score += 2
        
        // German compound word patterns
        if (hasGermanCompoundPatterns(word)) score += 1
        
        // Length and structure patterns
        if (hasGermanStructure(word)) score += 1
        
        return score
    }
    
    /**
     * Calculate English language score based on multiple indicators
     */
    private fun calculateEnglishScore(word: String): Int {
        var score = 0
        
        // English suffixes (strong indicator)
        if (hasEnglishSuffixes(word)) score += 3
        
        // English prefixes (moderate indicator)
        if (hasEnglishPrefixes(word)) score += 2
        
        // English spelling patterns
        if (hasEnglishSpellingPatterns(word)) score += 2
        
        // English structure patterns
        if (hasEnglishStructure(word)) score += 1
        
        return score
    }
    
    /**
     * Check for German compound word patterns
     */
    private fun hasGermanCompoundPatterns(word: String): Boolean {
        // German compound words often have multiple capital letters or specific patterns
        return word.contains(Regex("[A-Z][a-z]+[A-Z]")) || // Multiple capitals
               word.length > 8 && word.contains(Regex("[aeiou]{2,}")) // Long words with vowel clusters
    }
    
    /**
     * Check for German word structure patterns
     */
    private fun hasGermanStructure(word: String): Boolean {
        // German words often have specific consonant-vowel patterns
        return word.matches(Regex(".*[bcdfghjklmnpqrstvwxyz]{2,}.*")) && // Multiple consonants
               word.length > 4
    }
    
    /**
     * Check for English suffixes
     */
    private fun hasEnglishSuffixes(word: String): Boolean {
        val englishSuffixes = listOf(
            "ing", "tion", "sion", "ness", "ment", "able", "ible", "ful", "less", "ly",
            "ed", "er", "est", "ism", "ist", "ity", "ive", "ize", "ous", "ship", "ward"
        )
        
        return englishSuffixes.any { word.endsWith(it) && word.length > it.length + 1 }
    }
    
    /**
     * Check for English prefixes
     */
    private fun hasEnglishPrefixes(word: String): Boolean {
        val englishPrefixes = listOf(
            "un", "re", "pre", "dis", "mis", "over", "under", "out", "up", "down",
            "anti", "auto", "co", "de", "ex", "inter", "non", "post", "sub", "super"
        )
        
        return englishPrefixes.any { word.startsWith(it) && word.length > it.length + 1 }
    }
    
    /**
     * Check for English spelling patterns
     */
    private fun hasEnglishSpellingPatterns(word: String): Boolean {
        // English-specific spelling patterns
        return word.contains(Regex("(ph|th|sh|ch|ck|qu|gh)")) || // Common English letter combinations
               word.matches(Regex(".*[aeiou]{2,}.*")) || // Double vowels (common in English)
               word.contains(Regex("(ough|augh|eigh)")) // English-specific patterns
    }
    
    /**
     * Check for English word structure patterns
     */
    private fun hasEnglishStructure(word: String): Boolean {
        // English words often have specific patterns
        return word.matches(Regex(".*[aeiou].*[aeiou].*")) || // Multiple vowels
               word.length <= 6 && word.matches(Regex("[a-z]+")) // Short common words
    }
    
    /**
     * Get confidence level for language detection
     */
    fun getConfidence(hint: LanguageHint): SearchConfidence {
        return when (hint) {
            LanguageHint.GERMAN -> SearchConfidence.HIGH
            LanguageHint.ENGLISH -> SearchConfidence.HIGH
            LanguageHint.POSSIBLY_GERMAN -> SearchConfidence.MEDIUM
            LanguageHint.POSSIBLY_ENGLISH -> SearchConfidence.MEDIUM
            LanguageHint.AMBIGUOUS -> SearchConfidence.MEDIUM
            LanguageHint.UNKNOWN -> SearchConfidence.LOW
        }
    }
}

/**
 * Language detection hints
 */
enum class LanguageHint {
    GERMAN,          // Clearly German word
    ENGLISH,         // Clearly English word
    POSSIBLY_GERMAN, // Likely German based on indicators
    POSSIBLY_ENGLISH, // Likely English based on indicators
    AMBIGUOUS,       // Could be either language
    UNKNOWN          // Cannot determine language
}

/**
 * Search confidence levels
 */
enum class SearchConfidence {
    HIGH,    // Very confident in language detection
    MEDIUM,  // Somewhat confident, may need both directions
    LOW      // Low confidence, should search both directions
}
