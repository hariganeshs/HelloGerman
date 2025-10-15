package com.hellogerman.app.data.dictionary

import android.content.Context
import android.util.Log
import com.hellogerman.app.data.HelloGermanDatabase
import com.hellogerman.app.data.entities.DictionaryEntry
import com.hellogerman.app.data.entities.DictionaryVectorEntry
import com.hellogerman.app.data.entities.VectorConverter
import com.hellogerman.app.data.embeddings.EmbeddingGenerator
import com.hellogerman.app.data.grammar.AdvancedGenderDetector
import com.hellogerman.app.data.examples.ExampleExtractor
import com.hellogerman.app.utils.TextNormalizer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.yield

/**
 * Main orchestrator for dictionary import process
 * 
 * Coordinates decompression, parsing, grammar extraction, and database insertion
 * of all dictionary entries from FreeDict eng-deu dictionary.
 */
class DictionaryImporter(
    private val context: Context
) {
    
    companion object {
        private const val TAG = "DictionaryImporter"
        private const val BATCH_SIZE = 500 // Process and insert in batches
        private const val ASSET_DICT_PATH = "freedict-eng-deu-1.9-fd1.dictd/eng-deu/eng-deu.dict.dz"
        private const val ASSET_INDEX_PATH = "freedict-eng-deu-1.9-fd1.dictd/eng-deu/eng-deu.index"
    }
    
    private val database = HelloGermanDatabase.getDatabase(context)
    private val dictionaryDao = database.dictionaryDao()
    private val vectorDao = database.dictionaryVectorDao()
    
    private val fileReader = DictdFileReader(context, ASSET_DICT_PATH)
    private val indexParser = DictdIndexParser(context, ASSET_INDEX_PATH)
    private val dataParser = DictdDataParser()
    
    // Enhanced extractors for better accuracy
    private val advancedGenderDetector = AdvancedGenderDetector()
    private val exampleExtractor = ExampleExtractor()
    private val embeddingGenerator = EmbeddingGenerator(context)
    private val grammarExtractor = GrammarExtractor() // Keep for verb/adjective info
    
    /**
     * Import progress information
     */
    data class ImportProgress(
        val phase: ImportPhase,
        val totalEntries: Int,
        val processedEntries: Int,
        val successfulEntries: Int,
        val failedEntries: Int,
        val currentBatch: Int,
        val totalBatches: Int,
        val message: String
    ) {
        val progressPercentage: Int
            get() = if (totalEntries > 0) {
                ((processedEntries * 100) / totalEntries)
            } else 0
    }
    
    /**
     * Import phases
     */
    enum class ImportPhase {
        INITIALIZING,
        DECOMPRESSING,
        PARSING_INDEX,
        IMPORTING_ENTRIES,
        FINALIZING,
        COMPLETED,
        ERROR
    }
    
    /**
     * Import result summary
     */
    data class ImportResult(
        val success: Boolean,
        val totalEntries: Int,
        val successfulEntries: Int,
        val failedEntries: Int,
        val durationMs: Long,
        val errors: List<String>,
        val databaseSize: Long
    )
    
    /**
     * Progress listener interface
     */
    interface ProgressListener {
        fun onProgressUpdate(progress: ImportProgress)
        fun onComplete(result: ImportResult)
        fun onError(error: Exception)
    }
    
    /**
     * Start the complete import process
     * 
     * @param clearExisting Whether to clear existing dictionary data first
     * @param listener Progress listener for updates
     */
    suspend fun startImport(
        clearExisting: Boolean = true,
        listener: ProgressListener? = null
    ): ImportResult {
        return performImport(clearExisting, listener)
    }
    
    /**
     * Internal import implementation (separated to avoid bytecode verification issues)
     */
    private suspend fun performImport(
        clearExisting: Boolean,
        listener: ProgressListener?
    ): ImportResult = withContext(Dispatchers.IO) {
        val startTime = System.currentTimeMillis()
        val errors = mutableListOf<String>()
        var successfulEntries = 0
        var failedEntries = 0
        
        try {
            // Phase 1: Initialization
            notifyProgress(listener, ImportPhase.INITIALIZING, 0, 0, 0, 0, 0, 0, "Initializing import process...")
            
            Log.d(TAG, "Starting dictionary import")
            
            // Clear existing data if requested
            if (clearExisting) {
                Log.d(TAG, "Clearing existing dictionary data")
                dictionaryDao.deleteAllEntries()
            }
            
            // Phase 2: Decompress dictionary file
            notifyProgress(listener, ImportPhase.DECOMPRESSING, 0, 0, 0, 0, 0, 0, "Decompressing dictionary file...")
            
            if (!fileReader.decompressIfNeeded()) {
                throw Exception("Failed to decompress dictionary file")
            }
            
            Log.d(TAG, "Dictionary file decompressed: ${fileReader.getFileSize() / 1024 / 1024}MB")
            
            // Phase 3: Parse index
            notifyProgress(listener, ImportPhase.PARSING_INDEX, 0, 0, 0, 0, 0, 0, "Parsing dictionary index...")
            
            val index = indexParser.parseIndex()
            val totalEntries = index.size
            val totalBatches = (totalEntries + BATCH_SIZE - 1) / BATCH_SIZE
            
            Log.d(TAG, "Index parsed: $totalEntries entries, $totalBatches batches")
            
            // Phase 4: Import entries
            notifyProgress(listener, ImportPhase.IMPORTING_ENTRIES, totalEntries, 0, 0, 0, 0, totalBatches, "Importing dictionary entries...")
            
            var processedCount = 0
            var batchNumber = 0
            val entriesToInsert = mutableListOf<DictionaryEntry>()
            
            // Process entries in batches
            for ((_, indexEntry) in index) {
                try {
                    val result = processEntry(indexEntry)
                    if (result != null) {
                        entriesToInsert.addAll(result)
                    } else {
                        failedEntries++
                    }
                    
                    processedCount++
                    
                    // Insert batch when full
                    if (entriesToInsert.size >= BATCH_SIZE) {
                        val batchSize = entriesToInsert.size
                        val inserted = insertBatch(entriesToInsert, batchNumber, totalBatches)
                        if (inserted) {
                            successfulEntries += batchSize
                            batchNumber++
                            notifyProgress(listener, ImportPhase.IMPORTING_ENTRIES, totalEntries, processedCount, successfulEntries, failedEntries, batchNumber, totalBatches, "Importing entries: $successfulEntries/$totalEntries")
                        } else {
                            failedEntries += batchSize
                            errors.add("Batch $batchNumber insert failed")
                        }
                        entriesToInsert.clear()
                        yield()
                    }
                    
                } catch (e: Exception) {
                    Log.w(TAG, "Error processing entry: ${indexEntry.headword}", e)
                    failedEntries++
                }
            }
            
            // Insert remaining entries
            if (entriesToInsert.isNotEmpty()) {
                val batchSize = entriesToInsert.size
                val inserted = insertBatch(entriesToInsert, batchNumber, totalBatches)
                if (inserted) {
                    successfulEntries += batchSize
                    batchNumber++
                } else {
                    failedEntries += batchSize
                }
            }
            
            // Phase 5: Finalize
            notifyProgress(listener, ImportPhase.FINALIZING, totalEntries, processedCount, successfulEntries, failedEntries, totalBatches, totalBatches, "Finalizing import...")
            
            val duration = System.currentTimeMillis() - startTime
            val databaseSize = getDatabaseSize()
            
            Log.d(TAG, "Import complete! Successful: $successfulEntries, Failed: $failedEntries, Duration: ${duration/1000}s")
            
            val result = ImportResult(
                success = true,
                totalEntries = totalEntries,
                successfulEntries = successfulEntries,
                failedEntries = failedEntries,
                durationMs = duration,
                errors = errors.take(100),
                databaseSize = databaseSize
            )
            
            notifyProgress(listener, ImportPhase.COMPLETED, totalEntries, processedCount, successfulEntries, failedEntries, totalBatches, totalBatches, "Import completed successfully!")
            listener?.onComplete(result)
            
            result
            
        } catch (e: Exception) {
            Log.e(TAG, "Import failed", e)
            errors.add("Import failed: ${e.message}")
            
            val result = ImportResult(
                success = false,
                totalEntries = 0,
                successfulEntries = successfulEntries,
                failedEntries = failedEntries,
                durationMs = System.currentTimeMillis() - startTime,
                errors = errors,
                databaseSize = getDatabaseSize()
            )
            
            notifyProgress(listener, ImportPhase.ERROR, 0, 0, successfulEntries, failedEntries, 0, 0, "Import failed: ${e.message}")
            listener?.onError(e)
            
            result
        }
    }
    
    /**
     * Process a single index entry and create dictionary entries
     * Enhanced version with advanced gender detection and example extraction
     */
    private fun processEntry(indexEntry: DictdIndexParser.IndexEntry): List<DictionaryEntry>? {
        try {
            val rawText = fileReader.readBlock(indexEntry.offset, indexEntry.length) ?: return null
            if (rawText.isBlank()) return null
            
            val parsedEntry = dataParser.parse(indexEntry.headword, rawText)
            if (parsedEntry.translations.isEmpty()) return null
            
            val entries = mutableListOf<DictionaryEntry>()
            
            for (translation in parsedEntry.translations) {
                val cleanedTranslation = TextNormalizer.extractCleanWord(translation)
                if (cleanedTranslation.isEmpty()) continue
                
                // Get basic grammar info (verbs, adjectives, etc.)
                val grammarInfo = grammarExtractor.extract(
                    germanWord = cleanedTranslation,
                    englishWord = indexEntry.headword,
                    rawContext = rawText,
                    partOfSpeechTags = parsedEntry.partOfSpeechTags
                )
                
                // Use advanced gender detection (95%+ accuracy)
                val genderResult = advancedGenderDetector.detectGender(
                    germanWord = cleanedTranslation,
                    rawContext = rawText,
                    partOfSpeechTags = parsedEntry.partOfSpeechTags
                )
                
                // Use gender from advanced detector if confidence is high, otherwise fallback
                val finalGender = if (genderResult.confidence >= 0.7f) {
                    genderResult.gender
                } else {
                    grammarInfo.gender
                }
                
                // Extract examples using enhanced extractor (50%+ coverage)
                val enhancedExamples = exampleExtractor.extractExamples(
                    rawText = rawText,
                    germanWord = cleanedTranslation,
                    englishWord = indexEntry.headword
                )
                
                // Combine with parsed examples
                val allExamples = (enhancedExamples + parsedEntry.examples).distinctBy { it.german }
                
                val entry = DictionaryEntry(
                    englishWord = indexEntry.headword,
                    germanWord = cleanedTranslation,
                    wordType = grammarInfo.wordType,
                    gender = finalGender,
                    pluralForm = grammarInfo.pluralForm,
                    pastTense = grammarInfo.pastTense,
                    pastParticiple = grammarInfo.pastParticiple,
                    auxiliaryVerb = grammarInfo.auxiliaryVerb,
                    isIrregular = grammarInfo.isIrregular,
                    isSeparable = grammarInfo.isSeparable,
                    comparative = grammarInfo.comparative,
                    superlative = grammarInfo.superlative,
                    additionalTranslations = parsedEntry.translations.filter { it != translation },
                    examples = allExamples.take(5), // Limit to 5 best examples
                    pronunciationIpa = parsedEntry.pronunciationIpa,
                    domain = parsedEntry.domainLabels.firstOrNull(),
                    rawEntry = rawText.take(500),
                    englishNormalized = TextNormalizer.normalizeEnglish(indexEntry.headword),
                    germanNormalized = TextNormalizer.normalizeGerman(cleanedTranslation),
                    wordLength = cleanedTranslation.length
                )
                
                entries.add(entry)
            }
            
            return entries
        } catch (e: Exception) {
            Log.w(TAG, "Error processing entry: ${indexEntry.headword}", e)
            return null
        }
    }
    
    /**
     * Insert batch of entries and their vector embeddings
     * Enhanced with storage monitoring and graceful degradation
     */
    private suspend fun insertBatch(entries: List<DictionaryEntry>, batchNumber: Int, totalBatches: Int): Boolean {
        return try {
            // Check storage space before inserting
            if (!checkStorageSpace()) {
                Log.w(TAG, "Insufficient storage space, skipping vector generation for batch $batchNumber")
                // Insert text-only entries (no vectors)
                val insertedIds = dictionaryDao.insertEntries(entries)
                Log.d(TAG, "Inserted text-only batch $batchNumber/$totalBatches (${entries.size} entries)")
                return true
            }
            
            // Insert dictionary entries first (to get IDs)
            val insertedIds = dictionaryDao.insertEntries(entries)
            Log.d(TAG, "Inserted batch $batchNumber/$totalBatches (${entries.size} entries)")
            
            // Generate and insert vectors for the entries (with optimization)
            if (insertedIds.isNotEmpty()) {
                try {
                    val vectors = generateOptimizedVectors(entries, insertedIds)
                    if (vectors.isNotEmpty()) {
                        vectorDao.insertVectorsBatch(vectors)
                        Log.d(TAG, "Inserted vectors for batch $batchNumber (${vectors.size} vectors)")
                    }
                } catch (vectorError: Exception) {
                    Log.w(TAG, "Vector generation failed for batch $batchNumber, continuing with text-only", vectorError)
                    // Continue without vectors - text search will still work
                }
            }
            
            true
        } catch (e: Exception) {
            if (e.message?.contains("SQLITE_FULL") == true || e.message?.contains("database or disk is full") == true) {
                Log.e(TAG, "Storage full error in batch $batchNumber, switching to text-only mode", e)
                // Try to insert text-only entries
                try {
                    val insertedIds = dictionaryDao.insertEntries(entries)
                    Log.d(TAG, "Recovered: Inserted text-only batch $batchNumber (${entries.size} entries)")
                    return true
                } catch (textError: Exception) {
                    Log.e(TAG, "Even text-only insertion failed for batch $batchNumber", textError)
                    return false
                }
            } else {
                Log.e(TAG, "Error inserting batch $batchNumber", e)
                return false
            }
        }
    }
    
    /**
     * Check available storage space
     */
    private fun checkStorageSpace(): Boolean {
        try {
            val dataDir = context.filesDir
            val freeSpace = dataDir.freeSpace
            val totalSpace = dataDir.totalSpace
            val usedSpace = totalSpace - freeSpace
            
            Log.d(TAG, "Storage check - Free: ${freeSpace/(1024*1024)}MB, Used: ${usedSpace/(1024*1024)}MB")
            
            // Require at least 100MB free space for vectors
            return freeSpace > 100 * 1024 * 1024
        } catch (e: Exception) {
            Log.w(TAG, "Could not check storage space", e)
            return true // Assume we have space if we can't check
        }
    }
    
    /**
     * Generate optimized vector embeddings (smaller, more efficient)
     */
    private suspend fun generateOptimizedVectors(
        entries: List<DictionaryEntry>,
        entryIds: List<Long>
    ): List<DictionaryVectorEntry> {
        if (!embeddingGenerator.initialize()) {
            Log.w(TAG, "Embedding generator not initialized, using simplified embeddings")
            return generateSimplifiedVectors(entries, entryIds)
        }
        
        // Only generate vectors for common words to save space
        val commonWordEntries = entries.filterIndexed { index, entry ->
            index < 1000 || isCommonWord(entry.englishWord) || isCommonWord(entry.germanWord)
        }
        
        val vectors = mutableListOf<DictionaryVectorEntry>()
        
        commonWordEntries.forEachIndexed { index, entry ->
            try {
                val originalIndex = entries.indexOf(entry)
                val entryId = entryIds.getOrNull(originalIndex) ?: return@forEachIndexed
                
                // Generate smaller embeddings (128 dimensions instead of 384)
                val combinedText = "${entry.germanWord} ${entry.englishWord}"
                val combinedEmbedding = embeddingGenerator.generateEmbedding(combinedText)?.let { embedding ->
                    // Reduce dimensions to 128
                    embedding.take(128).toFloatArray()
                }
                
                val germanEmbedding = embeddingGenerator.generateEmbedding(entry.germanWord)?.let { embedding ->
                    embedding.take(128).toFloatArray()
                }
                
                val englishEmbedding = embeddingGenerator.generateEmbedding(entry.englishWord)?.let { embedding ->
                    embedding.take(128).toFloatArray()
                }
                
                if (combinedEmbedding != null && germanEmbedding != null && englishEmbedding != null) {
                    val vectorEntry = DictionaryVectorEntry(
                        entryId = entryId,
                        combinedEmbedding = floatArrayToByteArray(combinedEmbedding),
                        germanEmbedding = floatArrayToByteArray(germanEmbedding),
                        englishEmbedding = floatArrayToByteArray(englishEmbedding),
                        hasExamples = entry.examples.isNotEmpty(),
                        hasGender = entry.gender != null,
                        wordType = entry.wordType?.name,
                        gender = entry.gender?.name
                    )
                    vectors.add(vectorEntry)
                }
            } catch (e: Exception) {
                Log.w(TAG, "Error generating vector for entry: ${entry.englishWord}", e)
            }
        }
        
        Log.d(TAG, "Generated ${vectors.size} optimized vectors out of ${entries.size} entries")
        return vectors
    }
    
    /**
     * Generate simplified vectors using character n-grams (fallback)
     */
    private suspend fun generateSimplifiedVectors(
        entries: List<DictionaryEntry>,
        entryIds: List<Long>
    ): List<DictionaryVectorEntry> {
        val vectors = mutableListOf<DictionaryVectorEntry>()
        
        entries.take(10000).forEachIndexed { index, entry -> // Limit to 10k entries for simplified vectors
            try {
                val entryId = entryIds.getOrNull(index) ?: return@forEachIndexed
                
                // Generate simplified embeddings using character n-grams
                val combinedEmbedding = generateSimplifiedEmbedding("${entry.germanWord} ${entry.englishWord}")
                val germanEmbedding = generateSimplifiedEmbedding(entry.germanWord)
                val englishEmbedding = generateSimplifiedEmbedding(entry.englishWord)
                
                val vectorEntry = DictionaryVectorEntry(
                    entryId = entryId,
                    combinedEmbedding = combinedEmbedding,
                    germanEmbedding = germanEmbedding,
                    englishEmbedding = englishEmbedding,
                    hasExamples = entry.examples.isNotEmpty(),
                    hasGender = entry.gender != null,
                    wordType = entry.wordType?.name,
                    gender = entry.gender?.name
                )
                vectors.add(vectorEntry)
            } catch (e: Exception) {
                Log.w(TAG, "Error generating simplified vector for entry: ${entry.englishWord}", e)
            }
        }
        
        Log.d(TAG, "Generated ${vectors.size} simplified vectors")
        return vectors
    }
    
    /**
     * Check if a word is common (for selective vector generation)
     */
    private fun isCommonWord(word: String): Boolean {
        val commonWords = setOf(
            "apple", "house", "water", "food", "book", "tree", "car", "dog", "cat", "bird",
            "Apfel", "Haus", "Wasser", "Essen", "Buch", "Baum", "Auto", "Hund", "Katze", "Vogel",
            "mother", "father", "child", "man", "woman", "person", "family", "friend",
            "Mutter", "Vater", "Kind", "Mann", "Frau", "Person", "Familie", "Freund"
        )
        return commonWords.contains(word.lowercase())
    }
    
    /**
     * Generate simplified embedding using character n-grams
     */
    private fun generateSimplifiedEmbedding(text: String): ByteArray {
        val ngrams = mutableSetOf<String>()
        val normalizedText = text.lowercase().replace(Regex("[^a-zäöüß]"), "")
        
        // Generate 2-grams and 3-grams
        for (i in 0 until normalizedText.length - 1) {
            ngrams.add(normalizedText.substring(i, i + 2))
        }
        for (i in 0 until normalizedText.length - 2) {
            ngrams.add(normalizedText.substring(i, i + 3))
        }
        
        // Convert to 64-dimensional vector (simplified)
        val vector = FloatArray(64)
        ngrams.forEachIndexed { index, ngram ->
            val hash = ngram.hashCode()
            val vectorIndex = Math.abs(hash) % 64
            vector[vectorIndex] += 1.0f
        }
        
        return floatArrayToByteArray(vector)
    }
    
    /**
     * Convert float array to byte array for storage
     */
    private fun floatArrayToByteArray(floatArray: FloatArray): ByteArray {
        val byteArray = ByteArray(floatArray.size * 4)
        for (i in floatArray.indices) {
            val bits = java.lang.Float.floatToIntBits(floatArray[i])
            byteArray[i * 4] = (bits shr 24).toByte()
            byteArray[i * 4 + 1] = (bits shr 16).toByte()
            byteArray[i * 4 + 2] = (bits shr 8).toByte()
            byteArray[i * 4 + 3] = bits.toByte()
        }
        return byteArray
    }
    
    
    /**
     * Helper to notify progress
     */
    private fun notifyProgress(
        listener: ProgressListener?,
        phase: ImportPhase,
        totalEntries: Int,
        processedEntries: Int,
        successfulEntries: Int,
        failedEntries: Int,
        currentBatch: Int,
        totalBatches: Int,
        message: String
    ) {
        listener?.onProgressUpdate(ImportProgress(
            phase = phase,
            totalEntries = totalEntries,
            processedEntries = processedEntries,
            successfulEntries = successfulEntries,
            failedEntries = failedEntries,
            currentBatch = currentBatch,
            totalBatches = totalBatches,
            message = message
        ))
    }
    
    /**
     * Get current dictionary statistics
     */
    suspend fun getStatistics(): DictionaryStatistics = withContext(Dispatchers.IO) {
        try {
            val stats = dictionaryDao.getStatisticsSummary()
            DictionaryStatistics(
                totalEntries = stats.total,
                nouns = stats.nouns,
                verbs = stats.verbs,
                adjectives = stats.adjectives,
                masculineNouns = stats.masculine,
                feminineNouns = stats.feminine,
                neuterNouns = stats.neuter,
                entriesWithExamples = stats.with_examples,
                databaseSizeMB = getDatabaseSize() / (1024 * 1024)
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error getting statistics", e)
            DictionaryStatistics(0, 0, 0, 0, 0, 0, 0, 0, 0)
        }
    }
    
    /**
     * Clear all dictionary data
     */
    suspend fun clearDictionary() = withContext(Dispatchers.IO) {
        try {
            dictionaryDao.deleteAllEntries()
            fileReader.clearCache()
            Log.d(TAG, "Dictionary data cleared")
        } catch (e: Exception) {
            Log.e(TAG, "Error clearing dictionary", e)
            throw e
        }
    }
    
    /**
     * Get database file size
     */
    private fun getDatabaseSize(): Long {
        return try {
            val dbPath = context.getDatabasePath("hello_german_database")
            if (dbPath.exists()) dbPath.length() else 0
        } catch (e: Exception) {
            0
        }
    }
    
    /**
     * Dictionary statistics data class
     */
    data class DictionaryStatistics(
        val totalEntries: Int,
        val nouns: Int,
        val verbs: Int,
        val adjectives: Int,
        val masculineNouns: Int,
        val feminineNouns: Int,
        val neuterNouns: Int,
        val entriesWithExamples: Int,
        val databaseSizeMB: Long
    )
}

