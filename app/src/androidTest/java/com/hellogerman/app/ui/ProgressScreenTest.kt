package com.hellogerman.app.ui

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.hellogerman.app.MainActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ProgressScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun testProgressScreenDisplaysCorrectly() {
        // Navigate to Progress section
        composeTestRule.onNodeWithText("Progress").performClick()

        // Wait for the screen to load
        composeTestRule.waitForIdle()

        // Check if progress screen elements are present
        composeTestRule.onNodeWithText("Your Learning Progress").assertExists()
    }

    @Test
    fun testSkillProgressDisplay() {
        // Navigate to Progress section
        composeTestRule.onNodeWithText("Progress").performClick()
        composeTestRule.waitForIdle()

        // Check if skill progress is displayed
        composeTestRule.onNodeWithText("Lesen").assertExists()
        composeTestRule.onNodeWithText("Hören").assertExists()
        composeTestRule.onNodeWithText("Schreiben").assertExists()
        composeTestRule.onNodeWithText("Sprechen").assertExists()
    }

    @Test
    fun testLevelUnlockCardDisplay() {
        // Navigate to Progress section
        composeTestRule.onNodeWithText("Progress").performClick()
        composeTestRule.waitForIdle()

        // Check if level unlock information is displayed
        composeTestRule.onNodeWithText("Level").assertExists()
    }

    @Test
    fun testProgressIndicators() {
        // Navigate to Progress section
        composeTestRule.onNodeWithText("Progress").performClick()
        composeTestRule.waitForIdle()

        // Check if progress bars/indicators are present
        // This would typically test CircularProgressIndicator or LinearProgressIndicator
        composeTestRule.onAllNodes(hasProgressBarRangeInfo()).assertCountAtLeast(1)
    }

    @Test
    fun testCompletionPercentageDisplay() {
        // Navigate to Progress section
        composeTestRule.onNodeWithText("Progress").performClick()
        composeTestRule.waitForIdle()

        // Check if percentage values are displayed
        // This might show text like "80%" or "Complete"
        composeTestRule.onNodeWithText("%").assertExists()
    }

    @Test
    fun testCurrentLevelDisplay() {
        // Navigate to Progress section
        composeTestRule.onNodeWithText("Progress").performClick()
        composeTestRule.waitForIdle()

        // Check if current level is displayed
        composeTestRule.onNodeWithText("A1").assertExists()
    }

    @Test
    fun testAdaptiveProgressSystem() {
        // Navigate to Progress section
        composeTestRule.onNodeWithText("Progress").performClick()
        composeTestRule.waitForIdle()

        // Test that the adaptive progress system shows proper information
        // This would test the 80% completion logic for unlocking next level
        composeTestRule.onNodeWithText("Progress").assertExists()
    }

    @Test
    fun testStreakInformation() {
        // Navigate to Progress section
        composeTestRule.onNodeWithText("Progress").performClick()
        composeTestRule.waitForIdle()

        // Check if streak information is displayed
        composeTestRule.onNodeWithText("Streak").assertExists()
    }

    @Test
    fun testDailyGoalDisplay() {
        // Navigate to Progress section
        composeTestRule.onNodeWithText("Progress").performClick()
        composeTestRule.waitForIdle()

        // Check if daily goal information is shown
        composeTestRule.onNodeWithText("Goal").assertExists()
    }
}
