package com.hellogerman.app

import com.hellogerman.app.util.GrammarScorer
import org.junit.Assert.assertEquals
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
}


