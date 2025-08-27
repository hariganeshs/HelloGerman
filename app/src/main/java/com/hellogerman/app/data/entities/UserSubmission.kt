package com.hellogerman.app.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_submissions")
data class UserSubmission(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val lessonId: Int,
    val skill: String, // schreiben, sprechen
    val content: String, // text for writing, audio file path for speaking
    val score: Int = 0,
    val feedback: String = "",
    val submittedAt: Long = System.currentTimeMillis(),
    val timeSpent: Int = 0, // in seconds
    val wordCount: Int = 0,
    val audioDuration: Int = 0 // in seconds, for speaking submissions
)
