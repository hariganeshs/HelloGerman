package com.hellogerman.app.data.dictionary

import com.hellogerman.app.data.entities.GermanGender
import com.hellogerman.app.data.entities.WordType
import com.hellogerman.app.utils.GenderDetector
import com.hellogerman.app.utils.TextNormalizer

/**
 * Extractor for German grammar information from dictionary entries
 * 
 * Analyzes translation text and context to determine:
 * - Word type (noun, verb, adjective, etc.)
 * - Gender for nouns (der/die/das)
 * - Plural forms
 * - Verb information (auxiliary, irregular, separable)
 * - Adjective forms (comparative, superlative)
 */
class GrammarExtractor {
    
    companion object {
        private const val TAG = "GrammarExtractor"
        
        // Regex patterns for grammar extraction
        private val PLURAL_PATTERN = Regex("\\(pl\\.?\\s*([^)]+)\\)", RegexOption.IGNORE_CASE)
        private val PLURAL_ALT_PATTERN = Regex("\\bpl\\.?\\s+([A-ZÄÖÜ][a-zäöüß]+)")
        private val AUXILIARY_PATTERN = Regex("\\b(haben|sein)\\b", RegexOption.IGNORE_CASE)
        private val IRREGULAR_PATTERN = Regex("\\b(irregular|unregelmäßig|strong|stark)\\b", RegexOption.IGNORE_CASE)
        private val SEPARABLE_PATTERN = Regex("\\b(sep\\.|separable|trennbar)\\b", RegexOption.IGNORE_CASE)
        private val COMPARATIVE_PATTERN = Regex("\\b(comp\\.|comparative)\\s+([a-zäöüß]+)")
        private val SUPERLATIVE_PATTERN = Regex("\\b(sup\\.|superlative)\\s+([a-zäöüß]+)")
    }
    
    /**
     * Extracted grammar information
     */
    data class GrammarInfo(
        val wordType: WordType?,
        val gender: GermanGender?,
        val pluralForm: String?,
        val pastTense: String?,
        val pastParticiple: String?,
        val auxiliaryVerb: String?,
        val isIrregular: Boolean,
        val isSeparable: Boolean,
        val comparative: String?,
        val superlative: String?
    )
    
    /**
     * Extract all grammar information from a translation and context
     * 
     * @param germanWord The German translation
     * @param englishWord The English headword
     * @param rawContext The raw dictionary entry text
     * @param partOfSpeechTags Tags extracted from markup
     * @return Complete grammar information
     */
    fun extract(
        germanWord: String,
        englishWord: String,
        rawContext: String,
        partOfSpeechTags: List<String>
    ): GrammarInfo {
        
        // Determine word type
        val wordType = determineWordType(germanWord, englishWord, rawContext, partOfSpeechTags)
        
        // Extract information based on word type
        val gender = if (wordType == WordType.NOUN) {
            extractGender(germanWord, rawContext)
        } else null
        
        val pluralForm = if (wordType == WordType.NOUN) {
            extractPlural(rawContext)
        } else null
        
        val auxiliaryVerb = if (wordType == WordType.VERB) {
            extractAuxiliary(rawContext)
        } else null
        
        val isIrregular = if (wordType == WordType.VERB) {
            extractIsIrregular(rawContext)
        } else false
        
        val isSeparable = if (wordType == WordType.VERB) {
            extractIsSeparable(germanWord, rawContext)
        } else false
        
        val comparative = if (wordType == WordType.ADJECTIVE) {
            extractComparative(rawContext)
        } else null
        
        val superlative = if (wordType == WordType.ADJECTIVE) {
            extractSuperlative(rawContext)
        } else null
        
        return GrammarInfo(
            wordType = wordType,
            gender = gender,
            pluralForm = pluralForm,
            pastTense = null, // TODO: Extract from context if available
            pastParticiple = null, // TODO: Extract from context if available
            auxiliaryVerb = auxiliaryVerb,
            isIrregular = isIrregular,
            isSeparable = isSeparable,
            comparative = comparative,
            superlative = superlative
        )
    }
    
    /**
     * Determine word type from available information
     */
    private fun determineWordType(
        germanWord: String,
        englishWord: String,
        context: String,
        posTags: List<String>
    ): WordType? {
        
        // First check explicit POS tags
        if (posTags.isNotEmpty()) {
            return when (posTags[0]) {
                "NOUN" -> WordType.NOUN
                "VERB" -> WordType.VERB
                "ADJECTIVE" -> WordType.ADJECTIVE
                "ADVERB" -> WordType.ADVERB
                "PRONOUN" -> WordType.PRONOUN
                "PREPOSITION" -> WordType.PREPOSITION
                "CONJUNCTION" -> WordType.CONJUNCTION
                "INTERJECTION" -> WordType.INTERJECTION
                else -> null
            }
        }
        
        // Check context for hints
        val lowerContext = context.lowercase()
        when {
            lowerContext.contains("<noun>") || lowerContext.contains("<substantiv>") -> return WordType.NOUN
            lowerContext.contains("<verb>") -> return WordType.VERB
            lowerContext.contains("<adj") -> return WordType.ADJECTIVE
            lowerContext.contains("<adv") -> return WordType.ADVERB
        }
        
        // Analyze German word structure
        val lowerGerman = germanWord.lowercase()
        
        // Verbs typically end in -en, -ern, -eln
        if (lowerGerman.endsWith("en") || lowerGerman.endsWith("ern") || lowerGerman.endsWith("eln")) {
            // But could also be infinitive used as noun or adjective
            if (TextNormalizer.looksLikeGermanNoun(germanWord)) {
                return WordType.NOUN
            }
            return WordType.VERB
        }
        
        // Nouns are capitalized in German (if not all caps)
        if (TextNormalizer.looksLikeGermanNoun(germanWord)) {
            return WordType.NOUN
        }
        
        // Check for adverb endings
        if (lowerGerman.endsWith("lich") || lowerGerman.endsWith("weise") || lowerGerman.endsWith("weise")) {
            return WordType.ADVERB
        }
        
        // Phrases (multiple words)
        if (germanWord.contains(" ") || englishWord.contains(" ")) {
            return WordType.PHRASE
        }
        
        return null
    }
    
    /**
     * Extract gender for German nouns
     */
    private fun extractGender(germanWord: String, context: String): GermanGender? {
        return GenderDetector.detectGender(germanWord, context)
    }
    
    /**
     * Extract plural form from context
     */
    private fun extractPlural(context: String): String? {
        // Try explicit plural notation
        PLURAL_PATTERN.find(context)?.let { match ->
            return match.groupValues[1].trim()
        }
        
        // Try alternative pattern "pl. Form"
        PLURAL_ALT_PATTERN.find(context)?.let { match ->
            return match.groupValues[1].trim()
        }
        
        return null
    }
    
    /**
     * Extract auxiliary verb (haben or sein)
     */
    private fun extractAuxiliary(context: String): String? {
        val matches = AUXILIARY_PATTERN.findAll(context).map { it.value.lowercase() }.toList()
        
        return when {
            "haben" in matches && "sein" !in matches -> "haben"
            "sein" in matches && "haben" !in matches -> "sein"
            else -> null // Unclear or both mentioned
        }
    }
    
    /**
     * Check if verb is irregular
     */
    private fun extractIsIrregular(context: String): Boolean {
        return IRREGULAR_PATTERN.containsMatchIn(context)
    }
    
    /**
     * Check if verb is separable
     */
    private fun extractIsSeparable(germanWord: String, context: String): Boolean {
        // Check explicit notation
        if (SEPARABLE_PATTERN.containsMatchIn(context)) {
            return true
        }
        
        // Check for common separable prefixes
        val separablePrefixes = listOf(
            "ab", "an", "auf", "aus", "bei", "ein", "fest", "her", "hin",
            "los", "mit", "nach", "vor", "weg", "zu", "zurück"
        )
        
        val lowerWord = germanWord.lowercase()
        return separablePrefixes.any { lowerWord.startsWith(it) }
    }
    
    /**
     * Extract comparative form of adjective
     */
    private fun extractComparative(context: String): String? {
        return COMPARATIVE_PATTERN.find(context)?.groupValues?.getOrNull(2)?.trim()
    }
    
    /**
     * Extract superlative form of adjective
     */
    private fun extractSuperlative(context: String): String? {
        return SUPERLATIVE_PATTERN.find(context)?.groupValues?.getOrNull(2)?.trim()
    }
}

