package com.hellogerman.app.data.embeddings

import android.content.Context
import android.util.Log
import kotlin.math.sqrt

/**
 * Lightweight fallback embedding generator (no TFLite model required)
 * 
 * Uses simple character n-gram and word hashing for basic semantic similarity
 * This provides basic synonym detection without requiring an 80MB model file
 * 
 * Note: Can be replaced with full TFLite model later for better accuracy
 */
class SimplifiedEmbeddingGenerator(private val context: Context) {
    
    companion object {
        private const val TAG = "SimplifiedEmbeddingGenerator"
        private const val EMBEDDING_DIM = 384
        private const val NGRAM_SIZE = 3
    }
    
    private var isInitialized = false
    
    /**
     * Initialize the generator
     */
    fun initialize(): Boolean {
        isInitialized = true
        Log.d(TAG, "Simplified embedding generator initialized")
        return true
    }
    
    /**
     * Generate embedding vector for text using character n-grams
     * 
     * @param text Input text (German or English)
     * @return 384-dimensional embedding vector
     */
    fun generateEmbedding(text: String): FloatArray? {
        if (!isInitialized) {
            Log.w(TAG, "Embedding generator not initialized")
            return null
        }
        
        if (text.isBlank()) {
            return null
        }
        
        return try {
            val normalized = text.lowercase().trim()
            val embedding = FloatArray(EMBEDDING_DIM) { 0f }
            
            // Character n-grams
            val nGrams = extractNGrams(normalized, NGRAM_SIZE)
            nGrams.forEachIndexed { index, ngram ->
                val hash = ngram.hashCode()
                val position = Math.abs(hash % EMBEDDING_DIM)
                embedding[position] += 1f
            }
            
            // Word-level features
            val words = normalized.split(Regex("\\s+"))
            words.forEach { word ->
                if (word.isNotEmpty()) {
                    val hash = word.hashCode()
                    val position = Math.abs(hash % EMBEDDING_DIM)
                    embedding[position] += 2f // Words weighted more than n-grams
                }
            }
            
            // Normalize
            normalize(embedding)
            
        } catch (e: Exception) {
            Log.e(TAG, "Error generating embedding: ${e.message}", e)
            null
        }
    }
    
    /**
     * Extract character n-grams from text
     */
    private fun extractNGrams(text: String, n: Int): List<String> {
        val ngrams = mutableListOf<String>()
        
        if (text.length < n) {
            return listOf(text)
        }
        
        for (i in 0..text.length - n) {
            ngrams.add(text.substring(i, i + n))
        }
        
        return ngrams
    }
    
    /**
     * L2 normalization of embedding vector
     */
    private fun normalize(vector: FloatArray): FloatArray {
        val magnitude = sqrt(vector.fold(0f) { acc, value -> acc + value * value })
        
        if (magnitude > 0) {
            for (i in vector.indices) {
                vector[i] /= magnitude
            }
        }
        
        return vector
    }
    
    /**
     * Generate embeddings for multiple texts in batch
     */
    fun generateEmbeddingsBatch(texts: List<String>): List<FloatArray?> {
        return texts.map { generateEmbedding(it) }
    }
    
    /**
     * Calculate cosine similarity between two embeddings
     */
    fun cosineSimilarity(embedding1: FloatArray, embedding2: FloatArray): Float {
        require(embedding1.size == embedding2.size) {
            "Embeddings must have same dimension"
        }
        
        var dotProduct = 0f
        for (i in embedding1.indices) {
            dotProduct += embedding1[i] * embedding2[i]
        }
        
        return dotProduct.coerceIn(0f, 1f)
    }
    
    /**
     * Find most similar embeddings from a list
     */
    fun findMostSimilar(
        queryEmbedding: FloatArray,
        candidateEmbeddings: List<Pair<Long, FloatArray>>,
        topK: Int = 10
    ): List<Pair<Long, Float>> {
        
        val similarities = candidateEmbeddings.map { (id, embedding) ->
            val similarity = cosineSimilarity(queryEmbedding, embedding)
            id to similarity
        }
        
        return similarities
            .sortedByDescending { it.second }
            .take(topK)
    }
    
    /**
     * Release resources
     */
    fun close() {
        isInitialized = false
        Log.d(TAG, "Simplified embedding generator closed")
    }
}

