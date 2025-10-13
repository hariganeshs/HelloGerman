package com.hellogerman.app.data.dao

import androidx.room.*
import com.hellogerman.app.data.entities.DictionaryCache
import kotlinx.coroutines.flow.Flow

@Dao
interface DictionaryCacheDao {

    @Query("SELECT * FROM dictionary_cache WHERE word = :word LIMIT 1")
    suspend fun getCachedResult(word: String): DictionaryCache?

    @Query("SELECT * FROM dictionary_cache WHERE word = :word AND fromLanguage = :fromLanguage AND toLanguage = :toLanguage LIMIT 1")
    suspend fun getCachedResult(word: String, fromLanguage: String, toLanguage: String): DictionaryCache?

    @Query("SELECT * FROM dictionary_cache WHERE expiresAt > :currentTime")
    fun getValidCachedResults(currentTime: Long): Flow<List<DictionaryCache>>

    @Query("SELECT COUNT(*) FROM dictionary_cache")
    suspend fun getCacheSize(): Int

    @Query("DELETE FROM dictionary_cache WHERE expiresAt <= :currentTime")
    suspend fun deleteExpiredEntries(currentTime: Long)

    @Query("DELETE FROM dictionary_cache WHERE word = :word")
    suspend fun deleteCacheEntry(word: String)

    @Query("DELETE FROM dictionary_cache")
    suspend fun clearAllCache()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCacheEntry(cacheEntry: DictionaryCache)

    @Update
    suspend fun updateCacheEntry(cacheEntry: DictionaryCache)

    @Query("SELECT * FROM dictionary_cache ORDER BY fetchedAt DESC LIMIT :limit")
    fun getRecentEntries(limit: Int): Flow<List<DictionaryCache>>

    @Query("SELECT * FROM dictionary_cache WHERE cacheVersion < :version")
    suspend fun getOutdatedEntries(version: Int): List<DictionaryCache>
}