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
		assertEquals(4, badges.size)
	}

	@Test
	fun testGetBadgeEligibility_lowPerformance() {
		val badges = GrammarScorer.getBadgeEligibility(0.6f, 200, 1, "A1")
		assertTrue(badges.isEmpty())
	}

	@Test
	fun testGetBadgeEligibility_longStreak() {
		val badges = GrammarScorer.getBadgeEligibility(0.8f, 100, 35, "B1")
		assertTrue(badges.contains("monthly_champion"))
		assertTrue(badges.contains("weekly_warrior"))
		assertTrue(badges.contains("dedicated_learner"))
		assertTrue(badges.contains("grammar_master"))
		assertTrue(badges.contains("quick_learner"))
		assertTrue(badges.contains("b1_graduate"))
	}

	@Test
	fun testGetBadgeEligibility_highLevelC2() {
		val badges = GrammarScorer.getBadgeEligibility(0.85f, 150, 10, "C2")
		assertTrue(badges.contains("grammar_master"))
		assertTrue(badges.contains("weekly_warrior"))
		assertTrue(badges.contains("c2_graduate"))
	}

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
		val isCorrect = GrammarScorer.checkGrammarAnswer("der große Mann", "der große Mann", GrammarRule.ADJECTIVE_DECLENSION)
		assertTrue(isCorrect)
		
		val isWrong = GrammarScorer.checkGrammarAnswer("der großer Mann", "der große Mann", GrammarRule.ADJECTIVE_DECLENSION)
		assertFalse(isWrong)
	}

	@Test
	fun testTimeBonusCalculation() {
		// Test various time scenarios
		assertEquals(15, GrammarScorer.calculateAdvancedScore(5, 5, 100) - 100) // Fast bonus
		assertEquals(10, GrammarScorer.calculateAdvancedScore(5, 5, 200) - 100) // Medium bonus  
		assertEquals(5, GrammarScorer.calculateAdvancedScore(5, 5, 400) - 100)  // Small bonus
		assertEquals(0, GrammarScorer.calculateAdvancedScore(5, 5, 700) - 100)  // No bonus
	}
}