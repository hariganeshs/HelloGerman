package com.hellogerman.app.data.repository

import android.content.Context
import android.util.Log
import com.hellogerman.app.data.HelloGermanDatabase
import com.hellogerman.app.data.dao.DictionaryDao
import com.hellogerman.app.data.dao.DictionaryVectorDao
import com.hellogerman.app.data.embeddings.EmbeddingGenerator
import com.hellogerman.app.data.entities.DictionaryEntry
import com.hellogerman.app.data.entities.DictionaryVectorEntry
import com.hellogerman.app.data.entities.SearchLanguage
import com.hellogerman.app.data.entities.VectorConverter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Vector-based semantic search repository
 * 
 * Provides:
 * - Synonym discovery (e.g., "happy" → froh, glücklich, fröhlich)
 * - Contextual similarity (e.g., "greeting" → Hallo, Guten Tag, Grüß Gott)
 * - Better search ranking through semantic understanding
 * - Related word suggestions
 */
class VectorSearchRepository(private val context: Context) {
    
    companion object {
        private const val TAG = "VectorSearchRepository"
        
        // Similarity thresholds
        private const val MIN_SIMILARITY_THRESHOLD = 0.5f
        private const val STRONG_SIMILARITY_THRESHOLD = 0.75f
        
        // Search limits
        private const val DEFAULT_SEARCH_LIMIT = 50
        private const val BATCH_SIZE = 1000
    }
    
    private val database = HelloGermanDatabase.getDatabase(context)
    private val vectorDao: DictionaryVectorDao = database.dictionaryVectorDao()
    private val dictionaryDao: DictionaryDao = database.dictionaryDao()
    private val embeddingGenerator = EmbeddingGenerator(context)
    
    private var isInitialized = false
    
    /**
     * Initialize the vector search system
     */
    suspend fun initialize(): Boolean = withContext(Dispatchers.IO) {
        return@withContext try {
            if (isInitialized) {
                Log.d(TAG, "Vector search already initialized")
                return@withContext true
            }
            
            Log.d(TAG, "Initializing vector search...")
            
            // Initialize embedding generator
            val success = embeddingGenerator.initialize()
            if (!success) {
                Log.e(TAG, "Failed to initialize embedding generator")
                return@withContext false
            }
            
            isInitialized = true
            Log.d(TAG, "Vector search initialized successfully")
            true
            
        } catch (e: Exception) {
            Log.e(TAG, "Error initializing vector search: ${e.message}", e)
            false
        }
    }
    
    /**
     * Semantic search using vector similarity
     * 
     * Finds semantically similar words even if they don't match exactly
     * 
     * @param query Search query
     * @param language Search language
     * @param limit Maximum results
     * @param minSimilarity Minimum similarity threshold (0.0 to 1.0)
     * @return List of dictionary entries ranked by semantic similarity
     */
    suspend fun searchSemantic(
        query: String,
        language: SearchLanguage = SearchLanguage.ENGLISH,
        limit: Int = DEFAULT_SEARCH_LIMIT,
        minSimilarity: Float = MIN_SIMILARITY_THRESHOLD
    ): List<Pair<DictionaryEntry, Float>> = withContext(Dispatchers.IO) {
        
        if (!isInitialized) {
            Log.w(TAG, "Vector search not initialized")
            return@withContext emptyList()
        }
        
        if (query.isBlank()) {
            return@withContext emptyList()
        }
        
        try {
            // Generate query embedding
            val queryEmbedding = embeddingGenerator.generateEmbedding(query)
            if (queryEmbedding == null) {
                Log.w(TAG, "Failed to generate query embedding")
                return@withContext emptyList()
            }
            
            // Search through vector database in batches
            val results = mutableListOf<Pair<Long, Float>>()
            var offset = 0
            
            while (true) {
                val vectorBatch = vectorDao.getVectorsBatch(limit = BATCH_SIZE, offset = offset)
                if (vectorBatch.isEmpty()) break
                
                // Calculate similarity for each vector
                vectorBatch.forEach { vectorEntry ->
                    val similarity = when (language) {
                        SearchLanguage.ENGLISH -> {
                            val embedding = VectorConverter.byteArrayToFloatArray(vectorEntry.englishEmbedding)
                            embeddingGenerator.cosineSimilarity(queryEmbedding, embedding)
                        }
                        SearchLanguage.GERMAN -> {
                            val embedding = VectorConverter.byteArrayToFloatArray(vectorEntry.germanEmbedding)
                            embeddingGenerator.cosineSimilarity(queryEmbedding, embedding)
                        }
                    }
                    
                    if (similarity >= minSimilarity) {
                        results.add(vectorEntry.entryId to similarity)
                    }
                }
                
                offset += BATCH_SIZE
            }
            
            // Sort by similarity (descending) and take top results
            val topResults = results
                .sortedByDescending { it.second }
                .take(limit)
            
            // Fetch dictionary entries
            val entryIds = topResults.map { it.first }
            val entries = dictionaryDao.getEntryById(entryIds[0]) // Get first entry
            
            // Map to pairs of (entry, similarity)
            val entriesMap = mutableMapOf<Long, DictionaryEntry>()
            topResults.forEach { (entryId, _) ->
                val entry = dictionaryDao.getEntryById(entryId)
                if (entry != null) {
                    entriesMap[entryId] = entry
                }
            }
            
            topResults.mapNotNull { (entryId, similarity) ->
                entriesMap[entryId]?.let { it to similarity }
            }
            
        } catch (e: Exception) {
            Log.e(TAG, "Error in semantic search: ${e.message}", e)
            emptyList()
        }
    }
    
    /**
     * Find synonyms for a word
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
    ): List<Pair<DictionaryEntry, Float>> {
        
        return searchSemantic(
            query = word,
            language = language,
            limit = limit,
            minSimilarity = STRONG_SIMILARITY_THRESHOLD
        )
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
    ): List<Pair<DictionaryEntry, Float>> {
        
        return searchSemantic(
            query = word,
            language = language,
            limit = limit,
            minSimilarity = MIN_SIMILARITY_THRESHOLD
        )
    }
    
    /**
     * Hybrid search: Combine exact/prefix matching with semantic search
     * 
     * @param query Search query
     * @param exactMatches Exact matches from SQLite
     * @param language Search language
     * @param limit Total result limit
     * @return Combined and ranked results
     */
    suspend fun hybridSearch(
        query: String,
        exactMatches: List<DictionaryEntry>,
        language: SearchLanguage = SearchLanguage.ENGLISH,
        limit: Int = DEFAULT_SEARCH_LIMIT
    ): List<Pair<DictionaryEntry, Float>> = withContext(Dispatchers.IO) {
        
        try {
            // Get semantic matches
            val semanticMatches = searchSemantic(
                query = query,
                language = language,
                limit = limit,
                minSimilarity = MIN_SIMILARITY_THRESHOLD
            )
            
            // Combine results with scoring
            val combined = mutableMapOf<Long, Pair<DictionaryEntry, Float>>()
            
            // Add exact matches with highest score (1.0)
            exactMatches.forEach { entry ->
                combined[entry.id] = entry to 1.0f
            }
            
            // Add semantic matches (if not already present)
            semanticMatches.forEach { (entry, similarity) ->
                if (!combined.containsKey(entry.id)) {
                    // Scale semantic score (0.5-1.0 → 0.6-0.9 to rank below exact matches)
                    val scaledScore = 0.6f + (similarity * 0.3f)
                    combined[entry.id] = entry to scaledScore
                }
            }
            
            // Sort by score (descending) and take top results
            combined.values
                .sortedByDescending { it.second }
                .take(limit)
            
        } catch (e: Exception) {
            Log.e(TAG, "Error in hybrid search: ${e.message}", e)
            // Fallback to exact matches only
            exactMatches.map { it to 1.0f }
        }
    }
    
    /**
     * Check if vector database is populated
     */
    suspend fun isVectorDatabasePopulated(): Boolean = withContext(Dispatchers.IO) {
        try {
            val count = vectorDao.getTotalVectorCount()
            count > 0
        } catch (e: Exception) {
            Log.e(TAG, "Error checking vector database: ${e.message}", e)
            false
        }
    }
    
    /**
     * Get vector database statistics
     */
    suspend fun getVectorStatistics(): VectorStatistics = withContext(Dispatchers.IO) {
        try {
            VectorStatistics(
                totalVectors = vectorDao.getTotalVectorCount(),
                vectorsWithGender = vectorDao.getCountWithGender(),
                vectorsWithExamples = vectorDao.getCountWithExamples()
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error getting vector statistics: ${e.message}", e)
            VectorStatistics(0, 0, 0)
        }
    }
    
    /**
     * Clean up resources
     */
    fun close() {
        embeddingGenerator.close()
        isInitialized = false
    }
    
    /**
     * Vector database statistics
     */
    data class VectorStatistics(
        val totalVectors: Int,
        val vectorsWithGender: Int,
        val vectorsWithExamples: Int
    )
}

