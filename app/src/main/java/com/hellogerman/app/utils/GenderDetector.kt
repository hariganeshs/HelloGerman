package com.hellogerman.app.utils

import com.hellogerman.app.data.entities.GermanGender

/**
 * Utility object for detecting German noun gender
 * 
 * Uses linguistic rules and patterns to determine whether a German noun
 * is masculine (der), feminine (die), or neuter (das).
 */
object GenderDetector {
    
    /**
     * Detect gender from explicit markup tags or article mentions
     */
    fun detectFromMarkup(text: String): GermanGender? {
        val lowerText = text.lowercase()
        
        // Look for explicit gender tags
        when {
            Regex("<(masc|m|masculine)>").containsMatchIn(lowerText) -> return GermanGender.DER
            Regex("<(fem|f|feminine)>").containsMatchIn(lowerText) -> return GermanGender.DIE
            Regex("<(neut|n|neuter)>").containsMatchIn(lowerText) -> return GermanGender.DAS
        }
        
        // Look for article mentions at beginning of text or after common patterns
        val articlePattern = Regex("\\b(der|die|das)\\s+[A-ZÄÖÜ]")
        val match = articlePattern.find(text)
        if (match != null) {
            val article = match.groupValues[1].lowercase()
            return GermanGender.fromArticle(article)
        }
        
        return null
    }
    
    /**
     * Detect gender from word ending patterns (German grammar rules)
     */
    fun detectFromEnding(word: String): GermanGender? {
        if (word.length < 3) return null
        
        val lowerWord = word.lowercase()
        
        // Feminine endings (highest confidence)
        val feminineEndings = listOf(
            "ung", "heit", "keit", "schaft", "ion", "tät", "ik", "ur", 
            "ie", "enz", "anz", "age", "ade", "ette", "ine"
        )
        for (ending in feminineEndings) {
            if (lowerWord.endsWith(ending)) return GermanGender.DIE
        }
        
        // Neuter endings (high confidence)
        val neuterEndings = listOf(
            "chen", "lein", "ment", "um", "tum", "ma", "ett", "ett"
        )
        for (ending in neuterEndings) {
            if (lowerWord.endsWith(ending)) return GermanGender.DAS
        }
        
        // Masculine endings (medium confidence)
        val masculineEndings = listOf(
            "ismus", "or", "ling", "ig", "ich", "ner", "ant", "ent", "ist"
        )
        for (ending in masculineEndings) {
            if (lowerWord.endsWith(ending)) return GermanGender.DER
        }
        
        // Special case: words ending in -e are often feminine (but not always)
        if (lowerWord.endsWith("e") && lowerWord.length > 4) {
            // Check if it's not a verb infinitive or adjective
            if (!lowerWord.endsWith("en") && !isLikelyVerb(word)) {
                return GermanGender.DIE
            }
        }
        
        // Agent nouns ending in -er (but not diminutive -chen or -lein)
        if (lowerWord.endsWith("er") && 
            !lowerWord.endsWith("chen") && 
            !lowerWord.endsWith("lein") &&
            word.length > 4) {
            return GermanGender.DER
        }
        
        return null
    }
    
    /**
     * Comprehensive gender detection combining all methods
     */
    fun detectGender(word: String, context: String = ""): GermanGender? {
        // First try explicit markup in context
        if (context.isNotEmpty()) {
            detectFromMarkup(context)?.let { return it }
        }
        
        // Then try word ending patterns
        return detectFromEnding(word)
    }
    
    /**
     * Check if word is likely a verb (for exclusion from noun rules)
     */
    private fun isLikelyVerb(word: String): Boolean {
        val lowerWord = word.lowercase()
        return lowerWord.endsWith("en") || 
               lowerWord.endsWith("ern") || 
               lowerWord.endsWith("eln")
    }
    
    /**
     * Get confidence level for gender detection (0.0 to 1.0)
     */
    fun getConfidence(word: String, detectedGender: GermanGender?, context: String = ""): Float {
        if (detectedGender == null) return 0f
        
        // High confidence if found in markup
        if (context.isNotEmpty() && detectFromMarkup(context) != null) {
            return 1.0f
        }
        
        val lowerWord = word.lowercase()
        
        // High confidence endings
        val highConfidenceEndings = listOf(
            "ung", "heit", "keit", "schaft", "ion", "tät",  // feminine
            "chen", "lein", "ment", "tum",                  // neuter
            "ismus", "ling"                                 // masculine
        )
        
        for (ending in highConfidenceEndings) {
            if (lowerWord.endsWith(ending)) return 0.9f
        }
        
        // Medium confidence endings
        val mediumConfidenceEndings = listOf(
            "ik", "ur", "ie", "enz", "anz",  // feminine
            "um", "ma",                       // neuter
            "or", "ig", "ich", "ner"          // masculine
        )
        
        for (ending in mediumConfidenceEndings) {
            if (lowerWord.endsWith(ending)) return 0.7f
        }
        
        // Low confidence (general -er or -e patterns)
        if (lowerWord.endsWith("er") || lowerWord.endsWith("e")) {
            return 0.5f
        }
        
        return 0.3f
    }
}

