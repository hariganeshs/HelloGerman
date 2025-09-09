package com.hellogerman.app.gamification

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.Terrain
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
        // Themes - Only theme upgrades available
        Reward(
            id = "ocean_theme",
            title = "Ocean Theme",
            description = "Calming blue ocean theme with waves and aquatic colors",
            icon = Icons.Default.Waves,
            cost = 200,
            category = RewardCategory.THEMES,
            rarity = RewardRarity.RARE
        ),
        Reward(
            id = "forest_theme",
            title = "Forest Theme",
            description = "Natural green forest theme with earthy tones",
            icon = Icons.Default.Forest,
            cost = 200,
            category = RewardCategory.THEMES,
            rarity = RewardRarity.RARE
        ),
        Reward(
            id = "sunset_theme",
            title = "Sunset Theme",
            description = "Warm orange and pink sunset theme",
            icon = Icons.Default.WbTwilight,
            cost = 300,
            category = RewardCategory.THEMES,
            rarity = RewardRarity.EPIC
        ),
        Reward(
            id = "mountain_theme",
            title = "Mountain Theme",
            description = "Majestic mountain theme with cool blues and whites",
            icon = Icons.Default.Terrain,
            cost = 250,
            category = RewardCategory.THEMES,
            rarity = RewardRarity.RARE
        ),
        Reward(
            id = "desert_theme",
            title = "Desert Theme",
            description = "Sandy desert theme with golden hues",
            icon = Icons.Default.WbSunny,
            cost = 250,
            category = RewardCategory.THEMES,
            rarity = RewardRarity.RARE
        ),
        Reward(
            id = "space_theme",
            title = "Space Theme",
            description = "Cosmic space theme with stars and galaxies",
            icon = Icons.Default.Star,
            cost = 400,
            category = RewardCategory.THEMES,
            rarity = RewardRarity.EPIC
        ),
        Reward(
            id = "retro_theme",
            title = "Retro Theme",
            description = "Vintage 80s retro theme with neon colors",
            icon = Icons.Default.Palette,
            cost = 350,
            category = RewardCategory.THEMES,
            rarity = RewardRarity.EPIC
        ),
        Reward(
            id = "minimalist_theme",
            title = "Minimalist Theme",
            description = "Clean and simple minimalist design",
            icon = Icons.Default.Circle,
            cost = 150,
            category = RewardCategory.THEMES,
            rarity = RewardRarity.COMMON
        ),
        Reward(
            id = "autumn_theme",
            title = "Autumn Theme",
            description = "Beautiful autumn colors with warm oranges and reds",
            icon = Icons.Default.Nature,
            cost = 300,
            category = RewardCategory.THEMES,
            rarity = RewardRarity.RARE
        ),
        Reward(
            id = "winter_theme",
            title = "Winter Theme",
            description = "Cool winter theme with snow and ice colors",
            icon = Icons.Default.AcUnit,
            cost = 350,
            category = RewardCategory.THEMES,
            rarity = RewardRarity.EPIC
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
