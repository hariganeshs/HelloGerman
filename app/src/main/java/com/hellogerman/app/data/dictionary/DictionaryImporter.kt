package com.hellogerman.app.data.dictionary

import android.content.Context
import android.util.Log
import com.hellogerman.app.data.HelloGermanDatabase
import com.hellogerman.app.data.entities.DictionaryEntry
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
        
        // English → German dictionary paths
        private const val ASSET_ENG_DEU_DICT_PATH = "freedict-eng-deu-1.9-fd1.dictd/eng-deu/eng-deu.dict.dz"
        private const val ASSET_ENG_DEU_INDEX_PATH = "freedict-eng-deu-1.9-fd1.dictd/eng-deu/eng-deu.index"
        
        // German → English dictionary paths
        private const val ASSET_DEU_ENG_DICT_PATH = "freedict-deu-eng-1.9-fd1.dictd/deu-eng/deu-eng.dict.dz"
        private const val ASSET_DEU_ENG_INDEX_PATH = "freedict-deu-eng-1.9-fd1.dictd/deu-eng/deu-eng.index"
    }
    
    private val database = HelloGermanDatabase.getDatabase(context)
    private val dictionaryDao = database.dictionaryDao()
    
    // English → German readers/parsers
    private val engDeuFileReader = DictdFileReader(context, ASSET_ENG_DEU_DICT_PATH)
    private val engDeuIndexParser = DictdIndexParser(context, ASSET_ENG_DEU_INDEX_PATH)
    
    // German → English readers/parsers
    private val deuEngFileReader = DictdFileReader(context, ASSET_DEU_ENG_DICT_PATH)
    private val deuEngIndexParser = DictdIndexParser(context, ASSET_DEU_ENG_INDEX_PATH)
    
    private val dataParser = DictdDataParser()
    
    // Enhanced extractors for better accuracy
    private val advancedGenderDetector = AdvancedGenderDetector()
    private val exampleExtractor = ExampleExtractor()
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
            
            // Phase 2: Decompress English → German dictionary
            notifyProgress(listener, ImportPhase.DECOMPRESSING, 0, 0, 0, 0, 0, 0, "Decompressing English → German dictionary...")
            
            if (!engDeuFileReader.decompressIfNeeded()) {
                throw Exception("Failed to decompress English → German dictionary file")
            }
            
            Log.d(TAG, "Eng-Deu dictionary decompressed: ${engDeuFileReader.getFileSize() / 1024 / 1024}MB")
            
            // Phase 3: Parse English → German index
            notifyProgress(listener, ImportPhase.PARSING_INDEX, 0, 0, 0, 0, 0, 0, "Parsing English → German index...")
            
            val engDeuIndex = engDeuIndexParser.parseIndex()
            Log.d(TAG, "Eng-Deu index parsed: ${engDeuIndex.size} entries")
            
            // Phase 4: Decompress German → English dictionary
            notifyProgress(listener, ImportPhase.DECOMPRESSING, 0, 0, 0, 0, 0, 0, "Decompressing German → English dictionary...")
            
            if (!deuEngFileReader.decompressIfNeeded()) {
                throw Exception("Failed to decompress German → English dictionary file")
            }
            
            Log.d(TAG, "Deu-Eng dictionary decompressed: ${deuEngFileReader.getFileSize() / 1024 / 1024}MB")
            
            // Phase 5: Parse German → English index
            notifyProgress(listener, ImportPhase.PARSING_INDEX, 0, 0, 0, 0, 0, 0, "Parsing German → English index...")
            
            val deuEngIndex = deuEngIndexParser.parseIndex()
            Log.d(TAG, "Deu-Eng index parsed: ${deuEngIndex.size} entries")
            
            val totalEntries = engDeuIndex.size + deuEngIndex.size
            val totalBatches = (totalEntries + BATCH_SIZE - 1) / BATCH_SIZE
            
            // Phase 6: Import English → German entries
            notifyProgress(listener, ImportPhase.IMPORTING_ENTRIES, totalEntries, 0, 0, 0, 0, totalBatches, "Importing English → German entries...")
            
            var processedCount = 0
            var batchNumber = 0
            var entriesToInsert = mutableListOf<DictionaryEntry>()
            
            // Import eng-deu entries
            for ((_, indexEntry) in engDeuIndex) {
                try {
                    val result = processEngDeuEntry(indexEntry)
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
                            notifyProgress(listener, ImportPhase.IMPORTING_ENTRIES, totalEntries, processedCount, successfulEntries, failedEntries, batchNumber, totalBatches, "Importing Eng→Deu: $successfulEntries/$totalEntries")
                        } else {
                            failedEntries += batchSize
                            errors.add("Batch $batchNumber insert failed")
                        }
                        entriesToInsert.clear()
                        yield()
                    }
                    
                } catch (e: Exception) {
                    Log.w(TAG, "Error processing eng-deu entry: ${indexEntry.headword}", e)
                    failedEntries++
                }
            }
            
            // Insert remaining eng-deu entries
            if (entriesToInsert.isNotEmpty()) {
                val batchSize = entriesToInsert.size
                val inserted = insertBatch(entriesToInsert, batchNumber, totalBatches)
                if (inserted) {
                    successfulEntries += batchSize
                    batchNumber++
                } else {
                    failedEntries += batchSize
                }
                entriesToInsert.clear()
            }
            
            // Phase 7: Import German → English entries
            notifyProgress(listener, ImportPhase.IMPORTING_ENTRIES, totalEntries, processedCount, successfulEntries, failedEntries, batchNumber, totalBatches, "Importing German → English entries...")
            
            // Import deu-eng entries
            for ((_, indexEntry) in deuEngIndex) {
                try {
                    val result = processDeuEngEntry(indexEntry)
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
                            notifyProgress(listener, ImportPhase.IMPORTING_ENTRIES, totalEntries, processedCount, successfulEntries, failedEntries, batchNumber, totalBatches, "Importing Deu→Eng: $successfulEntries/$totalEntries")
                        } else {
                            failedEntries += batchSize
                            errors.add("Batch $batchNumber insert failed")
                        }
                        entriesToInsert.clear()
                        yield()
                    }
                    
                } catch (e: Exception) {
                    Log.w(TAG, "Error processing deu-eng entry: ${indexEntry.headword}", e)
                    failedEntries++
                }
            }
            
            // Insert remaining deu-eng entries
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
     * Process English → German entry (English word → German translations)
     */
    private fun processEngDeuEntry(indexEntry: DictdIndexParser.IndexEntry): List<DictionaryEntry>? {
        try {
            val rawText = engDeuFileReader.readBlock(indexEntry.offset, indexEntry.length) ?: return null
            if (rawText.isBlank()) return null
            
            val parsedEntry = dataParser.parse(indexEntry.headword, rawText)
            if (parsedEntry.translations.isEmpty()) return null
            
            val entries = mutableListOf<DictionaryEntry>()
            
            // parsedEntry.translations is now List<Translation> with gender info
            for (translation in parsedEntry.translations) {
                val cleanedTranslation = translation.word
                if (cleanedTranslation.isEmpty()) continue
                
                // Get basic grammar info (verbs, adjectives, etc.)
                val grammarInfo = grammarExtractor.extract(
                    germanWord = cleanedTranslation,
                    englishWord = indexEntry.headword,
                    rawContext = rawText,
                    partOfSpeechTags = parsedEntry.partOfSpeechTags
                )
                
                // PRIORITY 1: Use gender from FreeDict tags/articles (most reliable!)
                // PRIORITY 2: Use common words dictionary (100% accurate for covered words)
                // PRIORITY 3: Use gender detector as fallback
                val finalGender = translation.gender 
                    ?: CommonGermanWords.getGender(cleanedTranslation)
                    ?: run {
                        val genderResult = advancedGenderDetector.detectGender(
                            germanWord = cleanedTranslation,
                            rawContext = rawText,
                            partOfSpeechTags = parsedEntry.partOfSpeechTags
                        )
                        if (genderResult.confidence >= 0.7f) {
                            genderResult.gender
                        } else {
                            grammarInfo.gender
                        }
                    }
                
                // Examples are already extracted by parser with English translations
                val allExamples = parsedEntry.examples
                
                // Additional translations (just the words, not the full Translation objects)
                val additionalTranslations = parsedEntry.translations
                    .filter { it.word != cleanedTranslation }
                    .map { it.word }
                
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
                    additionalTranslations = additionalTranslations,
                    examples = allExamples.take(3), // Limit to 3 best examples
                    pronunciationIpa = parsedEntry.pronunciationIpa,
                    domain = translation.domain ?: parsedEntry.domainLabels.firstOrNull(),
                    rawEntry = rawText.take(500),
                    englishNormalized = TextNormalizer.normalizeEnglish(indexEntry.headword),
                    germanNormalized = TextNormalizer.normalizeGerman(cleanedTranslation),
                    wordLength = cleanedTranslation.length,
                    source = "FreeDict-EngDeu"
                )
                
                entries.add(entry)
            }
            
            return entries
        } catch (e: Exception) {
            Log.w(TAG, "Error processing eng-deu entry: ${indexEntry.headword}", e)
            return null
        }
    }
    
    /**
     * Process German → English entry (German word → English translations)
     * Note: headword is German, translations are English words
     * This is the PRIMARY source for gender information!
     */
    private fun processDeuEngEntry(indexEntry: DictdIndexParser.IndexEntry): List<DictionaryEntry>? {
        try {
            val rawText = deuEngFileReader.readBlock(indexEntry.offset, indexEntry.length) ?: return null
            if (rawText.isBlank()) return null
            
            val parsedEntry = dataParser.parse(indexEntry.headword, rawText)
            if (parsedEntry.translations.isEmpty()) return null
            
            val entries = mutableListOf<DictionaryEntry>()
            
            // For deu-eng: headword is German word, translations are English words
            val germanWord = indexEntry.headword
            
            // Extract gender from German headword in the raw text
            // FreeDict deu-eng has: "Mutter <fem, n, sg>" format
            val headwordGender = extractGenderFromDeuEngHeadword(rawText)
            
            // parsedEntry.translations are English words (no gender)
            for (translation in parsedEntry.translations) {
                val cleanedEnglishWord = translation.word
                if (cleanedEnglishWord.isEmpty()) continue
                
                // Get basic grammar info
                val grammarInfo = grammarExtractor.extract(
                    germanWord = germanWord,
                    englishWord = cleanedEnglishWord,
                    rawContext = rawText,
                    partOfSpeechTags = parsedEntry.partOfSpeechTags
                )
                
                // PRIORITY 1: Use gender from FreeDict headword tags (BEST!)
                // PRIORITY 2: Use common words dictionary (100% accurate for covered words)
                // PRIORITY 3: Use grammar info
                // PRIORITY 4: Use detector as last resort
                val finalGender = headwordGender 
                    ?: CommonGermanWords.getGender(germanWord)
                    ?: grammarInfo.gender 
                    ?: run {
                        val genderResult = advancedGenderDetector.detectGender(
                            germanWord = germanWord,
                            rawContext = rawText,
                            partOfSpeechTags = parsedEntry.partOfSpeechTags
                        )
                        if (genderResult.confidence >= 0.7f) genderResult.gender else null
                    }
                
                // Examples are already extracted by parser
                val allExamples = parsedEntry.examples
                
                // Additional translations (English words)
                val additionalTranslations = parsedEntry.translations
                    .filter { it.word != cleanedEnglishWord }
                    .map { it.word }
                
                val entry = DictionaryEntry(
                    englishWord = cleanedEnglishWord,
                    germanWord = germanWord,
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
                    additionalTranslations = additionalTranslations,
                    examples = allExamples.take(3),
                    pronunciationIpa = parsedEntry.pronunciationIpa,
                    domain = translation.domain ?: parsedEntry.domainLabels.firstOrNull(),
                    rawEntry = rawText.take(500),
                    englishNormalized = TextNormalizer.normalizeEnglish(cleanedEnglishWord),
                    germanNormalized = TextNormalizer.normalizeGerman(germanWord),
                    wordLength = germanWord.length,
                    source = "FreeDict-DeuEng"
                )
                
                entries.add(entry)
            }
            
            return entries
        } catch (e: Exception) {
            Log.w(TAG, "Error processing deu-eng entry: ${indexEntry.headword}", e)
            return null
        }
    }
    
    /**
     * Extract gender from German headword in deu-eng format: "Mutter <fem, n, sg>"
     */
    private fun extractGenderFromDeuEngHeadword(rawText: String): com.hellogerman.app.data.entities.GermanGender? {
        return when {
            rawText.contains("<fem>", ignoreCase = true) -> com.hellogerman.app.data.entities.GermanGender.DIE
            rawText.contains("<masc>", ignoreCase = true) -> com.hellogerman.app.data.entities.GermanGender.DER
            rawText.contains("<neut>", ignoreCase = true) -> com.hellogerman.app.data.entities.GermanGender.DAS
            else -> null
        }
    }
    
    /**
     * Insert batch of dictionary entries
     */
    private suspend fun insertBatch(entries: List<DictionaryEntry>, batchNumber: Int, totalBatches: Int): Boolean {
        return try {
            // Insert dictionary entries
            dictionaryDao.insertEntries(entries)
            Log.d(TAG, "Inserted batch $batchNumber/$totalBatches (${entries.size} entries)")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error inserting batch $batchNumber", e)
            false
        }
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
     * Clear all dictionary data and cached files
     */
    suspend fun clearDictionary() = withContext(Dispatchers.IO) {
        try {
            dictionaryDao.deleteAllEntries()
            engDeuFileReader.clearCache()
            deuEngFileReader.clearCache()
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

