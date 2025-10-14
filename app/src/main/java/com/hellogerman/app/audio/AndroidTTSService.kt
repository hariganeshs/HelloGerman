package com.hellogerman.app.audio

import android.content.Context
import android.media.MediaPlayer
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.io.File
import java.util.*
import kotlin.coroutines.resume

/**
 * Android Text-to-Speech service (100% Free & Offline)
 * 
 * Uses Android's built-in TTS engine for German pronunciation
 * - Completely free (no API costs)
 * - Works offline
 * - High quality German voices
 * - No quota limits
 */
class AndroidTTSService(private val context: Context) {
    
    companion object {
        private const val TAG = "AndroidTTSService"
        
        // Voice configuration
        private const val LANGUAGE_CODE = "de-DE"
        private val LOCALE = Locale.GERMAN
        
        // Speech parameters
        private const val SPEECH_RATE = 0.9f // Slightly slower for learning
        private const val PITCH = 1.0f // Normal pitch
        
        // Cache configuration
        private const val CACHE_DIR_NAME = "tts_audio_cache"
        private const val MAX_CACHE_SIZE_MB = 100 // 100 MB max cache
    }
    
    private var tts: TextToSpeech? = null
    private var isInitialized = false
    private var mediaPlayer: MediaPlayer? = null
    
    /**
     * Initialize TextToSpeech engine
     */
    suspend fun initialize(): Boolean = suspendCancellableCoroutine { continuation ->
        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.let { engine ->
                    // Set German language
                    val result = engine.setLanguage(LOCALE)
                    
                    if (result == TextToSpeech.LANG_MISSING_DATA || 
                        result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.e(TAG, "German language not supported or data missing")
                        isInitialized = false
                        continuation.resume(false)
                    } else {
                        // Configure speech parameters
                        engine.setSpeechRate(SPEECH_RATE)
                        engine.setPitch(PITCH)
                        
                        isInitialized = true
                        Log.d(TAG, "TTS initialized successfully for German")
                        continuation.resume(true)
                    }
                } ?: continuation.resume(false)
            } else {
                Log.e(TAG, "TTS initialization failed")
                isInitialized = false
                continuation.resume(false)
            }
        }
    }
    
    /**
     * Synthesize speech from text
     * 
     * @param text German text to synthesize
     * @return Audio file path, or null if synthesis fails
     */
    suspend fun synthesizeSpeech(text: String): String? = withContext(Dispatchers.IO) {
        if (text.isBlank()) {
            Log.w(TAG, "Empty text provided for synthesis")
            return@withContext null
        }
        
        if (!isInitialized) {
            Log.w(TAG, "TTS not initialized")
            return@withContext null
        }
        
        // Check cache first
        val cachedFile = getCachedAudio(text)
        if (cachedFile != null && cachedFile.exists()) {
            Log.d(TAG, "Using cached audio for: $text")
            return@withContext cachedFile.absolutePath
        }
        
        try {
            val audioFile = generateAudioFile(text)
            val result = synthesizeToFile(text, audioFile)
            
            if (result) {
                // Manage cache size
                manageCacheSize(getCacheDirectory())
                Log.d(TAG, "Successfully synthesized audio for: $text")
                audioFile.absolutePath
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error synthesizing speech: ${e.message}", e)
            null
        }
    }
    
    /**
     * Synthesize text directly to audio file
     */
    private suspend fun synthesizeToFile(text: String, audioFile: File): Boolean = 
        suspendCancellableCoroutine { continuation ->
            
        val ttsEngine = tts ?: run {
            continuation.resume(false)
            return@suspendCancellableCoroutine
        }
        
        val utteranceId = UUID.randomUUID().toString()
        
        // Set up progress listener
        ttsEngine.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {
                Log.d(TAG, "TTS synthesis started")
            }
            
            override fun onDone(utteranceId: String?) {
                Log.d(TAG, "TTS synthesis completed")
                continuation.resume(true)
            }
            
            override fun onError(utteranceId: String?) {
                Log.e(TAG, "TTS synthesis error")
                continuation.resume(false)
            }
            
            @Deprecated("Deprecated in Java")
            override fun onError(utteranceId: String?, errorCode: Int) {
                Log.e(TAG, "TTS synthesis error: $errorCode")
                continuation.resume(false)
            }
        })
        
        // Synthesize to file
        val result = ttsEngine.synthesizeToFile(text, null, audioFile, utteranceId)
        
        if (result != TextToSpeech.SUCCESS) {
            Log.e(TAG, "Failed to start synthesis")
            continuation.resume(false)
        }
    }
    
    /**
     * Speak text directly (without file caching)
     */
    suspend fun speak(text: String): Boolean = withContext(Dispatchers.IO) {
        if (!isInitialized) {
            Log.w(TAG, "TTS not initialized")
            return@withContext false
        }
        
        val ttsEngine = tts ?: return@withContext false
        
        try {
            val result = ttsEngine.speak(text, TextToSpeech.QUEUE_FLUSH, null, UUID.randomUUID().toString())
            result == TextToSpeech.SUCCESS
        } catch (e: Exception) {
            Log.e(TAG, "Error speaking text: ${e.message}", e)
            false
        }
    }
    
    /**
     * Play audio file
     */
    suspend fun playAudio(audioFilePath: String): Boolean = withContext(Dispatchers.IO) {
        try {
            // Stop any currently playing audio
            stopAudio()
            
            mediaPlayer = MediaPlayer().apply {
                setDataSource(audioFilePath)
                prepare()
                start()
            }
            
            Log.d(TAG, "Playing audio: $audioFilePath")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error playing audio: ${e.message}", e)
            false
        }
    }
    
    /**
     * Stop currently playing audio
     */
    fun stopAudio() {
        try {
            mediaPlayer?.apply {
                if (isPlaying) {
                    stop()
                }
                release()
            }
            mediaPlayer = null
            
            tts?.stop()
        } catch (e: Exception) {
            Log.e(TAG, "Error stopping audio: ${e.message}", e)
        }
    }
    
    /**
     * Get cached audio file for text
     */
    private fun getCachedAudio(text: String): File? {
        val cacheDir = getCacheDirectory()
        val filename = generateCacheFilename(text)
        val file = File(cacheDir, filename)
        
        return if (file.exists()) file else null
    }
    
    /**
     * Generate audio file for caching
     */
    private fun generateAudioFile(text: String): File {
        val cacheDir = getCacheDirectory()
        val filename = generateCacheFilename(text)
        return File(cacheDir, filename)
    }
    
    /**
     * Get or create TTS cache directory
     */
    private fun getCacheDirectory(): File {
        val cacheDir = File(context.cacheDir, CACHE_DIR_NAME)
        if (!cacheDir.exists()) {
            cacheDir.mkdirs()
        }
        return cacheDir
    }
    
    /**
     * Generate cache filename from text
     */
    private fun generateCacheFilename(text: String): String {
        val hash = text.hashCode().toString()
        return "tts_${hash}.wav"
    }
    
    /**
     * Manage cache size by deleting old files if limit exceeded
     */
    private fun manageCacheSize(cacheDir: File) {
        try {
            val files = cacheDir.listFiles() ?: return
            val totalSize = files.sumOf { it.length() }
            val maxSizeBytes = MAX_CACHE_SIZE_MB * 1024 * 1024
            
            if (totalSize > maxSizeBytes) {
                // Delete oldest files first
                val sortedFiles = files.sortedBy { it.lastModified() }
                var currentSize = totalSize
                
                for (file in sortedFiles) {
                    if (currentSize <= maxSizeBytes) break
                    currentSize -= file.length()
                    file.delete()
                    Log.d(TAG, "Deleted cached file: ${file.name}")
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error managing cache size: ${e.message}", e)
        }
    }
    
    /**
     * Clear all cached audio files
     */
    fun clearCache() {
        try {
            val cacheDir = getCacheDirectory()
            cacheDir.listFiles()?.forEach { it.delete() }
            Log.d(TAG, "TTS cache cleared")
        } catch (e: Exception) {
            Log.e(TAG, "Error clearing cache: ${e.message}", e)
        }
    }
    
    /**
     * Get cache statistics
     */
    fun getCacheStatistics(): CacheStatistics {
        return try {
            val cacheDir = getCacheDirectory()
            val files = cacheDir.listFiles() ?: emptyArray()
            val totalSize = files.sumOf { it.length() }
            
            CacheStatistics(
                fileCount = files.size,
                totalSizeBytes = totalSize,
                totalSizeMB = totalSize / (1024 * 1024)
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error getting cache statistics: ${e.message}", e)
            CacheStatistics(0, 0, 0)
        }
    }
    
    /**
     * Check if TTS is available and German is supported
     */
    fun isGermanSupported(): Boolean {
        return isInitialized && tts?.isLanguageAvailable(LOCALE) == TextToSpeech.LANG_AVAILABLE
    }
    
    /**
     * Get available voices for German
     */
    fun getAvailableVoices(): Set<String> {
        return try {
            tts?.voices
                ?.filter { it.locale == LOCALE }
                ?.map { it.name }
                ?.toSet() ?: emptySet()
        } catch (e: Exception) {
            Log.e(TAG, "Error getting voices: ${e.message}", e)
            emptySet()
        }
    }
    
    /**
     * Release resources
     */
    fun release() {
        stopAudio()
        tts?.shutdown()
        tts = null
        isInitialized = false
        Log.d(TAG, "TTS service released")
    }
    
    /**
     * Cache statistics data class
     */
    data class CacheStatistics(
        val fileCount: Int,
        val totalSizeBytes: Long,
        val totalSizeMB: Long
    )
}

