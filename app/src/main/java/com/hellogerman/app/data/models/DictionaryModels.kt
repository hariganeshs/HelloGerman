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
    val pronunciationInfo: PronunciationInfo? = null,
    val conjugations: VerbConjugations? = null,
    val etymology: String? = null,
    val wordType: String? = null, // noun, verb, adjective, etc.
    val gender: String? = null, // for German nouns (der, die, das)
    val difficulty: String? = null, // CEFR level (A1, A2, etc.)
    val wikidataLexemeData: WikidataLexemeData? = null // Enhanced grammatical information
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

data class PronunciationInfo(
    val ipa: String,
    val audioUrl: String? = null,
    val isAvailable: Boolean = true
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
    val subjunctive: Map<String, String> = emptyMap(),
    
    // Enhanced conjugation features
    val perfect: Map<String, String> = emptyMap(), // Perfect tense (haben/sein + past participle)
    val pluperfect: Map<String, String> = emptyMap(), // Pluperfect tense
    val futurePerfect: Map<String, String> = emptyMap(), // Future perfect tense
    val conditional: Map<String, String> = emptyMap(), // Conditional mood
    val auxiliary: String? = null, // haben or sein for perfect tenses
    val separablePrefix: String? = null, // For separable verbs like "anfangen"
    val infinitive: String? = null, // Base form of the verb
    val isIrregular: Boolean = false, // Whether the verb has irregular forms
    val isSeparable: Boolean = false // Whether the verb has a separable prefix
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
 * Reverso Context API response models
 */
data class ReversoExample(
    @SerializedName("src")
    val sourceText: String,

    @SerializedName("trg")
    val targetText: String,

    @SerializedName("src_context")
    val sourceContext: String? = null,

    @SerializedName("trg_context")
    val targetContext: String? = null,

    @SerializedName("src_lang")
    val sourceLang: String,

    @SerializedName("trg_lang")
    val targetLang: String
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

/**
 * English Dictionary API response models (Free Dictionary API)
 */
data class EnglishWordDefinition(
    val word: String,
    val phonetic: String? = null,
    val phonetics: List<Phonetic>? = null,
    val meanings: List<EnglishMeaning>,
    val license: License? = null,
    val sourceUrls: List<String>? = null
)

data class Phonetic(
    val text: String? = null,
    val audio: String? = null,
    val sourceUrl: String? = null,
    val license: License? = null
)

data class EnglishMeaning(
    val partOfSpeech: String,
    val definitions: List<EnglishDefinition>,
    val synonyms: List<String>? = null,
    val antonyms: List<String>? = null
)

data class EnglishDefinition(
    val definition: String,
    val synonyms: List<String>? = null,
    val antonyms: List<String>? = null,
    val example: String? = null
)

data class License(
    val name: String,
    val url: String
)

/**
 * LibreTranslate API response models
 */
data class LibreTranslateResponse(
    val translatedText: String,
    val detectedLanguage: DetectedLanguage? = null
)

data class DetectedLanguage(
    val confidence: Double,
    val language: String
)

/**
 * Wikidata Lexeme data model for enhanced grammatical information
 */
data class WikidataLexemeData(
    val lexemeId: String? = null,
    val lexicalCategory: String? = null, // noun, verb, adjective, etc.
    val language: String? = null,
    val grammaticalFeatures: List<String> = emptyList(),
    val forms: List<WikidataForm> = emptyList(),
    val senses: List<WikidataSense> = emptyList(),
    val gender: String? = null, // for German nouns (masculine, feminine, neuter)
    val plural: String? = null,
    val declensions: Map<String, String> = emptyMap() // case -> form
)

data class WikidataForm(
    val id: String,
    val representation: String,
    val grammaticalFeatures: List<String> = emptyList()
)

data class WikidataSense(
    val id: String,
    val gloss: String? = null
)

/**
 * Pronunciation data model for Wiktionary API
 */
data class PronunciationData(
    val ipa: String? = null,
    val audioUrl: String? = null
)

/**
 * Grammar data models for Leo-style comprehensive grammar information
 */

// Noun declension information
data class NounDeclension(
    val nominative: String,
    val genitive: String,
    val dative: String,
    val accusative: String,
    val plural: String
)

// Verb conjugation information
data class VerbConjugation(
    val infinitive: String,
    val present: Map<String, String>,
    val past: Map<String, String>,
    val perfect: Map<String, String>,
    val future: Map<String, String>,
    val participle: String,
    val auxiliary: String // haben or sein
)

// Adjective declension information
data class AdjectiveDeclension(
    val positive: String,
    val comparative: String,
    val superlative: String,
    val declensionTable: Map<String, Map<String, String>>
)

// Grammar data response models
data class NounDeclensionResponse(
    val noun: String,
    val declension: NounDeclension
)

data class VerbConjugationResponse(
    val verb: String,
    val conjugations: VerbConjugation
)

data class AdjectiveDeclensionResponse(
    val adjective: String,
    val declension: AdjectiveDeclension
)

// Comprehensive grammar information
data class GrammarInfo(
    val nounDeclension: NounDeclension? = null,
    val verbConjugation: VerbConjugation? = null,
    val adjectiveDeclension: AdjectiveDeclension? = null
)