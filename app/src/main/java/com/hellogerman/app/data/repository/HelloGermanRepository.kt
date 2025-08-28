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

class HelloGermanRepository(context: Context) {
    
    private val database = HelloGermanDatabase.getDatabase(context)
    private val userProgressDao = database.userProgressDao()
    private val lessonDao = database.lessonDao()
    private val userSubmissionDao = database.userSubmissionDao()
    private val grammarProgressDao: GrammarProgressDao = database.grammarProgressDao()
    
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
        }
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
    
    // Lesson Operations
    fun getLessonsBySkillAndLevel(skill: String, level: String): Flow<List<Lesson>> {
        return lessonDao.getLessonsBySkillAndLevel(skill, level)
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
    
    suspend fun getAllLessons(): List<Lesson> {
        return lessonDao.getAllLessons()
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
}
