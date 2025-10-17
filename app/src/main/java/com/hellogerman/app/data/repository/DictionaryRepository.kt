package com.hellogerman.app.data.repository

import android.content.Context
import android.util.Log
import com.hellogerman.app.data.HelloGermanDatabase
import com.hellogerman.app.data.entities.DictionaryEntry
import com.hellogerman.app.data.entities.GermanGender
import com.hellogerman.app.data.entities.SearchLanguage
import com.hellogerman.app.data.entities.WordType
import com.hellogerman.app.data.dictionary.DictionaryImporter
import com.hellogerman.app.utils.TextNormalizer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

/**
 * Main repository for dictionary operations
 * 
 * Provides high-level API for:
 * - Searching words (English→German and German→English)
 * - Autocomplete suggestions
 * - Word filtering by type and gender
 * - Dictionary import management
 * - Statistics
 */
class DictionaryRepository(private val context: Context) {
    
    companion object {
        private const val TAG = "DictionaryRepository"
    }
    
    private val database = HelloGermanDatabase.getDatabase(context)
    private val dictionaryDao = database.dictionaryDao()
    private val importer = DictionaryImporter(context)
    
    // Vector search for semantic capabilities
    private val vectorSearchRepo = VectorSearchRepository(context)
    
    /**
     * Search for a word in either language
     * 
     * @param query The word to search for
     * @param language Which language to search (ENGLISH or GERMAN)
     * @param exactMatch Whether to require exact match or allow fuzzy search
     * @return List of matching dictionary entries
     */
    suspend fun search(
        query: String,
        language: SearchLanguage = SearchLanguage.ENGLISH,
        exactMatch: Boolean = false
    ): List<DictionaryEntry> = withContext(Dispatchers.IO) {
        
        if (query.isBlank()) return@withContext emptyList()
        
        val normalized = when (language) {
            SearchLanguage.ENGLISH -> TextNormalizer.normalizeEnglish(query)
            SearchLanguage.GERMAN -> TextNormalizer.normalizeGerman(query)
        }
        
        when (language) {
            SearchLanguage.ENGLISH -> {
                // Always try exact match first for better results
                val exactResults = dictionaryDao.searchEnglishExact(normalized, limit = 10)
                if (exactResults.isNotEmpty()) {
                    exactResults
                } else if (exactMatch) {
                    exactResults
                } else {
                    // Try prefix match
                    val prefixResults = dictionaryDao.searchEnglishPrefix(normalized, limit = 50)
                    if (prefixResults.isNotEmpty()) {
                        prefixResults
                    } else {
                        // Fall back to fuzzy search
                        dictionaryDao.searchEnglishFuzzy(normalized, limit = 100)
                    }
                }
            }
            SearchLanguage.GERMAN -> {
                // Always try exact match first for better results
                val exactResults = dictionaryDao.searchGermanExact(normalized, limit = 10)
                if (exactResults.isNotEmpty()) {
                    exactResults
                } else if (exactMatch) {
                    exactResults
                } else {
                    // Try prefix match
                    val prefixResults = dictionaryDao.searchGermanPrefix(normalized, limit = 50)
                    if (prefixResults.isNotEmpty()) {
                        prefixResults
                    } else {
                        // Fall back to fuzzy search
                        dictionaryDao.searchGermanFuzzy(normalized, limit = 100)
                    }
                }
            }
        }
    }
    
    /**
     * Get autocomplete suggestions for a query
     * 
     * @param prefix The prefix to search for
     * @param language Which language to search
     * @param limit Maximum number of suggestions
     * @return List of word suggestions
     */
    suspend fun getSuggestions(
        prefix: String,
        language: SearchLanguage = SearchLanguage.ENGLISH,
        limit: Int = 20
    ): List<String> = withContext(Dispatchers.IO) {
        
        if (prefix.isBlank()) return@withContext emptyList()
        
        val normalized = when (language) {
            SearchLanguage.ENGLISH -> TextNormalizer.normalizeEnglish(prefix)
            SearchLanguage.GERMAN -> TextNormalizer.normalizeGerman(prefix)
        }
        
        when (language) {
            SearchLanguage.ENGLISH -> dictionaryDao.getEnglishSuggestions(normalized, limit)
            SearchLanguage.GERMAN -> dictionaryDao.getGermanSuggestions(normalized, limit)
        }
    }
    
    /**
     * Search with filters (word type, gender, etc.)
     */
    suspend fun searchWithFilters(
        query: String,
        wordType: WordType? = null,
        gender: GermanGender? = null,
        limit: Int = 50
    ): List<DictionaryEntry> = withContext(Dispatchers.IO) {
        
        val normalized = TextNormalizer.normalize(query)
        dictionaryDao.searchWithFilters(normalized, wordType, gender, limit)
    }
    
    /**
     * Get entry by ID
     */
    suspend fun getEntryById(id: Long): DictionaryEntry? = withContext(Dispatchers.IO) {
        dictionaryDao.getEntryById(id)
    }
    
    /**
     * Get random entries with examples (for learning)
     */
    suspend fun getEntriesWithExamples(limit: Int = 50): List<DictionaryEntry> = withContext(Dispatchers.IO) {
        dictionaryDao.getEntriesWithExamples(limit)
    }
    
    /**
     * Get entries by word type
     */
    suspend fun getEntriesByWordType(
        wordType: WordType,
        limit: Int = 100
    ): List<DictionaryEntry> = withContext(Dispatchers.IO) {
        dictionaryDao.getEntriesByWordType(wordType, limit)
    }
    
    /**
     * Get nouns by gender
     */
    suspend fun getNounsByGender(
        gender: GermanGender,
        limit: Int = 100
    ): List<DictionaryEntry> = withContext(Dispatchers.IO) {
        dictionaryDao.getNounsByGender(gender, limit)
    }
    
    /**
     * Observe search results reactively (Flow)
     */
    fun observeSearch(
        prefix: String,
        language: SearchLanguage = SearchLanguage.ENGLISH,
        limit: Int = 50
    ): Flow<List<DictionaryEntry>> {
        
        val normalized = when (language) {
            SearchLanguage.ENGLISH -> TextNormalizer.normalizeEnglish(prefix)
            SearchLanguage.GERMAN -> TextNormalizer.normalizeGerman(prefix)
        }
        
        return when (language) {
            SearchLanguage.ENGLISH -> dictionaryDao.observeEnglishPrefix(normalized, limit)
            SearchLanguage.GERMAN -> dictionaryDao.observeGermanPrefix(normalized, limit)
        }
    }
    
    // ==================== Dictionary Management ====================
    
    /**
     * Import dictionary from FreeDict files
     */
    suspend fun importDictionary(
        clearExisting: Boolean = true,
        progressListener: DictionaryImporter.ProgressListener? = null
    ): DictionaryImporter.ImportResult {
        return importer.startImport(clearExisting, progressListener)
    }
    
    /**
     * Get dictionary statistics
     */
    suspend fun getStatistics(): DictionaryImporter.DictionaryStatistics {
        return importer.getStatistics()
    }
    
    /**
     * Check if dictionary is imported
     */
    suspend fun isDictionaryImported(): Boolean = withContext(Dispatchers.IO) {
        dictionaryDao.getTotalEntryCount() > 0
    }
    
    /**
     * Get entry count
     */
    suspend fun getEntryCount(): Int = withContext(Dispatchers.IO) {
        dictionaryDao.getTotalEntryCount()
    }
    
    /**
     * Clear all dictionary data
     */
    suspend fun clearDictionary() {
        importer.clearDictionary()
    }
    
    // ==================== Vector/Semantic Search Methods ====================
    
    /**
     * Initialize vector search system
     * Should be called once at app startup
     */
    suspend fun initializeVectorSearch(): Boolean {
        return try {
            vectorSearchRepo.initialize()
        } catch (e: Exception) {
            Log.e(TAG, "Error initializing vector search: ${e.message}", e)
            false
        }
    }
    
    /**
     * Hybrid search combining exact matching with semantic similarity
     * 
     * @param query Search query
     * @param language Search language
     * @param useSemanticSearch Whether to enable semantic search (requires TFLite model)
     * @param limit Maximum results
     * @return List of entries with similarity scores
     */
    suspend fun searchHybrid(
        query: String,
        language: SearchLanguage = SearchLanguage.ENGLISH,
        useSemanticSearch: Boolean = true,
        limit: Int = 50
    ): List<Pair<DictionaryEntry, Float>> = withContext(Dispatchers.IO) {
        
        if (query.isBlank()) return@withContext emptyList()
        
        try {
            // Get exact/prefix matches from SQLite
            val exactMatches = search(query, language, exactMatch = false)
            
            // If semantic search is disabled or vector search isn't available, return exact matches
            if (!useSemanticSearch || !vectorSearchRepo.isVectorDatabasePopulated()) {
                return@withContext exactMatches.map { it to 1.0f }
            }
            
            // Combine with semantic search
            vectorSearchRepo.hybridSearch(
                query = query,
                exactMatches = exactMatches,
                language = language,
                limit = limit
            )
            
        } catch (e: Exception) {
            Log.e(TAG, "Error in hybrid search: ${e.message}", e)
            // Fallback to exact matches only
            search(query, language, exactMatch = false).map { it to 1.0f }
        }
    }
    
    /**
     * Find synonyms for a word using semantic similarity
     * 
     * @param word Word to find synonyms for
     * @param language Word language
     * @param limit Maximum number of synonyms
     * @return List of synonym entries with similarity scores
     */
    suspend fun findSynonyms(
        word: String,
        language: SearchLanguage = SearchLanguage.ENGLISH,
        limit: Int = 10
    ): List<Pair<DictionaryEntry, Float>> = withContext(Dispatchers.IO) {
        
        try {
            if (!vectorSearchRepo.isVectorDatabasePopulated()) {
                Log.w(TAG, "Vector database not populated, cannot find synonyms")
                return@withContext emptyList()
            }
            
            vectorSearchRepo.findSynonyms(word, language, limit)
            
        } catch (e: Exception) {
            Log.e(TAG, "Error finding synonyms: ${e.message}", e)
            emptyList()
        }
    }
    
    /**
     * Find related words (broader than synonyms)
     * 
     * @param word Word to find related words for
     * @param language Word language
     * @param limit Maximum number of related words
     * @return List of related entries with similarity scores
     */
    suspend fun findRelatedWords(
        word: String,
        language: SearchLanguage = SearchLanguage.ENGLISH,
        limit: Int = 20
    ): List<Pair<DictionaryEntry, Float>> = withContext(Dispatchers.IO) {
        
        try {
            if (!vectorSearchRepo.isVectorDatabasePopulated()) {
                return@withContext emptyList()
            }
            
            vectorSearchRepo.findRelatedWords(word, language, limit)
            
        } catch (e: Exception) {
            Log.e(TAG, "Error finding related words: ${e.message}", e)
            emptyList()
        }
    }
    
    /**
     * Check if vector/semantic search is available
     */
    suspend fun isSemanticSearchAvailable(): Boolean {
        return try {
            vectorSearchRepo.isVectorDatabasePopulated()
        } catch (e: Exception) {
            false
        }
    }
    
    /**
     * Get vector search statistics
     */
    suspend fun getVectorStatistics(): VectorSearchRepository.VectorStatistics {
        return try {
            vectorSearchRepo.getVectorStatistics()
        } catch (e: Exception) {
            VectorSearchRepository.VectorStatistics(0, 0, 0)
        }
    }
    
    // ==================== Utility Methods ====================
    
    /**
     * Detect language of query
     */
    fun detectLanguage(query: String): SearchLanguage {
        return if (TextNormalizer.looksGerman(query)) {
            SearchLanguage.GERMAN
        } else {
            SearchLanguage.ENGLISH
        }
    }
    
    /**
     * Format gender display text
     */
    fun formatGenderDisplay(gender: GermanGender?): String {
        return gender?.getArticle() ?: ""
    }
    
    /**
     * Format word with gender (for nouns)
     */
    fun formatWordWithGender(entry: DictionaryEntry): String {
        return if (entry.wordType == WordType.NOUN && entry.gender != null) {
            "${entry.gender.getArticle()} ${entry.germanWord}"
        } else {
            entry.germanWord
        }
    }
    
    /**
     * Debug method to check what entries exist for a specific word
     */
    suspend fun debugWord(word: String): String {
        val result = StringBuilder()
        result.appendLine("=== DEBUGGING WORD: '$word' ===")
        
        // Check English exact match
        val englishExact = dictionaryDao.searchEnglishExact(word, 10)
        result.appendLine("English exact matches: ${englishExact.size}")
        englishExact.forEach { entry ->
            result.appendLine("  - EN: '${entry.englishWord}' -> DE: '${entry.germanWord}' (${entry.wordType})")
        }
        
        // Check English prefix match
        val englishPrefix = dictionaryDao.searchEnglishPrefix(word, 10)
        result.appendLine("English prefix matches: ${englishPrefix.size}")
        englishPrefix.take(5).forEach { entry ->
            result.appendLine("  - EN: '${entry.englishWord}' -> DE: '${entry.germanWord}' (${entry.wordType})")
        }
        
        // Check German exact match
        val germanExact = dictionaryDao.searchGermanExact(word, 10)
        result.appendLine("German exact matches: ${germanExact.size}")
        germanExact.forEach { entry ->
            result.appendLine("  - DE: '${entry.germanWord}' -> EN: '${entry.englishWord}' (${entry.wordType})")
        }
        
        // Check German prefix match
        val germanPrefix = dictionaryDao.searchGermanPrefix(word, 10)
        result.appendLine("German prefix matches: ${germanPrefix.size}")
        germanPrefix.take(5).forEach { entry ->
            result.appendLine("  - DE: '${entry.germanWord}' -> EN: '${entry.englishWord}' (${entry.wordType})")
        }
        
        // Check total entries
        val totalEntries = dictionaryDao.getTotalEntryCount()
        result.appendLine("Total dictionary entries: $totalEntries")
        
        result.appendLine("=== END DEBUG ===")
        return result.toString()
    }
}

