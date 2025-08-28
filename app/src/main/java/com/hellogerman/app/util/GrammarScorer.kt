package com.hellogerman.app.util

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
}


