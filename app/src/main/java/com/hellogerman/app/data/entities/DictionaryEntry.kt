package com.hellogerman.app.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.hellogerman.app.data.converters.DictionaryTypeConverters

/**
 * Main dictionary entry entity for the FreeDict English-German dictionary
 * 
 * This entity stores comprehensive linguistic information for each word pair,
 * including grammar details, usage examples, and search optimization fields.
 */
@Entity(
    tableName = "dictionary_entries",
    indices = [
        Index(value = ["english_normalized"]),
        Index(value = ["german_normalized"]),
        Index(value = ["word_type"]),
        Index(value = ["gender"]),
        Index(value = ["word_length"])
    ]
)
@TypeConverters(DictionaryTypeConverters::class)
data class DictionaryEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    // Primary search keys
    @ColumnInfo(name = "english_word")
    val englishWord: String,
    
    @ColumnInfo(name = "german_word")
    val germanWord: String,
    
    // Linguistic information
    @ColumnInfo(name = "word_type")
    val wordType: WordType? = null,
    
    // Noun-specific fields
    @ColumnInfo(name = "gender")
    val gender: GermanGender? = null,
    
    @ColumnInfo(name = "plural_form")
    val pluralForm: String? = null,
    
    // Verb-specific fields
    @ColumnInfo(name = "past_tense")
    val pastTense: String? = null,
    
    @ColumnInfo(name = "past_participle")
    val pastParticiple: String? = null,
    
    @ColumnInfo(name = "auxiliary_verb")
    val auxiliaryVerb: String? = null,
    
    @ColumnInfo(name = "is_irregular")
    val isIrregular: Boolean = false,
    
    @ColumnInfo(name = "is_separable")
    val isSeparable: Boolean = false,
    
    // Adjective-specific fields
    @ColumnInfo(name = "comparative")
    val comparative: String? = null,
    
    @ColumnInfo(name = "superlative")
    val superlative: String? = null,
    
    // Additional translations and context
    @ColumnInfo(name = "additional_translations")
    val additionalTranslations: List<String> = emptyList(),
    
    @ColumnInfo(name = "examples")
    val examples: List<DictionaryExample> = emptyList(),
    
    // Pronunciation
    @ColumnInfo(name = "pronunciation_ipa")
    val pronunciationIpa: String? = null,
    
    // Metadata
    @ColumnInfo(name = "usage_level")
    val usageLevel: String? = null,
    
    @ColumnInfo(name = "domain")
    val domain: String? = null,
    
    @ColumnInfo(name = "raw_entry")
    val rawEntry: String,
    
    // Indexing and search optimization
    @ColumnInfo(name = "english_normalized")
    val englishNormalized: String,
    
    @ColumnInfo(name = "german_normalized")
    val germanNormalized: String,
    
    @ColumnInfo(name = "word_length")
    val wordLength: Int,
    
    // Import metadata
    @ColumnInfo(name = "source")
    val source: String = "FreeDict",
    
    @ColumnInfo(name = "import_date")
    val importDate: Long = System.currentTimeMillis(),
    
    @ColumnInfo(name = "import_version")
    val importVersion: Int = 1
)

/**
 * Word type classification for linguistic categorization
 */
enum class WordType {
    NOUN,           // Substantive (e.g., Haus, Mutter)
    VERB,           // Verb (e.g., gehen, essen)
    ADJECTIVE,      // Adjective (e.g., groß, schön)
    ADVERB,         // Adverb (e.g., schnell, hier)
    PRONOUN,        // Pronoun (e.g., ich, du, er)
    PREPOSITION,    // Preposition (e.g., in, auf, unter)
    CONJUNCTION,    // Conjunction (e.g., und, oder, aber)
    INTERJECTION,   // Interjection (e.g., oh, ah)
    ARTICLE,        // Article (e.g., der, die, das)
    PHRASE,         // Multi-word phrase
    UNKNOWN         // Could not determine type
}

/**
 * German grammatical gender for nouns
 */
enum class GermanGender {
    DER,    // Masculine (der Mann, der Tisch)
    DIE,    // Feminine (die Frau, die Tür)
    DAS;    // Neuter (das Kind, das Haus)
    
    /**
     * Get the article string for this gender
     */
    fun getArticle(): String = when(this) {
        DER -> "der"
        DIE -> "die"
        DAS -> "das"
    }
    
    companion object {
        /**
         * Parse gender from article string
         */
        fun fromArticle(article: String): GermanGender? = when(article.lowercase()) {
            "der", "m", "masc", "masculine" -> DER
            "die", "f", "fem", "feminine" -> DIE
            "das", "n", "neut", "neuter" -> DAS
            else -> null
        }
    }
}

/**
 * Example sentence in German with optional English translation
 */
data class DictionaryExample(
    val german: String,
    val english: String? = null
)

/**
 * Language direction for search operations
 */
enum class SearchLanguage {
    ENGLISH,    // Search English words
    GERMAN      // Search German words
}

