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
            println("DEBUG: Found ${existingLessons.size} lessons before clearing")
            
            // Clear all lessons and re-insert them to remove duplicates
            repository.clearAllLessons()
            val lessons = LessonContentGenerator.generateAllLessons()
            repository.insertLessons(lessons)
            
            // Get lesson count after re-inserting
            val newLessons = repository.getAllLessons()
            println("DEBUG: Inserted ${newLessons.size} lessons after clearing")
        }
    }
    
    fun resetEntireDatabase(context: Context) {
        val repository = HelloGermanRepository(context)
        
        CoroutineScope(Dispatchers.IO).launch {
            println("DEBUG: Resetting entire database...")
            
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
            println("DEBUG: Database reset complete. ${finalLessons.size} lessons inserted.")
        }
    }
}
