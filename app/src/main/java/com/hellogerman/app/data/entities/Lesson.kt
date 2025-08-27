package com.hellogerman.app.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "lessons")
data class Lesson(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val description: String,
    val level: String, // A1, A2, B1, B2, C1, C2
    val skill: String, // lesen, hoeren, schreiben, sprechen
    val content: String, // JSON string containing lesson content
    val isCompleted: Boolean = false,
    val score: Int = 0,
    val timeSpent: Int = 0, // in seconds
    val createdAt: Long = System.currentTimeMillis(),
    val orderIndex: Int = 0
)

// Content types for different skills
data class LesenContent(
    val text: String,
    val questions: List<Question>,
    val vocabulary: List<VocabularyItem>,
    val timeLimit: Int = 600 // 10 minutes default
)

data class HoerenContent(
    val script: String,
    val audioUrl: String? = null,
    val questions: List<Question>,
    val timeLimit: Int = 300 // 5 minutes default
)

data class SchreibenContent(
    val prompt: String,
    val minWords: Int,
    val maxWords: Int,
    val timeLimit: Int = 900, // 15 minutes default
    val tips: List<String> = emptyList()
)

data class SprechenContent(
    val prompt: String,
    val modelResponse: String,
    val timeLimit: Int = 120, // 2 minutes default
    val keywords: List<String> = emptyList()
)

data class Question(
    val id: Int,
    val question: String,
    val options: List<String>? = null,
    val correctAnswer: String,
    val correctAnswers: List<String>? = null, // For multiple correct answers
    val type: QuestionType, // MULTIPLE_CHOICE, TRUE_FALSE, FILL_BLANK, etc.
    val points: Int = 1,
    val questionEnglish: String? = null, // English translation for beginner levels
    val optionsEnglish: List<String>? = null, // English translations for options
    val matchingItems: Map<String, String>? = null, // For matching questions: key->value pairs
    val textForGaps: String? = null, // For gap-fill questions
    val gaps: List<String>? = null // For gap-fill questions: correct answers for gaps
)

enum class QuestionType {
    MULTIPLE_CHOICE,           // Single correct answer from options
    MULTIPLE_CORRECT_ANSWERS,  // Multiple correct answers from options
    TRUE_FALSE,               // True/False questions
    FILL_BLANK,              // Fill in the blank
    GAP_FILL,                // Fill gaps in a text
    TEXT_MATCHING,           // Match items from two columns
    OPEN_ENDED,              // Free text response
    DRAG_DROP,               // Drag and drop answers
    HOT_SPOT,                // Click on correct areas
    ORDERING                 // Put items in correct order
}

data class VocabularyItem(
    val word: String,
    val translation: String,
    val example: String
)
