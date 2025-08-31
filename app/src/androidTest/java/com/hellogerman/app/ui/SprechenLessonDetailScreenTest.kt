package com.hellogerman.app.ui

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.hellogerman.app.MainActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SprechenLessonDetailScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun testSprechenLessonDetailScreenDisplaysCorrectly() {
        // Navigate to Sprechen section
        composeTestRule.onNodeWithText("Sprechen").performClick()

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
        composeTestRule.onNodeWithText("Speaking Prompt").assertExists()
    }

    @Test
    fun testModelResponsePlayback() {
        // Navigate to a Sprechen lesson
        composeTestRule.onNodeWithText("Sprechen").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("A2").performClick()
        composeTestRule.onAllNodesWithText("Lesson").onFirst().performClick()

        // Wait for lesson to load
        composeTestRule.waitForIdle()

        // Check if model response section exists
        composeTestRule.onNodeWithText("Model Response").assertExists()

        // Check if play button exists
        composeTestRule.onNodeWithContentDescription("Play").assertExists()
    }

    @Test
    fun testSpeechRecognitionInterface() {
        // Navigate to a Sprechen lesson
        composeTestRule.onNodeWithText("Sprechen").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("A2").performClick()
        composeTestRule.onAllNodesWithText("Lesson").onFirst().performClick()

        // Start recording
        composeTestRule.onNodeWithText("Start Recording").performClick()

        // Check if recording interface appears
        composeTestRule.onNodeWithContentDescription("Recording").assertExists()
        composeTestRule.onNodeWithText("Recording...").assertExists()
    }

    @Test
    fun testRecordingControls() {
        // Navigate to a Sprechen lesson
        composeTestRule.onNodeWithText("Sprechen").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("A2").performClick()
        composeTestRule.onAllNodesWithText("Lesson").onFirst().performClick()

        // Start recording
        composeTestRule.onNodeWithText("Start Recording").performClick()

        // Check for recording controls
        composeTestRule.onNodeWithText("Stop Recording").assertExists()
        composeTestRule.onNodeWithText("Record Again").assertExists()
    }

    @Test
    fun testFeedbackAfterRecording() {
        // This test would require mocking speech recognition
        // For now, we'll test the UI structure

        // Navigate to a Sprechen lesson
        composeTestRule.onNodeWithText("Sprechen").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("A2").performClick()
        composeTestRule.onAllNodesWithText("Lesson").onFirst().performClick()

        // Start recording (in a real scenario, this would capture audio)
        composeTestRule.onNodeWithText("Start Recording").performClick()

        // Simulate stopping recording
        composeTestRule.onNodeWithText("Stop Recording").performClick()

        // Check if submit button appears
        composeTestRule.onNodeWithText("Submit").assertExists()
    }

    @Test
    fun testTimerDisplay() {
        // Navigate to a Sprechen lesson
        composeTestRule.onNodeWithText("Sprechen").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("A2").performClick()
        composeTestRule.onAllNodesWithText("Lesson").onFirst().performClick()

        // Check if timer is displayed
        composeTestRule.onNodeWithContentDescription("Timer").assertExists()
    }

    @Test
    fun testNetworkStatusIndicator() {
        // Navigate to a Sprechen lesson
        composeTestRule.onNodeWithText("Sprechen").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("A2").performClick()
        composeTestRule.onAllNodesWithText("Lesson").onFirst().performClick()

        // Check if network status is shown
        composeTestRule.onNodeWithText("Network").assertExists()
    }

    @Test
    fun testPronunciationTipsDisplay() {
        // This test would require completing a speaking exercise
        // For now, test the general structure

        // Navigate to a Sprechen lesson
        composeTestRule.onNodeWithText("Sprechen").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("A2").performClick()
        composeTestRule.onAllNodesWithText("Lesson").onFirst().performClick()

        // The UI should be structured to show pronunciation tips in feedback
        composeTestRule.onNodeWithText("Speaking Prompt").assertExists()
    }
}
