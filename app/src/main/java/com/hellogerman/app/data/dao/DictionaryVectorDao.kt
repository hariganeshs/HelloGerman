package com.hellogerman.app.data.dao

import androidx.room.*
import com.hellogerman.app.data.entities.DictionaryVectorEntry

/**
 * Data Access Object for dictionary vector operations
 * 
 * Provides storage and retrieval of embeddings for semantic search
 */
@Dao
interface DictionaryVectorDao {
    
    // ==================== INSERT OPERATIONS ====================
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVector(vector: DictionaryVectorEntry): Long
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVectors(vectors: List<DictionaryVectorEntry>): List<Long>
    
    @Transaction
    suspend fun insertVectorsBatch(vectors: List<DictionaryVectorEntry>) {
        insertVectors(vectors)
    }
    
    // ==================== QUERY OPERATIONS ====================
    
    /**
     * Get vector by entry ID
     */
    @Query("SELECT * FROM dictionary_vectors WHERE entry_id = :entryId")
    suspend fun getVectorByEntryId(entryId: Long): DictionaryVectorEntry?
    
    /**
     * Get all vectors (for similarity search)
     * Note: This loads all vectors into memory. For production, consider pagination
     */
    @Query("SELECT * FROM dictionary_vectors LIMIT :limit OFFSET :offset")
    suspend fun getVectorsBatch(limit: Int = 1000, offset: Int = 0): List<DictionaryVectorEntry>
    
    /**
     * Get vectors with specific filters
     */
    @Query("""
        SELECT * FROM dictionary_vectors
        WHERE (:wordType IS NULL OR word_type = :wordType)
          AND (:gender IS NULL OR gender = :gender)
          AND (:hasExamples IS NULL OR has_examples = :hasExamples)
        LIMIT :limit
    """)
    suspend fun getVectorsFiltered(
        wordType: String? = null,
        gender: String? = null,
        hasExamples: Boolean? = null,
        limit: Int = 1000
    ): List<DictionaryVectorEntry>
    
    /**
     * Get total vector count
     */
    @Query("SELECT COUNT(*) FROM dictionary_vectors")
    suspend fun getTotalVectorCount(): Int
    
    /**
     * Get vectors by entry IDs
     */
    @Query("SELECT * FROM dictionary_vectors WHERE entry_id IN (:entryIds)")
    suspend fun getVectorsByEntryIds(entryIds: List<Long>): List<DictionaryVectorEntry>
    
    // ==================== DELETE OPERATIONS ====================
    
    @Delete
    suspend fun deleteVector(vector: DictionaryVectorEntry)
    
    @Query("DELETE FROM dictionary_vectors WHERE entry_id = :entryId")
    suspend fun deleteVectorByEntryId(entryId: Long)
    
    @Query("DELETE FROM dictionary_vectors")
    suspend fun deleteAllVectors()
    
    // ==================== STATISTICS ====================
    
    @Query("SELECT COUNT(*) FROM dictionary_vectors WHERE has_gender = 1")
    suspend fun getCountWithGender(): Int
    
    @Query("SELECT COUNT(*) FROM dictionary_vectors WHERE has_examples = 1")
    suspend fun getCountWithExamples(): Int
    
    @Query("SELECT COUNT(*) FROM dictionary_vectors WHERE word_type = :wordType")
    suspend fun getCountByWordType(wordType: String): Int
}

