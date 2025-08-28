package com.hellogerman.app.gamification

import android.content.Context
import com.hellogerman.app.data.HelloGermanDatabase
import com.hellogerman.app.data.entities.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.math.*

class GameificationManager(context: Context) {
    
    private val database = HelloGermanDatabase.getDatabase(context)
    private val achievementDao = database.achievementDao()
    private val userLevelDao = database.userLevelDao()
    private val dailyChallengeDao = database.dailyChallengeDao()
    private val userStatsDao = database.userStatsDao()
    
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    
    // Initialize default achievements and user data
    suspend fun initializeGameification() {
        // Initialize user level if not exists
        if (userLevelDao.getUserLevelSync() == null) {
            userLevelDao.insertUserLevel(UserLevel())
        }
        
        // Initialize user stats if not exists
        if (userStatsDao.getUserStatsSync() == null) {
            userStatsDao.insertUserStats(UserStats())
        }
        
        // Initialize achievements
        achievementDao.insertAchievements(getDefaultAchievements())
        
        // Generate today's challenges
        generateDailyChallenges()
    }
    
    // Award XP and handle level ups
    suspend fun awardXP(amount: Long, reason: String = ""): LevelUpResult? {
        val currentLevel = userLevelDao.getUserLevelSync() ?: UserLevel()
        
        // Add XP
        userLevelDao.addXP(amount)
        userStatsDao.addPoints(amount)
        
        val newTotalXP = currentLevel.totalXP + amount
        val newCurrentLevelXP = currentLevel.currentLevelXP + amount
        
        // Check for level up
        if (newCurrentLevelXP >= currentLevel.nextLevelXP) {
            val newLevel = currentLevel.level + 1
            val remainingXP = newCurrentLevelXP - currentLevel.nextLevelXP
            val nextLevelXP = calculateNextLevelXP(newLevel)
            val newTitle = getLevelTitle(newLevel)
            
            userLevelDao.updateLevel(newLevel, remainingXP, nextLevelXP, newTitle)
            
            // Award level up bonus
            val levelUpBonus = newLevel * 50
            userStatsDao.addCoins(levelUpBonus)
            
            return LevelUpResult(
                newLevel = newLevel,
                newTitle = newTitle,
                coinsAwarded = levelUpBonus,
                xpGained = amount
            )
        }
        
        return null
    }
    
    // Award points and trigger achievement checks
    suspend fun awardPoints(points: Int, context: PointContext) {
        userStatsDao.addPoints(points.toLong())
        
        // Update daily challenges
        updateDailyChallengeProgress(ChallengeType.SCORE_POINTS, points)
        
        // Check for achievements
        checkAndUnlockAchievements(context)
    }
    
    // Complete a lesson and award appropriate XP/points
    suspend fun completeLessonz(
        skill: String,
        level: String,
        score: Int,
        timeSpent: Int,
        accuracy: Float,
        isPerfect: Boolean = false
    ): GameReward {
        // Base XP calculation
        var baseXP = when (level) {
            "A1" -> 50L
            "A2" -> 75L
            "B1" -> 100L
            "B2" -> 150L
            "C1" -> 200L
            "C2" -> 300L
            else -> 50L
        }
        
        // Score multiplier
        val scoreMultiplier = (score / 100f).coerceAtLeast(0.5f)
        baseXP = (baseXP * scoreMultiplier).toLong()
        
        // Speed bonus
        val speedBonus = if (timeSpent < 120) 25L else if (timeSpent < 300) 10L else 0L
        
        // Perfect bonus
        val perfectBonus = if (isPerfect) 50L else 0L
        
        val totalXP = baseXP + speedBonus + perfectBonus
        val coinsEarned = (totalXP / 10).toInt()
        
        // Update stats
        userStatsDao.incrementLessonsCompleted()
        userStatsDao.addTimeSpent(timeSpent.toLong())
        if (isPerfect) userStatsDao.incrementPerfectQuizzes()
        if (timeSpent < 120) userStatsDao.incrementFastCompletions()
        
        // Update daily challenges
        updateDailyChallengeProgress(ChallengeType.COMPLETE_LESSONS, 1)
        if (isPerfect) updateDailyChallengeProgress(ChallengeType.PERFECT_STREAK, 1)
        if (timeSpent < 60) updateDailyChallengeProgress(ChallengeType.SPEED_DEMON, 1)
        
        // Award XP and check for level up
        val levelUpResult = awardXP(totalXP, "Lesson completed")
        userStatsDao.addCoins(coinsEarned)
        
        // Check achievements
        checkAndUnlockAchievements(PointContext.LESSON_COMPLETED)
        
        return GameReward(
            xpGained = totalXP,
            coinsEarned = coinsEarned,
            levelUpResult = levelUpResult,
            achievementsUnlocked = emptyList() // TODO: Return actual unlocked achievements
        )
    }
    
    // Quiz completion with detailed scoring
    suspend fun completeQuiz(
        correctAnswers: Int,
        totalQuestions: Int,
        timeSpent: Int,
        skill: String,
        level: String
    ): GameReward {
        val accuracy = if (totalQuestions > 0) correctAnswers.toFloat() / totalQuestions else 0f
        val score = (accuracy * 100).toInt()
        val isPerfect = accuracy >= 1.0f
        
        userStatsDao.incrementQuizzesCompleted()
        userStatsDao.updateAverageAccuracy(accuracy)
        
        // Calculate streak
        val currentStats = userStatsDao.getUserStatsSync()
        val newStreak = if (accuracy >= 0.7f) (currentStats?.currentStreak ?: 0) + 1 else 0
        userStatsDao.updateStreak(newStreak)
        
        return completeLessonz(skill, level, score, timeSpent, accuracy, isPerfect)
    }
    
    // Check and unlock achievements based on current stats
    private suspend fun checkAndUnlockAchievements(context: PointContext) {
        val stats = userStatsDao.getUserStatsSync() ?: return
        val userLevel = userLevelDao.getUserLevelSync() ?: return
        
        val achievementsToCheck = listOf(
            // Learning achievements
            "first_lesson" to { stats.totalLessonsCompleted >= 1 },
            "lesson_enthusiast" to { stats.totalLessonsCompleted >= 10 },
            "lesson_master" to { stats.totalLessonsCompleted >= 50 },
            "lesson_legend" to { stats.totalLessonsCompleted >= 100 },
            
            // Speed achievements
            "speed_demon" to { stats.fastCompletions >= 5 },
            "lightning_fast" to { stats.fastCompletions >= 25 },
            
            // Accuracy achievements
            "perfectionist" to { stats.perfectQuizzes >= 1 },
            "accuracy_expert" to { stats.averageAccuracy >= 0.8f },
            "flawless_performer" to { stats.perfectQuizzes >= 10 },
            
            // Consistency achievements
            "week_warrior" to { stats.currentStreak >= 7 },
            "month_champion" to { stats.currentStreak >= 30 },
            "unstoppable" to { stats.longestStreak >= 50 },
            
            // Level achievements
            "level_up" to { userLevel.level >= 5 },
            "advanced_learner" to { userLevel.level >= 10 },
            "expert_student" to { userLevel.level >= 25 },
            "master_scholar" to { userLevel.level >= 50 },
            
            // Point achievements
            "point_collector" to { stats.totalPoints >= 1000 },
            "point_hoarder" to { stats.totalPoints >= 10000 },
            "point_millionaire" to { stats.totalPoints >= 100000 }
        )
        
        for ((achievementId, condition) in achievementsToCheck) {
            val achievement = achievementDao.getAchievementById(achievementId)
            if (achievement != null && !achievement.isUnlocked && condition()) {
                achievementDao.unlockAchievement(achievementId, System.currentTimeMillis())
                userStatsDao.addCoins(achievement.points)
                // TODO: Show achievement notification
            }
        }
    }
    
    // Generate daily challenges
    private suspend fun generateDailyChallenges() {
        val today = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
        val existingChallenges = dailyChallengeDao.getChallengesForDate(today)
        
        if (existingChallenges.isEmpty()) {
            val challenges = listOf(
                DailyChallenge(
                    date = today,
                    challengeType = ChallengeType.COMPLETE_LESSONS,
                    targetValue = 3,
                    rewardXP = 100,
                    rewardCoins = 50
                ),
                DailyChallenge(
                    date = today,
                    challengeType = ChallengeType.SCORE_POINTS,
                    targetValue = 500,
                    rewardXP = 75,
                    rewardCoins = 25
                ),
                DailyChallenge(
                    date = today,
                    challengeType = ChallengeType.PERFECT_STREAK,
                    targetValue = 2,
                    rewardXP = 150,
                    rewardCoins = 75
                )
            )
            dailyChallengeDao.insertChallenges(challenges)
        }
        
        // Clean up old challenges (older than 7 days)
        val cutoffDate = LocalDate.now().minusDays(7).format(DateTimeFormatter.ISO_LOCAL_DATE)
        dailyChallengeDao.deleteOldChallenges(cutoffDate)
    }
    
    // Update daily challenge progress
    private suspend fun updateDailyChallengeProgress(type: ChallengeType, amount: Int) {
        val today = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
        val challenges = dailyChallengeDao.getChallengesForDate(today)
        
        challenges.filter { it.challengeType == type && !it.isCompleted }.forEach { challenge ->
            val newProgress = challenge.currentProgress + amount
            val isCompleted = newProgress >= challenge.targetValue
            
            dailyChallengeDao.updateChallengeProgress(today, type, newProgress, isCompleted)
            
            if (isCompleted && !challenge.isCompleted) {
                // Award completion rewards
                awardXP(challenge.rewardXP.toLong(), "Daily challenge completed")
                userStatsDao.addCoins(challenge.rewardCoins)
            }
        }
    }
    
    // Helper functions
    private fun calculateNextLevelXP(level: Int): Long {
        return (100 * (1 + level * 0.5)).toLong()
    }
    
    private fun getLevelTitle(level: Int): String {
        return when {
            level < 5 -> "Beginner"
            level < 10 -> "Student"
            level < 20 -> "Learner"
            level < 30 -> "Scholar"
            level < 50 -> "Expert"
            level < 75 -> "Master"
            level < 100 -> "Grandmaster"
            else -> "Legend"
        }
    }
    
    // Get default achievements
    private fun getDefaultAchievements(): List<Achievement> {
        return listOf(
            // Learning achievements
            Achievement("first_lesson", "First Steps", "Complete your first lesson", "🎓", 50, AchievementCategory.LEARNING, AchievementRarity.COMMON),
            Achievement("lesson_enthusiast", "Eager Learner", "Complete 10 lessons", "📚", 150, AchievementCategory.LEARNING, AchievementRarity.RARE),
            Achievement("lesson_master", "Knowledge Seeker", "Complete 50 lessons", "🧠", 500, AchievementCategory.LEARNING, AchievementRarity.EPIC),
            Achievement("lesson_legend", "Learning Legend", "Complete 100 lessons", "👑", 1000, AchievementCategory.LEARNING, AchievementRarity.LEGENDARY),
            
            // Speed achievements
            Achievement("speed_demon", "Speed Demon", "Complete 5 lessons in under 2 minutes", "⚡", 200, AchievementCategory.SPEED, AchievementRarity.RARE),
            Achievement("lightning_fast", "Lightning Fast", "Complete 25 lessons in under 2 minutes", "🌩️", 750, AchievementCategory.SPEED, AchievementRarity.EPIC),
            
            // Accuracy achievements
            Achievement("perfectionist", "Perfectionist", "Get a perfect score on a quiz", "💎", 100, AchievementCategory.ACCURACY, AchievementRarity.COMMON),
            Achievement("accuracy_expert", "Accuracy Expert", "Maintain 80% average accuracy", "🎯", 300, AchievementCategory.ACCURACY, AchievementRarity.RARE),
            Achievement("flawless_performer", "Flawless Performer", "Get 10 perfect scores", "✨", 800, AchievementCategory.ACCURACY, AchievementRarity.EPIC),
            
            // Consistency achievements
            Achievement("week_warrior", "Week Warrior", "Maintain a 7-day streak", "🔥", 250, AchievementCategory.CONSISTENCY, AchievementRarity.RARE),
            Achievement("month_champion", "Month Champion", "Maintain a 30-day streak", "🏆", 1000, AchievementCategory.CONSISTENCY, AchievementRarity.EPIC),
            Achievement("unstoppable", "Unstoppable", "Reach a 50-day streak", "🚀", 2000, AchievementCategory.CONSISTENCY, AchievementRarity.LEGENDARY),
            
            // Level achievements
            Achievement("level_up", "Rising Star", "Reach level 5", "⭐", 100, AchievementCategory.MASTERY, AchievementRarity.COMMON),
            Achievement("advanced_learner", "Advanced Learner", "Reach level 10", "🌟", 300, AchievementCategory.MASTERY, AchievementRarity.RARE),
            Achievement("expert_student", "Expert Student", "Reach level 25", "💫", 800, AchievementCategory.MASTERY, AchievementRarity.EPIC),
            Achievement("master_scholar", "Master Scholar", "Reach level 50", "🌌", 2000, AchievementCategory.MASTERY, AchievementRarity.LEGENDARY),
            
            // Point achievements
            Achievement("point_collector", "Point Collector", "Earn 1,000 points", "💰", 100, AchievementCategory.EXPLORATION, AchievementRarity.COMMON),
            Achievement("point_hoarder", "Point Hoarder", "Earn 10,000 points", "💎", 500, AchievementCategory.EXPLORATION, AchievementRarity.RARE),
            Achievement("point_millionaire", "Point Millionaire", "Earn 100,000 points", "👑", 2000, AchievementCategory.EXPLORATION, AchievementRarity.LEGENDARY)
        )
    }
    
    // Data classes for results
    data class LevelUpResult(
        val newLevel: Int,
        val newTitle: String,
        val coinsAwarded: Int,
        val xpGained: Long
    )
    
    data class GameReward(
        val xpGained: Long,
        val coinsEarned: Int,
        val levelUpResult: LevelUpResult?,
        val achievementsUnlocked: List<Achievement>
    )
    
    enum class PointContext {
        LESSON_COMPLETED,
        QUIZ_COMPLETED,
        DAILY_CHALLENGE,
        ACHIEVEMENT_UNLOCKED,
        STREAK_BONUS
    }
}
