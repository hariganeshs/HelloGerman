package com.hellogerman.app.data.dictionary

import android.util.Log
import com.hellogerman.app.data.entities.DictionaryExample
import com.hellogerman.app.utils.TextNormalizer

/**
 * Parser for individual dictd dictionary entry text
 * 
 * Extracts structured information from raw dictionary entry text including:
 * - Translations
 * - Examples
 * - Pronunciations
 * - Domain markers
 * - Part of speech tags
 */
class DictdDataParser {
    
    companion object {
        private const val TAG = "DictdDataParser"
        
        // Regex patterns for parsing
        private val MARKUP_TAG_PATTERN = Regex("<([^>]+)>")
        private val BRACKET_LABEL_PATTERN = Regex("\\[([^\\]]+)\\]")
        private val PARENTHESES_PATTERN = Regex("\\(([^)]+)\\)")
        private val IPA_PATTERN = Regex("/([^/]+)/")
        private val EXAMPLE_PREFIX_PATTERN = Regex("^(e\\.g\\.|ex\\.|Example:|Ex:)\\s*", RegexOption.IGNORE_CASE)
    }
    
    /**
     * Parsed dictionary entry containing all extracted information
     */
    data class ParsedEntry(
        val headword: String,
        val translations: List<String>,
        val examples: List<DictionaryExample>,
        val pronunciationIpa: String?,
        val partOfSpeechTags: List<String>,
        val domainLabels: List<String>,
        val rawText: String
    )
    
    /**
     * Parse raw dictionary entry text into structured data
     * 
     * @param headword The English word being defined
     * @param rawText The raw text from the dictionary file
     * @return Parsed entry with extracted information
     */
    fun parse(headword: String, rawText: String): ParsedEntry {
        val cleanedText = TextNormalizer.cleanRawEntry(rawText)
        
        // Extract different components
        val pronunciationIpa = extractPronunciation(cleanedText)
        val partOfSpeechTags = extractPartOfSpeechTags(cleanedText)
        val domainLabels = extractDomainLabels(cleanedText)
        val examples = extractExamples(cleanedText)
        val translations = extractTranslations(cleanedText, headword)
        
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
     * Extract German translations from entry text
     */
    private fun extractTranslations(text: String, headword: String): List<String> {
        val translations = mutableListOf<String>()
        val lines = text.split('\n')
        
        for (line in lines) {
            var cleaned = line.trim()
            if (cleaned.isEmpty()) continue
            
            // Remove markup and labels
            cleaned = removeMarkup(cleaned)
            
            // Skip lines that are clearly not translations
            if (isMetadataLine(cleaned)) continue
            
            // Split by common separators
            val parts = cleaned.split(Regex("[;|/]"))
            
            for (part in parts) {
                val translation = cleanTranslation(part, headword)
                if (translation.isNotEmpty() && translation.length >= 2) {
                    translations.add(translation)
                }
            }
        }
        
        // Remove duplicates while preserving order
        val seen = mutableSetOf<String>()
        val unique = translations.filter { seen.add(it.lowercase()) }
        
        return unique.take(10) // Limit to top 10 translations
    }
    
    /**
     * Remove markup tags and labels from text
     */
    private fun removeMarkup(text: String): String {
        return text
            .replace(MARKUP_TAG_PATTERN, "") // Remove <tags>
            .replace(BRACKET_LABEL_PATTERN, "") // Remove [labels]
            .replace(IPA_PATTERN, "") // Remove /pronunciation/
            .replace(Regex("\\{[^}]+\\}"), "") // Remove {forms}
            .replace(Regex("\\s+"), " ") // Normalize whitespace
            .trim()
    }
    
    /**
     * Clean and normalize a translation string
     */
    private fun cleanTranslation(text: String, headword: String): String {
        var cleaned = text.trim()
        
        // Remove special characters from beginning/end
        cleaned = cleaned.trim('"', '\'', ',', ';', '.', '–', '—', '•', ' ')
        
        // Remove leading articles (der, die, das, ein, eine)
        cleaned = cleaned.replace(Regex("^(der|die|das|ein|eine)\\s+", RegexOption.IGNORE_CASE), "")
        
        // Skip if it's the same as headword
        if (cleaned.equals(headword, ignoreCase = true)) return ""
        
        // Skip very short or very long
        if (cleaned.length < 2 || cleaned.length > 100) return ""
        
        // Skip if it looks like metadata
        if (cleaned.contains("see:", ignoreCase = true) ||
            cleaned.contains("synonym:", ignoreCase = true) ||
            cleaned.contains("antonym:", ignoreCase = true)) {
            return ""
        }
        
        return cleaned
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
     * Extract example sentences from entry text
     */
    private fun extractExamples(text: String): List<DictionaryExample> {
        val examples = mutableListOf<DictionaryExample>()
        val lines = text.split('\n')
        
        for (line in lines) {
            val trimmed = line.trim()
            if (trimmed.isEmpty()) continue
            
            // Check if line starts with example prefix
            if (EXAMPLE_PREFIX_PATTERN.containsMatchIn(trimmed)) {
                val example = trimmed.replace(EXAMPLE_PREFIX_PATTERN, "").trim()
                if (example.length > 10) {
                    examples.add(DictionaryExample(german = example, english = null))
                }
            }
            
            // Check for parenthetical examples
            PARENTHESES_PATTERN.findAll(trimmed).forEach { match ->
                val content = match.groupValues[1]
                // Only if it looks like a sentence (has spaces and reasonable length)
                if (content.split(" ").size >= 3 && content.length in 10..200) {
                    examples.add(DictionaryExample(german = content, english = null))
                }
            }
        }
        
        return examples.distinct().take(5)
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

