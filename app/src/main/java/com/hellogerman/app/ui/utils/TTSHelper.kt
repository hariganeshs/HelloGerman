package com.hellogerman.app.ui.utils

import android.content.Context
import android.speech.tts.TextToSpeech
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.*

/**
 * Helper class for Text-to-Speech functionality
 * Supports German pronunciation for dictionary words
 */
class TTSHelper(private val context: Context) : TextToSpeech.OnInitListener {
    
    private var tts: TextToSpeech? = null
    private val _isInitialized = MutableStateFlow(false)
    val isInitialized: StateFlow<Boolean> = _isInitialized.asStateFlow()
    
    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()
    
    companion object {
        private const val TAG = "TTSHelper"
        private const val GERMAN_LANGUAGE_CODE = "de"
        private const val DEFAULT_SPEECH_RATE = 0.8f
        private const val DEFAULT_PITCH = 1.0f
    }
    
    init {
        initializeTTS()
    }
    
    private fun initializeTTS() {
        try {
            tts = TextToSpeech(context, this)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize TTS", e)
            _isInitialized.value = false
        }
    }
    
    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            tts?.let { textToSpeech ->
                // Set German as the language
                val result = textToSpeech.setLanguage(Locale.GERMAN)
                
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Log.w(TAG, "German language not supported, falling back to default")
                    // Try alternative German locales
                    val altResult = textToSpeech.setLanguage(Locale.Builder().setLanguage("de").setRegion("DE").build())
                    if (altResult == TextToSpeech.LANG_MISSING_DATA || altResult == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.e(TAG, "German language not available")
                    }
                }
                
                // Configure speech parameters
                textToSpeech.setSpeechRate(DEFAULT_SPEECH_RATE)
                textToSpeech.setPitch(DEFAULT_PITCH)
                
                _isInitialized.value = true
                Log.d(TAG, "TTS initialized successfully")
            }
        } else {
            Log.e(TAG, "TTS initialization failed with status: $status")
            _isInitialized.value = false
        }
    }
    
    /**
     * Speak the given German text
     */
    fun speakGerman(text: String) {
        if (!_isInitialized.value || text.isBlank()) {
            Log.w(TAG, "TTS not initialized or empty text")
            return
        }
        
        tts?.let { textToSpeech ->
            _isPlaying.value = true
            
            val result = textToSpeech.speak(
                text,
                TextToSpeech.QUEUE_FLUSH,
                null,
                "TTS_UTTERANCE_ID"
            )
            
            if (result == TextToSpeech.ERROR) {
                Log.e(TAG, "Failed to speak text: $text")
                _isPlaying.value = false
            } else {
                // Set up listener to track when speaking finishes
                textToSpeech.setOnUtteranceProgressListener(object : android.speech.tts.UtteranceProgressListener() {
                    override fun onStart(utteranceId: String?) {
                        _isPlaying.value = true
                    }
                    
                    override fun onDone(utteranceId: String?) {
                        _isPlaying.value = false
                    }
                    
                    override fun onError(utteranceId: String?) {
                        _isPlaying.value = false
                        Log.e(TAG, "TTS error for utteranceId: $utteranceId")
                    }
                })
            }
        }
    }
    
    /**
     * Speak a German word with slower speed for learning
     */
    fun speakWordSlowly(word: String) {
        tts?.setSpeechRate(0.5f) // Slower for pronunciation learning
        speakGerman(word)
        // Reset to normal speed after a delay
        tts?.setSpeechRate(DEFAULT_SPEECH_RATE)
    }
    
    /**
     * Stop current speech
     */
    fun stop() {
        tts?.stop()
        _isPlaying.value = false
    }
    
    /**
     * Check if TTS is currently speaking
     */
    fun isSpeaking(): Boolean {
        return tts?.isSpeaking == true
    }
    
    /**
     * Check if German language is available
     */
    fun isGermanAvailable(): Boolean {
        return tts?.isLanguageAvailable(Locale.GERMAN) == TextToSpeech.LANG_AVAILABLE ||
                tts?.isLanguageAvailable(Locale.Builder().setLanguage("de").setRegion("DE").build()) == TextToSpeech.LANG_AVAILABLE
    }
    
    /**
     * Set speech rate (0.5 = half speed, 1.0 = normal, 2.0 = double speed)
     */
    fun setSpeechRate(rate: Float) {
        tts?.setSpeechRate(rate.coerceIn(0.1f, 3.0f))
    }
    
    /**
     * Set pitch (0.5 = lower pitch, 1.0 = normal, 2.0 = higher pitch)
     */
    fun setPitch(pitch: Float) {
        tts?.setPitch(pitch.coerceIn(0.1f, 2.0f))
    }
    
    /**
     * Release TTS resources
     */
    fun release() {
        tts?.stop()
        tts?.shutdown()
        tts = null
        _isInitialized.value = false
        _isPlaying.value = false
    }
}
