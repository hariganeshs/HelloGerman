package com.hellogerman.app.data

import android.content.Context
import com.hellogerman.app.data.repository.HelloGermanRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.first

object DatabaseInitializer {
    
    fun initializeDatabase(context: Context) {
        val repository = HelloGermanRepository(context)
        
        CoroutineScope(Dispatchers.IO).launch {
            // Check if database is already initialized
            val userProgress = repository.getUserProgress().first()
            
            if (userProgress == null) {
                // Initialize user progress
                repository.insertUserProgress(com.hellogerman.app.data.entities.UserProgress())
            }
            
            // Check if lessons already exist before inserting
            val existingLessons = repository.getAllLessons()
            if (existingLessons.isEmpty()) {
                // Only insert lessons if none exist
                val lessons = LessonContentGenerator.generateAllLessons()
                repository.insertLessons(lessons)
            }

            // Seed grammar progress entries for all grammar lessons (grouped by topicKey)
            val allLessons = repository.getAllLessons()
            val grammarLessons = allLessons.filter { it.skill == "grammar" }
            val topics = grammarLessons.groupBy { it.level }
            topics.forEach { (level, levelLessons) ->
                levelLessons.forEachIndexed { index, lesson ->
                    // topicKey is embedded in GrammarContent JSON; seed per lesson fallback to title-based key
                    val topicKey = ("${level}_" + lesson.title.lowercase().replace(" ", "_")).take(64)
                    val progress = com.hellogerman.app.data.entities.GrammarProgress(
                        topicKey = topicKey,
                        level = level,
                        points = 0,
                        badgesJson = "[]",
                        streak = 0,
                        lastCompleted = 0L,
                        completedLessons = 0,
                        totalLessons = 1
                    )
                    repository.insertGrammarProgress(progress)
                }
            }
        }
    }
    
    fun forceReloadLessons(context: Context) {
        val repository = HelloGermanRepository(context)
        
        CoroutineScope(Dispatchers.IO).launch {
            // Clear all lessons and re-insert them
            repository.clearAllLessons()
            val lessons = LessonContentGenerator.generateAllLessons()
            repository.insertLessons(lessons)
        }
    }
    
    fun clearDuplicateLessons(context: Context) {
        val repository = HelloGermanRepository(context)
        
        CoroutineScope(Dispatchers.IO).launch {
            // Get current lesson count before clearing
            val existingLessons = repository.getAllLessons()
    
            
            // Clear all lessons and re-insert them to remove duplicates
            repository.clearAllLessons()
            val lessons = LessonContentGenerator.generateAllLessons()
            repository.insertLessons(lessons)
            
            // Get lesson count after re-inserting
            val newLessons = repository.getAllLessons()
    
        }
    }
    
    fun resetEntireDatabase(context: Context) {
        val repository = HelloGermanRepository(context)
        
        CoroutineScope(Dispatchers.IO).launch {
    
            
            // Clear all lessons
            repository.clearAllLessons()
            
            // Clear user progress (this will be recreated on next app start)
            // Note: This will reset all user data
            
            // Re-insert fresh lessons
            val lessons = LessonContentGenerator.generateAllLessons()
            repository.insertLessons(lessons)
            
            // Re-insert fresh user progress
            repository.insertUserProgress(com.hellogerman.app.data.entities.UserProgress())
            
            val finalLessons = repository.getAllLessons()
    
        }
    }
}
