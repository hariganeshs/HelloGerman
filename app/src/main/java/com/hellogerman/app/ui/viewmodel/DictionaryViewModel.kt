package com.hellogerman.app.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.hellogerman.app.data.models.DictionarySearchRequest
import com.hellogerman.app.data.models.DictionarySearchResult
import com.hellogerman.app.data.repository.OfflineDictionaryRepository
import com.hellogerman.app.data.repository.DictionaryRepository
import com.hellogerman.app.ui.utils.TTSHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Enhanced ViewModel for comprehensive dictionary functionality
 * Supports definitions, conjugations, examples, synonyms, and TTS
 */
class DictionaryViewModel(application: Application) : AndroidViewModel(application) {
    
    private val onlineRepository = DictionaryRepository(application)
    private val repository = OfflineDictionaryRepository(application, onlineRepository)
    private val ttsHelper = TTSHelper(application)
    
    init {
        // Initialize offline dictionary on startup
        viewModelScope.launch {
            try {
                repository.initialize()
                android.util.Log.d("DictionaryViewModel", "Offline dictionary initialized successfully")
            } catch (e: Exception) {
                android.util.Log.e("DictionaryViewModel", "Failed to initialize offline dictionary", e)
            }
        }
    }
    
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()
    
    private val _searchResult = MutableStateFlow<DictionarySearchResult?>(null)
    val searchResult: StateFlow<DictionarySearchResult?> = _searchResult.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()
    
    private val _fromLanguage = MutableStateFlow("de") // German
    val fromLanguage: StateFlow<String> = _fromLanguage.asStateFlow()
    
    private val _toLanguage = MutableStateFlow("en") // English
    val toLanguage: StateFlow<String> = _toLanguage.asStateFlow()
    
    private val _searchHistory = MutableStateFlow<List<String>>(emptyList())
    val searchHistory: StateFlow<List<String>> = _searchHistory.asStateFlow()
    
    // Selected tab for dictionary UI
    private val _selectedTab = MutableStateFlow(0) // 0=Overview, 1=Definitions, 2=Examples, 3=Conjugations, 4=Synonyms
    val selectedTab: StateFlow<Int> = _selectedTab.asStateFlow()
    
    // TTS states
    val isTTSInitialized: StateFlow<Boolean> = ttsHelper.isInitialized
    val isTTSPlaying: StateFlow<Boolean> = ttsHelper.isPlaying
    
    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
        _errorMessage.value = null
    }
    
    fun swapLanguages() {
        val temp = _fromLanguage.value
        _fromLanguage.value = _toLanguage.value
        _toLanguage.value = temp
    }
    
    fun setFromLanguage(lang: String) {
        _fromLanguage.value = lang
    }
    
    fun setToLanguage(lang: String) {
        _toLanguage.value = lang
    }
    
    fun setSelectedTab(tabIndex: Int) {
        _selectedTab.value = tabIndex
    }
    
    fun searchWord() {
        val query = _searchQuery.value.trim()
        if (query.isEmpty()) {
            _errorMessage.value = "Please enter a word to search"
            return
        }
        
        // Offline repository works without internet, so no connectivity check needed
        android.util.Log.d("DictionaryViewModel", "Starting search for: $query")
        
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            val request = DictionarySearchRequest(
                word = query,
                fromLang = _fromLanguage.value,
                toLang = _toLanguage.value
            )
            
            repository.searchWord(request).fold(
                onSuccess = { result ->
                    android.util.Log.d("DictionaryViewModel", "Search successful for: $query, hasResults: ${result.hasResults}, gender: ${result.gender}")
                    _searchResult.value = result
                    if (result.hasResults) {
                        addToSearchHistory(query)
                        // Reset to overview tab when new search is performed
                        _selectedTab.value = 0
                    } else {
                        _errorMessage.value = "No information found for '$query'. Try a different word or check spelling."
                    }
                },
                onFailure = { exception ->
                    _errorMessage.value = exception.message ?: "An error occurred while searching"
                }
            )
            
            _isLoading.value = false
        }
    }
    
    /**
     * Speak the searched word using TTS
     */
    fun speakWord() {
        val word = _searchQuery.value.trim()
        if (word.isNotEmpty() && _fromLanguage.value == "de") {
            ttsHelper.speakGerman(word)
        }
    }
    
    /**
     * Speak word slowly for pronunciation learning
     */
    fun speakWordSlowly() {
        val word = _searchQuery.value.trim()
        if (word.isNotEmpty() && _fromLanguage.value == "de") {
            ttsHelper.speakWordSlowly(word)
        }
    }
    
    /**
     * Speak an example sentence
     */
    fun speakExample(text: String) {
        if (_fromLanguage.value == "de") {
            ttsHelper.speakGerman(text)
        }
    }
    
    /**
     * Stop TTS playback
     */
    fun stopTTS() {
        ttsHelper.stop()
    }
    
    fun clearError() {
        _errorMessage.value = null
    }
    
    fun clearResults() {
        _searchResult.value = null
        _searchQuery.value = ""
        _errorMessage.value = null
        _selectedTab.value = 0
    }
    
    private fun addToSearchHistory(query: String) {
        val currentHistory = _searchHistory.value.toMutableList()
        if (!currentHistory.contains(query)) {
            currentHistory.add(0, query)
            if (currentHistory.size > 20) { // Keep more history for enhanced dictionary
                currentHistory.removeAt(currentHistory.size - 1)
            }
            _searchHistory.value = currentHistory
        }
    }
    
    fun selectFromHistory(word: String) {
        _searchQuery.value = word
        searchWord()
    }
    
    fun clearHistory() {
        _searchHistory.value = emptyList()
    }
    
    fun clearCache() {
        // Offline repository doesn't use cache, it uses database
        android.util.Log.d("DictionaryViewModel", "Cache clear requested but offline repository uses database")
    }

    fun getCacheSize(): Int {
        // Offline repository doesn't use cache, return 0
        return 0
    }

    /**
     * Reset the offline dictionary database
     * This will delete all data and repopulate with essential words
     */
    fun resetDictionaryDatabase() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _errorMessage.value = "Resetting dictionary database..."

                repository.resetDatabase()

                _errorMessage.value = "Dictionary database reset successfully!"
                android.util.Log.d("DictionaryViewModel", "Dictionary database reset completed")

                // Clear current results to force fresh search
                clearResults()

            } catch (e: Exception) {
                _errorMessage.value = "Failed to reset dictionary: ${e.message}"
                android.util.Log.e("DictionaryViewModel", "Failed to reset dictionary database", e)
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun getLanguageName(code: String): String {
        return when (code) {
            "de" -> "German"
            "en" -> "English"
            "fr" -> "French"
            "es" -> "Spanish"
            "it" -> "Italian"
            "pt" -> "Portuguese"
            "ru" -> "Russian"
            "pl" -> "Polish"
            "nl" -> "Dutch"
            "sv" -> "Swedish"
            else -> code.uppercase()
        }
    }
    
    fun getSupportedLanguages(): List<Pair<String, String>> {
        return listOf(
            "de" to "German",
            "en" to "English",
            "fr" to "French",
            "es" to "Spanish",
            "it" to "Italian",
            "pt" to "Portuguese",
            "ru" to "Russian",
            "pl" to "Polish",
            "nl" to "Dutch",
            "sv" to "Swedish"
        )
    }
    
    override fun onCleared() {
        super.onCleared()
        ttsHelper.release()
    }
}
