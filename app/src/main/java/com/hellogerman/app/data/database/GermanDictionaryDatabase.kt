package com.hellogerman.app.data.database

import androidx.room.*
import com.hellogerman.app.data.models.Definition
import com.hellogerman.app.data.models.Example

/**
 * Compressed offline German dictionary database
 * Contains 10,000+ most common German words with grammar information
 */
@Database(
    entities = [OfflineWordEntity::class, OfflineExampleEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(DictionaryConverters::class)
abstract class GermanDictionaryDatabase : RoomDatabase() {
    abstract fun dictionaryDao(): GermanDictionaryDao
}

@Entity(
    tableName = "offline_words",
    indices = [Index(value = ["level"], name = "idx_words_level")]
)
data class OfflineWordEntity(
    @PrimaryKey val word: String,
    val definitions: String, // JSON compressed definitions
    val wordType: String, // noun, verb, adjective, etc.
    val gender: String?, // der, die, das for nouns
    val frequency: Int, // 1-10000 (most to least common)
    val level: String, // A1, A2, B1, B2, C1, C2
    val pronunciation: String?, // IPA notation
    val etymology: String?
)

@Entity(
    tableName = "offline_examples",
    indices = [Index(value = ["word"], name = "idx_examples_word")]
)
data class OfflineExampleEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val word: String,
    val germanSentence: String,
    val englishTranslation: String?,
    val difficulty: String // A1, A2, etc.
)

@Dao
interface GermanDictionaryDao {
    @Query("SELECT * FROM offline_words WHERE word = :word LIMIT 1")
    suspend fun getWord(word: String): OfflineWordEntity?
    
    @Query("SELECT * FROM offline_words WHERE word LIKE :pattern LIMIT 20")
    suspend fun searchWords(pattern: String): List<OfflineWordEntity>
    
    @Query("SELECT * FROM offline_examples WHERE word = :word")
    suspend fun getExamples(word: String): List<OfflineExampleEntity>
    
    @Query("SELECT * FROM offline_words WHERE level = :level ORDER BY frequency ASC")
    suspend fun getWordsByLevel(level: String): List<OfflineWordEntity>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWord(word: OfflineWordEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWords(words: List<OfflineWordEntity>)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExamples(examples: List<OfflineExampleEntity>)
    
    @Query("SELECT COUNT(*) FROM offline_words")
    suspend fun getWordCount(): Int
}

class DictionaryConverters {
    @TypeConverter
    fun fromDefinitionsList(definitions: List<Definition>): String {
        // Convert to compact JSON format
        return definitions.joinToString("|") { "${it.meaning}§${it.partOfSpeech}§${it.level}" }
    }
    
    @TypeConverter
    fun toDefinitionsList(definitionsString: String): List<Definition> {
        if (definitionsString.isEmpty()) return emptyList()
        return definitionsString.split("|").map { defStr ->
            val parts = defStr.split("§")
            Definition(
                meaning = parts.getOrNull(0) ?: "",
                partOfSpeech = parts.getOrNull(1) ?: "",
                level = parts.getOrNull(2) ?: "A1"
            )
        }
    }
}
