package com.hellogerman.app.data.repository

import android.content.Context
import androidx.room.Room
import com.hellogerman.app.data.database.*
import com.hellogerman.app.data.models.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.*
import java.util.zip.GZIPInputStream
import java.util.zip.GZIPOutputStream
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Comprehensive Offline Caching Manager
 * Handles lesson data compression, caching, and offline storage optimization
 */
@Singleton
class OfflineCacheManager @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private val cacheDir = File(context.cacheDir, "lesson_cache")
    private val compressedCacheDir = File(context.cacheDir, "compressed_cache")

    init {
        // Ensure cache directories exist
        cacheDir.mkdirs()
        compressedCacheDir.mkdirs()
    }

    /**
     * Compress and cache lesson data
     */
    suspend fun compressAndCacheLessonData(lessonId: Int, data: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val compressedFile = File(compressedCacheDir, "lesson_${lessonId}.gz")
                val outputStream = GZIPOutputStream(FileOutputStream(compressedFile))
                outputStream.write(data.toByteArray(Charsets.UTF_8))
                outputStream.close()
                true
            } catch (e: Exception) {
                android.util.Log.e("OfflineCache", "Failed to compress lesson data: ${e.message}")
                false
            }
        }
    }

    /**
     * Decompress and retrieve cached lesson data
     */
    suspend fun getCachedLessonData(lessonId: Int): String? {
        return withContext(Dispatchers.IO) {
            try {
                val compressedFile = File(compressedCacheDir, "lesson_${lessonId}.gz")
                if (!compressedFile.exists()) return@withContext null

                val inputStream = GZIPInputStream(FileInputStream(compressedFile))
                val reader = BufferedReader(InputStreamReader(inputStream, Charsets.UTF_8))
                val data = reader.readText()
                reader.close()
                inputStream.close()
                data
            } catch (e: Exception) {
                android.util.Log.e("OfflineCache", "Failed to decompress lesson data: ${e.message}")
                null
            }
        }
    }

    /**
     * Cache user progress data
     */
    suspend fun cacheUserProgress(progressData: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val progressFile = File(cacheDir, "user_progress.json")
                progressFile.writeText(progressData, Charsets.UTF_8)
                true
            } catch (e: Exception) {
                android.util.Log.e("OfflineCache", "Failed to cache user progress: ${e.message}")
                false
            }
        }
    }

    /**
     * Get cached user progress data
     */
    suspend fun getCachedUserProgress(): String? {
        return withContext(Dispatchers.IO) {
            try {
                val progressFile = File(cacheDir, "user_progress.json")
                if (!progressFile.exists()) return@withContext null
                progressFile.readText(Charsets.UTF_8)
            } catch (e: Exception) {
                android.util.Log.e("OfflineCache", "Failed to read cached user progress: ${e.message}")
                null
            }
        }
    }

    /**
     * Clear old cached data to save space
     */
    suspend fun cleanupOldCache(maxAgeDays: Int = 7): Int {
        return withContext(Dispatchers.IO) {
            try {
                val maxAgeMillis = maxAgeDays * 24 * 60 * 60 * 1000L
                val currentTime = System.currentTimeMillis()
                var deletedCount = 0

                // Clean compressed cache
                compressedCacheDir.listFiles()?.forEach { file ->
                    if (currentTime - file.lastModified() > maxAgeMillis) {
                        if (file.delete()) deletedCount++
                    }
                }

                // Clean regular cache
                cacheDir.listFiles()?.forEach { file ->
                    if (currentTime - file.lastModified() > maxAgeMillis) {
                        if (file.delete()) deletedCount++
                    }
                }

                android.util.Log.d("OfflineCache", "Cleaned up $deletedCount old cache files")
                deletedCount
            } catch (e: Exception) {
                android.util.Log.e("OfflineCache", "Failed to cleanup cache: ${e.message}")
                0
            }
        }
    }

    /**
     * Get cache statistics
     */
    suspend fun getCacheStats(): Map<String, Any> {
        return withContext(Dispatchers.IO) {
            try {
                val compressedFiles = compressedCacheDir.listFiles()?.size ?: 0
                val cacheFiles = cacheDir.listFiles()?.size ?: 0
                val compressedSize = compressedCacheDir.listFiles()?.sumOf { it.length() } ?: 0L
                val cacheSize = cacheDir.listFiles()?.sumOf { it.length() } ?: 0L

                mapOf(
                    "compressed_files" to compressedFiles,
                    "cache_files" to cacheFiles,
                    "compressed_size_kb" to (compressedSize / 1024),
                    "cache_size_kb" to (cacheSize / 1024),
                    "total_size_kb" to ((compressedSize + cacheSize) / 1024)
                )
            } catch (e: Exception) {
                android.util.Log.e("OfflineCache", "Failed to get cache stats: ${e.message}")
                emptyMap()
            }
        }
    }

    /**
     * Clear all cached data
     */
    suspend fun clearAllCache(): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                var success = true
                compressedCacheDir.listFiles()?.forEach { file ->
                    if (!file.delete()) success = false
                }
                cacheDir.listFiles()?.forEach { file ->
                    if (!file.delete()) success = false
                }
                success
            } catch (e: Exception) {
                android.util.Log.e("OfflineCache", "Failed to clear cache: ${e.message}")
                false
            }
        }
    }
}

/**
 * Offline-first dictionary repository with comprehensive German coverage
 * Falls back to APIs only when words aren't found offline
 */
@Singleton
class OfflineDictionaryRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val onlineDictionaryRepository: DictionaryRepository // Fallback to existing online repo
) {
    
    private lateinit var database: GermanDictionaryDatabase
    private var isInitialized = false
    
    suspend fun initialize() {
        if (isInitialized) return
        
        withContext(Dispatchers.IO) {
            android.util.Log.d("OfflineDict", "Initializing offline dictionary database...")
            
            // Initialize Room database
            // First try to create from asset (if asset exists and has data)
            // If asset is empty or missing, create empty database and populate manually
            val assetExists = try {
                context.assets.open("german_dictionary.db").use { it.available() > 0 }
            } catch (e: Exception) {
                android.util.Log.d("OfflineDict", "Asset file not found or empty: ${e.message}")
                false
            }

            database = if (assetExists) {
                android.util.Log.d("OfflineDict", "Creating database from asset file")
                try {
                    val db = Room.databaseBuilder(
                        context,
                        GermanDictionaryDatabase::class.java,
                        "german_dictionary.db"
                    )
                    .createFromAsset("german_dictionary.db")
                    .fallbackToDestructiveMigration()
                    .build()

                    // Test if database is readable
                    val testCount = db.dictionaryDao().getWordCount()
                    android.util.Log.d("OfflineDict", "Asset database loaded successfully, word count: $testCount")

                    db
                } catch (e: Exception) {
                    android.util.Log.e("OfflineDict", "Failed to load asset database: ${e.message}, falling back to empty database")
                    // Fallback to empty database if asset file is corrupted
                    Room.databaseBuilder(
                        context,
                        GermanDictionaryDatabase::class.java,
                        "german_dictionary.db"
                    )
                    .fallbackToDestructiveMigration()
                    .build()
                }
            } else {
                android.util.Log.d("OfflineDict", "Asset file empty/missing, creating empty database")
                Room.databaseBuilder(
                    context,
                    GermanDictionaryDatabase::class.java,
                    "german_dictionary.db"
                )
                .fallbackToDestructiveMigration()
                .build()
            }
            
            // Populate database if empty
            val wordCount = database.dictionaryDao().getWordCount()
            android.util.Log.d("OfflineDict", "Current word count in database: $wordCount")
            
            if (wordCount == 0) {
                android.util.Log.d("OfflineDict", "Database is empty, populating with essential German words...")
                try {
                    populateDatabase()
                    val newWordCount = database.dictionaryDao().getWordCount()
                    android.util.Log.d("OfflineDict", "After population, word count: $newWordCount")
                } catch (e: Exception) {
                    android.util.Log.e("OfflineDict", "Failed to populate database: ${e.message}", e)
                }
            } else {
                android.util.Log.d("OfflineDict", "Database already contains $wordCount words")
            }
            
            isInitialized = true
            android.util.Log.d("OfflineDict", "Offline dictionary initialization complete")
        }
    }
    
    /**
     * Main search function - offline first, API fallback
     */
    suspend fun searchWord(request: DictionarySearchRequest): Result<DictionarySearchResult> {
        return try {
            if (!isInitialized) initialize()
            
            val word = request.word.lowercase().trim()
            
            // Debug logging
            android.util.Log.d("OfflineDict", "Searching for word: $word")
            
            // 1. Try offline database first
            val offlineResult = searchOfflineDatabase(word)
            android.util.Log.d("OfflineDict", "Offline result hasResults: ${offlineResult.hasResults}, gender: ${offlineResult.gender}")
            
            if (offlineResult.hasResults) {
                // If offline has no translations, try to fetch and merge online translations
                val mergedResult = if (offlineResult.translations.isEmpty()) {
                    try {
                        android.util.Log.d("OfflineDict", "Offline hit without translations. Fetching online translations for: $word")
                        val online = onlineDictionaryRepository.searchWord(
                            DictionarySearchRequest(
                                word = word,
                                fromLang = request.fromLang,
                                toLang = request.toLang
                            )
                        ).getOrNull()

                        val translations = online?.translations ?: emptyList()
                        if (translations.isNotEmpty()) {
                            android.util.Log.d("OfflineDict", "Merging ${translations.size} online translations into offline result for: $word")
                            offlineResult.copy(translations = translations)
                        } else {
                            offlineResult
                        }
                    } catch (e: Exception) {
                        android.util.Log.w("OfflineDict", "Failed to fetch online translations: ${e.message}")
                        offlineResult
                    }
                } else offlineResult

                android.util.Log.d("OfflineDict", "Returning merged offline result for: $word")
                return Result.success(mergedResult)
            }
            
            // 2. Try compound word analysis (German specialty)
            val compoundResult = analyzeCompoundWord(word)
            if (compoundResult.hasResults) {
                android.util.Log.d("OfflineDict", "Returning compound result for: $word")
                return Result.success(compoundResult)
            }
            
            // 3. Fallback to online APIs only if offline fails
            android.util.Log.d("OfflineDict", "Falling back to online APIs for: $word")
            return onlineDictionaryRepository.searchWord(request)
            
        } catch (e: Exception) {
            android.util.Log.e("OfflineDict", "Error searching for ${request.word}", e)
            // Even on error, try to return something useful
            val basicResult = createBasicResult(request.word)
            Result.success(basicResult)
        }
    }
    
    private suspend fun searchOfflineDatabase(word: String): DictionarySearchResult {
        val dao = database.dictionaryDao()
        
        // Get word data
        val wordEntity = dao.getWord(word)
        val examples = dao.getExamples(word)
        
        return if (wordEntity != null) {
            // Convert database entities to result
            val definitions = DictionaryConverters().toDefinitionsList(wordEntity.definitions)
            val examplesList = examples.map { 
                Example(it.germanSentence, it.englishTranslation) 
            }
            
            DictionarySearchResult(
                originalWord = word,
                fromLanguage = "de",
                toLanguage = "en",
                hasResults = true,
                definitions = definitions,
                examples = examplesList,
                wordType = wordEntity.wordType,
                gender = wordEntity.gender,
                pronunciation = wordEntity.pronunciation?.let { 
                    Pronunciation(it, "de")
                },
                difficulty = wordEntity.level
            )
        } else {
            // Empty result
            DictionarySearchResult(
                originalWord = word,
                fromLanguage = "de",
                toLanguage = "en",
                hasResults = false
            )
        }
    }
    
    /**
     * Analyze German compound words (Komposita)
     * e.g., "hausregeln" = "haus" + "regeln"
     */
    private suspend fun analyzeCompoundWord(word: String): DictionarySearchResult {
        if (word.length < 6) return createEmptyResult(word)
        
        val dao = database.dictionaryDao()
        
        // Try common compound patterns
        for (splitPoint in 3..word.length-3) {
            val firstPart = word.substring(0, splitPoint)
            val secondPart = word.substring(splitPoint)
            
            val firstWord = dao.getWord(firstPart)
            val secondWord = dao.getWord(secondPart)
            
            if (firstWord != null && secondWord != null) {
                // Found compound word components
                val combinedDefinition = "Compound word: ${firstWord.wordType} + ${secondWord.wordType}"
                
                return DictionarySearchResult(
                    originalWord = word,
                    fromLanguage = "de",
                    toLanguage = "en",
                    hasResults = true,
                    definitions = listOf(
                        Definition(combinedDefinition, "compound"),
                        Definition("First part: ${firstPart}", secondWord.wordType),
                        Definition("Second part: ${secondPart}", secondWord.wordType)
                    ),
                    examples = listOf(
                        Example("$word ist ein zusammengesetztes Wort.", "$word is a compound word.")
                    ),
                    wordType = "compound",
                    etymology = "Compound of $firstPart + $secondPart"
                )
            }
        }
        
        return createEmptyResult(word)
    }
    
    private fun createBasicResult(word: String): DictionarySearchResult {
        return DictionarySearchResult(
            originalWord = word,
            fromLanguage = "de",
            toLanguage = "en",
            hasResults = true,
            definitions = listOf(
                Definition("German word: $word", "unknown"),
                Definition("No offline definition available", "note")
            ),
            examples = listOf(
                Example("Suche nach '$word' online für mehr Informationen.", "Search for '$word' online for more information.")
            )
        )
    }
    
    private fun createEmptyResult(word: String): DictionarySearchResult {
        return DictionarySearchResult(
            originalWord = word,
            fromLanguage = "de",
            toLanguage = "en",
            hasResults = false
        )
    }
    
    /**
     * Populate database with comprehensive German data
     */
    private suspend fun populateDatabase() {
        val dao = database.dictionaryDao()
        val converter = DictionaryConverters()
        
        // Get essential German words
        val essentialWords = ComprehensiveGermanData.getEssentialGermanWords()
        
        // Convert to database entities
        val wordEntities = essentialWords.map { wordData ->
            val definitions = wordData.definition.split(";").map { def ->
                Definition(def.trim(), wordData.wordType, wordData.level)
            }
            
            OfflineWordEntity(
                word = wordData.word,
                definitions = converter.fromDefinitionsList(definitions),
                wordType = wordData.wordType,
                gender = wordData.gender,
                frequency = wordData.frequency,
                level = wordData.level,
                pronunciation = wordData.pronunciation,
                etymology = wordData.etymology
            )
        }
        
        // Create example entities
        val exampleEntities = essentialWords.flatMap { wordData ->
            wordData.germanExamples.zip(wordData.englishTranslations).map { (german, english) ->
                OfflineExampleEntity(
                    word = wordData.word,
                    germanSentence = german,
                    englishTranslation = english,
                    difficulty = wordData.level
                )
            }
        }
        
        // Insert into database
        dao.insertWords(wordEntities)
        dao.insertExamples(exampleEntities)
    }
    
    /**
     * Get word suggestions for autocomplete
     */
    suspend fun getWordSuggestions(prefix: String): List<String> {
        if (!isInitialized) return emptyList()
        
        return try {
            val suggestions = database.dictionaryDao().searchWords("$prefix%")
            suggestions.take(10).map { it.word }
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    /**
     * Get words by CEFR level
     */
    suspend fun getWordsByLevel(level: String): List<String> {
        if (!isInitialized) return emptyList()
        
        return try {
            val words = database.dictionaryDao().getWordsByLevel(level)
            words.map { it.word }
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    /**
     * Get database statistics
     */
    suspend fun getDatabaseStats(): DatabaseStats {
        if (!isInitialized) initialize()

        return try {
            val totalWords = database.dictionaryDao().getWordCount()
            DatabaseStats(
                totalWords = totalWords,
                offlineCapable = true,
                lastUpdated = System.currentTimeMillis()
            )
        } catch (e: Exception) {
            DatabaseStats(0, false, 0)
        }
    }

    /**
     * Force reset and repopulate the database
     * Useful for troubleshooting or updating dictionary data
     */
    suspend fun resetDatabase() {
        android.util.Log.d("OfflineDict", "Forcing database reset...")

        withContext(Dispatchers.IO) {
            try {
                // Close current database
                database.close()

                // Delete the database file
                val dbFile = context.getDatabasePath("german_dictionary.db")
                if (dbFile.exists()) {
                    dbFile.delete()
                    android.util.Log.d("OfflineDict", "Deleted existing database file")
                }

                // Reset initialization flag
                isInitialized = false

                // Re-initialize (this will create a new empty database and populate it)
                initialize()

                android.util.Log.d("OfflineDict", "Database reset complete")

            } catch (e: Exception) {
                android.util.Log.e("OfflineDict", "Failed to reset database: ${e.message}", e)
                throw e
            }
        }
    }
}

data class DatabaseStats(
    val totalWords: Int,
    val offlineCapable: Boolean,
    val lastUpdated: Long
)
