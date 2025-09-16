package com.hellogerman.app.data.dao

import androidx.room.*
import com.hellogerman.app.data.entities.UserVocabulary
import kotlinx.coroutines.flow.Flow

@Dao
interface UserVocabularyDao {
    
    @Query("SELECT * FROM user_vocabulary ORDER BY addedAt DESC")
    fun getAllVocabulary(): Flow<List<UserVocabulary>>
    
    @Query("SELECT * FROM user_vocabulary WHERE word = :word LIMIT 1")
    suspend fun getVocabularyByWord(word: String): UserVocabulary?
    
    @Query("SELECT * FROM user_vocabulary WHERE isFavorite = 1 ORDER BY addedAt DESC")
    fun getFavoriteVocabulary(): Flow<List<UserVocabulary>>
    
    @Query("SELECT * FROM user_vocabulary WHERE level = :level ORDER BY addedAt DESC")
    fun getVocabularyByLevel(level: String): Flow<List<UserVocabulary>>
    
    @Query("SELECT * FROM user_vocabulary WHERE category = :category ORDER BY addedAt DESC")
    fun getVocabularyByCategory(category: String): Flow<List<UserVocabulary>>
    
    @Query("SELECT * FROM user_vocabulary WHERE masteryLevel < :threshold ORDER BY lastReviewed ASC, addedAt ASC")
    fun getVocabularyForReview(threshold: Int = 3): Flow<List<UserVocabulary>>
    
    @Query("SELECT COUNT(*) FROM user_vocabulary")
    suspend fun getVocabularyCount(): Int
    
    @Query("SELECT COUNT(*) FROM user_vocabulary WHERE isFavorite = 1")
    suspend fun getFavoriteCount(): Int
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVocabulary(vocabulary: UserVocabulary)
    
    @Update
    suspend fun updateVocabulary(vocabulary: UserVocabulary)
    
    @Delete
    suspend fun deleteVocabulary(vocabulary: UserVocabulary)
    
    @Query("DELETE FROM user_vocabulary WHERE word = :word")
    suspend fun deleteVocabularyByWord(word: String)
    
    @Query("UPDATE user_vocabulary SET masteryLevel = :level WHERE word = :word")
    suspend fun updateMasteryLevel(word: String, level: Int)
    
    @Query("UPDATE user_vocabulary SET isFavorite = :isFavorite WHERE word = :word")
    suspend fun updateFavoriteStatus(word: String, isFavorite: Boolean)
    
    @Query("UPDATE user_vocabulary SET lastReviewed = :timestamp, reviewCount = reviewCount + 1 WHERE word = :word")
    suspend fun markAsReviewed(word: String, timestamp: Long = System.currentTimeMillis())
    
    @Query("SELECT DISTINCT category FROM user_vocabulary WHERE category IS NOT NULL")
    suspend fun getAvailableCategories(): List<String>
    
    @Query("SELECT DISTINCT level FROM user_vocabulary WHERE level IS NOT NULL")
    suspend fun getAvailableLevels(): List<String>
}
