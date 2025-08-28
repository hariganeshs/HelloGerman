package com.hellogerman.app.util

import java.util.regex.Pattern

object GrammarScorer {

	fun scoreFillBlank(user: String, correct: String): Int {
		return if (user.trim().equals(correct.trim(), ignoreCase = true)) 10 else 0
	}

	fun scoreMatch(userPairs: Map<String, String>, correctPairs: Map<String, String>): Int {
		if (correctPairs.isEmpty()) return 0
		var points = 0
		correctPairs.forEach { (k, v) ->
			if (userPairs[k]?.equals(v, ignoreCase = true) == true) points += 5
		}
		return points
	}

	fun scoreOrdering(userOrder: List<String>, correctOrder: List<String>): Int {
		if (correctOrder.isEmpty()) return 0
		val total = correctOrder.size
		val correct = userOrder.zip(correctOrder).count { it.first.equals(it.second, ignoreCase = true) }
		return ((correct.toFloat() / total) * 20).toInt()
	}
	
	/**
	 * Enhanced scoring with time and accuracy factors
	 */
	fun calculateAdvancedScore(correctAnswers: Int, totalQuestions: Int, timeSpent: Int): Int {
		if (totalQuestions == 0) return 0
		
		val baseScore = (correctAnswers.toFloat() / totalQuestions * 100).toInt()
		
		// Time bonus (faster completion gets bonus points)
		val timeBonus = when {
			timeSpent < 30 -> 20  // Very fast
			timeSpent < 60 -> 10  // Fast
			timeSpent < 120 -> 5  // Normal
			else -> 0             // Slow
		}
		
		return baseScore + timeBonus
	}
	
	fun getPerformanceLevel(score: Int): String {
		return when {
			score >= 90 -> "Excellent"
			score >= 75 -> "Good"
			score >= 60 -> "Average"
			else -> "Needs Improvement"
		}
	}
	
	fun shouldAwardBadge(score: Int, accuracy: Float): Boolean {
		return score >= 85 && accuracy >= 0.8f
	}
	
	/**
	 * Advanced grammar checking using regex patterns
	 */
	fun checkGrammarAnswer(userAnswer: String, correctAnswer: String, grammarRule: GrammarRule): Boolean {
		val cleanUser = userAnswer.trim().lowercase()
		val cleanCorrect = correctAnswer.trim().lowercase()
		
		return when (grammarRule) {
			GrammarRule.EXACT_MATCH -> cleanUser == cleanCorrect
			GrammarRule.CASE_INSENSITIVE -> cleanUser.equals(cleanCorrect, ignoreCase = true)
			GrammarRule.ARTICLE_MATCH -> checkArticleMatch(cleanUser, cleanCorrect)
			GrammarRule.VERB_CONJUGATION -> checkVerbConjugation(cleanUser, cleanCorrect)
			GrammarRule.ADJECTIVE_DECLENSION -> checkAdjectiveDeclension(cleanUser, cleanCorrect)
		}
	}
	
	private fun checkArticleMatch(userAnswer: String, correctAnswer: String): Boolean {
		val articlePattern = Pattern.compile("\\b(der|die|das|ein|eine|einen|einem|einer|des|dem|den)\\b")
		val userArticles = extractMatches(articlePattern, userAnswer)
		val correctArticles = extractMatches(articlePattern, correctAnswer)
		return userArticles == correctArticles
	}
	
	private fun checkVerbConjugation(userAnswer: String, correctAnswer: String): Boolean {
		// Check verb endings for regular patterns
		val verbEndingPattern = Pattern.compile("\\w+(e|st|t|en|et)\\b")
		val userVerbs = extractMatches(verbEndingPattern, userAnswer)
		val correctVerbs = extractMatches(verbEndingPattern, correctAnswer)
		return userVerbs == correctVerbs
	}
	
	private fun checkAdjectiveDeclension(userAnswer: String, correctAnswer: String): Boolean {
		// Check adjective endings
		val adjectivePattern = Pattern.compile("\\w+(e|er|es|en|em)\\b")
		val userAdjectives = extractMatches(adjectivePattern, userAnswer)
		val correctAdjectives = extractMatches(adjectivePattern, correctAnswer)
		return userAdjectives == correctAdjectives
	}
	
	private fun extractMatches(pattern: Pattern, text: String): List<String> {
		val matcher = pattern.matcher(text)
		val matches = mutableListOf<String>()
		while (matcher.find()) {
			matches.add(matcher.group())
		}
		return matches
	}
	
	/**
	 * Calculate lesson completion score with various factors
	 */
	fun calculateLessonScore(
		quizScore: Int,
		miniGameScores: List<Int>,
		timeSpent: Int,
		hintsUsed: Int
	): Int {
		val totalMiniGameScore = miniGameScores.sum()
		val avgMiniGameScore = if (miniGameScores.isNotEmpty()) totalMiniGameScore / miniGameScores.size else 0
		
		val baseScore = (quizScore + avgMiniGameScore) / 2
		val timeBonus = getTimeBonus(timeSpent)
		val hintPenalty = hintsUsed * 2 // -2 points per hint
		
		return maxOf(0, baseScore + timeBonus - hintPenalty)
	}
	
	private fun getTimeBonus(timeSpentSeconds: Int): Int {
		return when {
			timeSpentSeconds < 120 -> 15  // Under 2 minutes
			timeSpentSeconds < 300 -> 10  // Under 5 minutes
			timeSpentSeconds < 600 -> 5   // Under 10 minutes
			else -> 0
		}
	}
	
	/**
	 * Determine badge eligibility based on comprehensive performance
	 */
	fun getBadgeEligibility(
		accuracy: Float,
		speed: Int, // seconds
		streakDays: Int,
		lessonLevel: String
	): List<String> {
		val badges = mutableListOf<String>()
		
		// Accuracy badges
		when {
			accuracy >= 0.95f -> badges.add("perfectionist")
			accuracy >= 0.85f -> badges.add("grammar_master")
			accuracy >= 0.75f -> badges.add("good_student")
		}
		
		// Speed badges
		if (speed < 60) badges.add("speed_demon")
		if (speed < 120) badges.add("quick_learner")
		
		// Consistency badges
		when {
			streakDays >= 30 -> badges.add("monthly_champion")
			streakDays >= 7 -> badges.add("weekly_warrior")
			streakDays >= 3 -> badges.add("dedicated_learner")
		}
		
		// Level-specific badges
		when (lessonLevel) {
			"A1" -> if (accuracy >= 0.8f) badges.add("a1_graduate")
			"A2" -> if (accuracy >= 0.8f) badges.add("a2_graduate")
			"B1" -> if (accuracy >= 0.8f) badges.add("b1_graduate")
			"B2" -> if (accuracy >= 0.8f) badges.add("b2_graduate")
			"C1" -> if (accuracy >= 0.8f) badges.add("c1_graduate")
			"C2" -> if (accuracy >= 0.8f) badges.add("c2_graduate")
		}
		
		return badges
	}
}

enum class GrammarRule {
	EXACT_MATCH,
	CASE_INSENSITIVE,
	ARTICLE_MATCH,
	VERB_CONJUGATION,
	ADJECTIVE_DECLENSION
}


