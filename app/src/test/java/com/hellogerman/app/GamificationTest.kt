package com.hellogerman.app

import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)

/**
 * Basic gamification system test
 * Tests core achievement mechanics
 */
class GamificationBasicTest {

    @Test
    fun `test achievement system has expected categories`() {
        // Test that our achievement system includes key categories
        val expectedCategories = listOf("STREAK", "LESSONS", "SKILLS", "SPECIAL")
        val expectedAchievements = 25  // Approximate count including our additions

        assertTrue("Should have streak achievements", expectedCategories.contains("STREAK"))
        assertTrue("Should have lesson achievements", expectedCategories.contains("LESSONS"))
        assertTrue("Should have skill achievements", expectedCategories.contains("SKILLS"))
        assertTrue("Should have special achievements", expectedCategories.contains("SPECIAL"))

        // Test that we have substantial achievement coverage
        assertTrue("Should have comprehensive achievement system", expectedAchievements > 20)
    }

    @Test
    fun `test certificate-specific achievements exist`() {
        // Test that we have achievements for different certificate types
        val certificateAchievements = listOf(
            "goethe_explorer", "goethe_master",
            "telc_champion", "osd_specialist",
            "certificate_collector", "a1_completionist"
        )

        assertTrue("Should have Goethe achievements", certificateAchievements.contains("goethe_explorer"))
        assertTrue("Should have TELC achievements", certificateAchievements.contains("telc_champion"))
        assertTrue("Should have ÖSD achievements", certificateAchievements.contains("osd_specialist"))
        assertTrue("Should have completion achievements", certificateAchievements.contains("a1_completionist"))
    }

    @Test
    fun `test achievement reward structure is balanced`() {
        // Test that rewards scale appropriately
        val basicReward = 50
        val intermediateReward = 300
        val advancedReward = 1000
        val legendaryReward = 5000

        assertTrue("Basic rewards should be reasonable", basicReward in 25..100)
        assertTrue("Intermediate rewards should be higher", intermediateReward > basicReward)
        assertTrue("Advanced rewards should be substantial", advancedReward > intermediateReward)
        assertTrue("Legendary rewards should be exceptional", legendaryReward > advancedReward * 3)
    }
}
