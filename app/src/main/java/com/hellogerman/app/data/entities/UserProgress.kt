package com.hellogerman.app.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_progress")
data class UserProgress(
    @PrimaryKey val id: Int = 1,
    val currentLevel: String = "A1", // A1, A2, B1, B2, C1, C2
    val lesenScore: Int = 0,
    val hoerenScore: Int = 0,
    val schreibenScore: Int = 0,
    val sprechenScore: Int = 0,
    val grammarScore: Int = 0, // Add grammar score tracking
    val totalLessonsCompleted: Int = 0,
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val lastStudyDate: Long = 0,
    val dailyGoal: Int = 3, // lessons per day
    val isOnboarded: Boolean = false,
    val selectedLanguage: String = "en", // en, de
    val isDarkMode: Boolean = false,
    val textSize: Float = 1.0f,
    val showEnglishExplanations: Boolean = true, // Show English translations and explanations
    // Gamification fields
    val totalXP: Int = 0,
    val coins: Int = 0,
    val perfectLessons: Int = 0,
    val dictionaryUsage: Int = 0,
    val weeklyGoalProgress: Int = 0,
    val monthlyGoalProgress: Int = 0
)
