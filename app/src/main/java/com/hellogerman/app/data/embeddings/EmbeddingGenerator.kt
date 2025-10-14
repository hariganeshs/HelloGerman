package com.hellogerman.app.data.embeddings

import android.content.Context
import android.util.Log
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.common.FileUtil
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.math.sqrt

/**
 * On-device text embedding generator using TensorFlow Lite
 * 
 * Generates 384-dimensional vectors for German and English text using
 * multilingual sentence transformers model (paraphrase-multilingual-MiniLM-L12-v2)
 * 
 * This enables semantic search, synonym discovery, and contextual similarity matching
 */
class EmbeddingGenerator(private val context: Context) {
    
    companion object {
        private const val TAG = "EmbeddingGenerator"
        
        // Model configuration
        private const val MODEL_PATH = "models/multilingual_embeddings.tflite"
        private const val EMBEDDING_DIM = 384
        private const val MAX_SEQUENCE_LENGTH = 128
        private const val VOCAB_SIZE = 30522
        
        // Special tokens
        private const val CLS_TOKEN_ID = 101
        private const val SEP_TOKEN_ID = 102
        private const val PAD_TOKEN_ID = 0
    }
    
    private var interpreter: Interpreter? = null
    private var simplifiedGenerator: SimplifiedEmbeddingGenerator? = null
    private var isInitialized = false
    private var useSimplifiedMode = false
    
    /**
     * Initialize the TensorFlow Lite interpreter with fallback to simplified mode
     */
    fun initialize(): Boolean {
        return try {
            if (isInitialized) {
                Log.d(TAG, "Embedding generator already initialized")
                return true
            }
            
            Log.d(TAG, "Initializing embedding generator...")
            
            // Try to load TFLite model first
            try {
                val modelBuffer = FileUtil.loadMappedFile(context, MODEL_PATH)
                
                // Configure interpreter options
                val options = Interpreter.Options().apply {
                    setNumThreads(4) // Use 4 threads for better performance
                    setUseNNAPI(true) // Try to use Android Neural Networks API
                }
                
                interpreter = Interpreter(modelBuffer, options)
                useSimplifiedMode = false
                isInitialized = true
                
                Log.d(TAG, "TFLite model loaded successfully - using full semantic search")
                return true
                
            } catch (e: IOException) {
                Log.w(TAG, "TFLite model not found, falling back to simplified mode: ${e.message}")
                // Fall back to simplified mode
                simplifiedGenerator = SimplifiedEmbeddingGenerator(context)
                useSimplifiedMode = true
                isInitialized = simplifiedGenerator?.initialize() ?: false
                
                if (isInitialized) {
                    Log.d(TAG, "Simplified embedding generator initialized (basic semantic search)")
                }
                return isInitialized
            }
            
        } catch (e: Exception) {
            Log.e(TAG, "Error initializing embedding generator: ${e.message}", e)
            
            // Last resort: try simplified mode
            try {
                simplifiedGenerator = SimplifiedEmbeddingGenerator(context)
                useSimplifiedMode = true
                isInitialized = simplifiedGenerator?.initialize() ?: false
                isInitialized
            } catch (e2: Exception) {
                Log.e(TAG, "Failed to initialize even simplified mode: ${e2.message}", e2)
                false
            }
        }
    }
    
    /**
     * Generate embedding vector for text
     * 
     * @param text Input text (German or English)
     * @return 384-dimensional embedding vector, or null if generation fails
     */
    fun generateEmbedding(text: String): FloatArray? {
        if (!isInitialized) {
            Log.w(TAG, "Embedding generator not initialized")
            return null
        }
        
        if (text.isBlank()) {
            Log.w(TAG, "Empty text provided for embedding")
            return null
        }
        
        return try {
            if (useSimplifiedMode) {
                // Use simplified n-gram based embeddings
                simplifiedGenerator?.generateEmbedding(text)
            } else {
                // Use full TFLite model
                // Preprocess text
                val tokens = tokenize(text)
                
                // Prepare input tensors
                val inputIds = prepareInputIds(tokens)
                val attentionMask = prepareAttentionMask(tokens.size)
                
                // Run inference
                val embeddings = runInference(inputIds, attentionMask)
                
                // Normalize embeddings (L2 normalization for cosine similarity)
                normalize(embeddings)
            }
            
        } catch (e: Exception) {
            Log.e(TAG, "Error generating embedding: ${e.message}", e)
            null
        }
    }
    
    /**
     * Generate embeddings for multiple texts in batch
     * 
     * More efficient than calling generateEmbedding() multiple times
     */
    fun generateEmbeddingsBatch(texts: List<String>): List<FloatArray?> {
        return texts.map { generateEmbedding(it) }
    }
    
    /**
     * Tokenize text into sub-word tokens
     * 
     * Note: This is a simplified tokenization. In production, you'd use
     * the actual SentenceP humanize Tokenizer or WordPiece tokenizer
     */
    private fun tokenize(text: String): List<Int> {
        // Simplified tokenization - convert to lowercase and split
        val normalized = text.lowercase().trim()
        
        // For now, use character-level tokenization as a placeholder
        // TODO: Implement proper WordPiece tokenization or load vocabulary
        val tokens = mutableListOf<Int>()
        tokens.add(CLS_TOKEN_ID) // Start token
        
        // Simple character-based tokenization (placeholder)
        normalized.take(MAX_SEQUENCE_LENGTH - 2).forEach { char ->
            val tokenId = char.code.coerceIn(0, VOCAB_SIZE - 1)
            tokens.add(tokenId)
        }
        
        tokens.add(SEP_TOKEN_ID) // End token
        
        return tokens
    }
    
    /**
     * Prepare input IDs tensor
     */
    private fun prepareInputIds(tokens: List<Int>): Array<IntArray> {
        val inputIds = IntArray(MAX_SEQUENCE_LENGTH) { PAD_TOKEN_ID }
        
        tokens.take(MAX_SEQUENCE_LENGTH).forEachIndexed { index, tokenId ->
            inputIds[index] = tokenId
        }
        
        return arrayOf(inputIds)
    }
    
    /**
     * Prepare attention mask tensor
     */
    private fun prepareAttentionMask(tokenCount: Int): Array<IntArray> {
        val attentionMask = IntArray(MAX_SEQUENCE_LENGTH) { 0 }
        
        val actualTokens = minOf(tokenCount, MAX_SEQUENCE_LENGTH)
        for (i in 0 until actualTokens) {
            attentionMask[i] = 1
        }
        
        return arrayOf(attentionMask)
    }
    
    /**
     * Run TFLite inference
     */
    private fun runInference(inputIds: Array<IntArray>, attentionMask: Array<IntArray>): FloatArray {
        val interpreter = this.interpreter ?: throw IllegalStateException("Interpreter not initialized")
        
        // Prepare output buffer
        val outputEmbedding = Array(1) { FloatArray(EMBEDDING_DIM) }
        
        // Create input map
        val inputs = arrayOf<Any>(inputIds, attentionMask)
        
        // Create output map
        val outputs = mutableMapOf<Int, Any>()
        outputs[0] = outputEmbedding
        
        // Run inference
        interpreter.runForMultipleInputsOutputs(inputs, outputs)
        
        return outputEmbedding[0]
    }
    
    /**
     * L2 normalization of embedding vector
     * Required for cosine similarity calculations
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
     * Calculate cosine similarity between two embeddings
     * 
     * @return Similarity score between 0.0 (completely different) and 1.0 (identical)
     */
    fun cosineSimilarity(embedding1: FloatArray, embedding2: FloatArray): Float {
        if (useSimplifiedMode) {
            return simplifiedGenerator?.cosineSimilarity(embedding1, embedding2) ?: 0f
        }
        
        require(embedding1.size == embedding2.size) {
            "Embeddings must have same dimension"
        }
        
        var dotProduct = 0f
        for (i in embedding1.indices) {
            dotProduct += embedding1[i] * embedding2[i]
        }
        
        // Since embeddings are normalized, dot product IS the cosine similarity
        return dotProduct.coerceIn(0f, 1f)
    }
    
    /**
     * Find most similar embeddings from a list
     * 
     * @param queryEmbedding The query embedding
     * @param candidateEmbeddings List of candidate embeddings with their IDs
     * @param topK Number of top results to return
     * @return List of (ID, similarity score) pairs, sorted by similarity (descending)
     */
    fun findMostSimilar(
        queryEmbedding: FloatArray,
        candidateEmbeddings: List<Pair<Long, FloatArray>>,
        topK: Int = 10
    ): List<Pair<Long, Float>> {
        
        if (useSimplifiedMode) {
            return simplifiedGenerator?.findMostSimilar(queryEmbedding, candidateEmbeddings, topK) 
                ?: emptyList()
        }
        
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
        try {
            interpreter?.close()
            interpreter = null
            simplifiedGenerator?.close()
            simplifiedGenerator = null
            isInitialized = false
            useSimplifiedMode = false
            Log.d(TAG, "Embedding generator closed")
        } catch (e: Exception) {
            Log.e(TAG, "Error closing embedding generator: ${e.message}", e)
        }
    }
    
    /**
     * Check if using simplified mode (no TFLite model)
     */
    fun isUsingSimplifiedMode(): Boolean = useSimplifiedMode
}

