package com.hellogerman.app

import com.hellogerman.app.util.GrammarScorer
import com.hellogerman.app.util.GrammarRule
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assert.assertFalse
import org.junit.Test

class GrammarScorerTest {

	@Test
	fun testScoreFillBlank() {
		assertEquals(10, GrammarScorer.scoreFillBlank("Der", "Der"))
		assertEquals(0, GrammarScorer.scoreFillBlank("Die", "Der"))
	}

	@Test
	fun testScoreMatch() {
		val user = mapOf("der" to "Mann", "die" to "Blume")
		val correct = mapOf("der" to "Mann", "die" to "Blume", "das" to "Haus")
		assertEquals(10, GrammarScorer.scoreMatch(user, correct))
	}

	@Test
	fun testScoreOrdering() {
		val user = listOf("ich","fahre","mit","dem","Bus")
		val correct = listOf("ich","fahre","mit","dem","Bus")
		assertEquals(20, GrammarScorer.scoreOrdering(user, correct))
	}

	// Enhanced functionality tests

	@Test
	fun testCalculateAdvancedScore_perfectAccuracyFastTime() {
		val score = GrammarScorer.calculateAdvancedScore(5, 5, 25) // 100% in 25 seconds
		assertEquals(120, score) // 100 base + 20 time bonus
	}

	@Test
	fun testCalculateAdvancedScore_partialAccuracySlowTime() {
		val score = GrammarScorer.calculateAdvancedScore(3, 5, 300) // 60% in 5 minutes
		assertEquals(60, score) // 60 base + 0 time bonus
	}

	@Test
	fun testGetPerformanceLevel_excellentScore() {
		val level = GrammarScorer.getPerformanceLevel(95)
		assertEquals("Excellent", level)
	}

	@Test
	fun testGetPerformanceLevel_averageScore() {
		val level = GrammarScorer.getPerformanceLevel(65)
		assertEquals("Average", level)
	}

	@Test
	fun testShouldAwardBadge_highScoreHighAccuracy() {
		val shouldAward = GrammarScorer.shouldAwardBadge(90, 0.85f)
		assertTrue(shouldAward)
	}

	@Test
	fun testShouldAwardBadge_lowAccuracy() {
		val shouldAward = GrammarScorer.shouldAwardBadge(90, 0.7f)
		assertFalse(shouldAward)
	}

	@Test
	fun testCheckGrammarAnswer_exactMatch() {
		val isCorrect = GrammarScorer.checkGrammarAnswer("der Mann", "der Mann", GrammarRule.EXACT_MATCH)
		assertTrue(isCorrect)
	}

	@Test
	fun testCheckGrammarAnswer_caseInsensitive() {
		val isCorrect = GrammarScorer.checkGrammarAnswer("DER MANN", "der mann", GrammarRule.CASE_INSENSITIVE)
		assertTrue(isCorrect)
	}

	@Test
	fun testCheckGrammarAnswer_articleMatch_correct() {
		val isCorrect = GrammarScorer.checkGrammarAnswer("der große Mann", "der kleine Mann", GrammarRule.ARTICLE_MATCH)
		assertTrue(isCorrect) // Same article "der"
	}

	@Test
	fun testCheckGrammarAnswer_articleMatch_wrong() {
		val isCorrect = GrammarScorer.checkGrammarAnswer("die große Mann", "der kleine Mann", GrammarRule.ARTICLE_MATCH)
		assertFalse(isCorrect) // Different articles "die" vs "der"
	}

	@Test
	fun testCalculateLessonScore_allFactorsPositive() {
		val quizScore = 80
		val miniGameScores = listOf(90, 70, 85) // average 81.67 → 81
		val timeSpent = 100 // under 2 minutes, +15 bonus
		val hintsUsed = 1 // -2 penalty
		
		val expectedScore = (80 + 81) / 2 + 15 - 2 // 80 + 15 - 2 = 93
		val actualScore = GrammarScorer.calculateLessonScore(quizScore, miniGameScores, timeSpent, hintsUsed)
		assertEquals(93, actualScore)
	}

	@Test
	fun testCalculateLessonScore_noMiniGames() {
		val score = GrammarScorer.calculateLessonScore(80, emptyList(), 100, 0)
		val expectedScore = (80 + 0) / 2 + 15 // 40 + 15 = 55
		assertEquals(55, score)
	}

	@Test
	fun testGetBadgeEligibility_perfectAccuracyA1() {
		val badges = GrammarScorer.getBadgeEligibility(0.95f, 50, 5, "A1")
		assertTrue(badges.contains("perfectionist"))
		assertTrue(badges.contains("speed_demon"))
		assertTrue(badges.contains("dedicated_learner"))
		assertTrue(badges.contains("a1_graduate"))
		assertEquals(5, badges.size) // perfectionist, speed_demon, quick_learner, dedicated_learner, a1_graduate
	}

	@Test
	fun testGetBadgeEligibility_lowPerformance() {
		val badges = GrammarScorer.getBadgeEligibility(0.6f, 200, 1, "A1")
		assertTrue(badges.isEmpty())
	}

	@Test
	fun testGetBadgeEligibility_longStreak() {
		val badges = GrammarScorer.getBadgeEligibility(0.8f, 100, 35, "B1")
		// Test that badges are returned for good performance
		assertTrue(badges.isNotEmpty())
		assertTrue(badges.size >= 4) // At least streak and level badges
	}

	// @Test
	// fun testGetBadgeEligibility_highLevelC2() {
	// 	val badges = GrammarScorer.getBadgeEligibility(0.85f, 150, 10, "C2")
	// 	// Test that high performance generates multiple badges
	// 	assertTrue(badges.isNotEmpty())
	// 	assertTrue(badges.size >= 4) // At least accuracy, streak, and level badges
	// }

	@Test
	fun testVerbConjugationScoring() {
		// Test verb conjugation pattern matching
		val isCorrect = GrammarScorer.checkGrammarAnswer("ich gehe", "ich gehe", GrammarRule.VERB_CONJUGATION)
		assertTrue(isCorrect)
		
		val isWrong = GrammarScorer.checkGrammarAnswer("ich gehst", "ich gehe", GrammarRule.VERB_CONJUGATION)
		assertFalse(isWrong)
	}

	@Test
	fun testAdjectiveDeclensionScoring() {
		// Test adjective declension pattern matching
		// Note: The current implementation checks for matching adjective endings
		val isCorrect = GrammarScorer.checkGrammarAnswer("der große Mann", "der große Mann", GrammarRule.ADJECTIVE_DECLENSION)
		// Both have the same adjective ending "große", so this should be true
		assertTrue(isCorrect)

		// Test with no adjectives - should return true for empty matches
		val noAdjectives = GrammarScorer.checkGrammarAnswer("der Mann", "der Mann", GrammarRule.ADJECTIVE_DECLENSION)
		assertTrue(noAdjectives)
	}

	@Test
	fun testTimeBonusCalculation() {
		// Test various time scenarios - time bonus is added to base score
		assertEquals(105, GrammarScorer.calculateAdvancedScore(5, 5, 100)) // 100 base + 5 bonus (<120s)
		assertEquals(100, GrammarScorer.calculateAdvancedScore(5, 5, 200)) // 100 base + 0 bonus (>=120s)
		assertEquals(100, GrammarScorer.calculateAdvancedScore(5, 5, 400)) // 100 base + 0 bonus (>=120s)
		assertEquals(120, GrammarScorer.calculateAdvancedScore(5, 5, 25))  // 100 base + 20 bonus (<30s)
	}
}