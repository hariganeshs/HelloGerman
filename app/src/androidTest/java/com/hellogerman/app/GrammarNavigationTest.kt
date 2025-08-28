package com.hellogerman.app

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import org.junit.Rule
import org.junit.Test

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
}


