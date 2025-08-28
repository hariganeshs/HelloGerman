package com.hellogerman.app.data

import com.hellogerman.app.data.entities.*

// Grammar-specific data classes (not defined in Lesson.kt)
data class GrammarContent(
    val topicKey: String,
    val explanations: List<String>,
    val explanationsEn: List<String> = emptyList(),
    val examples: List<String>,
    val miniGames: List<GrammarMiniGame> = emptyList(),
    val quiz: List<GrammarQuestion> = emptyList()
)

data class GrammarQuestion(
    val question: String,
    val options: List<String>,
    val correct: String,
    val points: Int,
    val questionEn: String? = null
)

sealed class GrammarMiniGame {
    data class DragDrop(val buckets: List<String>, val items: List<Pair<String, String>>) : GrammarMiniGame()
    data class Match(val pairs: List<Pair<String, String>>) : GrammarMiniGame()
    data class FillBlank(val text: String, val answer: String) : GrammarMiniGame()
    data class SentenceBuilder(val words: List<String>, val correctOrder: List<String>) : GrammarMiniGame()
}
