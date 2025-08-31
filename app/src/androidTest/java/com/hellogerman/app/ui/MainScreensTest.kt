package com.hellogerman.app.ui

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.hellogerman.app.MainActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainScreensTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun testMainScreenDisplaysAllModules() {
        // Check if all main modules are displayed
        composeTestRule.onNodeWithText("Lesen").assertExists()
        composeTestRule.onNodeWithText("Hören").assertExists()
        composeTestRule.onNodeWithText("Schreiben").assertExists()
        composeTestRule.onNodeWithText("Sprechen").assertExists()
    }

    @Test
    fun testNavigationToLesenModule() {
        // Click on Lesen module
        composeTestRule.onNodeWithText("Lesen").performClick()

        // Check if Lesen screen loads
        composeTestRule.onNodeWithText("Reading Lessons").assertExists()
    }

    @Test
    fun testNavigationToHoerenModule() {
        // Click on Hören module
        composeTestRule.onNodeWithText("Hören").performClick()

        // Check if Hören screen loads
        composeTestRule.onNodeWithText("Listening Lessons").assertExists()
    }

    @Test
    fun testNavigationToSchreibenModule() {
        // Click on Schreiben module
        composeTestRule.onNodeWithText("Schreiben").performClick()

        // Check if Schreiben screen loads
        composeTestRule.onNodeWithText("Writing Lessons").assertExists()
    }

    @Test
    fun testNavigationToSprechenModule() {
        // Click on Sprechen module
        composeTestRule.onNodeWithText("Sprechen").performClick()

        // Check if Sprechen screen loads
        composeTestRule.onNodeWithText("Speaking Lessons").assertExists()
    }

    @Test
    fun testA2LevelAvailability() {
        // Test Lesen module
        composeTestRule.onNodeWithText("Lesen").performClick()
        composeTestRule.onNodeWithText("A2").assertExists()

        // Go back and test Hören module
        composeTestRule.onNodeWithContentDescription("Back").performClick()
        composeTestRule.onNodeWithText("Hören").performClick()
        composeTestRule.onNodeWithText("A2").assertExists()

        // Go back and test Schreiben module
        composeTestRule.onNodeWithContentDescription("Back").performClick()
        composeTestRule.onNodeWithText("Schreiben").performClick()
        composeTestRule.onNodeWithText("A2").assertExists()

        // Go back and test Sprechen module
        composeTestRule.onNodeWithContentDescription("Back").performClick()
        composeTestRule.onNodeWithText("Sprechen").performClick()
        composeTestRule.onNodeWithText("A2").assertExists()
    }

    @Test
    fun testA2LessonsCount() {
        // Test Lesen A2 lessons
        composeTestRule.onNodeWithText("Lesen").performClick()
        composeTestRule.onNodeWithText("A2").performClick()

        // Should have at least 20 lessons (we added 25)
        composeTestRule.onAllNodesWithText("Lesson").assertCountAtLeast(20)

        // Test Hören A2 lessons
        composeTestRule.onNodeWithContentDescription("Back").performClick()
        composeTestRule.onNodeWithText("Hören").performClick()
        composeTestRule.onNodeWithText("A2").performClick()
        composeTestRule.onAllNodesWithText("Lesson").assertCountAtLeast(20)

        // Test Schreiben A2 lessons
        composeTestRule.onNodeWithContentDescription("Back").performClick()
        composeTestRule.onNodeWithText("Schreiben").performClick()
        composeTestRule.onNodeWithText("A2").performClick()
        composeTestRule.onAllNodesWithText("Lesson").assertCountAtLeast(20)

        // Test Sprechen A2 lessons
        composeTestRule.onNodeWithContentDescription("Back").performClick()
        composeTestRule.onNodeWithText("Sprechen").performClick()
        composeTestRule.onNodeWithText("A2").performClick()
        composeTestRule.onAllNodesWithText("Lesson").assertCountAtLeast(20)
    }

    @Test
    fun testLessonTitlesAreDescriptive() {
        // Test Lesen A2 lessons have descriptive titles
        composeTestRule.onNodeWithText("Lesen").performClick()
        composeTestRule.onNodeWithText("A2").performClick()

        // Check that lesson titles contain relevant keywords
        composeTestRule.onNodeWithText("Travel").assertExists()
        composeTestRule.onNodeWithText("Email").assertExists()
    }

    @Test
    fun testBottomNavigation() {
        // Test navigation between different sections using bottom navigation
        composeTestRule.onNodeWithText("Progress").performClick()
        composeTestRule.onNodeWithText("Your Learning Progress").assertExists()

        // Navigate back to home
        composeTestRule.onNodeWithText("Home").performClick()
        composeTestRule.onNodeWithText("Lesen").assertExists()
    }

    @Test
    fun testBackNavigation() {
        // Test back navigation works properly
        composeTestRule.onNodeWithText("Lesen").performClick()
        composeTestRule.onNodeWithText("Reading Lessons").assertExists()

        composeTestRule.onNodeWithContentDescription("Back").performClick()
        composeTestRule.onNodeWithText("Lesen").assertExists()
    }
}
