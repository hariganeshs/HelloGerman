package com.hellogerman.app.data.examples

import android.util.Log
import com.hellogerman.app.data.entities.DictionaryExample
import java.util.Locale

/**
 * Enhanced example sentence extractor
 * 
 * Extracts high-quality German example sentences from dictionary entries
 * with improved parsing and validation
 * 
 * Target: 50%+ coverage (up from 11%)
 */
class ExampleExtractor {
    
    companion object {
        private const val TAG = "ExampleExtractor"
        
        // Example markers
        private val EXAMPLE_MARKERS = setOf(
            "e.g.", "ex.", "example:", "ex:", "beispiel:",
            "z.b.", "zum beispiel:"
        )
        
        // Minimum/maximum example length
        private const val MIN_EXAMPLE_LENGTH = 10
        private const val MAX_EXAMPLE_LENGTH = 200
        
        // Minimum word count for valid examples
        private const val MIN_WORD_COUNT = 3
    }
    
    /**
     * Extract examples from raw dictionary entry text
     * 
     * @param rawText Raw dictionary entry text
     * @param germanWord The German word (for context)
     * @param englishWord The English word (for context)
     * @return List of extracted examples
     */
    fun extractExamples(
        rawText: String,
        germanWord: String = "",
        englishWord: String = ""
    ): List<DictionaryExample> {
        
        val examples = mutableListOf<DictionaryExample>()
        
        // Strategy 1: Extract quoted examples (format: "English" - German)
        val quotedExamples = extractQuotedExamples(rawText)
        examples.addAll(quotedExamples)
        
        // Strategy 2: Extract examples with explicit markers
        val markedExamples = extractMarkedExamples(rawText)
        examples.addAll(markedExamples)
        
        // Strategy 3: Extract parenthetical examples
        val parentheticalExamples = extractParentheticalExamples(rawText)
        examples.addAll(parentheticalExamples)
        
        // Remove duplicates and validate
        return examples
            .distinctBy { it.german.lowercase(Locale.GERMAN) }
            .filter { isValidExample(it) }
            .take(5) // Limit to 5 best examples per entry
    }
    
    /**
     * Extract quoted examples in format: "English text" - German text
     */
    private fun extractQuotedExamples(text: String): List<DictionaryExample> {
        val examples = mutableListOf<DictionaryExample>()
        
        // Pattern: "English" - German
        val pattern = Regex("\"([^\"]+)\"\\s*-\\s*([^\"\\n]+)")
        
        pattern.findAll(text).forEach { match ->
            val english = match.groupValues[1].trim()
            val german = match.groupValues[2].trim()
            
            if (english.isNotEmpty() && german.isNotEmpty()) {
                // Clean the German part (remove trailing notes, etc.)
                val cleanGerman = cleanExampleText(german)
                
                examples.add(DictionaryExample(
                    german = cleanGerman,
                    english = english
                ))
            }
        }
        
        return examples
    }
    
    /**
     * Extract examples with explicit markers (e.g., "Example:", "z.B.")
     */
    private fun extractMarkedExamples(text: String): List<DictionaryExample> {
        val examples = mutableListOf<DictionaryExample>()
        val lines = text.split('\n')
        
        for (line in lines) {
            val lower = line.lowercase(Locale.GERMAN).trim()
            
            // Check if line starts with or contains example marker
            val hasMarker = EXAMPLE_MARKERS.any { marker ->
                lower.startsWith(marker) || lower.contains(" $marker ")
            }
            
            if (hasMarker) {
                // Extract the example text after the marker
                var exampleText = line
                EXAMPLE_MARKERS.forEach { marker ->
                    exampleText = exampleText
                        .replace(Regex(marker, RegexOption.IGNORE_CASE), "")
                        .trim()
                }
                
                val cleanText = cleanExampleText(exampleText)
                
                if (cleanText.length >= MIN_EXAMPLE_LENGTH) {
                    examples.add(DictionaryExample(
                        german = cleanText,
                        english = null
                    ))
                }
            }
        }
        
        return examples
    }
    
    /**
     * Extract parenthetical examples: (example text)
     */
    private fun extractParentheticalExamples(text: String): List<DictionaryExample> {
        val examples = mutableListOf<DictionaryExample>()
        
        // Pattern: (text with at least 3 words)
        val pattern = Regex("\\(([^)]+)\\)")
        
        pattern.findAll(text).forEach { match ->
            val content = match.groupValues[1].trim()
            
            // Only if it looks like a sentence (has spaces, reasonable length)
            val wordCount = content.split(Regex("\\s+")).size
            if (wordCount >= MIN_WORD_COUNT && 
                content.length in MIN_EXAMPLE_LENGTH..MAX_EXAMPLE_LENGTH) {
                
                val cleanContent = cleanExampleText(content)
                
                // Skip if it's just metadata
                if (!isMetadata(cleanContent)) {
                    examples.add(DictionaryExample(
                        german = cleanContent,
                        english = null
                    ))
                }
            }
        }
        
        return examples
    }
    
    /**
     * Clean example text by removing annotations, notes, etc.
     */
    private fun cleanExampleText(text: String): String {
        var cleaned = text
        
        // Remove markup tags
        cleaned = cleaned.replace(Regex("<[^>]+>"), "")
        
        // Remove domain labels
        cleaned = cleaned.replace(Regex("\\[[^\\]]+\\]"), "")
        
        // Remove Note: markers
        cleaned = cleaned.replace(Regex("Note:.*", RegexOption.IGNORE_CASE), "")
        
        // Remove trailing punctuation artifacts
        cleaned = cleaned.trim(',', ';', ':', '-', '–', '—', ' ')
        
        // Normalize whitespace
        cleaned = cleaned.replace(Regex("\\s+"), " ").trim()
        
        return cleaned
    }
    
    /**
     * Check if text is metadata rather than an example
     */
    private fun isMetadata(text: String): Boolean {
        val lower = text.lowercase(Locale.GERMAN)
        
        return lower.startsWith("see:") ||
               lower.startsWith("synonym:") ||
               lower.startsWith("compare:") ||
               lower.startsWith("cf.") ||
               lower.startsWith("also:") ||
               lower.contains("database") ||
               lower.contains("zoological") ||
               lower.contains("botanical") ||
               text.length < MIN_EXAMPLE_LENGTH
    }
    
    /**
     * Validate if an example is high quality
     */
    private fun isValidExample(example: DictionaryExample): Boolean {
        val text = example.german
        
        // Check length
        if (text.length < MIN_EXAMPLE_LENGTH || text.length > MAX_EXAMPLE_LENGTH) {
            return false
        }
        
        // Check word count
        val wordCount = text.split(Regex("\\s+")).size
        if (wordCount < MIN_WORD_COUNT) {
            return false
        }
        
        // Should not be all uppercase (likely an abbreviation or title)
        if (text == text.uppercase(Locale.GERMAN)) {
            return false
        }
        
        // Should contain at least one German character or common word
        val hasGermanContent = text.any { it in "äöüßÄÖÜ" } ||
            listOf("der", "die", "das", "ein", "eine", "ist", "sind", "hat", "haben")
                .any { text.lowercase(Locale.GERMAN).contains(it) }
        
        return hasGermanContent
    }
    
    /**
     * Generate contextual example using word in a simple sentence
     * Used as fallback when no examples are found
     */
    fun generateSimpleExample(germanWord: String, wordType: String?, gender: String?): DictionaryExample? {
        val normalized = germanWord.trim()
        
        return when (wordType?.uppercase()) {
            "NOUN" -> {
                val article = when (gender?.uppercase()) {
                    "DER", "MASCULINE" -> "Der"
                    "DIE", "FEMININE" -> "Die"
                    "DAS", "NEUTER" -> "Das"
                    else -> "Das"
                }
                DictionaryExample(
                    german = "$article $normalized ist wichtig.",
                    english = "The $normalized is important."
                )
            }
            "VERB" -> {
                DictionaryExample(
                    german = "Ich ${normalized}e gern.",
                    english = "I like to ${normalized}."
                )
            }
            "ADJECTIVE" -> {
                DictionaryExample(
                    german = "Das ist sehr $normalized.",
                    english = "That is very $normalized."
                )
            }
            else -> null
        }
    }
    
    /**
     * Filter examples by CEFR level (A1-C2)
     * Simpler, shorter examples are likely A1-A2
     */
    fun filterByLevel(examples: List<DictionaryExample>, targetLevel: String = "A1"): List<DictionaryExample> {
        return when (targetLevel.uppercase()) {
            "A1", "A2" -> {
                // Prefer shorter, simpler examples
                examples
                    .filter { it.german.length <= 80 }
                    .filter { it.german.split(Regex("\\s+")).size <= 10 }
            }
            "B1", "B2" -> {
                // Medium complexity
                examples
                    .filter { it.german.length in 40..120 }
            }
            "C1", "C2" -> {
                // Can include complex examples
                examples
            }
            else -> examples
        }
    }
}

