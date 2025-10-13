package com.hellogerman.app.data.dictionary

import android.content.Context
import android.util.Log
import java.io.BufferedReader
import java.io.InputStreamReader

/**
 * Parser for dictd .index files
 * 
 * The .index file contains a lookup table mapping headwords to their location
 * in the .dict file. Format is tab-separated:
 * headword \t offset_base64 \t length_base64
 */
class DictdIndexParser(
    private val context: Context,
    private val assetPath: String  // e.g., "freedict-eng-deu-1.9-fd1.dictd/eng-deu/eng-deu.index"
) {
    
    companion object {
        private const val TAG = "DictdIndexParser"
    }
    
    /**
     * Index entry containing location information for a dictionary entry
     */
    data class IndexEntry(
        val headword: String,
        val offset: Long,    // Byte offset in .dict file
        val length: Int      // Length in bytes
    )
    
    /**
     * Parse the entire index file into memory
     * 
     * @return Map of headword (lowercase) to IndexEntry
     */
    fun parseIndex(): Map<String, IndexEntry> {
        val index = mutableMapOf<String, IndexEntry>()
        var lineCount = 0
        var skippedCount = 0
        
        try {
            Log.d(TAG, "Parsing index file: $assetPath")
            
            context.assets.open(assetPath).use { input ->
                BufferedReader(InputStreamReader(input, Charsets.UTF_8)).use { reader ->
                    reader.lineSequence().forEach { line ->
                        lineCount++
                        
                        // Skip empty lines
                        if (line.isBlank()) {
                            skippedCount++
                            return@forEach
                        }
                        
                        // Parse line: headword \t offset64 \t length64
                        val parts = line.split('\t')
                        if (parts.size < 3) {
                            skippedCount++
                            return@forEach
                        }
                        
                        val headword = parts[0]
                        
                        // Skip metadata entries (start with "00database")
                        if (headword.isEmpty() || headword.startsWith("00database")) {
                            skippedCount++
                            return@forEach
                        }
                        
                        val offset64 = parts[1]
                        val length64 = parts[2]
                        
                        try {
                            val offset = decodeBase64Number(offset64)
                            val length = decodeBase64Number(length64).toInt()
                            
                            // Store with lowercase key for case-insensitive lookup
                            val key = headword.lowercase()
                            
                            // Only store first occurrence (skip duplicates)
                            if (!index.containsKey(key)) {
                                index[key] = IndexEntry(headword, offset, length)
                            }
                        } catch (e: Exception) {
                            Log.w(TAG, "Error parsing index entry: $headword", e)
                            skippedCount++
                        }
                        
                        // Log progress every 10000 entries
                        if (lineCount % 10000 == 0) {
                            Log.d(TAG, "Parsed $lineCount index entries (${index.size} unique)...")
                        }
                    }
                }
            }
            
            Log.d(TAG, "Index parsing complete. Total: ${index.size} entries (skipped: $skippedCount)")
            
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing index file", e)
        }
        
        return index
    }
    
    /**
     * Parse only headwords (for getting word list without full index)
     * More memory-efficient if you only need the word list
     */
    fun parseHeadwordsOnly(): List<String> {
        val headwords = mutableListOf<String>()
        
        try {
            context.assets.open(assetPath).use { input ->
                BufferedReader(InputStreamReader(input, Charsets.UTF_8)).use { reader ->
                    reader.lineSequence().forEach { line ->
                        if (line.isBlank()) return@forEach
                        
                        val parts = line.split('\t')
                        if (parts.isEmpty()) return@forEach
                        
                        val headword = parts[0]
                        if (headword.isNotEmpty() && !headword.startsWith("00database")) {
                            headwords.add(headword)
                        }
                    }
                }
            }
            
            Log.d(TAG, "Parsed ${headwords.size} headwords")
            
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing headwords", e)
        }
        
        return headwords
    }
    
    /**
     * Get total entry count without parsing entire index
     */
    fun getEntryCount(): Int {
        var count = 0
        
        try {
            context.assets.open(assetPath).use { input ->
                BufferedReader(InputStreamReader(input, Charsets.UTF_8)).use { reader ->
                    reader.lineSequence().forEach { line ->
                        if (line.isNotBlank()) {
                            val parts = line.split('\t')
                            if (parts.isNotEmpty() && parts[0].isNotEmpty() && !parts[0].startsWith("00database")) {
                                count++
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error counting entries", e)
        }
        
        return count
    }
    
    /**
     * Decode base64-encoded number from dictd format
     * 
     * dictd uses a custom base64 encoding for numbers where:
     * A-Z = 0-25, a-z = 26-51, 0-9 = 52-61, + = 62, / = 63
     */
    private fun decodeBase64Number(encoded: String): Long {
        var result = 0L
        
        for (ch in encoded) {
            val value = when (ch) {
                in 'A'..'Z' -> ch.code - 'A'.code
                in 'a'..'z' -> 26 + (ch.code - 'a'.code)
                in '0'..'9' -> 52 + (ch.code - '0'.code)
                '+' -> 62
                '/' -> 63
                else -> 0
            }
            result = (result shl 6) or value.toLong()
        }
        
        return result
    }
}

