package com.hellogerman.app.debug

import android.content.Context
import com.hellogerman.app.data.HelloGermanDatabase
import com.hellogerman.app.data.dao.DictionaryDao
import kotlinx.coroutines.runBlocking

/**
 * Debug script to check what dictionary entries exist for specific words
 * This helps identify why search is not working correctly
 */
class DictionaryDebugger(private val context: Context) {
    
    private val database = HelloGermanDatabase.getDatabase(context)
    private val dictionaryDao = database.dictionaryDao()
    
    /**
     * Check what entries exist for a specific word
     */
    fun debugWord(word: String) = runBlocking {
        println("=== DEBUGGING WORD: '$word' ===")
        
        // Check English exact match
        val englishExact = dictionaryDao.searchEnglishExact(word, 10)
        println("English exact matches: ${englishExact.size}")
        englishExact.forEach { entry ->
            println("  - EN: '${entry.englishWord}' -> DE: '${entry.germanWord}' (${entry.wordType})")
        }
        
        // Check English prefix match
        val englishPrefix = dictionaryDao.searchEnglishPrefix(word, 10)
        println("English prefix matches: ${englishPrefix.size}")
        englishPrefix.forEach { entry ->
            println("  - EN: '${entry.englishWord}' -> DE: '${entry.germanWord}' (${entry.wordType})")
        }
        
        // Check German exact match
        val germanExact = dictionaryDao.searchGermanExact(word, 10)
        println("German exact matches: ${germanExact.size}")
        germanExact.forEach { entry ->
            println("  - DE: '${entry.germanWord}' -> EN: '${entry.englishWord}' (${entry.wordType})")
        }
        
        // Check German prefix match
        val germanPrefix = dictionaryDao.searchGermanPrefix(word, 10)
        println("German prefix matches: ${germanPrefix.size}")
        germanPrefix.forEach { entry ->
            println("  - DE: '${entry.germanWord}' -> EN: '${entry.englishWord}' (${entry.wordType})")
        }
        
        // Check total entries
        val totalEntries = dictionaryDao.getEntryCount()
        println("Total dictionary entries: $totalEntries")
        
        println("=== END DEBUG ===\n")
    }
    
    /**
     * Check if dictionary is imported
     */
    fun checkImportStatus() = runBlocking {
        println("=== DICTIONARY IMPORT STATUS ===")
        val totalEntries = dictionaryDao.getEntryCount()
        val isImported = dictionaryDao.isDictionaryImported()
        
        println("Total entries: $totalEntries")
        println("Is imported: $isImported")
        
        if (totalEntries < 100000) {
            println("⚠️  WARNING: Low entry count suggests incomplete import")
        } else {
            println("✅ Entry count looks good")
        }
        
        println("=== END STATUS ===\n")
    }
    
    /**
     * Check some sample entries to see what's in the database
     */
    fun checkSampleEntries() = runBlocking {
        println("=== SAMPLE ENTRIES ===")
        
        // Get first 10 entries
        val sampleEntries = dictionaryDao.getAllEntries(10)
        println("First 10 entries:")
        sampleEntries.forEach { entry ->
            println("  - EN: '${entry.englishWord}' -> DE: '${entry.germanWord}' (${entry.wordType})")
        }
        
        println("=== END SAMPLES ===\n")
    }
}

// Usage example:
// val debugger = DictionaryDebugger(context)
// debugger.checkImportStatus()
// debugger.checkSampleEntries()
// debugger.debugWord("apple")
// debugger.debugWord("apfel")
