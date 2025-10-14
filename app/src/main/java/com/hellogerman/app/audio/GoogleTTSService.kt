package com.hellogerman.app.audio

import android.content.Context
import android.media.MediaPlayer
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.File
import java.io.IOException

/**
 * Google Cloud Text-to-Speech service
 * 
 * Provides high-quality German pronunciation audio using Google Cloud TTS API
 * - Free tier: 1 million characters per month
 * - Multiple German voices available
 * - Caches audio locally to minimize API calls
 */
class GoogleTTSService(private val context: Context) {
    
    companion object {
        private const val TAG = "GoogleTTSService"
        
        // Google Cloud TTS API endpoint
        private const val TTS_API_URL = "https://texttospeech.googleapis.com/v1/text:synthesize"
        
        // API Key (Note: In production, use a secure backend server to proxy requests)
        // For now, this is a placeholder - you'll need to add your own API key
        private const val API_KEY = "YOUR_GOOGLE_CLOUD_TTS_API_KEY"
        
        // Voice configuration
        private const val LANGUAGE_CODE = "de-DE"
        private const val VOICE_NAME = "de-DE-Wavenet-F" // Female voice
        // Alternative voices: de-DE-Wavenet-A (female), de-DE-Wavenet-B (male), 
        // de-DE-Wavenet-C (female), de-DE-Wavenet-D (male)
        
        // Audio configuration
        private const val AUDIO_ENCODING = "MP3"
        private const val SPEAKING_RATE = 0.9f // Slightly slower for learning
        private const val PITCH = 0.0f // Normal pitch
        
        // Cache configuration
        private const val CACHE_DIR_NAME = "tts_audio_cache"
        private const val MAX_CACHE_SIZE_MB = 100 // 100 MB max cache
        
        // Free tier limits
        private const val MONTHLY_CHARACTER_LIMIT = 1_000_000
    }
    
    private val client = OkHttpClient()
    private var mediaPlayer: MediaPlayer? = null
    private var monthlyCharacterUsage = 0
    
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
        
        // Check cache first
        val cachedFile = getCachedAudio(text)
        if (cachedFile != null && cachedFile.exists()) {
            Log.d(TAG, "Using cached audio for: $text")
            return@withContext cachedFile.absolutePath
        }
        
        // Check monthly usage limit
        if (monthlyCharacterUsage + text.length > MONTHLY_CHARACTER_LIMIT) {
            Log.w(TAG, "Monthly character limit reached. Consider using fallback TTS.")
            return@withContext synthesizeWithAndroidTTS(text)
        }
        
        try {
            // Build request JSON
            val requestJson = buildTTSRequest(text)
            
            // Make API request
            val requestBody = requestJson.toRequestBody("application/json".toMediaType())
            val request = Request.Builder()
                .url("$TTS_API_URL?key=$API_KEY")
                .post(requestBody)
                .build()
            
            val response = client.newCall(request).execute()
            
            if (!response.isSuccessful) {
                Log.e(TAG, "TTS API request failed: ${response.code}")
                return@withContext synthesizeWithAndroidTTS(text)
            }
            
            // Parse response
            val responseBody = response.body?.string() ?: return@withContext null
            val jsonResponse = JSONObject(responseBody)
            val audioContentBase64 = jsonResponse.getString("audioContent")
            
            // Decode base64 audio
            val audioBytes = android.util.Base64.decode(audioContentBase64, android.util.Base64.DEFAULT)
            
            // Save to cache
            val audioFile = saveAudioToCache(text, audioBytes)
            
            // Update usage tracking
            monthlyCharacterUsage += text.length
            
            Log.d(TAG, "Successfully synthesized audio for: $text")
            audioFile?.absolutePath
            
        } catch (e: IOException) {
            Log.e(TAG, "Network error during TTS synthesis: ${e.message}", e)
            synthesizeWithAndroidTTS(text)
        } catch (e: Exception) {
            Log.e(TAG, "Error synthesizing speech: ${e.message}", e)
            null
        }
    }
    
    /**
     * Build Google Cloud TTS API request JSON
     */
    private fun buildTTSRequest(text: String): String {
        val json = JSONObject()
        
        // Input text
        val input = JSONObject()
        input.put("text", text)
        json.put("input", input)
        
        // Voice parameters
        val voice = JSONObject()
        voice.put("languageCode", LANGUAGE_CODE)
        voice.put("name", VOICE_NAME)
        json.put("voice", voice)
        
        // Audio configuration
        val audioConfig = JSONObject()
        audioConfig.put("audioEncoding", AUDIO_ENCODING)
        audioConfig.put("speakingRate", SPEAKING_RATE)
        audioConfig.put("pitch", PITCH)
        json.put("audioConfig", audioConfig)
        
        return json.toString()
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
     * Save audio bytes to cache
     */
    private fun saveAudioToCache(text: String, audioBytes: ByteArray): File? {
        return try {
            val cacheDir = getCacheDirectory()
            val filename = generateCacheFilename(text)
            val file = File(cacheDir, filename)
            
            file.writeBytes(audioBytes)
            
            // Manage cache size
            manageCacheSize(cacheDir)
            
            file
        } catch (e: Exception) {
            Log.e(TAG, "Error saving audio to cache: ${e.message}", e)
            null
        }
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
        return "tts_${hash}.mp3"
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
     * Fallback to Android's built-in TTS
     * Used when Google Cloud TTS quota is exceeded or API fails
     */
    private suspend fun synthesizeWithAndroidTTS(text: String): String? = withContext(Dispatchers.IO) {
        try {
            // Note: This is a placeholder. Actual implementation would use
            // android.speech.tts.TextToSpeech
            Log.d(TAG, "Using Android TTS fallback for: $text")
            
            // Implementation would involve:
            // 1. Initialize TextToSpeech
            // 2. Set language to German
            // 3. Synthesize to file
            // 4. Return file path
            
            // For now, return null
            // TODO: Implement Android TTS fallback
            null
        } catch (e: Exception) {
            Log.e(TAG, "Error with Android TTS fallback: ${e.message}", e)
            null
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
        } catch (e: Exception) {
            Log.e(TAG, "Error stopping audio: ${e.message}", e)
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
     * Release resources
     */
    fun release() {
        stopAudio()
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

