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
            
            // Check existing lessons; if missing grammar lessons, insert only grammar lessons
            val existingLessons = repository.getAllLessons()
            if (existingLessons.isEmpty()) {
                val lessons = LessonContentGenerator.generateAllLessons()
                repository.insertLessons(lessons)
            } else {
                val hasGrammar = existingLessons.any { it.skill == "grammar" }
                if (!hasGrammar) {
                    val grammarOnly = LessonContentGenerator.generateAllLessons().filter { it.skill == "grammar" }
                    repository.insertLessons(grammarOnly)
                }
            }

            // Seed grammar progress entries based on lessons' topicKey from JSON content
            val allLessons = repository.getAllLessons().filter { it.skill == "grammar" }
            val gson = com.google.gson.Gson()
            allLessons.forEach { lesson ->
                val topicKey: String = try {
                    val map: Map<*, *> = gson.fromJson(lesson.content, Map::class.java)
                    (map["topicKey"] as? String) ?: (lesson.level.lowercase() + "_" + lesson.title.lowercase().replace(" ", "_"))
                } catch (e: Exception) {
                    lesson.level.lowercase() + "_" + lesson.title.lowercase().replace(" ", "_")
                }
                val existing = repository.getGrammarProgressByTopic(topicKey)
                if (existing == null) {
                    val progress = com.hellogerman.app.data.entities.GrammarProgress(
                        topicKey = topicKey,
                        level = lesson.level,
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
