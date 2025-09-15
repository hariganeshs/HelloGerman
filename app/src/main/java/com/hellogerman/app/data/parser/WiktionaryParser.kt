package com.hellogerman.app.data.parser

import com.hellogerman.app.data.models.*
import java.util.regex.Pattern

/**
 * Parser for Wiktionary wikitext content to extract structured dictionary information
 */
class WiktionaryParser {
    
    companion object {
        // Multiple pattern variations to catch different Wiktionary formats
        private val DEFINITION_PATTERNS = listOf(
            Pattern.compile("===\\s*Bedeutungen\\s*===\\n([\\s\\S]*?)(?=\\n===|\\z)"),
            Pattern.compile("==\\s*Bedeutungen\\s*==\\n([\\s\\S]*?)(?=\\n==|\\z)"),
            Pattern.compile("===\\s*Definitionen\\s*===\\n([\\s\\S]*?)(?=\\n===|\\z)"),
            Pattern.compile("===\\s*Definition\\s*===\\n([\\s\\S]*?)(?=\\n===|\\z)")
        )
        
        private val EXAMPLES_PATTERNS = listOf(
            Pattern.compile("====\\s*Beispiele\\s*====\\n([\\s\\S]*?)(?=\\n====|\\n===|\\z)"),
            Pattern.compile("===\\s*Beispiele\\s*===\\n([\\s\\S]*?)(?=\\n===|\\z)"),
            Pattern.compile("====\\s*Examples\\s*====\\n([\\s\\S]*?)(?=\\n====|\\n===|\\z)")
        )
        
        private val ETYMOLOGY_PATTERNS = listOf(
            Pattern.compile("===\\s*Herkunft\\s*===\\n([\\s\\S]*?)(?=\\n===|\\z)"),
            Pattern.compile("===\\s*Etymology\\s*===\\n([\\s\\S]*?)(?=\\n===|\\z)")
        )
        
        private val PRONUNCIATION_PATTERNS = listOf(
            Pattern.compile("===\\s*Aussprache\\s*===\\n([\\s\\S]*?)(?=\\n===|\\z)"),
            Pattern.compile("===\\s*Pronunciation\\s*===\\n([\\s\\S]*?)(?=\\n===|\\z)")
        )
        
        private val SYNONYMS_PATTERNS = listOf(
            Pattern.compile("===\\s*Synonyme\\s*===\\n([\\s\\S]*?)(?=\\n===|\\z)"),
            Pattern.compile("===\\s*Synonyms\\s*===\\n([\\s\\S]*?)(?=\\n===|\\z)")
        )
        
        private val WORD_TYPE_PATTERNS = listOf(
            Pattern.compile("===\\s*(Substantiv|Verb|Adjektiv|Adverb|Pronomen|Präposition|Konjunktion)\\s*==="),
            Pattern.compile("==\\s*(Substantiv|Verb|Adjektiv|Adverb|Pronomen|Präposition|Konjunktion)\\s*=="),
            Pattern.compile("===\\s*(Noun|Verb|Adjective|Adverb|Pronoun|Preposition|Conjunction)\\s*===")
        )
        
        private val GENDER_PATTERNS = listOf(
            // German template patterns
            Pattern.compile("\\{\\{Deutsch\\s+(Substantiv|Artikel)\\s+(.*)\\}\\}"),
            Pattern.compile("\\{\\{de-noun\\|(.*)\\}\\}"),
            Pattern.compile("'''(der|die|das)\\s+\\w+'''"),
            
            // Enhanced gender detection patterns
            Pattern.compile("\\{\\{Wortart\\|Substantiv\\|Deutsch\\}\\}.*?\\{\\{([mfn])\\}\\}", Pattern.DOTALL),
            Pattern.compile("Genus\\s*=\\s*([mfn])"),
            Pattern.compile("\\{\\{([mfn])\\}\\}"),
            Pattern.compile("\\{\\{Genus\\|([mfn])\\}\\}"),
            Pattern.compile("\\{\\{Gender\\|([mfn])\\}\\}"),
            
            // Direct article detection in text
            Pattern.compile("\\b(der|die|das)\\s+\\w+", Pattern.CASE_INSENSITIVE),
            Pattern.compile("Nominativ\\s+Singular\\s+(der|die|das)", Pattern.CASE_INSENSITIVE),
            Pattern.compile("\\|\\s*Nominativ\\s*=\\s*(der|die|das)", Pattern.CASE_INSENSITIVE),
            
            // German grammar terminology
            Pattern.compile("maskulin|männlich", Pattern.CASE_INSENSITIVE),
            Pattern.compile("feminin|weiblich", Pattern.CASE_INSENSITIVE),
            Pattern.compile("neutrum|sächlich", Pattern.CASE_INSENSITIVE),
            
            // Declension patterns
            Pattern.compile("\\{\\{Deutsch\\s+Substantiv\\s+Übersicht\\s*\\|.*?Nominativ\\s+Singular\\s*=\\s*(der|die|das)", Pattern.DOTALL),
            Pattern.compile("\\{\\{Flexion.*?\\|(der|die|das)", Pattern.DOTALL)
        )
        
        // IPA pronunciation patterns
        private val IPA_PATTERNS = listOf(
            Pattern.compile("\\{\\{IPA\\}\\}\\s*(.+?)(?=\\n|$)"),
            Pattern.compile("\\{\\{IPA\\|(.+?)\\}\\}"),
            Pattern.compile("\\[([^\\]]+)\\]")
        )
        
        // Audio file patterns
        private val AUDIO_PATTERNS = listOf(
            Pattern.compile("\\{\\{Audio\\|([^}]+)\\}\\}"),
            Pattern.compile("\\{\\{audio\\|([^}]+)\\}\\}")
        )
        
        // Definition line patterns
        private val DEFINITION_LINE_PATTERNS = listOf(
            Pattern.compile(":\\[\\d+\\]\\s*(.+)"),
            Pattern.compile("#\\s*(.+)"),
            Pattern.compile("\\d+\\.\\s*(.+)")
        )
        
        // Example line patterns
        private val EXAMPLE_LINE_PATTERNS = listOf(
            Pattern.compile(":\\[\\d+\\]\\s*[\"'](.+?)[\"']"),
            Pattern.compile(":\\[\\d+\\]\\s*(.+)"),
            Pattern.compile("#\\s*[\"'](.+?)[\"']"),
            Pattern.compile("#\\s*(.+)")
        )
    }
    
    /**
     * Parse Wiktionary wikitext content and extract dictionary information
     */
    fun parseWiktionaryContent(word: String, wikitext: String, language: String = "de"): DictionarySearchResult {
        return try {
            val definitions = extractDefinitions(wikitext)
            val examples = extractExamples(wikitext)
            val etymology = extractEtymology(wikitext)
            val pronunciation = extractPronunciation(wikitext)
            val synonyms = extractSynonyms(wikitext)
            val wordType = extractWordType(wikitext)
            val gender = if (language.lowercase() in listOf("de", "german")) extractGender(wikitext, word).also { extractedGender ->
                android.util.Log.d("WiktionaryParser", "Gender extraction for '$word': extracted='$extractedGender'")
            } else null

            // Enhanced extraction - try simpler patterns if main ones fail
            val enhancedDefinitions = if (definitions.isEmpty()) {
                extractSimpleDefinitions(wikitext)
            } else definitions

            val enhancedExamples = if (examples.isEmpty()) {
                extractSimpleExamples(wikitext)
            } else examples

            // Determine target language based on source language
            val targetLang = when (language.lowercase()) {
                "de", "german" -> "en"
                "en", "english" -> "de"
                else -> "en" // Default fallback
            }

            DictionarySearchResult(
                originalWord = word,
                fromLanguage = language,
                toLanguage = targetLang,
                hasResults = enhancedDefinitions.isNotEmpty() || enhancedExamples.isNotEmpty() ||
                           synonyms.isNotEmpty() || pronunciation != null,
                definitions = enhancedDefinitions,
                examples = enhancedExamples,
                synonyms = synonyms,
                pronunciation = pronunciation,
                etymology = etymology,
                wordType = wordType,
                gender = gender
            )
        } catch (e: Exception) {
            // Even on parsing error, try basic extraction
            val basicDefinitions = extractSimpleDefinitions(wikitext)
            val basicExamples = extractSimpleExamples(wikitext)

            // Determine target language based on source language
            val targetLang = when (language.lowercase()) {
                "de", "german" -> "en"
                "en", "english" -> "de"
                else -> "en" // Default fallback
            }

            DictionarySearchResult(
                originalWord = word,
                fromLanguage = language,
                toLanguage = targetLang,
                hasResults = basicDefinitions.isNotEmpty() || basicExamples.isNotEmpty(),
                definitions = basicDefinitions,
                examples = basicExamples
            )
        }
    }
    
    private fun extractDefinitions(wikitext: String): List<Definition> {
        val definitions = mutableListOf<Definition>()
        
        // Try multiple definition patterns
        for (pattern in DEFINITION_PATTERNS) {
            val matcher = pattern.matcher(wikitext)
            if (matcher.find()) {
                val definitionText = matcher.group(1) ?: continue
                val lines = definitionText.split("\n")
                
                for (line in lines) {
                    val trimmed = line.trim()
                    if (trimmed.isNotBlank() && !trimmed.startsWith("{{") && !trimmed.startsWith("==")) {
                        // Try multiple definition line patterns
                        for (linePattern in DEFINITION_LINE_PATTERNS) {
                            val definitionMatch = linePattern.matcher(trimmed)
                            if (definitionMatch.find()) {
                                val meaning = cleanWikitext(definitionMatch.group(1) ?: "")
                                if (meaning.isNotBlank() && meaning.length > 5) { // Minimum length check
                                    definitions.add(Definition(
                                        meaning = meaning,
                                        partOfSpeech = extractWordType(wikitext)
                                    ))
                                }
                                break
                            }
                        }
                    }
                }
                
                if (definitions.isNotEmpty()) break // Found definitions, stop looking
            }
        }
        
        return definitions.take(5) // Limit to 5 definitions
    }
    
    private fun extractExamples(wikitext: String): List<Example> {
        val examples = mutableListOf<Example>()
        
        // Try multiple example patterns
        for (pattern in EXAMPLES_PATTERNS) {
            val matcher = pattern.matcher(wikitext)
            if (matcher.find()) {
                val exampleText = matcher.group(1) ?: continue
                val lines = exampleText.split("\n")
                
                for (line in lines) {
                    val trimmed = line.trim()
                    if (trimmed.isNotBlank() && !trimmed.startsWith("{{") && !trimmed.startsWith("==")) {
                        // Try multiple example line patterns
                        for (linePattern in EXAMPLE_LINE_PATTERNS) {
                            val exampleMatch = linePattern.matcher(trimmed)
                            if (exampleMatch.find()) {
                                val sentence = cleanWikitext(exampleMatch.group(1) ?: "")
                                if (sentence.isNotBlank() && sentence.length > 10) { // Minimum length check
                                    examples.add(Example(
                                        sentence = sentence,
                                        source = "Wiktionary"
                                    ))
                                }
                                break
                            }
                        }
                    }
                }
                
                if (examples.isNotEmpty()) break // Found examples, stop looking
            }
        }
        
        return examples.take(5) // Limit to 5 examples
    }
    
    private fun extractEtymology(wikitext: String): String? {
        for (pattern in ETYMOLOGY_PATTERNS) {
            val matcher = pattern.matcher(wikitext)
            if (matcher.find()) {
                val etymology = cleanWikitext(matcher.group(1) ?: "")
                if (etymology.isNotBlank()) return etymology
            }
        }
        return null
    }
    
    private fun extractPronunciation(wikitext: String): Pronunciation? {
        for (pattern in PRONUNCIATION_PATTERNS) {
            val matcher = pattern.matcher(wikitext)
            if (matcher.find()) {
                val pronunciationText = matcher.group(1) ?: continue
                
                // Extract IPA
                var ipa: String? = null
                for (ipaPattern in IPA_PATTERNS) {
                    val ipaMatcher = ipaPattern.matcher(pronunciationText)
                    if (ipaMatcher.find()) {
                        ipa = cleanWikitext(ipaMatcher.group(1) ?: "")
                        if (ipa.isNotBlank()) break
                    }
                }
                
                // Extract audio file
                var audioFile: String? = null
                for (audioPattern in AUDIO_PATTERNS) {
                    val audioMatcher = audioPattern.matcher(pronunciationText)
                    if (audioMatcher.find()) {
                        audioFile = audioMatcher.group(1)
                        break
                    }
                }
                
                if (ipa != null || audioFile != null) {
                    return Pronunciation(
                        ipa = ipa?.takeIf { it.isNotBlank() },
                        audioUrl = audioFile?.let { "https://upload.wikimedia.org/wikipedia/commons/$it" },
                        region = "Deutschland"
                    )
                }
            }
        }
        return null
    }
    
    private fun extractSynonyms(wikitext: String): List<String> {
        val synonyms = mutableListOf<String>()
        
        for (pattern in SYNONYMS_PATTERNS) {
            val matcher = pattern.matcher(wikitext)
            if (matcher.find()) {
                val synonymText = matcher.group(1) ?: continue
                val lines = synonymText.split("\n")
                
                for (line in lines) {
                    val trimmed = line.trim()
                    if (trimmed.isNotBlank() && !trimmed.startsWith("{{") && !trimmed.startsWith("==")) {
                        // Extract numbered synonyms like ":[1] synonym1, synonym2"
                        val synonymMatch = Pattern.compile(":\\[\\d+\\]\\s*(.+)").matcher(trimmed)
                        if (synonymMatch.find()) {
                            val synonymList = (synonymMatch.group(1) ?: "").split(",")
                            synonymList.forEach { synonym ->
                                val cleaned = cleanWikitext(synonym.trim())
                                if (cleaned.isNotBlank() && cleaned.length > 2) {
                                    synonyms.add(cleaned)
                                }
                            }
                        }
                    }
                }
                
                if (synonyms.isNotEmpty()) break
            }
        }
        
        return synonyms.take(8) // Limit to 8 synonyms
    }
    
    private fun extractWordType(wikitext: String): String? {
        for (pattern in WORD_TYPE_PATTERNS) {
            val matcher = pattern.matcher(wikitext)
            if (matcher.find()) {
                return when (matcher.group(1)) {
                    "Substantiv" -> "noun"
                    "Verb" -> "verb"
                    "Adjektiv" -> "adjective"
                    "Adverb" -> "adverb"
                    "Pronomen" -> "pronoun"
                    "Präposition" -> "preposition"
                    "Konjunktion" -> "conjunction"
                    "Noun" -> "noun"
                    "Verb" -> "verb"
                    "Adjective" -> "adjective"
                    "Adverb" -> "adverb"
                    "Pronoun" -> "pronoun"
                    "Preposition" -> "preposition"
                    "Conjunction" -> "conjunction"
                    else -> matcher.group(1).lowercase()
                }
            }
        }
        return null
    }
    
    private fun extractGender(wikitext: String, headword: String): String? {
        // 1) Prefer explicit Genus markers/templates (highest priority)
        Regex("Genus\\s*=\\s*([mfn])", RegexOption.IGNORE_CASE).find(wikitext)?.let { m ->
            android.util.Log.d("WiktionaryParser", "Found Genus=m pattern for '$headword'")
            return when (m.groupValues[1].lowercase()) {
                "m" -> "der"
                "f" -> "die"
                "n" -> "das"
                else -> null
            }
        }

        Regex("\\{\\{Genus\\|([mfn])\\}}", RegexOption.IGNORE_CASE).find(wikitext)?.let { m ->
            android.util.Log.d("WiktionaryParser", "Found {{Genus|m}} pattern for '$headword'")
            return when (m.groupValues[1].lowercase()) {
                "m" -> "der"
                "f" -> "die"
                "n" -> "das"
                else -> null
            }
        }
        
        // 1.5) Check for explicit gender markers in word type templates
        Regex("\\{\\{Wortart\\|Substantiv\\|Deutsch\\}\\}.*?\\{\\{([mfn])\\}\\}", setOf(RegexOption.IGNORE_CASE, RegexOption.DOT_MATCHES_ALL)).find(wikitext)?.let { m ->
            android.util.Log.d("WiktionaryParser", "Found Wortart {{m}} pattern for '$headword'")
            return when (m.groupValues[1].lowercase()) {
                "m" -> "der"
                "f" -> "die"
                "n" -> "das"
                else -> null
            }
        }

        Regex("\\{\\{de-?noun\\|([^}]*)}}", RegexOption.IGNORE_CASE).find(wikitext)?.let { m ->
            val params = m.groupValues[1].split('|').map { it.trim().lowercase() }
            params.firstOrNull { it in setOf("m", "f", "n", "m.", "f.", "n.") }?.let { token ->
                return when (token.first()) {
                    'm' -> "der"
                    'f' -> "die"
                    'n' -> "das"
                    else -> null
                }
            }
            params.firstOrNull { it.startsWith("genus=") }?.substringAfter('=')?.let { g ->
                return when (g.firstOrNull()?.lowercaseChar()) {
                    'm' -> "der"
                    'f' -> "die"
                    'n' -> "das"
                    else -> null
                }
            }
        }

        // 2) Anchored article detection (must reference the headword in nominative singular contexts)
        val escaped = Regex.escape(headword)
        val anchored = listOf(
            Regex("'''(der|die|das)\\s+" + escaped + "'''", RegexOption.IGNORE_CASE),
            Regex("Nominativ\\s+Singular\\s+(der|die|das)\\s+" + escaped, RegexOption.IGNORE_CASE),
            Regex("\\b(der|die|das)\\s+(" + escaped + ")\\b", RegexOption.IGNORE_CASE)
        )
        for (rx in anchored) {
            rx.find(wikitext)?.let { m ->
                val article = m.groupValues[1].lowercase()
                if (article in listOf("der", "die", "das")) return article
            }
        }

        // 3) As a last resort, run legacy patterns but reject plural lines by requiring the following token to match headword
        val broad = Regex("\\b(der|die|das)\\s+([A-ZÄÖÜa-zäöüß]+)", RegexOption.IGNORE_CASE)
        broad.find(wikitext)?.let { m ->
            val article = m.groupValues[1].lowercase()
            val following = m.groupValues[2].lowercase()
            if (following == headword.lowercase()) return article
        }

        // Fallback to semantic keywords only (avoid direct 'der/die/das' matches that may come from plural lines)
        val semantics = listOf(
            Regex("maskulin|männlich", RegexOption.IGNORE_CASE) to "der",
            Regex("feminin|weiblich", RegexOption.IGNORE_CASE) to "die",
            Regex("neutrum|sächlich", RegexOption.IGNORE_CASE) to "das"
        )
        for ((rx, article) in semantics) {
            if (rx.containsMatchIn(wikitext)) return article
        }

        return null
    }
    
    /**
     * Extract simple definitions as fallback
     */
    private fun extractSimpleDefinitions(wikitext: String): List<Definition> {
        val definitions = mutableListOf<Definition>()
        
        // Look for any numbered definition patterns
        val patterns = listOf(
            ":\\s*\\[?\\d+\\]?\\s*(.{20,})",
            "#\\s*(.{20,})",
            "\\d+\\.\\s*(.{20,})"
        )
        
        for (pattern in patterns) {
            val matches = Regex(pattern).findAll(wikitext)
            for (match in matches.take(3)) {
                val meaning = cleanWikitext(match.groupValues[1])
                if (meaning.isNotBlank() && meaning.length > 10) {
                    definitions.add(Definition(meaning))
                }
            }
            if (definitions.isNotEmpty()) break
        }
        
        return definitions
    }
    
    /**
     * Extract simple examples as fallback
     */
    private fun extractSimpleExamples(wikitext: String): List<Example> {
        val examples = mutableListOf<Example>()
        
        // Look for quoted text that might be examples
        val patterns = listOf(
            "\"([^\"]{15,100})\"",
            "'([^']{15,100})'"
        )
        
        for (pattern in patterns) {
            val matches = Regex(pattern).findAll(wikitext)
            for (match in matches.take(3)) {
                val sentence = cleanWikitext(match.groupValues[1])
                if (sentence.isNotBlank() && sentence.length > 10) {
                    examples.add(Example(sentence, source = "Wiktionary"))
                }
            }
            if (examples.isNotEmpty()) break
        }
        
        return examples
    }
    
    /**
     * Clean wikitext markup from text
     */
    private fun cleanWikitext(text: String): String {
        return text
            .replace(Regex("\\[\\[[^|\\]]*\\|([^\\]]+)\\]\\]"), "$1") // [[link|text]] -> text
            .replace(Regex("\\[\\[([^\\]]+)\\]\\]"), "$1") // [[text]] -> text
            .replace(Regex("\\{\\{[^}]*\\}\\}"), "") // Remove templates {{...}}
            .replace(Regex("'''([^']+)'''"), "$1") // '''bold''' -> bold
            .replace(Regex("''([^']+)''"), "$1") // ''italic'' -> italic
            .replace(Regex("<[^>]*>"), "") // Remove HTML tags
            .replace(Regex("&[^;]+;"), "") // Remove HTML entities
            .replace(Regex("\\s+"), " ") // Normalize whitespace
            .trim()
    }
}
