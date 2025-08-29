package com.hellogerman.app.data.models

import com.google.gson.annotations.SerializedName

/**
 * Request model for dictionary searches
 */
data class DictionarySearchRequest(
    val word: String,
    val fromLang: String = "de",
    val toLang: String = "en"
)

/**
 * Legacy alias for backward compatibility
 */
typealias TranslationRequest = DictionarySearchRequest

/**
 * Data models for MyMemory Translation API responses
 */

data class MyMemoryTranslationResponse(
    @SerializedName("responseData")
    val responseData: ResponseData,
    
    @SerializedName("responseStatus")
    val responseStatus: Int,
    
    @SerializedName("responseDetails")
    val responseDetails: String?,
    
    @SerializedName("matches")
    val matches: List<Match>?
)

data class ResponseData(
    @SerializedName("translatedText")
    val translatedText: String,
    
    @SerializedName("match")
    val match: Float
)

data class Match(
    @SerializedName("id")
    val id: String,
    
    @SerializedName("segment")
    val segment: String,
    
    @SerializedName("translation")
    val translation: String,
    
    @SerializedName("quality")
    val quality: String?,
    
    @SerializedName("reference")
    val reference: String?,
    
    @SerializedName("usage-count")
    val usageCount: Int?,
    
    @SerializedName("subject")
    val subject: String?,
    
    @SerializedName("created-by")
    val createdBy: String?,
    
    @SerializedName("last-updated-by")
    val lastUpdatedBy: String?,
    
    @SerializedName("create-date")
    val createDate: String?,
    
    @SerializedName("last-update-date")
    val lastUpdateDate: String?,
    
    @SerializedName("match")
    val match: Float
)

/**
 * Comprehensive dictionary search result for enhanced UI
 */
data class DictionarySearchResult(
    val originalWord: String,
    val translations: List<String> = emptyList(),
    val fromLanguage: String,
    val toLanguage: String,
    val hasResults: Boolean,
    
    // Enhanced dictionary features
    val definitions: List<Definition> = emptyList(),
    val examples: List<Example> = emptyList(),
    val synonyms: List<String> = emptyList(),
    val antonyms: List<String> = emptyList(),
    val pronunciation: Pronunciation? = null,
    val conjugations: VerbConjugations? = null,
    val etymology: String? = null,
    val wordType: String? = null, // noun, verb, adjective, etc.
    val gender: String? = null, // for German nouns (der, die, das)
    val difficulty: String? = null // CEFR level (A1, A2, etc.)
)



// Enhanced dictionary data models

data class Definition(
    val meaning: String,
    val partOfSpeech: String? = null,
    val context: String? = null,
    val level: String? = null
)

data class Example(
    val sentence: String,
    val translation: String? = null,
    val source: String? = null
)

data class Pronunciation(
    val ipa: String? = null,
    val audioUrl: String? = null,
    val region: String? = null
)

/**
 * Wiktionary API response models
 */
data class WiktionaryResponse(
    @SerializedName("parse")
    val parse: WiktionaryParse?
)

data class WiktionaryParse(
    @SerializedName("title")
    val title: String,
    
    @SerializedName("pageid")
    val pageId: Int,
    
    @SerializedName("wikitext")
    val wikitext: WiktionaryWikitext?
)

data class WiktionaryWikitext(
    @SerializedName("*")
    val content: String
)

/**
 * German Verb API response models
 */
data class VerbConjugationResponse(
    @SerializedName("verb")
    val verb: String,
    
    @SerializedName("conjugations")
    val conjugations: VerbConjugations
)

data class VerbConjugations(
    @SerializedName("present")
    val present: Map<String, String> = emptyMap(),
    
    @SerializedName("past")
    val past: Map<String, String> = emptyMap(),
    
    @SerializedName("future")
    val future: Map<String, String> = emptyMap(),
    
    @SerializedName("participle")
    val participle: Participle? = null,
    
    @SerializedName("imperative")
    val imperative: Map<String, String> = emptyMap(),
    
    @SerializedName("subjunctive")
    val subjunctive: Map<String, String> = emptyMap()
)

data class Participle(
    @SerializedName("present")
    val present: String? = null,
    
    @SerializedName("past")
    val past: String? = null
)

/**
 * OpenThesaurus API response models
 */
data class OpenThesaurusResponse(
    @SerializedName("synsets")
    val synsets: List<Synset>
)

data class Synset(
    @SerializedName("id")
    val id: Int,
    
    @SerializedName("categories")
    val categories: List<String>,
    
    @SerializedName("terms")
    val terms: List<Term>
)

data class Term(
    @SerializedName("term")
    val term: String,
    
    @SerializedName("level")
    val level: Int? = null
)

/**
 * Cached dictionary entry for offline access
 */
data class CachedDictionaryEntry(
    val word: String,
    val language: String,
    val result: DictionarySearchResult,
    val timestamp: Long = System.currentTimeMillis()
)
