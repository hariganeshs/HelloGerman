package com.hellogerman.app.data.repository

import com.hellogerman.app.data.entities.UserProgress
import org.junit.Assert.*
import org.junit.Test

class HelloGermanRepositoryTest {

    @Test
    fun `test level unlock status data class works correctly`() {
        // Test the data classes used in repository
        val levelUnlockStatus = LevelUnlockStatus(
            currentLevel = "A2",
            overallProgress = 85.5,
            skillProgress = mapOf("lesen" to 80.0, "hoeren" to 85.0),
            canUnlock = true,
            lessonsCompleted = 20,
            totalLessons = 25,
            nextLevel = "B1"
        )

        assertTrue("Should be able to unlock", levelUnlockStatus.canUnlock)
        assertEquals("Next level should be B1", "B1", levelUnlockStatus.nextLevel)
        assertEquals("Current level should be A2", "A2", levelUnlockStatus.currentLevel)
        assertEquals("Lessons completed should be 20", 20, levelUnlockStatus.lessonsCompleted)
    }

    @Test
    fun `test level completion info data class works correctly`() {
        val levelCompletionInfo = LevelCompletionInfo(
            completed = 20,
            total = 25,
            averageScore = 78.5,
            progressPercentage = 80.0,
            isComplete = false
        )

        assertEquals("Completed should be 20", 20, levelCompletionInfo.completed)
        assertEquals("Total should be 25", 25, levelCompletionInfo.total)
        assertEquals("Average score should be 78.5", 78.5, levelCompletionInfo.averageScore, 0.1)
        assertEquals("Progress percentage should be 80%", 80.0, levelCompletionInfo.progressPercentage, 0.1)
        assertFalse("Should not be complete", levelCompletionInfo.isComplete)
    }

    @Test
    fun `test user progress entity has correct structure for A2 level`() {
        val userProgress = UserProgress(
            id = 1,
            currentLevel = "A2",
            lesenScore = 80,
            hoerenScore = 75,
            schreibenScore = 85,
            sprechenScore = 70,
            grammarScore = 78,
            totalLessonsCompleted = 20,
            currentStreak = 5
        )

        assertEquals("Current level should be A2", "A2", userProgress.currentLevel)
        assertEquals("Lesen score should be 80", 80, userProgress.lesenScore)
        assertEquals("Hören score should be 75", 75, userProgress.hoerenScore)
        assertEquals("Schreiben score should be 85", 85, userProgress.schreibenScore)
        assertEquals("Sprechen score should be 70", 70, userProgress.sprechenScore)
        assertEquals("Grammar score should be 78", 78, userProgress.grammarScore)
        assertEquals("Total lessons completed should be 20", 20, userProgress.totalLessonsCompleted)
        assertEquals("Current streak should be 5", 5, userProgress.currentStreak)
    }
}
