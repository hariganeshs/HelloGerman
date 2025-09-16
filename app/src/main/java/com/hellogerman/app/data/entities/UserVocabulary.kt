package com.hellogerman.app.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity for storing user-added vocabulary words
 * Allows users to save words they want to study later
 */
@Entity(tableName = "user_vocabulary")
data class UserVocabulary(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val word: String, // German word
    val translation: String, // English translation
    val gender: String? = null, // der, die, das
    val level: String? = null, // A1, A2, B1, B2, C1, C2
    val category: String? = null, // General, Business, Medical, etc.
    val notes: String? = null, // User's personal notes
    val addedAt: Long = System.currentTimeMillis(),
    val lastReviewed: Long? = null, // When user last studied this word
    val reviewCount: Int = 0, // How many times user has reviewed this word
    val masteryLevel: Int = 0, // 0-5 scale of how well user knows this word
    val isFavorite: Boolean = false, // User can mark words as favorites
    val source: String = "dictionary" // Where the word was added from (dictionary, lesson, etc.)
)
