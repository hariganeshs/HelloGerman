package com.hellogerman.app.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.hellogerman.app.data.repository.LeoDictionaryRepository
import com.hellogerman.app.data.models.*
import com.hellogerman.app.ui.utils.TTSHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for Leo-style dictionary with comprehensive grammar information
 */
class LeoDictionaryViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = LeoDictionaryRepository(application)
    private val ttsHelper = TTSHelper(application)

    // UI State
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _searchResult = MutableStateFlow<LeoDictionarySearchResult?>(null)
    val searchResult: StateFlow<LeoDictionarySearchResult?> = _searchResult.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _searchHistory = MutableStateFlow<List<String>>(emptyList())
    val searchHistory: StateFlow<List<String>> = _searchHistory.asStateFlow()

    // Current search language (de for German, en for English)
    private val _currentLanguage = MutableStateFlow("de")
    val currentLanguage: StateFlow<String> = _currentLanguage.asStateFlow()

    // TTS state
    val isTTSInitialized: StateFlow<Boolean> = ttsHelper.isInitialized
    val isTTSPlaying: StateFlow<Boolean> = ttsHelper.isPlaying

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun searchWord() {
        val query = _searchQuery.value.trim()
        if (query.isEmpty()) return

        _isLoading.value = true
        _errorMessage.value = null

        viewModelScope.launch {
            try {
                val result = if (_currentLanguage.value == "de") {
                    repository.searchGermanWord(query)
                } else {
                    repository.searchEnglishWord(query)
                }

                _searchResult.value = result

                // Add to search history
                if (result.hasResults) {
                    addToSearchHistory(query)
                }

            } catch (e: Exception) {
                _errorMessage.value = "Search failed: ${e.message}"
                _searchResult.value = null
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun setLanguage(language: String) {
        _currentLanguage.value = language
    }

    fun clearResults() {
        _searchResult.value = null
        _errorMessage.value = null
    }

    fun clearError() {
        _errorMessage.value = null
    }

    private fun addToSearchHistory(word: String) {
        val currentHistory = _searchHistory.value.toMutableList()
        currentHistory.remove(word) // Remove if already exists
        currentHistory.add(0, word) // Add to beginning
        _searchHistory.value = currentHistory.take(20) // Keep only last 20
    }

    fun selectFromHistory(word: String) {
        _searchQuery.value = word
        searchWord()
    }

    fun clearHistory() {
        _searchHistory.value = emptyList()
    }

    fun clearCache() {
        repository.clearCache()
    }

    /**
     * Play pronunciation of a German word using TTS
     */
    fun playGermanPronunciation(word: String) {
        if (word.isNotBlank()) {
            ttsHelper.speakGerman(word)
        }
    }

    /**
     * Play pronunciation of an English word using TTS
     */
    fun playEnglishPronunciation(word: String) {
        if (word.isNotBlank()) {
            ttsHelper.speakEnglish(word)
        }
    }

    /**
     * Play pronunciation of a word based on current language setting
     */
    fun playPronunciation(word: String) {
        when (_currentLanguage.value) {
            "de" -> playGermanPronunciation(word)
            "en" -> playEnglishPronunciation(word)
            else -> playGermanPronunciation(word) // Default to German
        }
    }

    /**
     * Stop current TTS playback
     */
    fun stopPronunciation() {
        ttsHelper.stop()
    }

    override fun onCleared() {
        super.onCleared()
        ttsHelper.release()
    }
}