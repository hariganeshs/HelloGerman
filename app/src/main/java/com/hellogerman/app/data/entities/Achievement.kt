package com.hellogerman.app.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "achievements")
data class Achievement(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val icon: String, // Emoji or icon identifier
    val points: Int,
    val category: AchievementCategory,
    val rarity: AchievementRarity,
    val unlockedAt: Long = 0L,
    val isUnlocked: Boolean = false,
    val progress: Int = 0,
    val maxProgress: Int = 1
)

enum class AchievementCategory {
    LEARNING,
    SPEED,
    ACCURACY,
    CONSISTENCY, 
    SOCIAL,
    EXPLORATION,
    MASTERY
}

enum class AchievementRarity {
    COMMON,    // Bronze
    RARE,      // Silver  
    EPIC,      // Gold
    LEGENDARY  // Diamond
}

@Entity(tableName = "user_level")
data class UserLevel(
    @PrimaryKey val id: Int = 1,
    val level: Int = 1,
    val totalXP: Long = 0L,
    val currentLevelXP: Long = 0L,
    val nextLevelXP: Long = 100L,
    val title: String = "Beginner",
    val prestige: Int = 0
)

@Entity(tableName = "daily_challenges")
data class DailyChallenge(
    @PrimaryKey val date: String, // YYYY-MM-DD format
    val challengeType: ChallengeType,
    val targetValue: Int,
    val currentProgress: Int = 0,
    val isCompleted: Boolean = false,
    val rewardXP: Int,
    val rewardCoins: Int
)

enum class ChallengeType {
    COMPLETE_LESSONS,
    SCORE_POINTS,
    PERFECT_STREAK,
    GRAMMAR_MASTER,
    SPEED_DEMON,
    VOCABULARY_COLLECTOR
}

@Entity(tableName = "user_stats")
data class UserStats(
    @PrimaryKey val id: Int = 1,
    val totalLessonsCompleted: Int = 0,
    val totalQuizzesCompleted: Int = 0,
    val totalTimeSpent: Long = 0L, // in seconds
    val averageAccuracy: Float = 0f,
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val totalCoins: Int = 0,
    val totalPoints: Long = 0L,
    val perfectQuizzes: Int = 0,
    val fastCompletions: Int = 0,
    val lastActiveDate: Long = System.currentTimeMillis()
)
