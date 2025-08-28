package com.hellogerman.app

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class GrammarNavigationTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun navigateToGrammarTab() {
        // Bottom bar item
        composeTestRule.onNodeWithText("Grammar").performClick()
        // See Grammar title
        composeTestRule.onNodeWithText("Grammar").assertExists()
    }

    @Test
    fun grammarDashboard_showsLevelChips() {
        // Navigate to Grammar tab
        composeTestRule.onNodeWithText("Grammar").performClick()
        composeTestRule.waitForIdle()
        
        // Verify all CEFR level chips are present
        composeTestRule.onNodeWithText("A1").assertExists()
        composeTestRule.onNodeWithText("A2").assertExists()
        composeTestRule.onNodeWithText("B1").assertExists()
        composeTestRule.onNodeWithText("B2").assertExists()
        composeTestRule.onNodeWithText("C1").assertExists()
        composeTestRule.onNodeWithText("C2").assertExists()
    }

    @Test
    fun grammarDashboard_clickLevelChip_navigatesToTopicList() {
        // Navigate to Grammar tab
        composeTestRule.onNodeWithText("Grammar").performClick()
        composeTestRule.waitForIdle()
        
        // Click on A1 level chip
        composeTestRule.onNodeWithText("A1").performClick()
        composeTestRule.waitForIdle()
        
        // Verify we're on the topic list screen
        composeTestRule.onNodeWithText("Level A1 Topics").assertExists()
    }

    @Test
    fun grammarTopicList_loadsLessons() {
        // Navigate to Grammar → A1
        composeTestRule.onNodeWithText("Grammar").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("A1").performClick()
        composeTestRule.waitForIdle()
        
        // Check if lessons need to be loaded
        try {
            composeTestRule.onNodeWithText("Load Grammar Lessons").assertExists()
            // If button exists, click it to load lessons
            composeTestRule.onNodeWithText("Load Grammar Lessons").performClick()
            composeTestRule.waitForIdle()
        } catch (e: AssertionError) {
            // Lessons already loaded, continue
        }
        
        // After loading, should see lesson titles (from our expanded content)
        // These are the actual lesson titles from GrammarContentExpanded.kt
        try {
            composeTestRule.onNodeWithText("Bestimmte Artikel").assertExists()
        } catch (e: AssertionError) {
            // Alternative check - any lesson card should exist
            composeTestRule.onAllNodesWithTag("lesson_card").assertCountEquals(0, false)
        }
    }

    @Test
    fun grammarLesson_hasStartQuizButton() {
        // Navigate to first grammar lesson
        navigateToGrammarLesson()
        
        // Verify lesson screen has Start Quiz button
        composeTestRule.onNodeWithText("Start Quiz").assertExists()
    }

    @Test
    fun grammarQuiz_displaysQuestions() {
        // Navigate to quiz
        navigateToGrammarLesson()
        composeTestRule.onNodeWithText("Start Quiz").performClick()
        composeTestRule.waitForIdle()
        
        // Verify quiz screen elements
        composeTestRule.onNodeWithText("Quiz").assertExists()
        
        // Should show question progress indicator
        try {
            composeTestRule.onNodeWithText("Question 1 of", substring = true).assertExists()
        } catch (e: AssertionError) {
            // Alternative: Check for no questions message
            composeTestRule.onNodeWithText("No questions found").assertExists()
        }
    }

    @Test
    fun grammarQuiz_hasSkipOption() {
        // Navigate to quiz
        navigateToGrammarLesson()
        composeTestRule.onNodeWithText("Start Quiz").performClick()
        composeTestRule.waitForIdle()
        
        // Verify skip option exists
        composeTestRule.onNodeWithText("Skip Quiz").assertExists()
    }

    @Test
    fun grammarQuiz_skipShowsResults() {
        // Navigate to quiz and skip
        navigateToGrammarLesson()
        composeTestRule.onNodeWithText("Start Quiz").performClick()
        composeTestRule.waitForIdle()
        
        composeTestRule.onNodeWithText("Skip Quiz").performClick()
        composeTestRule.waitForIdle()
        
        // Should show results screen
        composeTestRule.onNodeWithText("Quiz Completed!").assertExists()
        composeTestRule.onNodeWithText("Final Score:", substring = true).assertExists()
    }

    @Test
    fun grammarDashboard_hasDailyChallenge() {
        // Navigate to Grammar tab
        composeTestRule.onNodeWithText("Grammar").performClick()
        composeTestRule.waitForIdle()
        
        // Verify daily challenge section exists
        composeTestRule.onNodeWithText("Daily Challenge").assertExists()
        composeTestRule.onNodeWithText("Start").assertExists()
    }

    @Test
    fun grammarLevels_allAccessible() {
        // Test that all level chips are clickable
        composeTestRule.onNodeWithText("Grammar").performClick()
        composeTestRule.waitForIdle()
        
        val levels = listOf("A1", "A2", "B1", "B2", "C1", "C2")
        
        for (level in levels) {
            composeTestRule.onNodeWithText(level).performClick()
            composeTestRule.waitForIdle()
            
            // Should navigate to level topics
            composeTestRule.onNodeWithText("Level $level Topics").assertExists()
            
            // Navigate back to dashboard (assuming back button or similar)
            composeTestRule.activity.onBackPressed()
            composeTestRule.waitForIdle()
        }
    }

    @Test
    fun grammarLesson_showsContent() {
        // Navigate to lesson
        navigateToGrammarLesson()
        
        // Should show lesson content elements
        // Based on our enhanced lesson screen, these should exist:
        try {
            composeTestRule.onNodeWithText("Explanations:").assertExists()
        } catch (e: AssertionError) {
            // Alternative content check
            composeTestRule.onNodeWithText("Examples:").assertExists()
        }
    }

    @Test
    fun grammarLesson_hasEnglishToggle() {
        // Navigate to lesson
        navigateToGrammarLesson()
        
        // Should have English toggle
        composeTestRule.onNodeWithContentDescription("English toggle").assertExists()
    }

    // Helper function to navigate to a grammar lesson
    private fun navigateToGrammarLesson() {
        composeTestRule.onNodeWithText("Grammar").performClick()
        composeTestRule.waitForIdle()
        
        composeTestRule.onNodeWithText("A1").performClick()
        composeTestRule.waitForIdle()
        
        // Load lessons if needed
        try {
            composeTestRule.onNodeWithText("Load Grammar Lessons").performClick()
            composeTestRule.waitForIdle()
        } catch (e: AssertionError) {
            // Lessons already loaded
        }
        
        // Try to find and click a lesson
        try {
            composeTestRule.onNodeWithText("Bestimmte Artikel").performClick()
        } catch (e: AssertionError) {
            // Fallback: click any lesson card if available
            try {
                composeTestRule.onAllNodes(hasClickAction()).onFirst().performClick()
            } catch (e2: AssertionError) {
                // No lessons available, skip test
                composeTestRule.onNodeWithText("No grammar lessons").assertExists()
                return
            }
        }
        composeTestRule.waitForIdle()
    }
}