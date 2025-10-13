package com.hellogerman.app.utils

import java.text.Normalizer
import java.util.Locale

/**
 * Utility object for normalizing text for dictionary search
 * 
 * Normalization ensures consistent searching by:
 * - Converting to lowercase
 * - Removing diacritics (accents) where appropriate
 * - Removing extra whitespace
 * - Handling special characters
 */
object TextNormalizer {
    
    /**
     * Normalize text for search indexing
     * 
     * @param text The text to normalize
     * @param preserveGermanChars Whether to preserve German-specific characters (ä, ö, ü, ß)
     * @return Normalized text suitable for search
     */
    fun normalize(text: String, preserveGermanChars: Boolean = true): String {
        if (text.isBlank()) return ""
        
        var normalized = text.trim().lowercase(Locale.GERMAN)
        
        // Remove extra whitespace
        normalized = normalized.replace(Regex("\\s+"), " ")
        
        if (!preserveGermanChars) {
            // Convert German special characters to ASCII equivalents
            normalized = normalized
                .replace("ä", "a")
                .replace("ö", "o")
                .replace("ü", "u")
                .replace("ß", "ss")
        }
        
        // Remove other diacritics while preserving German umlauts if requested
        if (!preserveGermanChars) {
            normalized = removeDiacritics(normalized)
        }
        
        return normalized
    }
    
    /**
     * Normalize for English words (more aggressive normalization)
     */
    fun normalizeEnglish(text: String): String {
        return normalize(text, preserveGermanChars = false)
    }
    
    /**
     * Normalize for German words (preserve umlauts and ß)
     */
    fun normalizeGerman(text: String): String {
        return normalize(text, preserveGermanChars = true)
    }
    
    /**
     * Remove diacritics from text
     */
    private fun removeDiacritics(text: String): String {
        val normalized = Normalizer.normalize(text, Normalizer.Form.NFD)
        return normalized.replace(Regex("\\p{InCombiningDiacriticalMarks}+"), "")
    }
    
    /**
     * Clean raw dictionary entry text
     * Remove markup, excessive whitespace, control characters
     */
    fun cleanRawEntry(text: String): String {
        var cleaned = text.trim()
        
        // Remove control characters
        cleaned = cleaned.replace(Regex("[\\p{Cntrl}&&[^\n\t]]"), "")
        
        // Normalize whitespace but preserve newlines
        cleaned = cleaned.replace(Regex("[ \\t]+"), " ")
        
        return cleaned
    }
    
    /**
     * Extract clean word from text with potential markup
     */
    fun extractCleanWord(text: String): String {
        var cleaned = text.trim()
        
        // Remove common dictionary markup
        cleaned = cleaned
            .replace(Regex("<[^>]+>"), "") // Remove <tags>
            .replace(Regex("\\[[^\\]]+\\]"), "") // Remove [brackets]
            .replace(Regex("\\([^)]*\\)"), "") // Remove (parentheses)
            .replace(Regex("\\{[^}]+\\}"), "") // Remove {braces}
            .trim()
        
        // Take first word if multiple words
        cleaned = cleaned.split(Regex("\\s+|[,;/|]"))[0]
        
        return cleaned
    }
    
    /**
     * Check if text appears to be German (contains umlauts or ß)
     */
    fun looksGerman(text: String): Boolean {
        return text.contains(Regex("[äöüßÄÖÜ]"))
    }
    
    /**
     * Check if text is likely a German noun (starts with capital letter)
     */
    fun looksLikeGermanNoun(text: String): Boolean {
        if (text.isEmpty()) return false
        val firstChar = text[0]
        return firstChar.isUpperCase() && !text.all { it.isUpperCase() }
    }
}

