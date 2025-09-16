package com.hellogerman.app.data.repository

import android.content.Context
import com.hellogerman.app.data.HelloGermanDatabase
import com.hellogerman.app.data.dao.LessonDao
import com.hellogerman.app.data.dao.GrammarProgressDao
import com.hellogerman.app.data.dao.UserProgressDao
import com.hellogerman.app.data.dao.UserSubmissionDao
import com.hellogerman.app.data.entities.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

// Data classes for enhanced progress tracking
data class LevelUnlockStatus(
    val currentLevel: String,
    val overallProgress: Double,
    val skillProgress: Map<String, Double>,
    val canUnlock: Boolean,
    val lessonsCompleted: Int,
    val totalLessons: Int,
    val nextLevel: String?
)

data class LevelCompletionInfo(
    val completed: Int,
    val total: Int,
    val averageScore: Double,
    val progressPercentage: Double,
    val isComplete: Boolean
)

class HelloGermanRepository(context: Context) {
    
    private val database = HelloGermanDatabase.getDatabase(context)
    private val userProgressDao = database.userProgressDao()
    private val lessonDao = database.lessonDao()
    private val userSubmissionDao = database.userSubmissionDao()
    private val grammarProgressDao: GrammarProgressDao = database.grammarProgressDao()
    private val achievementDao: com.hellogerman.app.data.dao.AchievementDao = database.achievementDao()
    private val userVocabularyDao: com.hellogerman.app.data.dao.UserVocabularyDao = database.userVocabularyDao()
    
    // User Progress Operations
    fun getUserProgress(): Flow<UserProgress?> = userProgressDao.getUserProgress()
    
    suspend fun insertUserProgress(userProgress: UserProgress) {
        userProgressDao.insertUserProgress(userProgress)
    }
    
    suspend fun updateUserProgress(userProgress: UserProgress) {
        userProgressDao.updateUserProgress(userProgress)
    }
    
    suspend fun updateCurrentLevel(level: String) {
        userProgressDao.updateCurrentLevel(level)
    }
    
    suspend fun updateSkillScore(skill: String, score: Int) {
        when (skill) {
            "lesen" -> userProgressDao.updateLesenScore(score)
            "hoeren" -> userProgressDao.updateHoerenScore(score)
            "schreiben" -> userProgressDao.updateSchreibenScore(score)
            "sprechen" -> userProgressDao.updateSprechenScore(score)
            "grammar" -> userProgressDao.updateGrammarScore(score)
        }
    }
    
    suspend fun addXP(xp: Int) {
        userProgressDao.addXP(xp)
    }
    
    suspend fun addCoins(coins: Int) {
        userProgressDao.addCoins(coins)
    }

    suspend fun deductCoins(coins: Int) {
        if (coins <= 0) return
        userProgressDao.addCoins(-coins)
    }
    
    suspend fun incrementPerfectLessons() {
        userProgressDao.incrementPerfectLessons()
    }
    
    suspend fun incrementDictionaryUsage() {
        userProgressDao.incrementDictionaryUsage()
    }
    
    suspend fun incrementLessonsCompleted() {
        userProgressDao.incrementLessonsCompleted()
    }
    
    suspend fun updateStreak(streak: Int) {
        userProgressDao.updateStreak(streak)
    }
    
    suspend fun updateLastStudyDate(date: Long) {
        userProgressDao.updateLastStudyDate(date)
    }

    suspend fun markTutorialCompleted() {
        userProgressDao.markTutorialCompleted()
    }

    suspend fun markOnboarded() {
        userProgressDao.markOnboarded()
    }

    suspend fun updateSelectedTheme(theme: String) {
        userProgressDao.updateSelectedTheme(theme)
    }
    
    // Lesson Operations
    fun getLessonsBySkillAndLevel(skill: String, level: String): Flow<List<Lesson>> {
        return lessonDao.getLessonsBySkillAndLevel(skill, level).map { lessons ->
            android.util.Log.d("HelloGermanRepository", "Repository returning ${lessons.size} lessons for $skill/$level")
            lessons.forEachIndexed { index, lesson ->
                android.util.Log.d("HelloGermanRepository", "  Repo Lesson $index: ${lesson.title} (ID: ${lesson.id})")
            }
            lessons
        }
    }
    
    suspend fun getLessonById(lessonId: Int): Lesson? {
        return lessonDao.getLessonById(lessonId)
    }
    
    fun getAllLessonsBySkill(skill: String): Flow<List<Lesson>> {
        return lessonDao.getAllLessonsBySkill(skill)
    }
    
    suspend fun insertLesson(lesson: Lesson) {
        lessonDao.insertLesson(lesson)
    }
    
    suspend fun insertLessons(lessons: List<Lesson>) {
        lessonDao.insertLessons(lessons)
    }
    
    suspend fun updateLesson(lesson: Lesson) {
        lessonDao.updateLesson(lesson)
    }
    
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
    
    suspend fun clearAllLessons() {
        lessonDao.deleteAllLessons()
    }
    suspend fun clearLessonsBySkill(skill: String) {
        lessonDao.deleteLessonsBySkill(skill)
    }
    
    suspend fun deleteGrammarLessons() {
        lessonDao.deleteAllGrammarLessons()
    }
    
    suspend fun getAllLessons(): List<Lesson> {
        return lessonDao.getAllLessons()
    }

    // Achievement persistence helpers
    suspend fun isAchievementUnlocked(id: String): Boolean {
        return achievementDao.getAchievementById(id)?.isUnlocked == true
    }

    suspend fun unlockAchievement(id: String, rewardXP: Int, rewardCoins: Int) {
        achievementDao.unlockAchievement(id, System.currentTimeMillis())
        if (rewardXP != 0) addXP(rewardXP)
        if (rewardCoins != 0) addCoins(rewardCoins)
    }

    suspend fun seedAchievements() {
        val items = com.hellogerman.app.gamification.AchievementManager.getAllAchievements().map { gm ->
            com.hellogerman.app.data.entities.Achievement(
                id = gm.id,
                title = gm.title,
                description = gm.description,
                icon = gm.id, // store id as icon key placeholder
                points = gm.rewardXP,
                category = com.hellogerman.app.data.entities.AchievementCategory.LEARNING,
                rarity = when (gm.rarity) {
                    com.hellogerman.app.gamification.AchievementRarity.COMMON -> com.hellogerman.app.data.entities.AchievementRarity.COMMON
                    com.hellogerman.app.gamification.AchievementRarity.RARE -> com.hellogerman.app.data.entities.AchievementRarity.RARE
                    com.hellogerman.app.gamification.AchievementRarity.EPIC -> com.hellogerman.app.data.entities.AchievementRarity.EPIC
                    com.hellogerman.app.gamification.AchievementRarity.LEGENDARY -> com.hellogerman.app.data.entities.AchievementRarity.LEGENDARY
                }
            )
        }
        achievementDao.insertAchievements(items)
    }
    
    // User Submission Operations
    fun getSubmissionsByLesson(lessonId: Int): Flow<List<UserSubmission>> {
        return userSubmissionDao.getSubmissionsByLesson(lessonId)
    }
    
    fun getSubmissionsBySkill(skill: String): Flow<List<UserSubmission>> {
        return userSubmissionDao.getSubmissionsBySkill(skill)
    }
    
    suspend fun getSubmissionById(submissionId: Int): UserSubmission? {
        return userSubmissionDao.getSubmissionById(submissionId)
    }
    
    suspend fun insertSubmission(submission: UserSubmission): Long {
        return userSubmissionDao.insertSubmission(submission)
    }
    
    suspend fun updateSubmission(submission: UserSubmission) {
        userSubmissionDao.updateSubmission(submission)
    }
    
    suspend fun deleteSubmission(submission: UserSubmission) {
        userSubmissionDao.deleteSubmission(submission)
    }
    
    suspend fun getAverageScoreBySkill(skill: String): Double? {
        return userSubmissionDao.getAverageScoreBySkill(skill)
    }
    
    suspend fun getSubmissionCountBySkill(skill: String): Int {
        return userSubmissionDao.getSubmissionCountBySkill(skill)
    }
    
    // Grammar Progress Operations
    fun getAllGrammarProgress(): Flow<List<GrammarProgress>> {
        return grammarProgressDao.getAll()
    }
    
    fun getGrammarProgressByLevel(level: String): Flow<List<GrammarProgress>> {
        return grammarProgressDao.getByLevel(level)
    }
    
    suspend fun getGrammarProgressByTopic(topicKey: String): GrammarProgress? {
        return grammarProgressDao.getByTopic(topicKey)
    }
    
    suspend fun insertGrammarProgress(progress: GrammarProgress) {
        grammarProgressDao.insert(progress)
    }
    
    suspend fun addGrammarPoints(topicKey: String, delta: Int) {
        grammarProgressDao.addPoints(topicKey, delta, System.currentTimeMillis())
    }
    
    suspend fun incrementGrammarCompleted(topicKey: String) {
        grammarProgressDao.incrementCompletedLessons(topicKey)
    }
    
    suspend fun updateGrammarStreak(topicKey: String, streak: Int) {
        grammarProgressDao.updateStreak(topicKey, streak)
    }
    
    fun getTotalGrammarPoints(): Flow<Int> {
        return grammarProgressDao.totalPoints()
    }

    suspend fun awardBadge(topicKey: String, badgeId: String) {
        val existing = getGrammarProgressByTopic(topicKey)
        val gson = com.google.gson.Gson()
        val current = try {
            gson.fromJson(existing?.badgesJson ?: "[]", Array<String>::class.java).toMutableList()
        } catch (e: Exception) { mutableListOf<String>() }
        if (!current.contains(badgeId)) {
            current.add(badgeId)
            grammarProgressDao.updateBadges(topicKey, gson.toJson(current))
        }
    }

    fun getWeakGrammarTopics(limit: Int = 5): Flow<List<GrammarProgress>> {
        return grammarProgressDao.getWeakest(limit)
    }
    
    // Progress Tracking
    suspend fun getProgressPercentage(skill: String, level: String): Double {
        val completed = getCompletedLessonsCount(skill, level)
        val total = getTotalLessonsCount(skill, level)
        return if (total > 0) (completed.toDouble() / total) * 100 else 0.0
    }
    
    suspend fun shouldAdvanceLevel(skill: String, level: String): Boolean {
        val averageScore = getAverageScore(skill, level) ?: 0.0
        return averageScore >= 70.0
    }

    suspend fun getNextLevel(currentLevel: String): String? {
        val levels = listOf("A1", "A2", "B1", "B2", "C1", "C2")
        val currentIndex = levels.indexOf(currentLevel)
        return if (currentIndex >= 0 && currentIndex < levels.size - 1) {
            levels[currentIndex + 1]
        } else null
    }

    // Enhanced adaptive progress system
    suspend fun checkLevelUnlock(currentLevel: String): LevelUnlockStatus {
        val skills = listOf("lesen", "hoeren", "schreiben", "sprechen", "grammar")
        var totalCompleted = 0
        var totalLessons = 0
        val skillProgress = mutableMapOf<String, Double>()

        skills.forEach { skill ->
            val completed = getCompletedLessonsCount(skill, currentLevel)
            val total = getTotalLessonsCount(skill, currentLevel)
            val percentage = if (total > 0) (completed.toDouble() / total) * 100 else 0.0

            totalCompleted += completed
            totalLessons += total
            skillProgress[skill] = percentage
        }

        val overallProgress = if (totalLessons > 0) (totalCompleted.toDouble() / totalLessons) * 100 else 0.0
        val canUnlock = overallProgress >= 80.0

        return LevelUnlockStatus(
            currentLevel = currentLevel,
            overallProgress = overallProgress,
            skillProgress = skillProgress,
            canUnlock = canUnlock,
            lessonsCompleted = totalCompleted,
            totalLessons = totalLessons,
            nextLevel = if (canUnlock) getNextLevel(currentLevel) else null
        )
    }

    suspend fun getLevelCompletionStatus(currentLevel: String): Map<String, LevelCompletionInfo> {
        val skills = listOf("lesen", "hoeren", "schreiben", "sprechen", "grammar")
        val completionStatus = mutableMapOf<String, LevelCompletionInfo>()

        skills.forEach { skill ->
            val completed = getCompletedLessonsCount(skill, currentLevel)
            val total = getTotalLessonsCount(skill, currentLevel)
            val averageScore = getAverageScore(skill, currentLevel) ?: 0.0
            val progress = if (total > 0) (completed.toDouble() / total) * 100 else 0.0

            completionStatus[skill] = LevelCompletionInfo(
                completed = completed,
                total = total,
                averageScore = averageScore,
                progressPercentage = progress,
                isComplete = progress >= 80.0
            )
        }

        return completionStatus
    }

    suspend fun autoAdvanceLevelIfReady(): Boolean {
        val currentProgress = getUserProgress().first()
        currentProgress?.let { progress ->
            val unlockStatus = checkLevelUnlock(progress.currentLevel)
            if (unlockStatus.canUnlock && unlockStatus.nextLevel != null) {
                updateCurrentLevel(unlockStatus.nextLevel!!)
                return true
            }
        }
        return false
    }
    
    // User Vocabulary Operations
    fun getAllUserVocabulary(): Flow<List<UserVocabulary>> = userVocabularyDao.getAllVocabulary()
    
    fun getFavoriteVocabulary(): Flow<List<UserVocabulary>> = userVocabularyDao.getFavoriteVocabulary()
    
    fun getVocabularyByLevel(level: String): Flow<List<UserVocabulary>> = userVocabularyDao.getVocabularyByLevel(level)
    
    fun getVocabularyByCategory(category: String): Flow<List<UserVocabulary>> = userVocabularyDao.getVocabularyByCategory(category)
    
    fun getVocabularyForReview(threshold: Int = 3): Flow<List<UserVocabulary>> = userVocabularyDao.getVocabularyForReview(threshold)
    
    suspend fun getVocabularyByWord(word: String): UserVocabulary? = userVocabularyDao.getVocabularyByWord(word)
    
    suspend fun addVocabularyToUserList(
        word: String,
        translation: String,
        gender: String? = null,
        level: String? = null,
        category: String? = null,
        notes: String? = null,
        source: String = "dictionary"
    ): Boolean {
        return try {
            // Check if word already exists
            val existing = userVocabularyDao.getVocabularyByWord(word)
            if (existing != null) {
                return false // Word already exists
            }
            
            val vocabulary = UserVocabulary(
                word = word,
                translation = translation,
                gender = gender,
                level = level,
                category = category,
                notes = notes,
                source = source
            )
            userVocabularyDao.insertVocabulary(vocabulary)
            true
        } catch (e: Exception) {
            false
        }
    }
    
    suspend fun updateVocabulary(vocabulary: UserVocabulary) {
        userVocabularyDao.updateVocabulary(vocabulary)
    }
    
    suspend fun deleteVocabulary(vocabulary: UserVocabulary) {
        userVocabularyDao.deleteVocabulary(vocabulary)
    }
    
    suspend fun deleteVocabularyByWord(word: String) {
        userVocabularyDao.deleteVocabularyByWord(word)
    }
    
    suspend fun updateMasteryLevel(word: String, level: Int) {
        userVocabularyDao.updateMasteryLevel(word, level)
    }
    
    suspend fun toggleFavoriteStatus(word: String): Boolean {
        val vocabulary = userVocabularyDao.getVocabularyByWord(word)
        vocabulary?.let {
            val newStatus = !it.isFavorite
            userVocabularyDao.updateFavoriteStatus(word, newStatus)
            return newStatus
        }
        return false
    }
    
    suspend fun markAsReviewed(word: String) {
        userVocabularyDao.markAsReviewed(word)
    }
    
    suspend fun getVocabularyCount(): Int = userVocabularyDao.getVocabularyCount()
    
    suspend fun getFavoriteCount(): Int = userVocabularyDao.getFavoriteCount()
    
    suspend fun getAvailableCategories(): List<String> = userVocabularyDao.getAvailableCategories()
    
    suspend fun getAvailableLevels(): List<String> = userVocabularyDao.getAvailableLevels()
}
