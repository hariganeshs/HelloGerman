package com.hellogerman.app.data.parser

import java.util.regex.Pattern

/**
 * Parser for extracting IPA pronunciation and audio information from Wiktionary content
 * Handles the parsing of wikitext to extract pronunciation data
 */
class IpaExtractor {

    /**
     * Extract IPA pronunciation from Wiktionary wikitext content
     * @param wikitext The raw wikitext content from Wiktionary
     * @return The IPA transcription if found, null otherwise
     */
    fun extractIpa(wikitext: String): String? {
        // Look for German IPA templates: {{IPA|de|/ˈmʊtɐ/}}
        val ipaPattern = Pattern.compile("\\{\\{IPA\\|de\\|([^}]+)\\}\\}")
        val matcher = ipaPattern.matcher(wikitext)

        if (matcher.find()) {
            val ipaContent = matcher.group(1)
            // Remove surrounding slashes if present
            return ipaContent?.removeSurrounding("/", "/")
                ?.removeSurrounding("[", "]")
                ?.trim()
        }

        // Alternative pattern for pronunciation sections
        val pronunciationSectionPattern = Pattern.compile("===Pronunciation===(.*?)===|\\{\\{pron\\|de\\|([^}]+)\\}\\}")
        val sectionMatcher = pronunciationSectionPattern.matcher(wikitext)

        if (sectionMatcher.find()) {
            val pronunciationContent = sectionMatcher.group(1) ?: sectionMatcher.group(2)
            if (pronunciationContent != null) {
                // Look for IPA notation within the pronunciation section
                val ipaInSectionPattern = Pattern.compile("/([^/]+)/|\\[([^]]+)\\]")
                val ipaMatcher = ipaInSectionPattern.matcher(pronunciationContent)
                if (ipaMatcher.find()) {
                    return ipaMatcher.group(1) ?: ipaMatcher.group(2)
                }
            }
        }

        return null
    }

    /**
     * Extract audio URL from Wiktionary wikitext content
     * @param wikitext The raw wikitext content from Wiktionary
     * @return The audio URL if found, null otherwise
     */
    fun extractAudioUrl(wikitext: String): String? {
        // Look for audio templates: {{Audio|De-Mutter.ogg}}
        val audioPattern = Pattern.compile("\\{\\{Audio\\|([^}]+)\\.ogg\\}\\}|\\{\\{Audio\\|([^}]+)\\.mp3\\}\\}|\\{\\{IPA audio\\|([^}]+)\\}\\}")

        var matcher = audioPattern.matcher(wikitext)
        if (matcher.find()) {
            val audioFilename = matcher.group(1) ?: matcher.group(2) ?: matcher.group(3)
            if (audioFilename != null) {
                return "https://upload.wikimedia.org/wikipedia/commons/$audioFilename.ogg"
            }
        }

        // Also check for direct audio links
        val directAudioPattern = Pattern.compile("https?://[^\\s}]+\\.(ogg|mp3)")
        matcher = directAudioPattern.matcher(wikitext)

        if (matcher.find()) {
            return matcher.group()
        }

        return null
    }

    /**
     * Extract both IPA and audio information from Wiktionary content
     * @param wikitext The raw wikitext content from Wiktionary
     * @return A pair containing IPA and audio URL, either can be null
     */
    fun extractPronunciationData(wikitext: String): Pair<String?, String?> {
        val ipa = extractIpa(wikitext)
        val audioUrl = extractAudioUrl(wikitext)
        return Pair(ipa, audioUrl)
    }

    /**
     * Extract German pronunciation section from Wiktionary content
     * @param wikitext The raw wikitext content from Wiktionary
     * @return The pronunciation section content if found, null otherwise
     */
    fun extractPronunciationSection(wikitext: String): String? {
        val pattern = Pattern.compile("(?s)\\{\\{de-IPA\\}\\}.*?\\{\\{/de-IPA\\}\\}|\\{\\{IPA\\|de\\|.*?\\}\\}|===Pronunciation===\\s*(.*?)\\s*(?:===|$)")

        val matcher = pattern.matcher(wikitext)
        if (matcher.find()) {
            return matcher.group(1) ?: matcher.group()
        }

        return null
    }
}
