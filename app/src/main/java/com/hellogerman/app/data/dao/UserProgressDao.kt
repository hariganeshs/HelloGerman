package com.hellogerman.app.data.dao

import androidx.room.*
import com.hellogerman.app.data.entities.UserProgress
import kotlinx.coroutines.flow.Flow

@Dao
interface UserProgressDao {
    
    @Query("SELECT * FROM user_progress WHERE id = 1")
    fun getUserProgress(): Flow<UserProgress?>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserProgress(userProgress: UserProgress)
    
    @Update
    suspend fun updateUserProgress(userProgress: UserProgress)
    
    @Query("UPDATE user_progress SET currentLevel = :level WHERE id = 1")
    suspend fun updateCurrentLevel(level: String)
    
    @Query("UPDATE user_progress SET lesenScore = :score WHERE id = 1")
    suspend fun updateLesenScore(score: Int)
    
    @Query("UPDATE user_progress SET hoerenScore = :score WHERE id = 1")
    suspend fun updateHoerenScore(score: Int)
    
    @Query("UPDATE user_progress SET schreibenScore = :score WHERE id = 1")
    suspend fun updateSchreibenScore(score: Int)
    
    @Query("UPDATE user_progress SET sprechenScore = :score WHERE id = 1")
    suspend fun updateSprechenScore(score: Int)
    
    @Query("UPDATE user_progress SET totalLessonsCompleted = totalLessonsCompleted + 1 WHERE id = 1")
    suspend fun incrementLessonsCompleted()
    
    @Query("UPDATE user_progress SET currentStreak = :streak, longestStreak = CASE WHEN :streak > longestStreak THEN :streak ELSE longestStreak END WHERE id = 1")
    suspend fun updateStreak(streak: Int)
    
    @Query("UPDATE user_progress SET lastStudyDate = :date WHERE id = 1")
    suspend fun updateLastStudyDate(date: Long)
}
