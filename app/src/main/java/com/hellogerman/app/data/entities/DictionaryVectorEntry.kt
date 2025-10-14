package com.hellogerman.app.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Vector embeddings for dictionary entries
 * 
 * Stores 384-dimensional embeddings for semantic search, synonym discovery,
 * and contextual similarity matching
 */
@Entity(
    tableName = "dictionary_vectors",
    foreignKeys = [
        ForeignKey(
            entity = DictionaryEntry::class,
            parentColumns = ["id"],
            childColumns = ["entry_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["entry_id"], unique = true)
    ]
)
data class DictionaryVectorEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    @ColumnInfo(name = "entry_id")
    val entryId: Long,
    
    // Combined embedding (German + English word)
    @ColumnInfo(name = "combined_embedding", typeAffinity = ColumnInfo.BLOB)
    val combinedEmbedding: ByteArray,
    
    // German word embedding (for German-only search)
    @ColumnInfo(name = "german_embedding", typeAffinity = ColumnInfo.BLOB)
    val germanEmbedding: ByteArray,
    
    // English word embedding (for English-only search)
    @ColumnInfo(name = "english_embedding", typeAffinity = ColumnInfo.BLOB)
    val englishEmbedding: ByteArray,
    
    // Metadata for filtering
    @ColumnInfo(name = "has_examples")
    val hasExamples: Boolean = false,
    
    @ColumnInfo(name = "has_gender")
    val hasGender: Boolean = false,
    
    @ColumnInfo(name = "word_type")
    val wordType: String? = null,
    
    @ColumnInfo(name = "gender")
    val gender: String? = null,
    
    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis()
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        
        other as DictionaryVectorEntry
        
        if (id != other.id) return false
        if (entryId != other.entryId) return false
        
        return true
    }
    
    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + entryId.hashCode()
        return result
    }
}

/**
 * Helper functions for converting between FloatArray and ByteArray
 */
object VectorConverter {
    
    /**
     * Convert FloatArray to ByteArray for database storage
     */
    fun floatArrayToByteArray(floats: FloatArray): ByteArray {
        val bytes = ByteArray(floats.size * 4)
        val buffer = java.nio.ByteBuffer.wrap(bytes)
        buffer.order(java.nio.ByteOrder.LITTLE_ENDIAN)
        
        floats.forEach { buffer.putFloat(it) }
        
        return bytes
    }
    
    /**
     * Convert ByteArray from database to FloatArray
     */
    fun byteArrayToFloatArray(bytes: ByteArray): FloatArray {
        val buffer = java.nio.ByteBuffer.wrap(bytes)
        buffer.order(java.nio.ByteOrder.LITTLE_ENDIAN)
        
        val floats = FloatArray(bytes.size / 4)
        for (i in floats.indices) {
            floats[i] = buffer.getFloat()
        }
        
        return floats
    }
}

