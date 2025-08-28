package com.hellogerman.app.gamification

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Comprehensive Reward System for Maximum Engagement
 */

data class Reward(
    val id: String,
    val title: String,
    val description: String,
    val icon: ImageVector,
    val cost: Int,
    val category: RewardCategory,
    val rarity: RewardRarity,
    val isUnlocked: Boolean = false
)

enum class RewardCategory {
    THEMES, AVATARS, POWER_UPS, CUSTOMIZATION, CONTENT
}

enum class RewardRarity {
    COMMON, RARE, EPIC, LEGENDARY
}

data class DailyChallenge(
    val id: String,
    val title: String,
    val description: String,
    val icon: ImageVector,
    val rewardXP: Int,
    val rewardCoins: Int,
    val progress: Int = 0,
    val maxProgress: Int,
    val isCompleted: Boolean = false,
    val expiresAt: Long = System.currentTimeMillis() + 24 * 60 * 60 * 1000 // 24 hours
)

data class UserStats(
    val totalXP: Int = 0,
    val currentLevel: Int = 1,
    val coins: Int = 0,
    val longestStreak: Int = 0,
    val perfectLessons: Int = 0,
    val favoriteSkill: String = "none",
    val weeklyGoalProgress: Int = 0,
    val monthlyGoalProgress: Int = 0
)

object RewardSystem {
    
    fun getAllRewards(): List<Reward> = listOf(
        // Themes
        Reward(
            id = "dark_theme",
            title = "Dark Mode",
            description = "Elegant dark theme for night studying",
            icon = Icons.Default.DarkMode,
            cost = 100,
            category = RewardCategory.THEMES,
            rarity = RewardRarity.COMMON
        ),
        Reward(
            id = "ocean_theme",
            title = "Ocean Theme",
            description = "Calming blue ocean theme",
            icon = Icons.Default.Waves,
            cost = 200,
            category = RewardCategory.THEMES,
            rarity = RewardRarity.RARE
        ),
        Reward(
            id = "forest_theme",
            title = "Forest Theme",
            description = "Natural green forest theme",
            icon = Icons.Default.Forest,
            cost = 200,
            category = RewardCategory.THEMES,
            rarity = RewardRarity.RARE
        ),
        Reward(
            id = "sunset_theme",
            title = "Sunset Theme",
            description = "Warm orange sunset theme",
            icon = Icons.Default.WbTwilight,
            cost = 300,
            category = RewardCategory.THEMES,
            rarity = RewardRarity.EPIC
        ),
        
        // Power-ups
        Reward(
            id = "double_xp",
            title = "Double XP",
            description = "2x XP for next 3 lessons",
            icon = Icons.Default.FlashOn,
            cost = 50,
            category = RewardCategory.POWER_UPS,
            rarity = RewardRarity.COMMON
        ),
        Reward(
            id = "streak_freeze",
            title = "Streak Freeze",
            description = "Protect your streak for 1 day",
            icon = Icons.Default.Shield,
            cost = 150,
            category = RewardCategory.POWER_UPS,
            rarity = RewardRarity.RARE
        ),
        Reward(
            id = "hint_boost",
            title = "Hint Boost",
            description = "Extra hints for difficult questions",
            icon = Icons.AutoMirrored.Filled.Help,
            cost = 30,
            category = RewardCategory.POWER_UPS,
            rarity = RewardRarity.COMMON
        ),
        Reward(
            id = "perfect_bonus",
            title = "Perfect Bonus",
            description = "Bonus XP for perfect scores",
            icon = Icons.Default.Stars,
            cost = 100,
            category = RewardCategory.POWER_UPS,
            rarity = RewardRarity.RARE
        ),
        
        // Customization
        Reward(
            id = "custom_goal",
            title = "Custom Goals",
            description = "Set personalized daily goals",
            icon = Icons.Default.Flag,
            cost = 80,
            category = RewardCategory.CUSTOMIZATION,
            rarity = RewardRarity.COMMON
        ),
        Reward(
            id = "advanced_stats",
            title = "Advanced Stats",
            description = "Detailed progress analytics",
            icon = Icons.Default.Analytics,
            cost = 250,
            category = RewardCategory.CUSTOMIZATION,
            rarity = RewardRarity.EPIC
        ),
        
        // Content
        Reward(
            id = "bonus_lessons",
            title = "Bonus Lessons",
            description = "Unlock 10 extra lessons",
            icon = Icons.Default.Add,
            cost = 300,
            category = RewardCategory.CONTENT,
            rarity = RewardRarity.EPIC
        ),
        Reward(
            id = "advanced_grammar",
            title = "Advanced Grammar",
            description = "Unlock C2 level grammar",
            icon = Icons.Default.School,
            cost = 500,
            category = RewardCategory.CONTENT,
            rarity = RewardRarity.LEGENDARY
        )
    )
    
    fun generateDailyChallenges(): List<DailyChallenge> = listOf(
        DailyChallenge(
            id = "daily_lessons",
            title = "Daily Dedication",
            description = "Complete 3 lessons today",
            icon = Icons.Default.Today,
            rewardXP = 100,
            rewardCoins = 25,
            maxProgress = 3
        ),
        DailyChallenge(
            id = "perfect_score",
            title = "Perfectionist",
            description = "Get 100% on any lesson",
            icon = Icons.Default.Star,
            rewardXP = 150,
            rewardCoins = 30,
            maxProgress = 1
        ),
        DailyChallenge(
            id = "grammar_focus",
            title = "Grammar Focus",
            description = "Complete 2 grammar lessons",
            icon = Icons.Default.Spellcheck,
            rewardXP = 120,
            rewardCoins = 20,
            maxProgress = 2
        ),
        DailyChallenge(
            id = "streak_maintain",
            title = "Streak Master",
            description = "Maintain your learning streak",
            icon = Icons.Default.LocalFireDepartment,
            rewardXP = 80,
            rewardCoins = 15,
            maxProgress = 1
        ),
        DailyChallenge(
            id = "dictionary_use",
            title = "Word Explorer",
            description = "Use dictionary 5 times",
            icon = Icons.Default.Translate,
            rewardXP = 60,
            rewardCoins = 10,
            maxProgress = 5
        ),
        DailyChallenge(
            id = "speed_challenge",
            title = "Speed Challenge",
            description = "Complete lesson in under 5 minutes",
            icon = Icons.Default.Speed,
            rewardXP = 200,
            rewardCoins = 40,
            maxProgress = 1
        )
    )
    
    fun calculateLevel(totalXP: Int): Int {
        // XP required increases exponentially for higher levels
        return when {
            totalXP < 100 -> 1
            totalXP < 300 -> 2
            totalXP < 600 -> 3
            totalXP < 1000 -> 4
            totalXP < 1500 -> 5
            totalXP < 2100 -> 6
            totalXP < 2800 -> 7
            totalXP < 3600 -> 8
            totalXP < 4500 -> 9
            totalXP < 5500 -> 10
            totalXP < 6700 -> 11
            totalXP < 8000 -> 12
            totalXP < 9500 -> 13
            totalXP < 11200 -> 14
            totalXP < 13000 -> 15
            totalXP < 15000 -> 16
            totalXP < 17200 -> 17
            totalXP < 19600 -> 18
            totalXP < 22200 -> 19
            totalXP < 25000 -> 20
            else -> 20 + (totalXP - 25000) / 3000
        }
    }
    
    fun getXPForNextLevel(currentLevel: Int): Int {
        return when (currentLevel) {
            1 -> 100
            2 -> 300
            3 -> 600
            4 -> 1000
            5 -> 1500
            6 -> 2100
            7 -> 2800
            8 -> 3600
            9 -> 4500
            10 -> 5500
            11 -> 6700
            12 -> 8000
            13 -> 9500
            14 -> 11200
            15 -> 13000
            16 -> 15000
            17 -> 17200
            18 -> 19600
            19 -> 22200
            20 -> 25000
            else -> 25000 + ((currentLevel - 20) * 3000)
        }
    }
    
    fun getLevelTitle(level: Int): String {
        return when {
            level < 5 -> "Beginner"
            level < 10 -> "Student"
            level < 15 -> "Scholar"
            level < 20 -> "Expert"
            level < 25 -> "Master"
            level < 30 -> "Guru"
            else -> "Legend"
        }
    }
}
