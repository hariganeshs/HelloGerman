package com.hellogerman.app.gamification

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.ui.graphics.vector.ImageVector
import com.hellogerman.app.data.entities.UserProgress

/**
 * Comprehensive Achievement System for Maximum User Engagement
 */

data class Achievement(
    val id: String,
    val title: String,
    val description: String,
    val icon: ImageVector,
    val rewardXP: Int,
    val rewardCoins: Int = 0,
    val isUnlocked: Boolean = false,
    val progress: Int = 0,
    val maxProgress: Int = 1,
    val category: AchievementCategory,
    val rarity: AchievementRarity
)

enum class AchievementCategory {
    STREAK, LESSONS, GRAMMAR, VOCABULARY, SKILLS, SOCIAL, SPECIAL
}

enum class AchievementRarity {
    COMMON, RARE, EPIC, LEGENDARY
}

object AchievementManager {
    
    fun getAllAchievements(): List<Achievement> = listOf(
        // Streak Achievements
        Achievement(
            id = "first_day",
            title = "First Steps",
            description = "Complete your first day of learning",
            icon = Icons.Default.Star,
            rewardXP = 50,
            rewardCoins = 10,
            category = AchievementCategory.STREAK,
            rarity = AchievementRarity.COMMON
        ),
        Achievement(
            id = "week_warrior",
            title = "Week Warrior",
            description = "Maintain a 7-day learning streak",
            icon = Icons.Default.LocalFireDepartment,
            rewardXP = 200,
            rewardCoins = 50,
            maxProgress = 7,
            category = AchievementCategory.STREAK,
            rarity = AchievementRarity.RARE
        ),
        Achievement(
            id = "unstoppable",
            title = "Unstoppable",
            description = "Reach a 30-day streak",
            icon = Icons.Default.Whatshot,
            rewardXP = 1000,
            rewardCoins = 200,
            maxProgress = 30,
            category = AchievementCategory.STREAK,
            rarity = AchievementRarity.EPIC
        ),
        Achievement(
            id = "legend",
            title = "Legend",
            description = "Achieve a 100-day streak",
            icon = Icons.Default.EmojiEvents,
            rewardXP = 5000,
            rewardCoins = 1000,
            maxProgress = 100,
            category = AchievementCategory.STREAK,
            rarity = AchievementRarity.LEGENDARY
        ),
        
        // Lesson Achievements
        Achievement(
            id = "first_lesson",
            title = "Getting Started",
            description = "Complete your first lesson",
            icon = Icons.Default.School,
            rewardXP = 25,
            rewardCoins = 5,
            category = AchievementCategory.LESSONS,
            rarity = AchievementRarity.COMMON
        ),
        Achievement(
            id = "dedicated_learner",
            title = "Dedicated Learner",
            description = "Complete 50 lessons",
            icon = Icons.AutoMirrored.Filled.MenuBook,
            rewardXP = 500,
            rewardCoins = 100,
            maxProgress = 50,
            category = AchievementCategory.LESSONS,
            rarity = AchievementRarity.RARE
        ),
        Achievement(
            id = "scholar",
            title = "Scholar",
            description = "Complete 200 lessons",
            icon = Icons.Default.Psychology,
            rewardXP = 2000,
            rewardCoins = 400,
            maxProgress = 200,
            category = AchievementCategory.LESSONS,
            rarity = AchievementRarity.EPIC
        ),
        
        // Grammar Achievements
        Achievement(
            id = "grammar_rookie",
            title = "Grammar Rookie",
            description = "Score 100 points in grammar",
            icon = Icons.Default.Spellcheck,
            rewardXP = 100,
            rewardCoins = 20,
            maxProgress = 100,
            category = AchievementCategory.GRAMMAR,
            rarity = AchievementRarity.COMMON
        ),
        Achievement(
            id = "grammar_master",
            title = "Grammar Master",
            description = "Score 1000 points in grammar",
            icon = Icons.Default.CheckCircle,
            rewardXP = 800,
            rewardCoins = 150,
            maxProgress = 1000,
            category = AchievementCategory.GRAMMAR,
            rarity = AchievementRarity.EPIC
        ),
        
        // Skills Achievements
        Achievement(
            id = "reading_pro",
            title = "Reading Pro",
            description = "Reach 80% in reading skill",
            icon = Icons.Default.Book,
            rewardXP = 300,
            rewardCoins = 60,
            maxProgress = 80,
            category = AchievementCategory.SKILLS,
            rarity = AchievementRarity.RARE
        ),
        Achievement(
            id = "listening_expert",
            title = "Listening Expert",
            description = "Reach 80% in listening skill",
            icon = Icons.Default.Headphones,
            rewardXP = 300,
            rewardCoins = 60,
            maxProgress = 80,
            category = AchievementCategory.SKILLS,
            rarity = AchievementRarity.RARE
        ),
        Achievement(
            id = "writing_wizard",
            title = "Writing Wizard",
            description = "Reach 80% in writing skill",
            icon = Icons.Default.Edit,
            rewardXP = 300,
            rewardCoins = 60,
            maxProgress = 80,
            category = AchievementCategory.SKILLS,
            rarity = AchievementRarity.RARE
        ),
        Achievement(
            id = "speaking_champion",
            title = "Speaking Champion",
            description = "Reach 80% in speaking skill",
            icon = Icons.Default.RecordVoiceOver,
            rewardXP = 300,
            rewardCoins = 60,
            maxProgress = 80,
            category = AchievementCategory.SKILLS,
            rarity = AchievementRarity.RARE
        ),
        Achievement(
            id = "polyglot",
            title = "Polyglot",
            description = "Reach 90% in all skills",
            icon = Icons.Default.Language,
            rewardXP = 2000,
            rewardCoins = 500,
            maxProgress = 90,
            category = AchievementCategory.SKILLS,
            rarity = AchievementRarity.LEGENDARY
        ),
        
        // Special Achievements
        Achievement(
            id = "dictionary_explorer",
            title = "Dictionary Explorer",
            description = "Use the dictionary 25 times",
            icon = Icons.Default.Translate,
            rewardXP = 150,
            rewardCoins = 30,
            maxProgress = 25,
            category = AchievementCategory.SPECIAL,
            rarity = AchievementRarity.COMMON
        ),
        Achievement(
            id = "perfectionist",
            title = "Perfectionist",
            description = "Get 100% on 10 lessons",
            icon = Icons.Default.Diamond,
            rewardXP = 800,
            rewardCoins = 160,
            maxProgress = 10,
            category = AchievementCategory.SPECIAL,
            rarity = AchievementRarity.EPIC
        ),
        Achievement(
            id = "speed_demon",
            title = "Speed Demon",
            description = "Complete 5 lessons in one day",
            icon = Icons.Default.Speed,
            rewardXP = 400,
            rewardCoins = 80,
            maxProgress = 5,
            category = AchievementCategory.SPECIAL,
            rarity = AchievementRarity.RARE
        ),
        Achievement(
            id = "night_owl",
            title = "Night Owl",
            description = "Study after 10 PM",
            icon = Icons.Default.NightlightRound,
            rewardXP = 100,
            rewardCoins = 20,
            category = AchievementCategory.SPECIAL,
            rarity = AchievementRarity.COMMON
        ),
        Achievement(
            id = "early_bird",
            title = "Early Bird",
            description = "Study before 7 AM",
            icon = Icons.Default.WbSunny,
            rewardXP = 100,
            rewardCoins = 20,
            category = AchievementCategory.SPECIAL,
            rarity = AchievementRarity.COMMON
        )
    )
    
    fun checkAchievements(userProgress: UserProgress?, grammarPoints: Int): List<Achievement> {
        val unlockedAchievements = mutableListOf<Achievement>()
        val achievements = getAllAchievements()
        
        userProgress?.let { progress ->
            achievements.forEach { achievement ->
                if (!achievement.isUnlocked && shouldUnlockAchievement(achievement, progress, grammarPoints)) {
                    unlockedAchievements.add(achievement.copy(isUnlocked = true))
                }
            }
        }
        
        return unlockedAchievements
    }
    
    private fun shouldUnlockAchievement(achievement: Achievement, userProgress: UserProgress, grammarPoints: Int): Boolean {
        return when (achievement.id) {
            "first_day" -> userProgress.currentStreak >= 1
            "week_warrior" -> userProgress.currentStreak >= 7
            "unstoppable" -> userProgress.currentStreak >= 30
            "legend" -> userProgress.currentStreak >= 100
            "first_lesson" -> userProgress.totalLessonsCompleted >= 1
            "dedicated_learner" -> userProgress.totalLessonsCompleted >= 50
            "scholar" -> userProgress.totalLessonsCompleted >= 200
            "grammar_rookie" -> grammarPoints >= 100
            "grammar_master" -> grammarPoints >= 1000
            "reading_pro" -> userProgress.lesenScore >= 80
            "listening_expert" -> userProgress.hoerenScore >= 80
            "writing_wizard" -> userProgress.schreibenScore >= 80
            "speaking_champion" -> userProgress.sprechenScore >= 80
            "polyglot" -> userProgress.lesenScore >= 90 && userProgress.hoerenScore >= 90 && 
                         userProgress.schreibenScore >= 90 && userProgress.sprechenScore >= 90
            else -> false
        }
    }
}
