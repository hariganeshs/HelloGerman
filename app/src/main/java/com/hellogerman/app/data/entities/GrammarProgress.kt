package com.hellogerman.app.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "grammar_progress")
data class GrammarProgress(
	@PrimaryKey(autoGenerate = true) val id: Int = 0,
	val topicKey: String, // e.g., a1_articles_definite, b1_relative_clauses
	val level: String, // A1..C2
	val points: Int = 0,
	val badgesJson: String = "[]", // JSON array of badge ids
	val streak: Int = 0,
	val lastCompleted: Long = 0L,
	val completedLessons: Int = 0,
	val totalLessons: Int = 0
)


