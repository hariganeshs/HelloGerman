package com.hellogerman.app.data.models

import com.google.gson.annotations.SerializedName

/**
 * DictionarySearchRequest: A request to look up a word in the dictionary
 *
 * This is like filling out a form to ask the dictionary a question.
 *
 * @param word: The word you want to look up (like "Haus" or "house")
 * @param fromLang: The language of the word you're looking up (defaults to "de" for German)
 * @param toLang: The language you want the translation in (defaults to "en" for English)
 *
 * Example: DictionarySearchRequest("Haus", "de", "en") means "Translate 'Haus' from German to English"
 */
data class DictionarySearchRequest(
    val word: String,
    val fromLang: String = "de",
    val toLang: String = "en"
)

/**
 * TranslationRequest: An alias for DictionarySearchRequest
 *
 * This is just another name for the same thing - kept for compatibility with older code
 * that might have used the name "TranslationRequest" instead of "DictionarySearchRequest".
 */
typealias TranslationRequest = DictionarySearchRequest

/**
 * Data models for MyMemory Translation API responses
 *
 * These classes represent responses from the MyMemory translation service,
 * which is like an online translation service that remembers previous translations.
 */

data class MyMemoryTranslationResponse(
    @SerializedName("responseData")
    val responseData: ResponseData, // The main translation result

    @SerializedName("responseStatus")
    val responseStatus: Int, // Status code (200 for success, etc.)

    @SerializedName("responseDetails")
    val responseDetails: String?, // Additional details about the response

    @SerializedName("matches")
    val matches: List<Match>? // Alternative translation matches from memory
)

data class ResponseData(
    @SerializedName("translatedText")
    val translatedText: String, // The actual translated text

    @SerializedName("match")
    val match: Float // How confident the service is in this translation (0.0 to 1.0)
)

data class Match(
    @SerializedName("id")
    val id: String, // Unique identifier for this translation memory entry

    @SerializedName("segment")
    val segment: String, // The original text that was translated

    @SerializedName("translation")
    val translation: String, // The translated text

    @SerializedName("quality")
    val quality: String?, // Quality rating of this translation

    @SerializedName("reference")
    val reference: String?, // Reference source for this translation

    @SerializedName("usage-count")
    val usageCount: Int?, // How many times this translation has been used

    @SerializedName("subject")
    val subject: String?, // Subject category (like "technical", "medical", etc.)

    @SerializedName("created-by")
    val createdBy: String?, // Who created this translation entry

    @SerializedName("last-updated-by")
    val lastUpdatedBy: String?, // Who last updated this entry

    @SerializedName("create-date")
    val createDate: String?, // When this translation was first created

    @SerializedName("last-update-date")
    val lastUpdateDate: String?, // When this translation was last updated

    @SerializedName("match")
    val match: Float // Confidence score for this particular match
)

/**
 * DictionarySearchResult: Complete results from a dictionary search
 *
 * This is like a comprehensive report from a dictionary that includes not just translations
 * but also detailed linguistic information to help learners understand and use words properly.
 *
 * @param originalWord: The word that was searched for
 * @param translations: List of translations in the target language
 * @param fromLanguage: The source language of the original word
 * @param toLanguage: The target language for translations
 * @param hasResults: Whether any translations or information was found
 * @param definitions: Detailed explanations of what the word means
 * @param examples: Sample sentences showing how to use the word
 * @param synonyms: Words with similar meanings
 * @param antonyms: Words with opposite meanings
 * @param pronunciation: Information about how to pronounce the word (IPA notation, audio)
 * @param pronunciationInfo: Additional pronunciation details
 * @param conjugations: Verb conjugation tables (for verbs)
 * @param etymology: Word history and origins
 * @param wordType: What kind of word this is (noun, verb, adjective, etc.)
 * @param gender: For German nouns, whether it's der/die/das (masculine/feminine/neuter)
 * @param difficulty: Learning difficulty level (A1, A2, B1, etc. from CEFR)
 * @param wikidataLexemeData: Advanced linguistic data from Wikidata
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

/**
 * Definition: A detailed explanation of what a word means
 *
 * This is like a dictionary definition that explains the meaning of a word in detail.
 *
 * @param meaning: The explanation of what the word means
 * @param partOfSpeech: What type of word this is (noun, verb, adjective, etc.)
 * @param context: The situation or field where this meaning applies (like "formal", "technical", "colloquial")
 * @param level: The difficulty level of this word/meaning (A1, A2, B1, etc.)
 */
data class Definition(
    val meaning: String,
    val partOfSpeech: String? = null,
    val context: String? = null,
    val level: String? = null
)

/**
 * Example: A sample sentence showing how to use a word
 *
 * This is like seeing a word used in a real sentence from a book or conversation.
 *
 * @param sentence: The example sentence containing the word
 * @param source: Where this example comes from (book title, website, etc.)
 */
data class Example(
    val sentence: String,
    val source: String? = null
)

/**
 * Pronunciation: Information about how to pronounce a word
 *
 * This is like the pronunciation guide you see in dictionaries with phonetic symbols and audio.
 *
 * @param ipa: International Phonetic Alphabet notation showing exactly how to pronounce the word
 * @param audioUrl: Link to an audio file where you can hear the pronunciation
 * @param region: The regional accent or variant (like "British English" vs "American English")
 */
data class Pronunciation(
    val ipa: String? = null,
    val audioUrl: String? = null,
    val region: String? = null
)

/**
 * PronunciationInfo: Additional details about word pronunciation
 *
 * This provides extra information about pronunciation beyond the basic Pronunciation class.
 *
 * @param ipa: The phonetic notation for pronunciation
 * @param audioUrl: Link to audio pronunciation (optional)
 * @param isAvailable: Whether pronunciation information is available for this word
 */
data class PronunciationInfo(
    val ipa: String,
    val audioUrl: String? = null,
    val isAvailable: Boolean = true
)

/**
 * Wiktionary API response models
 *
 * These classes represent the structure of data we get back from Wiktionary when we ask for
 * detailed grammar information about a word. Wiktionary is like Wikipedia but for words.
 */

data class WiktionaryResponse(
    @SerializedName("parse")
    val parse: WiktionaryParse? // The parsed content of the Wiktionary page
)

data class WiktionaryParse(
    @SerializedName("title")
    val title: String, // The title of the Wiktionary page (usually the word itself)

    @SerializedName("pageid")
    val pageId: Int, // Unique identifier for this page on Wiktionary

    @SerializedName("wikitext")
    val wikitext: WiktionaryWikitext? // The raw wiki markup content of the page
)

data class WiktionaryWikitext(
    @SerializedName("*")
    val content: String // The actual wiki markup text with all the grammar information
)

/**
 * VerbConjugations: Complete conjugation information for a verb
 *
 * This is like a comprehensive verb conjugation table that shows all the different forms
 * a verb can take in different tenses, persons, and moods.
 *
 * @param present: Present tense forms (ich gehe, du gehst, er geht, etc.)
 * @param past: Past tense forms (Simple Past/Präteritum)
 * @param future: Future tense forms (ich werde gehen, etc.)
 * @param participle: Present and past participle forms (gehend, gegangen)
 * @param imperative: Command forms (geh!, geht!, gehen Sie!)
 * @param subjunctive: Subjunctive mood forms (Konjunktiv)
 * @param perfect: Present perfect forms (ich habe gegangen)
 * @param pluperfect: Past perfect forms (ich hatte gegangen)
 * @param futurePerfect: Future perfect forms (ich werde gegangen sein)
 * @param conditional: Conditional mood forms (ich würde gehen)
 * @param auxiliary: Helper verb for perfect tenses ("haben" or "sein")
 * @param separablePrefix: For separable verbs like "anfangen" (an- + fangen)
 * @param infinitive: The base "to" form of the verb (gehen)
 * @param isIrregular: Whether this verb follows irregular patterns
 * @param isSeparable: Whether this verb has a separable prefix
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

/**
 * Participle: Present and past participle forms of a verb
 *
 * Participles are verb forms that can act like adjectives or be used in compound tenses.
 *
 * @param present: Present participle (gehend - going/walking)
 * @param past: Past participle (gegangen - gone/walked)
 */
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
 * CachedDictionaryEntry: A saved dictionary lookup for quick access later
 *
 * This is like a bookmark or saved search result that lets you quickly recall
 * dictionary information without having to look it up again.
 *
 * @param word: The word that was looked up
 * @param language: The language of the word
 * @param result: The complete dictionary results that were found
 * @param timestamp: When this entry was saved (defaults to current time)
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
/**
 * Leo-Dictionary specific models for comprehensive German-English dictionary
 *
 * These are specialized data models for the Leo dictionary system, which provides
 * particularly detailed information about German words including comprehensive grammar tables.
 */

// Word types for Leo dictionary
enum class WordType {
    NOUN, VERB, ADJECTIVE, ADVERB, PRONOUN, PREPOSITION, CONJUNCTION, INTERJECTION, UNKNOWN
}

// German gender enum
enum class GermanGender {
    DER, DIE, DAS;

    fun getArticle(): String = when(this) {
        DER -> "der"
        DIE -> "die"
        DAS -> "das"
    }

    companion object {
        fun fromArticle(article: String): GermanGender? = when(article.lowercase()) {
            "der" -> DER
            "die" -> DIE
            "das" -> DAS
            else -> null
        }
    }
}

// Main Leo dictionary entry
data class LeoDictionaryEntry(
    val germanWord: String,
    val englishTranslations: List<String>,
    val wordType: WordType,

    // Noun-specific fields
    val gender: GermanGender? = null,
    val article: String? = null,
    val plural: String? = null,
    val declension: NounDeclensionTable? = null,

    // Verb-specific fields
    val conjugation: VerbConjugationTable? = null,
    val auxiliary: String? = null, // "haben" or "sein"
    val isIrregular: Boolean = false,
    val isSeparable: Boolean = false,
    val separablePrefix: String? = null,

    // Adjective-specific fields
    val comparative: String? = null,
    val superlative: String? = null,
    val adjectiveDeclension: AdjectiveDeclensionTable? = null,

    // Common fields
    val pronunciation: Pronunciation? = null,
    val examples: List<GermanExample> = emptyList(),
    val difficulty: String? = null, // A1, A2, B1, etc.
    val etymology: String? = null,
    val source: String = "FreeDict" // FreeDict or Wiktionary
)

// Noun declension table (full case system)
data class NounDeclensionTable(
    val nominative: CaseForms,
    val genitive: CaseForms,
    val dative: CaseForms,
    val accusative: CaseForms
)

data class CaseForms(
    val singular: String,
    val plural: String
)

// Verb conjugation table (comprehensive)
data class VerbConjugationTable(
    val infinitive: String,
    val present: PersonForms,
    val past: PersonForms, // Präteritum
    val perfect: PersonForms, // Perfekt
    val future: PersonForms, // Futur I
    val futurePerfect: PersonForms? = null, // Futur II
    val subjunctive: PersonForms? = null, // Konjunktiv
    val imperative: ImperativeForms,
    val participles: Participles
)

data class PersonForms(
    val ich: String,
    val du: String,
    val erSieEs: String,
    val wir: String,
    val ihr: String,
    val sieSie: String
)

data class ImperativeForms(
    val du: String,
    val ihr: String,
    val sie: String
)

data class Participles(
    val present: String,
    val past: String
)

// Adjective declension table
data class AdjectiveDeclensionTable(
    val positive: AdjectiveForms,
    val comparative: AdjectiveForms,
    val superlative: AdjectiveForms
)

data class AdjectiveForms(
    val masculine: String,
    val feminine: String,
    val neuter: String,
    val plural: String
)

// German example with optional translation
data class GermanExample(
    val sentence: String, // German sentence
    val translation: String? = null, // English translation
    val context: String? = null // usage context
)

// Search request for Leo dictionary
data class LeoDictionarySearchRequest(
    val word: String,
    val fromLang: String = "de", // "de" for German, "en" for English
    val toLang: String = "en"
)

// Search result for Leo dictionary
data class LeoDictionarySearchResult(
    val originalWord: String,
    val entries: List<LeoDictionaryEntry>,
    val hasResults: Boolean,
    val searchTime: Long = System.currentTimeMillis()
)