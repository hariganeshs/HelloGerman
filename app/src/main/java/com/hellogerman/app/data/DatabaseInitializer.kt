package com.hellogerman.app.data

import android.content.Context
import com.google.gson.Gson
import com.hellogerman.app.data.entities.HoerenContent
import com.hellogerman.app.data.entities.LesenContent
import com.hellogerman.app.data.entities.Lesson
import com.hellogerman.app.data.entities.Question
import com.hellogerman.app.data.entities.QuestionType
import com.hellogerman.app.data.entities.SchreibenContent
import com.hellogerman.app.data.entities.SprechenContent
import com.hellogerman.app.data.entities.VocabularyItem
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

            // Seed achievements into persistence (idempotent via REPLACE)
            try { repository.seedAchievements() } catch (_: Exception) {}
            
            // Check existing lessons and force reload if we have the new expanded content
            val existingLessons = repository.getAllLessons()
            val a1Lessons = existingLessons.filter { it.level == "A1" }
            val b1LesenExisting = existingLessons.count { it.level == "B1" && it.skill == "lesen" }
            val b1HoerenExisting = existingLessons.count { it.level == "B1" && it.skill == "hoeren" }
            val b1SchreibenExisting = existingLessons.count { it.level == "B1" && it.skill == "schreiben" }
            val b1SprechenExisting = existingLessons.count { it.level == "B1" && it.skill == "sprechen" }

            // Detect missing reading quizzes in existing lessons (legacy data without questions)
            val missingReadingQuizzes = try {
                val gson = Gson()
                val lesenSamples = existingLessons.filter { it.skill == "lesen" }.take(10)
                lesenSamples.any { lesson ->
                    val raw = lesson.content
                    if (!raw.contains("\"questions\"")) true else {
                        val parsed = try { gson.fromJson(raw, LesenContent::class.java) } catch (_: Exception) { null }
                        parsed?.questions.isNullOrEmpty()
                    }
                }
            } catch (_: Exception) { true }

            // Force reload if we don't have the expanded content (be more aggressive)
            val shouldForceReload = existingLessons.isEmpty() ||
                                   a1Lessons.size < 100 || // We should have 104+ A1 lessons with expanded content
                                   !existingLessons.any { it.source == "Goethe" } ||
                                   !existingLessons.any { it.source == "TELC" } ||
                                   !existingLessons.any { it.source == "ÖSD" } ||
                                   // Ensure expanded B1 content is present (45 per skill)
                                   b1LesenExisting < 40 || // ensure full B1 lesen set
                                   b1HoerenExisting < 40 ||
                                   b1SchreibenExisting < 40 ||
                                   b1SprechenExisting < 40 ||
                                   // Check total lesson count - should be around 1053 with expanded content
                                   existingLessons.size < 900 ||
                                   // Ensure reading lessons include quizzes
                                   missingReadingQuizzes

            if (shouldForceReload) {
                // Clear existing lessons and reload with expanded content
                repository.clearAllLessons()
                val lessons = LessonContentGenerator.generateAllLessons()
                
                // Debug: Print lesson counts by level
                val a1Count = lessons.filter { it.level == "A1" }.size
                val a2Count = lessons.filter { it.level == "A2" }.size
                val b1Count = lessons.filter { it.level == "B1" }.size
                val lesenA2Count = lessons.filter { it.level == "A2" && it.skill == "lesen" }.size
                val hoerenA2Count = lessons.filter { it.level == "A2" && it.skill == "hoeren" }.size
                val schreibenA2Count = lessons.filter { it.level == "A2" && it.skill == "schreiben" }.size
                val sprechenA2Count = lessons.filter { it.level == "A2" && it.skill == "sprechen" }.size
                val lesenB1Count = lessons.filter { it.level == "B1" && it.skill == "lesen" }.size
                val hoerenB1Count = lessons.filter { it.level == "B1" && it.skill == "hoeren" }.size
                val schreibenB1Count = lessons.filter { it.level == "B1" && it.skill == "schreiben" }.size
                val sprechenB1Count = lessons.filter { it.level == "B1" && it.skill == "sprechen" }.size

                android.util.Log.d("DatabaseInitializer", "Generated lessons - Total: ${lessons.size}")
                android.util.Log.d("DatabaseInitializer", "A1 lessons: $a1Count")
                android.util.Log.d("DatabaseInitializer", "A2 lessons: $a2Count")
                android.util.Log.d("DatabaseInitializer", "B1 lessons: $b1Count")
                android.util.Log.d("DatabaseInitializer", "A2 Lesen: $lesenA2Count")
                android.util.Log.d("DatabaseInitializer", "A2 Hören: $hoerenA2Count")
                android.util.Log.d("DatabaseInitializer", "A2 Schreiben: $schreibenA2Count")
                android.util.Log.d("DatabaseInitializer", "A2 Sprechen: $sprechenA2Count")
                android.util.Log.d("DatabaseInitializer", "B1 Lesen: $lesenB1Count")
                android.util.Log.d("DatabaseInitializer", "B1 Hören: $hoerenB1Count")
                android.util.Log.d("DatabaseInitializer", "B1 Schreiben: $schreibenB1Count")
                android.util.Log.d("DatabaseInitializer", "B1 Sprechen: $sprechenB1Count")
                
                repository.insertLessons(lessons)
            } else {
                // Original logic for grammar lessons
                val hasGrammar = existingLessons.any { it.skill == "grammar" }
                if (!hasGrammar) {
                    val grammarOnly = LessonContentGenerator.generateAllLessons().filter { it.skill == "grammar" }
                    repository.insertLessons(grammarOnly)
                } else {
                    // Check if existing grammar lessons have placeholder content and refresh them
                    val grammarLessons = existingLessons.filter { it.skill == "grammar" }
                    val hasPlaceholderContent = grammarLessons.any { lesson ->
                        lesson.content.contains("\"Wähle richtig\"") ||
                        lesson.content.contains("\"Wähle korrekt\"") ||
                        lesson.content.contains("\"A\",\"B\",\"C\"")
                    }
                    if (hasPlaceholderContent) {
                        // Delete old grammar lessons and insert fresh ones
                        repository.deleteGrammarLessons()
                        val grammarOnly = LessonContentGenerator.generateAllLessons().filter { it.skill == "grammar" }
                        repository.insertLessons(grammarOnly)
                    }
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