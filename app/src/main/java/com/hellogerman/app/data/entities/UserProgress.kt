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
    val totalLessonsCompleted: Int = 0,
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val lastStudyDate: Long = 0,
    val dailyGoal: Int = 3, // lessons per day
    val isOnboarded: Boolean = false,
    val selectedLanguage: String = "en", // en, de
    val isDarkMode: Boolean = false,
    val textSize: Float = 1.0f
)
