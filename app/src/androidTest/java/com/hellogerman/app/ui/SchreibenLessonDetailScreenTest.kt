package com.hellogerman.app.ui

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.hellogerman.app.MainActivity
import com.hellogerman.app.R
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SchreibenLessonDetailScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun testSchreibenLessonDetailScreenDisplaysCorrectly() {
        // Navigate to Schreiben section
        composeTestRule.onNodeWithText("Schreiben").performClick()

        // Wait for the screen to load
        composeTestRule.waitForIdle()

        // Check if A2 level is available
        composeTestRule.onNodeWithText("A2").assertExists()

        // Click on A2 level
        composeTestRule.onNodeWithText("A2").performClick()

        // Check if lessons are displayed
        composeTestRule.onNodeWithText("A2").assertExists()

        // Click on first lesson
        composeTestRule.onAllNodesWithText("Lesson").onFirst().performClick()

        // Check if lesson detail screen loads
        composeTestRule.onNodeWithText("Writing Prompt").assertExists()
    }

    @Test
    fun testWritingEditorFunctionality() {
        // Navigate to a Schreiben lesson
        composeTestRule.onNodeWithText("Schreiben").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("A2").performClick()
        composeTestRule.onAllNodesWithText("Lesson").onFirst().performClick()

        // Wait for lesson to load
        composeTestRule.waitForIdle()

        // Check if writing editor is present
        composeTestRule.onNodeWithText("Start Writing").assertExists()

        // Click start writing
        composeTestRule.onNodeWithText("Start Writing").performClick()

        // Check if text field appears
        composeTestRule.onNode(hasSetTextAction()).assertExists()

        // Type some text
        composeTestRule.onNode(hasSetTextAction()).performTextInput("Das ist ein Test.")

        // Check if word count updates
        composeTestRule.onNodeWithText("Words: 4").assertExists()
    }

    @Test
    fun testTimerFunctionality() {
        // Navigate to a Schreiben lesson
        composeTestRule.onNodeWithText("Schreiben").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("A2").performClick()
        composeTestRule.onAllNodesWithText("Lesson").onFirst().performClick()

        // Start writing
        composeTestRule.onNodeWithText("Start Writing").performClick()

        // Check if timer is displayed
        composeTestRule.onNodeWithContentDescription("Timer").assertExists()
    }

    @Test
    fun testFeedbackSystem() {
        // Navigate to a Schreiben lesson
        composeTestRule.onNodeWithText("Schreiben").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("A2").performClick()
        composeTestRule.onAllNodesWithText("Lesson").onFirst().performClick()

        // Start writing and submit
        composeTestRule.onNodeWithText("Start Writing").performClick()
        composeTestRule.onNode(hasSetTextAction()).performTextInput("Das ist ein Test.")
        composeTestRule.onNodeWithText("Submit").performClick()

        // Check if feedback is displayed
        composeTestRule.onNodeWithText("Feedback").assertExists()
    }

    @Test
    fun testGrammarFeedbackDisplay() {
        // Navigate to a lesson and submit text with errors
        composeTestRule.onNodeWithText("Schreiben").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("A2").performClick()
        composeTestRule.onAllNodesWithText("Lesson").onFirst().performClick()

        composeTestRule.onNodeWithText("Start Writing").performClick()
        composeTestRule.onNode(hasSetTextAction()).performTextInput("ich gehe ins kino")
        composeTestRule.onNodeWithText("Submit").performClick()

        // Check if grammar feedback appears
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Grammatik").assertExists()
    }

    @Test
    fun testNavigationBetweenSteps() {
        // Test navigation between prompt, writing, and feedback steps
        composeTestRule.onNodeWithText("Schreiben").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("A2").performClick()
        composeTestRule.onAllNodesWithText("Lesson").onFirst().performClick()

        // Should start at prompt step
        composeTestRule.onNodeWithText("Writing Prompt").assertExists()

        // Move to writing step
        composeTestRule.onNodeWithText("Start Writing").performClick()
        composeTestRule.onNodeWithText("Submit").assertExists()

        // Submit and move to feedback step
        composeTestRule.onNode(hasSetTextAction()).performTextInput("Test text")
        composeTestRule.onNodeWithText("Submit").performClick()
        composeTestRule.onNodeWithText("Feedback").assertExists()
    }
}
