package com.hellogerman.app.data.repository

import android.content.Context
import com.hellogerman.app.data.HelloGermanDatabase
import com.hellogerman.app.data.entities.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flowOf

class HelloGermanRepository(private val context: Context) {

    private val database = HelloGermanDatabase.getDatabase(context)
    private val userProgressDao = database.userProgressDao()
    private val lessonDao = database.lessonDao()
    private val grammarProgressDao = database.grammarProgressDao()
    private val achievementDao = database.achievementDao()
    private val userVocabularyDao = database.userVocabularyDao()

    // User Progress
    fun getUserProgress(): Flow<UserProgress?> = userProgressDao.getUserProgress()

    suspend fun insertUserProgress(userProgress: UserProgress) = userProgressDao.insertUserProgress(userProgress)

    suspend fun updateUserProgress(userProgress: UserProgress) = userProgressDao.updateUserProgress(userProgress)

    suspend fun updateCurrentLevel(level: String) = userProgressDao.updateCurrentLevel(level)

    suspend fun updateSkillScore(skill: String, score: Int) {
        when (skill.lowercase()) {
            "lesen" -> userProgressDao.updateLesenScore(score)
            "hoeren" -> userProgressDao.updateHoerenScore(score)
            "schreiben" -> userProgressDao.updateSchreibenScore(score)
            "sprechen" -> userProgressDao.updateSprechenScore(score)
            "grammar" -> userProgressDao.updateGrammarScore(score)
            else -> throw IllegalArgumentException("Unknown skill: $skill")
        }
    }

    suspend fun incrementLessonsCompleted() = userProgressDao.incrementLessonsCompleted()

    suspend fun incrementPerfectLessons() = userProgressDao.incrementPerfectLessons()

    suspend fun addXP(xp: Int) = userProgressDao.addXP(xp)

    suspend fun addCoins(coins: Int) = userProgressDao.addCoins(coins)

    suspend fun deductCoins(coins: Int) = userProgressDao.deductCoins(coins)

    suspend fun updateStreak(streak: Int) = userProgressDao.updateStreak(streak)

    suspend fun updateLastStudyDate(date: Long) = userProgressDao.updateLastStudyDate(date)

    suspend fun updateSelectedTheme(themeId: String) = userProgressDao.updateSelectedTheme(themeId)

    suspend fun markTutorialCompleted() = userProgressDao.markTutorialCompleted()

    suspend fun markOnboarded() = userProgressDao.markOnboarded()

    // Lessons
    suspend fun getAllLessons(): List<Lesson> = lessonDao.getAllLessons()

    suspend fun insertLessons(lessons: List<Lesson>) = lessonDao.insertLessons(lessons)

    suspend fun clearAllLessons() = lessonDao.deleteAllLessons()

    suspend fun deleteGrammarLessons() = lessonDao.deleteAllGrammarLessons()

    fun getLessonsBySkillAndLevel(skill: String, level: String) = lessonDao.getLessonsBySkillAndLevel(skill, level)

    suspend fun getLessonById(lessonId: Int): Lesson? = lessonDao.getLessonById(lessonId)

    suspend fun updateLessonProgress(lessonId: Int, completed: Boolean, score: Int, timeSpent: Int) {
        lessonDao.updateLessonProgress(lessonId, completed, score, timeSpent)
    }

    suspend fun getCompletedLessonsCount(skill: String, level: String): Int {
        return lessonDao.getCompletedLessonsCount(skill, level)
    }

    suspend fun getTotalLessonsCount(skill: String, level: String): Int {
        return lessonDao.getTotalLessonsCount(skill, level)
    }

    suspend fun getAverageScore(skill: String, level: String): Double? {
        return lessonDao.getAverageScore(skill, level)
    }

    suspend fun getNextLevel(currentLevel: String): String? {
        // Simple level progression logic
        return when (currentLevel) {
            "A1" -> "A2"
            "A2" -> "B1"
            "B1" -> "B2"
            "B2" -> "C1"
            "C1" -> "C2"
            else -> null
        }
    }

    // Grammar Progress
    suspend fun getGrammarProgressByTopic(topicKey: String): GrammarProgress? = grammarProgressDao.getByTopic(topicKey)

    suspend fun insertGrammarProgress(progress: GrammarProgress) = grammarProgressDao.insert(progress)

    fun getTotalGrammarPoints(): Flow<Int> = grammarProgressDao.totalPoints()

    fun getAllGrammarProgress(): Flow<List<GrammarProgress>> = grammarProgressDao.getAll()

    fun getWeakGrammarTopics(): Flow<List<String>> = flowOf(emptyList()) // Placeholder

    suspend fun addGrammarPoints(topicKey: String, points: Int) {
        val timestamp = System.currentTimeMillis()
        grammarProgressDao.addPoints(topicKey, points, timestamp)
    }

    suspend fun incrementGrammarCompleted(topicKey: String) {
        grammarProgressDao.incrementCompletedLessons(topicKey)
    }

    suspend fun updateGrammarStreak(topicKey: String, streak: Int) {
        grammarProgressDao.updateStreak(topicKey, streak)
    }

    suspend fun awardBadge(topicKey: String, badgeId: String) {
        // This would need to be implemented - for now just log
        // TODO: Implement badge awarding system
    }

    // Achievements
    suspend fun seedAchievements() {
        // Placeholder - achievements would be seeded here
    }

    suspend fun isAchievementUnlocked(id: String): Boolean {
        val achievement = achievementDao.getAchievementById(id)
        return achievement?.isUnlocked == true
    }

    suspend fun unlockAchievement(id: String, timestamp: Long) = achievementDao.unlockAchievement(id, timestamp)

    // Vocabulary
    suspend fun getVocabularyCount(): Int = userVocabularyDao.getVocabularyCount()

    fun getAllUserVocabulary(): Flow<List<UserVocabulary>> = userVocabularyDao.getAllVocabulary()

    suspend fun toggleFavoriteStatus(word: String) {
        val current = userVocabularyDao.getVocabularyByWord(word)
        current?.let {
            userVocabularyDao.updateFavoriteStatus(word, !it.isFavorite)
        }
    }

    suspend fun deleteVocabularyByWord(word: String) = userVocabularyDao.deleteVocabularyByWord(word)

    // Level progression methods
    suspend fun shouldAdvanceLevel(skill: String, level: String): Boolean {
        val completed = getCompletedLessonsCount(skill, level)
        val total = getTotalLessonsCount(skill, level)
        return if (total > 0) (completed.toDouble() / total) >= 0.8 else false
    }

    suspend fun getProgressPercentage(skill: String, level: String): Double {
        val completed = getCompletedLessonsCount(skill, level)
        val total = getTotalLessonsCount(skill, level)
        return if (total > 0) (completed.toDouble() / total) * 100.0 else 0.0
    }

    suspend fun checkLevelUnlock(currentLevel: String): LevelUnlockStatus {
        val skills = listOf("lesen", "hoeren", "schreiben", "sprechen", "grammar")
        var totalCompleted = 0
        var totalLessons = 0

        for (skill in skills) {
            val completed = getCompletedLessonsCount(skill, currentLevel)
            val total = getTotalLessonsCount(skill, currentLevel)
            totalCompleted += completed
            totalLessons += total
        }

        val overallProgress = if (totalLessons > 0) (totalCompleted.toDouble() / totalLessons) * 100.0 else 0.0
        val canUnlock = overallProgress >= 80.0
        val nextLevel = getNextLevel(currentLevel)

        return LevelUnlockStatus(
            currentLevel = currentLevel,
            overallProgress = overallProgress,
            canUnlock = canUnlock,
            nextLevel = nextLevel,
            lessonsCompleted = totalCompleted,
            totalLessons = totalLessons
        )
    }

    suspend fun getLevelCompletionStatus(currentLevel: String): Map<String, LevelCompletionInfo> {
        val skills = listOf("lesen", "hoeren", "schreiben", "sprechen", "grammar")
        val result = mutableMapOf<String, LevelCompletionInfo>()

        for (skill in skills) {
            val completed = getCompletedLessonsCount(skill, currentLevel)
            val total = getTotalLessonsCount(skill, currentLevel)
            val progress = if (total > 0) (completed.toDouble() / total) * 100.0 else 0.0
            val canUnlock = progress >= 80.0
            val nextLevel = getNextLevel(currentLevel)

            result[skill] = LevelCompletionInfo(
                lessonsCompleted = completed,
                totalLessons = total,
                progress = progress,
                canUnlock = canUnlock,
                nextLevel = nextLevel
            )
        }

        return result
    }

    suspend fun autoAdvanceLevelIfReady(): Boolean {
        val userProgress = getUserProgress()
        val currentProgress = userProgress.firstOrNull() ?: return false
        val unlockStatus = checkLevelUnlock(currentProgress.currentLevel)

        if (unlockStatus.canUnlock && unlockStatus.nextLevel != null) {
            updateCurrentLevel(unlockStatus.nextLevel)
            return true
        }
        return false
    }
}

// Placeholder classes for missing types
data class LevelUnlockStatus(
    val currentLevel: String,
    val overallProgress: Double,
    val canUnlock: Boolean,
    val nextLevel: String?,
    val lessonsCompleted: Int = 0,
    val totalLessons: Int = 0
)

data class LevelCompletionInfo(
    val lessonsCompleted: Int = 0,
    val totalLessons: Int = 0,
    val progress: Double = 0.0,
    val canUnlock: Boolean = false,
    val nextLevel: String? = null
)