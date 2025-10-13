package com.hellogerman.app.data.dictionary

import android.content.Context
import android.util.Log
import java.io.File
import java.io.RandomAccessFile
import java.util.zip.GZIPInputStream

/**
 * Low-level reader for dictd dictionary files (.dict.dz format)
 * 
 * Handles decompression and random access reading of dictionary content.
 * The .dict.dz files are GZIP-compressed text files that we decompress once
 * and cache for fast random access during import.
 */
class DictdFileReader(
    private val context: Context,
    private val assetPath: String  // e.g., "freedict-eng-deu-1.9-fd1.dictd/eng-deu/eng-deu.dict.dz"
) {
    
    companion object {
        private const val TAG = "DictdFileReader"
        private const val BUFFER_SIZE = 8192
    }
    
    // Cache directory for decompressed dictionary
    private val cacheDir = File(context.filesDir, "dictionary_cache")
    private val dictFileName = File(assetPath).nameWithoutExtension.removeSuffix(".dict") + ".dict"
    private val cachedDictFile = File(cacheDir, dictFileName)
    
    @Volatile
    private var isDecompressed = false
    
    /**
     * Decompress the dictionary file if not already decompressed
     * This is a one-time operation per install
     */
    fun decompressIfNeeded(): Boolean {
        if (isDecompressed || (cachedDictFile.exists() && cachedDictFile.length() > 0)) {
            isDecompressed = true
            return true
        }
        
        return try {
            Log.d(TAG, "Decompressing dictionary file: $assetPath")
            
            // Create cache directory
            cacheDir.mkdirs()
            
            // Open compressed file from assets
            context.assets.open(assetPath).use { input ->
                // Decompress using GZIP
                GZIPInputStream(input, BUFFER_SIZE).use { gzipStream ->
                    // Write decompressed data to cache file
                    cachedDictFile.outputStream().buffered(BUFFER_SIZE).use { output ->
                        val buffer = ByteArray(BUFFER_SIZE)
                        var bytesRead: Int
                        var totalBytes = 0L
                        
                        while (gzipStream.read(buffer).also { bytesRead = it } != -1) {
                            output.write(buffer, 0, bytesRead)
                            totalBytes += bytesRead
                            
                            // Log progress every 1MB
                            if (totalBytes % (1024 * 1024) == 0L) {
                                Log.d(TAG, "Decompressed ${totalBytes / (1024 * 1024)}MB...")
                            }
                        }
                        
                        Log.d(TAG, "Decompression complete. Total size: ${totalBytes / (1024 * 1024)}MB")
                    }
                }
            }
            
            isDecompressed = true
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error decompressing dictionary", e)
            // Clean up partial file
            if (cachedDictFile.exists()) {
                cachedDictFile.delete()
            }
            false
        }
    }
    
    /**
     * Read a block of text from the decompressed dictionary file
     * 
     * @param offset Byte offset where to start reading
     * @param length Number of bytes to read
     * @return The text content at that position
     */
    fun readBlock(offset: Long, length: Int): String? {
        if (!isDecompressed && !decompressIfNeeded()) {
            Log.e(TAG, "Dictionary file not decompressed")
            return null
        }
        
        if (!cachedDictFile.exists()) {
            Log.e(TAG, "Cached dictionary file not found: ${cachedDictFile.absolutePath}")
            return null
        }
        
        return try {
            RandomAccessFile(cachedDictFile, "r").use { raf ->
                // Seek to position
                raf.seek(offset)
                
                // Read bytes
                val buffer = ByteArray(length)
                var totalRead = 0
                
                while (totalRead < length) {
                    val read = raf.read(buffer, totalRead, length - totalRead)
                    if (read == -1) break
                    totalRead += read
                }
                
                // Convert to string
                String(buffer, 0, totalRead, Charsets.UTF_8)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error reading block at offset $offset, length $length", e)
            null
        }
    }
    
    /**
     * Get the size of the decompressed dictionary file
     */
    fun getFileSize(): Long {
        return if (cachedDictFile.exists()) {
            cachedDictFile.length()
        } else {
            0L
        }
    }
    
    /**
     * Check if dictionary file is ready for reading
     */
    fun isReady(): Boolean {
        return isDecompressed && cachedDictFile.exists() && cachedDictFile.length() > 0
    }
    
    /**
     * Clear cached dictionary file to free up space
     */
    fun clearCache() {
        try {
            if (cachedDictFile.exists()) {
                cachedDictFile.delete()
                Log.d(TAG, "Cleared dictionary cache")
            }
            isDecompressed = false
        } catch (e: Exception) {
            Log.e(TAG, "Error clearing cache", e)
        }
    }
    
    /**
     * Get cache file path for external reference
     */
    fun getCacheFilePath(): String {
        return cachedDictFile.absolutePath
    }
}

