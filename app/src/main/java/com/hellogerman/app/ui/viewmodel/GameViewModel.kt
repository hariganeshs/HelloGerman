package com.hellogerman.app.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.hellogerman.app.data.entities.*
import com.hellogerman.app.gamification.GameificationManager
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class GameViewModel(application: Application) : AndroidViewModel(application) {
    
    private val gamificationManager = GameificationManager(application)
    
    // State flows for UI
    private val _userLevel = MutableStateFlow<UserLevel?>(null)
    val userLevel: StateFlow<UserLevel?> = _userLevel.asStateFlow()
    
    private val _userStats = MutableStateFlow<UserStats?>(null)
    val userStats: StateFlow<UserStats?> = _userStats.asStateFlow()
    
    private val _dailyChallenges = MutableStateFlow<List<DailyChallenge>>(emptyList())
    val dailyChallenges: StateFlow<List<DailyChallenge>> = _dailyChallenges.asStateFlow()
    
    private val _achievements = MutableStateFlow<List<Achievement>>(emptyList())
    val achievements: StateFlow<List<Achievement>> = _achievements.asStateFlow()
    
    private val _recentRewards = MutableStateFlow<List<GameificationManager.GameReward>>(emptyList())
    val recentRewards: StateFlow<List<GameificationManager.GameReward>> = _recentRewards.asStateFlow()
    
    // Events
    private val _showLevelUpDialog = MutableStateFlow(false)
    val showLevelUpDialog: StateFlow<Boolean> = _showLevelUpDialog.asStateFlow()
    
    private val _showXPGain = MutableStateFlow(false)
    val showXPGain: StateFlow<Boolean> = _showXPGain.asStateFlow()
    
    private val _lastXPGain = MutableStateFlow(0L)
    val lastXPGain: StateFlow<Long> = _lastXPGain.asStateFlow()
    
    init {
        initializeGameification()
        loadGameData()
    }
    
    private fun initializeGameification() {
        viewModelScope.launch {
            gamificationManager.initializeGameification()
        }
    }
    
    private fun loadGameData() {
        viewModelScope.launch {
            // Load user level, stats, challenges, and achievements
            // You'll need to implement flow collection from gamification manager
            // For now, using placeholder data
            
            _userLevel.value = UserLevel(
                level = 5,
                totalXP = 2500L,
                currentLevelXP = 250L,
                nextLevelXP = 500L,
                title = "Scholar"
            )
            
            _userStats.value = UserStats(
                totalLessonsCompleted = 25,
                totalQuizzesCompleted = 30,
                totalTimeSpent = 7200L,
                averageAccuracy = 0.85f,
                currentStreak = 7,
                longestStreak = 12,
                totalCoins = 1500,
                totalPoints = 2500L,
                perfectQuizzes = 8,
                fastCompletions = 5
            )
            
            _dailyChallenges.value = getSampleDailyChallenges()
            _achievements.value = getSampleAchievements()
        }
    }
    
    // Complete a lesson with full gamification integration
    fun completeLessonWithGamification(
        skill: String,
        level: String,
        score: Int,
        timeSpent: Int,
        correctAnswers: Int,
        totalQuestions: Int
    ) {
        viewModelScope.launch {
            val accuracy = if (totalQuestions > 0) correctAnswers.toFloat() / totalQuestions else 0f
            val isPerfect = accuracy >= 1.0f
            
            // Complete lesson through gamification manager
            val reward = gamificationManager.completeLessonz(
                skill = skill,
                level = level,
                score = score,
                timeSpent = timeSpent,
                accuracy = accuracy,
                isPerfect = isPerfect
            )
            
            // Update UI state
            handleGameReward(reward)
            
            // Refresh game data
            loadGameData()
        }
    }
    
    // Complete quiz with gamification
    fun completeQuizWithGamification(
        correctAnswers: Int,
        totalQuestions: Int,
        timeSpent: Int,
        skill: String,
        level: String
    ) {
        viewModelScope.launch {
            val reward = gamificationManager.completeQuiz(
                correctAnswers = correctAnswers,
                totalQuestions = totalQuestions,
                timeSpent = timeSpent,
                skill = skill,
                level = level
            )
            
            handleGameReward(reward)
            loadGameData()
        }
    }
    
    // Award XP manually
    fun awardXP(amount: Long, reason: String = "") {
        viewModelScope.launch {
            val levelUpResult = gamificationManager.awardXP(amount, reason)
            
            // Show XP gain notification
            _lastXPGain.value = amount
            _showXPGain.value = true
            
            // Show level up dialog if leveled up
            if (levelUpResult != null) {
                _showLevelUpDialog.value = true
            }
            
            loadGameData()
        }
    }
    
    // Handle game rewards (XP, level ups, achievements)
    private fun handleGameReward(reward: GameificationManager.GameReward) {
        // Show XP gain
        if (reward.xpGained > 0) {
            _lastXPGain.value = reward.xpGained
            _showXPGain.value = true
        }
        
        // Show level up dialog
        if (reward.levelUpResult != null) {
            _showLevelUpDialog.value = true
        }
        
        // Add to recent rewards
        val currentRewards = _recentRewards.value.toMutableList()
        currentRewards.add(0, reward)
        if (currentRewards.size > 5) currentRewards.removeAt(currentRewards.size - 1)
        _recentRewards.value = currentRewards
    }
    
    // UI event handlers
    fun dismissXPGain() {
        _showXPGain.value = false
    }
    
    fun dismissLevelUpDialog() {
        _showLevelUpDialog.value = false
    }
    
    // Get user's progress for a specific skill
    fun getSkillProgress(skill: String): Flow<Float> {
        return flow {
            // Calculate progress based on completed lessons for this skill
            // This is a placeholder - implement based on your lesson tracking
            when (skill) {
                "lesen" -> emit(0.75f)
                "hoeren" -> emit(0.60f)
                "schreiben" -> emit(0.45f)
                "sprechen" -> emit(0.30f)
                "grammar" -> emit(0.85f)
                else -> emit(0.0f)
            }
        }
    }
    
    // Sample data functions (replace with real data loading)
    private fun getSampleDailyChallenges(): List<DailyChallenge> {
        val calendar = java.util.Calendar.getInstance()
        val year = calendar.get(java.util.Calendar.YEAR)
        val month = calendar.get(java.util.Calendar.MONTH) + 1 // Calendar months are 0-based
        val day = calendar.get(java.util.Calendar.DAY_OF_MONTH)
        val today = String.format("%04d-%02d-%02d", year, month, day)
        return listOf(
            DailyChallenge(
                date = today,
                challengeType = ChallengeType.COMPLETE_LESSONS,
                targetValue = 3,
                currentProgress = 2,
                isCompleted = false,
                rewardXP = 100,
                rewardCoins = 50
            ),
            DailyChallenge(
                date = today,
                challengeType = ChallengeType.SCORE_POINTS,
                targetValue = 500,
                currentProgress = 350,
                isCompleted = false,
                rewardXP = 75,
                rewardCoins = 25
            ),
            DailyChallenge(
                date = today,
                challengeType = ChallengeType.PERFECT_STREAK,
                targetValue = 2,
                currentProgress = 2,
                isCompleted = true,
                rewardXP = 150,
                rewardCoins = 75
            )
        )
    }
    
    private fun getSampleAchievements(): List<Achievement> {
        return listOf(
            Achievement(
                id = "first_lesson",
                title = "First Steps",
                description = "Complete your first lesson",
                icon = "🎓",
                points = 50,
                category = AchievementCategory.LEARNING,
                rarity = AchievementRarity.COMMON,
                isUnlocked = true,
                unlockedAt = System.currentTimeMillis() - 86400000 // Yesterday
            ),
            Achievement(
                id = "week_warrior",
                title = "Week Warrior",
                description = "Maintain a 7-day streak",
                icon = "🔥",
                points = 250,
                category = AchievementCategory.CONSISTENCY,
                rarity = AchievementRarity.RARE,
                isUnlocked = true,
                unlockedAt = System.currentTimeMillis() - 3600000 // 1 hour ago
            ),
            Achievement(
                id = "perfectionist",
                title = "Perfectionist",
                description = "Get a perfect score on a quiz",
                icon = "💎",
                points = 100,
                category = AchievementCategory.ACCURACY,
                rarity = AchievementRarity.COMMON,
                isUnlocked = true,
                unlockedAt = System.currentTimeMillis() - 1800000 // 30 minutes ago
            )
        )
    }
}
