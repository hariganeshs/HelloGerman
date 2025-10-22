package com.hellogerman.app.data.dictionary

import android.util.Log
import com.hellogerman.app.data.entities.DictionaryExample
import com.hellogerman.app.data.entities.GermanGender
import com.hellogerman.app.utils.TextNormalizer

/**
 * Parser for individual dictd dictionary entry text
 * 
 * Extracts structured information from raw dictionary entry text including:
 * - Translations with gender information
 * - Examples (German sentences with English translations)
 * - Pronunciations
 * - Domain markers
 * - Part of speech tags
 */
class DictdDataParser {
    
    companion object {
        private const val TAG = "DictdDataParser"
        
        // Regex patterns for parsing FreeDict format
        private val MARKUP_TAG_PATTERN = Regex("<([^>]+)>")
        private val BRACKET_LABEL_PATTERN = Regex("\\[([^\\]]+)\\]")
        private val PARENTHESES_PATTERN = Regex("\\(([^)]+)\\)")
        private val IPA_PATTERN = Regex("/([^/]+)/")
        // Multiple example patterns for better coverage
        private val EXAMPLE_PATTERN_QUOTED = Regex("\"([^\"]+)\"\\s*[-–—]\\s*(.+)")  // "German" - English
        private val EXAMPLE_PATTERN_COLON = Regex("([A-ZÄÖÜ][^:]+):\\s*([^\\n]+)")    // German: English
        private val EXAMPLE_PATTERN_PIPE = Regex("([^|]+)\\|\\s*([^\\n]+)")           // German | English
        private val SEE_ALSO_PATTERN = Regex("see:|synonym:|antonym:", RegexOption.IGNORE_CASE)
        
        // Debug words to track
        private val DEBUG_WORDS = setOf("mother", "father", "apple", "Mutter", "Vater", "Apfel")
    }
    
    /**
     * Translation with extracted gender information
     */
    data class Translation(
        val word: String,               // "Mutter" (clean word)
        val gender: GermanGender?,      // DIE (from <fem> tag or "die Mutter")
        val withArticle: String?,       // "die Mutter" (if article present)
        val domain: String? = null      // Domain label if present
    )
    
    /**
     * Parsed dictionary entry containing all extracted information
     */
    data class ParsedEntry(
        val headword: String,
        val translations: List<Translation>,
        val examples: List<DictionaryExample>,
        val pronunciationIpa: String?,
        val partOfSpeechTags: List<String>,
        val domainLabels: List<String>,
        val rawText: String
    )
    
    /**
     * Parse raw dictionary entry text into structured data
     * 
     * @param headword The word being defined (English or German)
     * @param rawText The raw text from the dictionary file
     * @return Parsed entry with extracted information
     */
    fun parse(headword: String, rawText: String): ParsedEntry {
        val cleanedText = TextNormalizer.cleanRawEntry(rawText)
        
        val isDebugWord = DEBUG_WORDS.contains(headword.lowercase())
        if (isDebugWord) {
            Log.d(TAG, "═══ Parsing: $headword ═══")
            Log.d(TAG, "Raw text: ${rawText.take(200)}...")
        }
        
        // Extract different components
        val pronunciationIpa = extractPronunciation(cleanedText)
        val partOfSpeechTags = extractPartOfSpeechTags(cleanedText)
        val domainLabels = extractDomainLabels(cleanedText)
        val examples = extractExamples(cleanedText, isDebugWord)
        val translations = extractTranslations(cleanedText, headword, domainLabels, isDebugWord)
        
        if (isDebugWord) {
            Log.d(TAG, "Found ${translations.size} translations: ${translations.map { "${it.word} (${it.gender})" }}")
            Log.d(TAG, "Found ${examples.size} examples")
        }
        
        return ParsedEntry(
            headword = headword,
            translations = translations,
            examples = examples,
            pronunciationIpa = pronunciationIpa,
            partOfSpeechTags = partOfSpeechTags,
            domainLabels = domainLabels,
            rawText = cleanedText
        )
    }
    
    /**
     * Extract translations with gender information from FreeDict format
     */
    private fun extractTranslations(
        text: String, 
        headword: String, 
        domainLabels: List<String>,
        isDebugWord: Boolean
    ): List<Translation> {
        val translations = mutableListOf<Translation>()
        val lines = text.split('\n')
        
        for (line in lines) {
            var cleaned = line.trim()
            if (cleaned.isEmpty()) continue
            
            // Skip metadata lines (see:, synonym:, etc.)
            if (isMetadataLine(cleaned)) continue
            
            // Extract domain from line if present
            val lineDomain = extractLineDomain(cleaned)
            
            // Split by semicolon or comma (but NOT slash - that's for OR)
            val parts = cleaned.split(Regex("[;,]"))
            
            for (part in parts) {
                val trimmed = part.trim()
                if (trimmed.isEmpty()) continue
                
                // Extract gender from FreeDict tags: <fem>, <masc>, <neut>
                val gender = extractGenderFromTags(trimmed)
                
                // Extract gender from article if present: "die Mutter", "der Vater"
                val (wordWithArticle, articleGender) = extractWordAndArticle(trimmed)
                
                // Use tag gender first, then article gender
                val finalGender = gender ?: articleGender
                
                // Clean the word (remove tags, brackets, but keep the actual word)
                val cleanWord = cleanTranslationWord(trimmed)
                
                // Validate it's a real word (not phrase, not English)
                if (isValidGermanWord(cleanWord, isDebugWord) && !looksLikePhrase(trimmed)) {
                    // Check if it's common vocabulary (not too technical)
                    val domain = lineDomain ?: domainLabels.firstOrNull()
                    val isCommon = isCommonVocabulary(cleanWord, domain)
                    
                    if (isCommon || domainLabels.isEmpty()) {
                        translations.add(Translation(
                            word = cleanWord,
                            gender = finalGender,
                            withArticle = wordWithArticle,
                            domain = domain
                        ))
                        
                        if (isDebugWord) {
                            Log.d(TAG, "✓ ACCEPTED: '$cleanWord' (gender: $finalGender, domain: $domain)")
                        }
                    } else if (isDebugWord) {
                        Log.d(TAG, "✗ REJECTED: '$cleanWord' (too technical: $domain)")
                    }
                } else if (isDebugWord) {
                    if (looksLikePhrase(trimmed)) {
                        Log.d(TAG, "✗ REJECTED: '$cleanWord' (looks like phrase)")
                    } else {
                        Log.d(TAG, "✗ REJECTED: '$cleanWord' (invalid German word)")
                    }
                }
            }
        }
        
        // Remove duplicates while preserving order
        val seen = mutableSetOf<String>()
        val unique = translations.filter { seen.add(it.word.lowercase()) }
        
        // Return top 5 quality translations
        return unique.take(5)
    }
    
    /**
     * Extract gender from FreeDict markup tags: <fem>, <masc>, <neut>
     */
    private fun extractGenderFromTags(text: String): GermanGender? {
        return when {
            text.contains("<fem>", ignoreCase = true) -> GermanGender.DIE
            text.contains("<masc>", ignoreCase = true) -> GermanGender.DER
            text.contains("<neut>", ignoreCase = true) -> GermanGender.DAS
            else -> null
        }
    }
    
    /**
     * Extract word with article and determine gender from article
     * Returns: Pair(word with article, gender from article)
     */
    private fun extractWordAndArticle(text: String): Pair<String?, GermanGender?> {
        val articlePattern = Regex("\\b(der|die|das)\\s+([A-ZÄÖÜ][a-zäöüß]+)", RegexOption.IGNORE_CASE)
        val match = articlePattern.find(text)
        
        if (match != null) {
            val article = match.groupValues[1].lowercase()
            val word = match.groupValues[2]
            val gender = when(article) {
                "der" -> GermanGender.DER
                "die" -> GermanGender.DIE
                "das" -> GermanGender.DAS
                else -> null
            }
            return Pair("$article $word", gender)
        }
        
        return Pair(null, null)
    }
    
    /**
     * Clean translation word by removing markup tags and extra characters
     * BUT preserve the actual German word
     */
    private fun cleanTranslationWord(text: String): String {
        var cleaned = text
        
        // Remove markup tags <...>
        cleaned = cleaned.replace(MARKUP_TAG_PATTERN, "")
        
        // Remove bracket labels [...]
        cleaned = cleaned.replace(BRACKET_LABEL_PATTERN, "")
        
        // Remove IPA pronunciation /.../ 
        cleaned = cleaned.replace(IPA_PATTERN, "")
        
        // Remove curly braces {...}
        cleaned = cleaned.replace(Regex("\\{[^}]+\\}"), "")
        
        // Remove leading articles (der, die, das, ein, eine)
        // This is OK now because we already extracted gender above
        cleaned = cleaned.replace(Regex("^(der|die|das|ein|eine)\\s+", RegexOption.IGNORE_CASE), "")
        
        // Remove special characters from beginning/end
        cleaned = cleaned.trim('"', '\'', ',', ';', '.', '–', '—', '•', ' ', '(', ')')
        
        // Normalize whitespace
        cleaned = cleaned.replace(Regex("\\s+"), " ").trim()
        
        return cleaned
    }
    
    /**
     * Check if text is a valid German word (not English, not phrase)
     */
    private fun isValidGermanWord(word: String, isDebug: Boolean = false): Boolean {
        // Must be 2-50 characters
        if (word.length !in 2..50) {
            if (isDebug) Log.d(TAG, "  - Too short/long: ${word.length}")
            return false
        }
        
        // Must start with uppercase for nouns (German convention)
        val startsWithUpper = word[0].isUpperCase()
        
        // Check for German-specific characters
        val hasGermanChars = word.contains(Regex("[äöüßÄÖÜ]"))
        
        // Reject all-lowercase (likely English)
        if (word.all { it.isLowerCase() || !it.isLetter() }) {
            if (isDebug) Log.d(TAG, "  - All lowercase: $word")
            return false
        }
        
        // Reject multi-word phrases (>2 words)
        val wordCount = word.split(" ").size
        if (wordCount > 2) {
            if (isDebug) Log.d(TAG, "  - Too many words: $wordCount")
            return false
        }
        
        // Reject English verb patterns
        if (word.endsWith("ing") || word.endsWith("ed")) {
            if (isDebug) Log.d(TAG, "  - English verb pattern")
            return false
        }
        
        // Reject words that look like English technical terms
        val englishPatterns = listOf(
            "ton",   // Appleton, newton
            "let",   // Applet, booklet  
            "layer", // layer (English word)
            "net",   // internet-related
            "web",   // web-related
            "soft",  // software-related
            "hard"   // hardware-related
        )
        
        val wordLower = word.lowercase()
        if (englishPatterns.any { wordLower.contains(it) } && !hasGermanChars) {
            if (isDebug) Log.d(TAG, "  - Contains English technical pattern")
            return false
        }
        
        // Reject words that are just English with capital first letter
        // like "Internet", "Computer" (unless they have umlauts or common German words)
        val commonGermanWords = setOf(
            "mutter", "vater", "kind", "apfel", "haus", "wasser", "brot",
            "tisch", "stuhl", "schule", "arbeit", "stadt", "land"
        )
        
        // Must have German indicators OR be a known common word OR be a capitalized German-style word
        if (!hasGermanChars && wordLower !in commonGermanWords && !startsWithUpper) {
            if (isDebug) Log.d(TAG, "  - No German indicators")
            return false
        }
        
        return true
    }
    
    /**
     * Check if text looks like a phrase (not a simple word)
     */
    private fun looksLikePhrase(text: String): Boolean {
        val words = text.split(" ")
        
        // More than 3 words = definitely a phrase
        if (words.size > 3) return true
        
        // Contains question words
        if (text.matches(Regex(".*\\b(wer|was|wann|wo|wie|warum)\\b.*", RegexOption.IGNORE_CASE))) {
            return true
        }
        
        // Contains verbs in conjugated form
        if (text.contains(Regex("\\b(ist|sind|hat|haben|wird|werden|macht|machen|kann|können)\\b"))) {
            return true
        }
        
        return false
    }
    
    /**
     * Check if word is common vocabulary (not too technical)
     */
    private fun isCommonVocabulary(word: String, domain: String?): Boolean {
        // If no domain, assume common
        if (domain == null) return true
        
        // Highly technical domains
        val technicalDomains = setOf(
            "chem", "biochem", "phys", "math", "med", "anat", "bot", "zool", 
            "myc", "ornith", "min", "geol", "astron", "tech", "electr"
        )
        
        // Common domains
        val commonDomains = setOf(
            "soc", "fam", "gen", "food", "cook", "cloth", "home"
        )
        
        val domainLower = domain.lowercase().removeSuffix(".")
        
        return when {
            commonDomains.any { domainLower.contains(it) } -> true
            technicalDomains.any { domainLower.contains(it) } -> false
            else -> true // Unknown domain = likely common
        }
    }
    
    /**
     * Extract domain label from a line
     */
    private fun extractLineDomain(text: String): String? {
        val match = BRACKET_LABEL_PATTERN.find(text)
        return match?.groupValues?.getOrNull(1)?.trim()?.removeSuffix(".")
    }
    
    /**
     * Extract pronunciation (IPA) from text
     */
    private fun extractPronunciation(text: String): String? {
        val match = IPA_PATTERN.find(text)
        return match?.groupValues?.getOrNull(1)?.trim()
    }
    
    /**
     * Extract part-of-speech tags from markup
     */
    private fun extractPartOfSpeechTags(text: String): List<String> {
        val tags = mutableListOf<String>()
        
        // Look for <noun>, <verb>, <adj>, etc.
        MARKUP_TAG_PATTERN.findAll(text).forEach { match ->
            val tag = match.groupValues[1].lowercase()
            when {
                tag in listOf("noun", "substantiv", "n") -> tags.add("NOUN")
                tag in listOf("verb", "v") -> tags.add("VERB")
                tag in listOf("adj", "adjective", "adjektiv") -> tags.add("ADJECTIVE")
                tag in listOf("adv", "adverb") -> tags.add("ADVERB")
                tag in listOf("pron", "pronoun") -> tags.add("PRONOUN")
                tag in listOf("prep", "preposition") -> tags.add("PREPOSITION")
                tag in listOf("conj", "conjunction") -> tags.add("CONJUNCTION")
                tag in listOf("interj", "interjection") -> tags.add("INTERJECTION")
            }
        }
        
        return tags.distinct()
    }
    
    /**
     * Extract domain/subject labels like (bot.), (cook.), (tech.)
     */
    private fun extractDomainLabels(text: String): List<String> {
        val domains = mutableListOf<String>()
        
        BRACKET_LABEL_PATTERN.findAll(text).forEach { match ->
            val label = match.groupValues[1].trim().removeSuffix(".")
            if (label.isNotEmpty() && label.length <= 20) {
                domains.add(label)
            }
        }
        
        return domains.distinct().take(3)
    }
    
    /**
     * Extract example sentences from FreeDict format: "German" - English
     */
    private fun extractExamples(text: String, isDebugWord: Boolean = false): List<DictionaryExample> {
        val examples = mutableListOf<DictionaryExample>()
        val lines = text.split('\n')
        
        for (line in lines) {
            val trimmed = line.trim()
            if (trimmed.isEmpty()) continue
            
            // Skip metadata lines
            if (trimmed.startsWith("[") || trimmed.startsWith("<") || 
                SEE_ALSO_PATTERN.containsMatchIn(trimmed)) {
                continue
            }
            
            var matched = false
            var german: String? = null
            var english: String? = null
            
            // Try different example patterns
            // Pattern 1: "German sentence" - English translation (most common in FreeDict)
            EXAMPLE_PATTERN_QUOTED.find(trimmed)?.let { match ->
                german = match.groupValues[1].trim()
                english = match.groupValues[2].trim()
                matched = true
            }
            
            // Pattern 2: German sentence: English translation
            if (!matched) {
                EXAMPLE_PATTERN_COLON.find(trimmed)?.let { match ->
                    val possibleGerman = match.groupValues[1].trim()
                    val possibleEnglish = match.groupValues[2].trim()
                    // Only accept if German part looks German
                    if (possibleGerman.length >= 10 && !SEE_ALSO_PATTERN.containsMatchIn(possibleGerman)) {
                        german = possibleGerman
                        english = possibleEnglish
                        matched = true
                    }
                }
            }
            
            // Pattern 3: German sentence | English translation
            if (!matched) {
                EXAMPLE_PATTERN_PIPE.find(trimmed)?.let { match ->
                    val possibleGerman = match.groupValues[1].trim()
                    val possibleEnglish = match.groupValues[2].trim()
                    if (possibleGerman.length >= 10) {
                        german = possibleGerman
                        english = possibleEnglish
                        matched = true
                    }
                }
            }
            
            // If we found an example, validate and add it
            if (matched && german != null && english != null) {
                // Validate it's a quality German example
                if (isValidGermanExample(german!!)) {
                    examples.add(DictionaryExample(
                        german = german!!,
                        english = english!!
                    ))
                    
                    if (isDebugWord) {
                        Log.d(TAG, "✓ Example: \"$german\" - $english")
                    }
                } else if (isDebugWord) {
                    Log.d(TAG, "✗ Bad example: $german (validation failed)")
                }
            }
        }
        
        // Return top 3 quality examples
        return examples.distinct().take(3)
    }
    
    /**
     * Validate that example is quality German text
     */
    private fun isValidGermanExample(text: String): Boolean {
        // Must be 10-200 characters
        if (text.length !in 10..200) return false
        
        // Must contain German characters OR capitalized nouns
        val hasGermanChars = text.contains(Regex("[äöüßÄÖÜ]"))
        val hasCapitalizedWords = text.split(" ").any { 
            it.isNotEmpty() && it[0].isUpperCase() && it.length > 1 
        }
        
        // Should not be all lowercase (likely English)
        if (text == text.lowercase()) return false
        
        // Must have German indicators
        if (!hasGermanChars && !hasCapitalizedWords) return false
        
        return true
    }
    
    /**
     * Check if line is metadata (not a translation)
     */
    private fun isMetadataLine(text: String): Boolean {
        val lower = text.lowercase()
        return lower.startsWith("see:") ||
               lower.startsWith("synonym:") ||
               lower.startsWith("antonym:") ||
               lower.startsWith("compare:") ||
               lower.startsWith("cf.") ||
               lower.startsWith("also:") ||
               lower.contains("00database")
    }
}

