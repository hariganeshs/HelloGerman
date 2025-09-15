package com.hellogerman.app.data.dao

import androidx.room.*
import com.hellogerman.app.data.entities.DictionaryCacheEntry
import kotlinx.coroutines.flow.Flow

/**
 * DAO for dictionary cache operations
 */
@Dao
interface DictionaryCacheDao {
    
    @Query("SELECT * FROM dictionary_cache WHERE word = :word AND fromLanguage = :fromLanguage AND toLanguage = :toLanguage AND expiresAt > :currentTime")
    suspend fun getCachedEntry(word: String, fromLanguage: String, toLanguage: String, currentTime: Long = System.currentTimeMillis()): DictionaryCacheEntry?
    
    @Query("SELECT * FROM dictionary_cache WHERE word = :word AND fromLanguage = :fromLanguage AND toLanguage = :toLanguage")
    fun getCachedEntryFlow(word: String, fromLanguage: String, toLanguage: String): Flow<DictionaryCacheEntry?>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCacheEntry(entry: DictionaryCacheEntry)
    
    @Query("DELETE FROM dictionary_cache WHERE expiresAt < :currentTime")
    suspend fun deleteExpiredEntries(currentTime: Long = System.currentTimeMillis())
    
    @Query("DELETE FROM dictionary_cache WHERE word = :word AND fromLanguage = :fromLanguage AND toLanguage = :toLanguage")
    suspend fun deleteCacheEntry(word: String, fromLanguage: String, toLanguage: String)
    
    @Query("DELETE FROM dictionary_cache")
    suspend fun clearAllCache()
    
    @Query("SELECT COUNT(*) FROM dictionary_cache")
    suspend fun getCacheSize(): Int
    
    @Query("SELECT * FROM dictionary_cache ORDER BY fetchedAt DESC LIMIT :limit")
    suspend fun getRecentEntries(limit: Int = 50): List<DictionaryCacheEntry>
    
    @Query("SELECT * FROM dictionary_cache WHERE word LIKE :query OR sources LIKE :query ORDER BY fetchedAt DESC LIMIT :limit")
    suspend fun searchCacheEntries(query: String, limit: Int = 20): List<DictionaryCacheEntry>
}
