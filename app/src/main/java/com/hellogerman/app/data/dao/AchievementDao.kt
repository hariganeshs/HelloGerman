package com.hellogerman.app.data.dao

import androidx.room.*
import com.hellogerman.app.data.entities.*
import kotlinx.coroutines.flow.Flow

@Dao
interface AchievementDao {
    
    @Query("SELECT * FROM achievements ORDER BY rarity DESC, points DESC")
    fun getAllAchievements(): Flow<List<Achievement>>
    
    @Query("SELECT * FROM achievements WHERE isUnlocked = 1 ORDER BY unlockedAt DESC")
    fun getUnlockedAchievements(): Flow<List<Achievement>>
    
    @Query("SELECT * FROM achievements WHERE isUnlocked = 0")
    fun getLockedAchievements(): Flow<List<Achievement>>
    
    @Query("SELECT * FROM achievements WHERE category = :category")
    fun getAchievementsByCategory(category: AchievementCategory): Flow<List<Achievement>>
    
    @Query("SELECT * FROM achievements WHERE id = :id")
    suspend fun getAchievementById(id: String): Achievement?
    
    @Update
    suspend fun updateAchievement(achievement: Achievement)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAchievements(achievements: List<Achievement>)
    
    @Query("UPDATE achievements SET isUnlocked = 1, unlockedAt = :timestamp WHERE id = :achievementId")
    suspend fun unlockAchievement(achievementId: String, timestamp: Long)
    
    @Query("UPDATE achievements SET progress = :progress WHERE id = :achievementId")
    suspend fun updateProgress(achievementId: String, progress: Int)
    
    @Query("SELECT COUNT(*) FROM achievements WHERE isUnlocked = 1")
    suspend fun getUnlockedCount(): Int
    
    @Query("SELECT COUNT(*) FROM achievements WHERE isUnlocked = 1 AND rarity = :rarity")
    suspend fun getUnlockedCountByRarity(rarity: AchievementRarity): Int
}

@Dao
interface UserLevelDao {
    
    @Query("SELECT * FROM user_level WHERE id = 1")
    fun getUserLevel(): Flow<UserLevel>
    
    @Query("SELECT * FROM user_level WHERE id = 1")
    suspend fun getUserLevelSync(): UserLevel?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserLevel(userLevel: UserLevel)
    
    @Query("UPDATE user_level SET totalXP = totalXP + :xp WHERE id = 1")
    suspend fun addXP(xp: Long)
    
    @Query("UPDATE user_level SET level = :level, currentLevelXP = :currentXP, nextLevelXP = :nextXP, title = :title WHERE id = 1")
    suspend fun updateLevel(level: Int, currentXP: Long, nextXP: Long, title: String)
    
    @Query("UPDATE user_level SET prestige = prestige + 1, level = 1, currentLevelXP = 0, nextLevelXP = 100 WHERE id = 1")
    suspend fun prestige()
}

@Dao
interface DailyChallengeDao {
    
    @Query("SELECT * FROM daily_challenges WHERE date = :date")
    suspend fun getChallengesForDate(date: String): List<DailyChallenge>
    
    @Query("SELECT * FROM daily_challenges WHERE date = :date")
    fun getChallengesForDateFlow(date: String): Flow<List<DailyChallenge>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChallenges(challenges: List<DailyChallenge>)
    
    @Query("UPDATE daily_challenges SET currentProgress = :progress, isCompleted = :isCompleted WHERE date = :date AND challengeType = :type")
    suspend fun updateChallengeProgress(date: String, type: ChallengeType, progress: Int, isCompleted: Boolean)
    
    @Query("SELECT COUNT(*) FROM daily_challenges WHERE isCompleted = 1 AND date = :date")
    suspend fun getCompletedChallengesCount(date: String): Int
    
    @Query("DELETE FROM daily_challenges WHERE date < :cutoffDate")
    suspend fun deleteOldChallenges(cutoffDate: String)
}

@Dao
interface UserStatsDao {
    
    @Query("SELECT * FROM user_stats WHERE id = 1")
    fun getUserStats(): Flow<UserStats>
    
    @Query("SELECT * FROM user_stats WHERE id = 1")
    suspend fun getUserStatsSync(): UserStats?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserStats(userStats: UserStats)
    
    @Query("UPDATE user_stats SET totalLessonsCompleted = totalLessonsCompleted + 1 WHERE id = 1")
    suspend fun incrementLessonsCompleted()
    
    @Query("UPDATE user_stats SET totalQuizzesCompleted = totalQuizzesCompleted + 1 WHERE id = 1")
    suspend fun incrementQuizzesCompleted()
    
    @Query("UPDATE user_stats SET totalTimeSpent = totalTimeSpent + :timeSpent WHERE id = 1")
    suspend fun addTimeSpent(timeSpent: Long)
    
    @Query("UPDATE user_stats SET totalPoints = totalPoints + :points WHERE id = 1")
    suspend fun addPoints(points: Long)
    
    @Query("UPDATE user_stats SET totalCoins = totalCoins + :coins WHERE id = 1")
    suspend fun addCoins(coins: Int)
    
    @Query("UPDATE user_stats SET currentStreak = :streak, longestStreak = CASE WHEN :streak > longestStreak THEN :streak ELSE longestStreak END WHERE id = 1")
    suspend fun updateStreak(streak: Int)
    
    @Query("UPDATE user_stats SET perfectQuizzes = perfectQuizzes + 1 WHERE id = 1")
    suspend fun incrementPerfectQuizzes()
    
    @Query("UPDATE user_stats SET fastCompletions = fastCompletions + 1 WHERE id = 1")
    suspend fun incrementFastCompletions()
    
    @Query("UPDATE user_stats SET averageAccuracy = :accuracy WHERE id = 1")
    suspend fun updateAverageAccuracy(accuracy: Float)
    
    @Query("UPDATE user_stats SET lastActiveDate = :date WHERE id = 1")
    suspend fun updateLastActiveDate(date: Long)
}
