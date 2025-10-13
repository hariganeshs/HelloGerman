package com.hellogerman.app.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "dictionary_cache")
data class DictionaryCache(
    @PrimaryKey
    val word: String,
    val fromLanguage: String,
    val toLanguage: String,
    val searchResult: String,
    val sources: String,
    val fetchedAt: Long,
    val expiresAt: Long,
    val cacheVersion: Int
)