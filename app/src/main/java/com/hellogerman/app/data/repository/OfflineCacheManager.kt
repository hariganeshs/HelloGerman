package com.hellogerman.app.data.repository

import android.content.Context
import android.util.Log
import java.io.*
import java.util.zip.GZIPInputStream
import java.util.zip.GZIPOutputStream

class OfflineCacheManager(private val context: Context) {

    companion object {
        private const val TAG = "OfflineCacheManager"
        private const val CACHE_DIR = "lesson_cache"
        private const val ALL_LESSONS_KEY = "all_lessons"
    }

    private val cacheDir: File = File(context.cacheDir, CACHE_DIR).apply {
        if (!exists()) mkdirs()
    }

    /**
     * Compresses and caches lesson data
     * @param lessonId The lesson ID (-1 for all lessons)
     * @param jsonData The JSON data to cache
     * @return true if caching was successful, false otherwise
     */
    fun compressAndCacheLessonData(lessonId: Int, jsonData: String): Boolean {
        return try {
            val fileName = if (lessonId == -1) ALL_LESSONS_KEY else "lesson_$lessonId"
            val cacheFile = File(cacheDir, "$fileName.gz")

            FileOutputStream(cacheFile).use { fos ->
                GZIPOutputStream(fos).use { gzos ->
                    OutputStreamWriter(gzos, Charsets.UTF_8).use { writer ->
                        writer.write(jsonData)
                    }
                }
            }

            Log.d(TAG, "Successfully cached lesson data for lessonId: $lessonId")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Failed to cache lesson data for lessonId: $lessonId", e)
            false
        }
    }

    /**
     * Retrieves cached lesson data
     * @param lessonId The lesson ID (-1 for all lessons)
     * @return The cached JSON data or null if not found
     */
    fun getCachedLessonData(lessonId: Int): String? {
        return try {
            val fileName = if (lessonId == -1) ALL_LESSONS_KEY else "lesson_$lessonId"
            val cacheFile = File(cacheDir, "$fileName.gz")

            if (!cacheFile.exists()) {
                Log.d(TAG, "Cache file not found for lessonId: $lessonId")
                return null
            }

            FileInputStream(cacheFile).use { fis ->
                GZIPInputStream(fis).use { gzis ->
                    InputStreamReader(gzis, Charsets.UTF_8).use { reader ->
                        reader.readText()
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to retrieve cached lesson data for lessonId: $lessonId", e)
            null
        }
    }

    /**
     * Clears all cached lesson data
     */
    fun clearCache(): Boolean {
        return try {
            cacheDir.listFiles()?.forEach { file ->
                if (file.name.endsWith(".gz")) {
                    file.delete()
                }
            }
            Log.d(TAG, "Cache cleared successfully")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Failed to clear cache", e)
            false
        }
    }

    /**
     * Checks if cached data exists for a lesson
     * @param lessonId The lesson ID (-1 for all lessons)
     * @return true if cached data exists, false otherwise
     */
    fun hasCachedData(lessonId: Int): Boolean {
        val fileName = if (lessonId == -1) ALL_LESSONS_KEY else "lesson_$lessonId"
        val cacheFile = File(cacheDir, "$fileName.gz")
        return cacheFile.exists()
    }
}