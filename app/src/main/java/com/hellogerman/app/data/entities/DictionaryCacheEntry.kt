package com.hellogerman.app.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.hellogerman.app.data.converters.DictionarySearchResultConverter
import com.hellogerman.app.data.models.DictionarySearchResult

/**
 * Entity for caching dictionary search results in Room database
 */
@Entity(tableName = "dictionary_cache")
@TypeConverters(DictionarySearchResultConverter::class)
data class DictionaryCacheEntry(
    @PrimaryKey val word: String,
    val fromLanguage: String,
    val toLanguage: String,
    val searchResult: DictionarySearchResult,
    val sources: List<String>, // List of sources used (e.g., ["Wiktionary", "Tatoeba", "OpenThesaurus"])
    val fetchedAt: Long = System.currentTimeMillis(),
    val expiresAt: Long = System.currentTimeMillis() + (72 * 60 * 60 * 1000), // 72 hours TTL
    val cacheVersion: Int = 1 // For cache invalidation when data models change
)
