package com.hellogerman.app.data.dao

import androidx.room.*
import com.hellogerman.app.data.entities.DictionaryEntry
import com.hellogerman.app.data.entities.GermanGender
import com.hellogerman.app.data.entities.WordType
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for dictionary operations
 * 
 * Provides efficient database queries for searching, filtering, and
 * managing dictionary entries with optimized indexes.
 */
@Dao
interface DictionaryDao {
    
    // ==================== INSERT OPERATIONS ====================
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEntry(entry: DictionaryEntry): Long
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEntries(entries: List<DictionaryEntry>): List<Long>
    
    @Transaction
    suspend fun insertEntriesBatch(entries: List<DictionaryEntry>) {
        insertEntries(entries)
    }
    
    // ==================== UPDATE & DELETE ====================
    
    @Update
    suspend fun updateEntry(entry: DictionaryEntry)
    
    @Delete
    suspend fun deleteEntry(entry: DictionaryEntry)
    
    @Query("DELETE FROM dictionary_entries")
    suspend fun deleteAllEntries()
    
    @Query("DELETE FROM dictionary_entries WHERE source = :source")
    suspend fun deleteEntriesBySource(source: String)
    
    // ==================== ENGLISH SEARCH ====================
    
    /**
     * Search for exact English word match with improved ranking
     */
    @Query("""
        SELECT * FROM dictionary_entries 
        WHERE english_normalized = :word 
        ORDER BY 
            CASE WHEN word_type = 'NOUN' THEN 0 ELSE 1 END,
            CASE WHEN gender IS NOT NULL THEN 0 ELSE 1 END,
            word_length ASC,
            english_word ASC
        LIMIT :limit
    """)
    suspend fun searchEnglishExact(word: String, limit: Int = 50): List<DictionaryEntry>
    
    /**
     * Search for English words starting with prefix with improved ranking
     */
    @Query("""
        SELECT * FROM dictionary_entries 
        WHERE english_normalized LIKE :prefix || '%' 
        ORDER BY 
            CASE WHEN english_normalized = :prefix THEN 0 ELSE 1 END,
            CASE WHEN word_type = 'NOUN' THEN 0 ELSE 1 END,
            CASE WHEN gender IS NOT NULL THEN 0 ELSE 1 END,
            word_length ASC, 
            english_word ASC
        LIMIT :limit
    """)
    suspend fun searchEnglishPrefix(prefix: String, limit: Int = 50): List<DictionaryEntry>
    
    /**
     * Search English words containing query (less efficient, for fuzzy search) with improved ranking
     */
    @Query("""
        SELECT * FROM dictionary_entries 
        WHERE english_normalized LIKE '%' || :query || '%' 
        ORDER BY 
            CASE WHEN english_normalized = :query THEN 0 ELSE 1 END,
            CASE WHEN english_normalized LIKE :query || '%' THEN 0 ELSE 1 END,
            CASE WHEN word_type = 'NOUN' THEN 0 ELSE 1 END,
            word_length ASC,
            english_word ASC
        LIMIT :limit
    """)
    suspend fun searchEnglishFuzzy(query: String, limit: Int = 100): List<DictionaryEntry>
    
    /**
     * Get English word suggestions for autocomplete
     */
    @Query("""
        SELECT DISTINCT english_word FROM dictionary_entries 
        WHERE english_normalized LIKE :prefix || '%' 
        ORDER BY word_length ASC, english_word ASC
        LIMIT :limit
    """)
    suspend fun getEnglishSuggestions(prefix: String, limit: Int = 20): List<String>
    
    // ==================== GERMAN SEARCH (Reverse Lookup) ====================
    
    /**
     * Search for exact German word match with improved ranking
     */
    @Query("""
        SELECT * FROM dictionary_entries 
        WHERE german_normalized = :word 
        ORDER BY 
            CASE WHEN word_type = 'NOUN' THEN 0 ELSE 1 END,
            CASE WHEN gender IS NOT NULL THEN 0 ELSE 1 END,
            word_length ASC,
            german_word ASC
        LIMIT :limit
    """)
    suspend fun searchGermanExact(word: String, limit: Int = 50): List<DictionaryEntry>
    
    /**
     * Search for German words starting with prefix with improved ranking
     */
    @Query("""
        SELECT * FROM dictionary_entries 
        WHERE german_normalized LIKE :prefix || '%' 
        ORDER BY 
            CASE WHEN german_normalized = :prefix THEN 0 ELSE 1 END,
            CASE WHEN word_type = 'NOUN' THEN 0 ELSE 1 END,
            CASE WHEN gender IS NOT NULL THEN 0 ELSE 1 END,
            word_length ASC, 
            german_word ASC
        LIMIT :limit
    """)
    suspend fun searchGermanPrefix(prefix: String, limit: Int = 50): List<DictionaryEntry>
    
    /**
     * Search German words containing query with improved ranking
     */
    @Query("""
        SELECT * FROM dictionary_entries 
        WHERE german_normalized LIKE '%' || :query || '%' 
        ORDER BY 
            CASE WHEN german_normalized = :query THEN 0 ELSE 1 END,
            CASE WHEN german_normalized LIKE :query || '%' THEN 0 ELSE 1 END,
            CASE WHEN word_type = 'NOUN' THEN 0 ELSE 1 END,
            word_length ASC,
            german_word ASC
        LIMIT :limit
    """)
    suspend fun searchGermanFuzzy(query: String, limit: Int = 100): List<DictionaryEntry>
    
    /**
     * Get German word suggestions for autocomplete
     */
    @Query("""
        SELECT DISTINCT german_word FROM dictionary_entries 
        WHERE german_normalized LIKE :prefix || '%' 
        ORDER BY word_length ASC, german_word ASC
        LIMIT :limit
    """)
    suspend fun getGermanSuggestions(prefix: String, limit: Int = 20): List<String>
    
    // ==================== FILTERED SEARCH ====================
    
    /**
     * Search with multiple filters
     */
    @Query("""
        SELECT * FROM dictionary_entries
        WHERE (:wordType IS NULL OR word_type = :wordType)
          AND (:gender IS NULL OR gender = :gender)
          AND (english_normalized LIKE :searchQuery || '%' 
               OR german_normalized LIKE :searchQuery || '%')
        ORDER BY english_word ASC, german_word ASC
        LIMIT :limit
    """)
    suspend fun searchWithFilters(
        searchQuery: String,
        wordType: WordType? = null,
        gender: GermanGender? = null,
        limit: Int = 50
    ): List<DictionaryEntry>
    
    /**
     * Get entries by word type
     */
    @Query("""
        SELECT * FROM dictionary_entries 
        WHERE word_type = :wordType 
        ORDER BY english_word ASC
        LIMIT :limit
    """)
    suspend fun getEntriesByWordType(wordType: WordType, limit: Int = 100): List<DictionaryEntry>
    
    /**
     * Get nouns by gender
     */
    @Query("""
        SELECT * FROM dictionary_entries 
        WHERE gender = :gender 
        ORDER BY german_word ASC
        LIMIT :limit
    """)
    suspend fun getNounsByGender(gender: GermanGender, limit: Int = 100): List<DictionaryEntry>
    
    // ==================== SPECIFIC LOOKUPS ====================
    
    /**
     * Get entry by ID
     */
    @Query("SELECT * FROM dictionary_entries WHERE id = :id")
    suspend fun getEntryById(id: Long): DictionaryEntry?
    
    /**
     * Get entries with examples
     */
    @Query("""
        SELECT * FROM dictionary_entries 
        WHERE examples IS NOT NULL AND examples != '[]'
        ORDER BY RANDOM()
        LIMIT :limit
    """)
    suspend fun getEntriesWithExamples(limit: Int = 50): List<DictionaryEntry>
    
    // ==================== STATISTICS ====================
    
    @Query("SELECT COUNT(*) FROM dictionary_entries")
    suspend fun getTotalEntryCount(): Int
    
    @Query("SELECT COUNT(*) FROM dictionary_entries WHERE word_type = :wordType")
    suspend fun getCountByWordType(wordType: WordType): Int
    
    @Query("SELECT COUNT(*) FROM dictionary_entries WHERE gender = :gender")
    suspend fun getCountByGender(gender: GermanGender): Int
    
    @Query("SELECT COUNT(*) FROM dictionary_entries WHERE source = :source")
    suspend fun getCountBySource(source: String): Int
    
    @Query("SELECT COUNT(*) FROM dictionary_entries WHERE examples IS NOT NULL AND examples != '[]'")
    suspend fun getCountWithExamples(): Int
    
    @Query("SELECT MIN(import_date) FROM dictionary_entries")
    suspend fun getOldestImportDate(): Long?
    
    @Query("SELECT MAX(import_date) FROM dictionary_entries")
    suspend fun getNewestImportDate(): Long?
    
    /**
     * Get dictionary statistics summary
     */
    @Query("""
        SELECT 
            COUNT(*) as total,
            COUNT(CASE WHEN word_type = 'NOUN' THEN 1 END) as nouns,
            COUNT(CASE WHEN word_type = 'VERB' THEN 1 END) as verbs,
            COUNT(CASE WHEN word_type = 'ADJECTIVE' THEN 1 END) as adjectives,
            COUNT(CASE WHEN gender = 'DER' THEN 1 END) as masculine,
            COUNT(CASE WHEN gender = 'DIE' THEN 1 END) as feminine,
            COUNT(CASE WHEN gender = 'DAS' THEN 1 END) as neuter,
            COUNT(CASE WHEN examples IS NOT NULL AND examples != '[]' THEN 1 END) as with_examples
        FROM dictionary_entries
    """)
    suspend fun getStatisticsSummary(): DictionaryStatistics
    
    // ==================== FLOW (REACTIVE) QUERIES ====================
    
    @Query("""
        SELECT * FROM dictionary_entries 
        WHERE english_normalized LIKE :prefix || '%' 
        ORDER BY word_length ASC, english_word ASC
        LIMIT :limit
    """)
    fun observeEnglishPrefix(prefix: String, limit: Int = 50): Flow<List<DictionaryEntry>>
    
    @Query("""
        SELECT * FROM dictionary_entries 
        WHERE german_normalized LIKE :prefix || '%' 
        ORDER BY word_length ASC, german_word ASC
        LIMIT :limit
    """)
    fun observeGermanPrefix(prefix: String, limit: Int = 50): Flow<List<DictionaryEntry>>
    
    // ==================== MAINTENANCE ====================
    
    @Query("SELECT DISTINCT source FROM dictionary_entries")
    suspend fun getAllSources(): List<String>
    
    @Query("SELECT DISTINCT word_type FROM dictionary_entries WHERE word_type IS NOT NULL")
    suspend fun getAllWordTypes(): List<WordType>
    
    @Query("DELETE FROM dictionary_entries WHERE import_date < :beforeTimestamp")
    suspend fun deleteEntriesOlderThan(beforeTimestamp: Long)
}

/**
 * Data class for dictionary statistics
 */
data class DictionaryStatistics(
    val total: Int,
    val nouns: Int,
    val verbs: Int,
    val adjectives: Int,
    val masculine: Int,
    val feminine: Int,
    val neuter: Int,
    val with_examples: Int
)

