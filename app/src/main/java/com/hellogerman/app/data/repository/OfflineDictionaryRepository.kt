package com.hellogerman.app.data.repository

import android.content.Context
import com.hellogerman.app.data.dictionary.FreedictReader
import com.hellogerman.app.data.dictionary.GermanDictionary
import com.hellogerman.app.data.models.*
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
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

                val inputStream = java.util.zip.GZIPInputStream(FileInputStream(compressedFile))
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

    private lateinit var deReader: FreedictReader
    private lateinit var enReader: FreedictReader
    private var isInitialized = false

    suspend fun initialize() {
        if (isInitialized) return

        withContext(Dispatchers.IO) {
            android.util.Log.d("OfflineDict", "Initializing FreeDict readers...")

            deReader = FreedictReader.buildGermanToEnglish(context)
            enReader = FreedictReader.buildEnglishToGerman(context)

            deReader.initializeIfNeeded()
            enReader.initializeIfNeeded()

            android.util.Log.d(
                "OfflineDict",
                "Initialized FreeDict: deu-eng size=${deReader.size()}, eng-deu size=${enReader.size()}"
            )

            isInitialized = true
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
            
            // 1. Try offline FreeDict first
            val offlineResult = searchOfflineFreedict(word, request.fromLang, request.toLang)
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
            
            // 2. Optional: compound analysis could be added later if needed
            
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
    
    private fun searchOfflineFreedict(word: String, fromLang: String, toLang: String): DictionarySearchResult {
        val isGerman = fromLang.lowercase() in listOf("de", "german")
        val reader = if (isGerman) deReader else enReader
        val entry = reader.lookupExact(word)

        if (entry == null) {
            return DictionarySearchResult(
                originalWord = word,
                fromLanguage = fromLang,
                toLanguage = toLang,
                hasResults = false
            )
        }

        // Build definitions from translations (lightweight)
        val defs = entry.translations.map { t -> Definition(meaning = t, partOfSpeech = null, level = null) }

        // Prefer explicit gender parsed from FreeDict raw entry when available
        // This captures <masc>/<fem>/<neut> tags present in both deu→eng and eng→deu datasets
        val explicitGender = entry.gender

        // Fallback heuristic for EN→DE: infer from first translation article (if explicit not found)
        val heuristicGender = if (!isGerman && explicitGender == null) {
            entry.translations.firstOrNull()?.let { t ->
                when {
                    Regex("\\bder\\b", RegexOption.IGNORE_CASE).containsMatchIn(t) -> "der"
                    Regex("\\bdie\\b", RegexOption.IGNORE_CASE).containsMatchIn(t) -> "die"
                    Regex("\\bdas\\b", RegexOption.IGNORE_CASE).containsMatchIn(t) -> "das"
                    else -> null
                }
            }
        } else null

        // Determine basic word type
        val inferredWordType: String? = when {
            (explicitGender ?: heuristicGender) != null -> "noun"
            // Rough heuristic: many German infinitives end with "en" (gehen, machen)
            isGerman && word.length > 3 && word.endsWith("en") -> "verb"
            else -> null
        }

        return DictionarySearchResult(
            originalWord = word,
            translations = entry.translations,
            fromLanguage = fromLang,
            toLanguage = toLang,
            hasResults = entry.translations.isNotEmpty(),
            definitions = defs,
            gender = explicitGender ?: heuristicGender,
            wordType = inferredWordType
        )
    }
    
    // Compound analysis: intentionally omitted in first FreeDict migration
    
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
    
    // Population of Room DB: removed — FreeDict is now the primary source
    
    /**
     * Get word suggestions for autocomplete
     */
    suspend fun getWordSuggestions(prefix: String): List<String> {
        if (!isInitialized) return emptyList()
        return try {
            val de = deReader.suggest(prefix, 10)
            if (de.isNotEmpty()) return de
            enReader.suggest(prefix, 10)
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    /**
     * Get words by CEFR level
     */
    suspend fun getWordsByLevel(level: String): List<String> {
        // Not supported by FreeDict — return empty
        return emptyList()
    }
    
    /**
     * Get database statistics
     */
    suspend fun getDatabaseStats(): DatabaseStats {
        if (!isInitialized) initialize()
        val total = deReader.size() + enReader.size()
        return DatabaseStats(
            totalWords = total,
            offlineCapable = true,
            lastUpdated = System.currentTimeMillis()
        )
    }

    /**
     * Force reset and repopulate the database
     * Useful for troubleshooting or updating dictionary data
     */
    suspend fun resetDatabase() {
        android.util.Log.d("OfflineDict", "Resetting FreeDict caches...")
        withContext(Dispatchers.IO) {
            try {
                if (this@OfflineDictionaryRepository::deReader.isInitialized) deReader.clearCache()
                if (this@OfflineDictionaryRepository::enReader.isInitialized) enReader.clearCache()
                isInitialized = false
                initialize()
                android.util.Log.d("OfflineDict", "FreeDict reset complete")
            } catch (e: Exception) {
                android.util.Log.e("OfflineDict", "Failed to reset FreeDict: ${e.message}", e)
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
