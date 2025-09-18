package com.hellogerman.app.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.hellogerman.app.data.models.DictionarySearchRequest
import com.hellogerman.app.data.models.DictionarySearchResult
import com.hellogerman.app.data.models.UnifiedSearchResult
import com.hellogerman.app.data.repository.OfflineDictionaryRepository
import com.hellogerman.app.data.repository.UnifiedDictionaryRepository
import com.hellogerman.app.data.repository.DictionaryRepository
import com.hellogerman.app.data.repository.HelloGermanRepository
import com.hellogerman.app.data.dictionary.LanguageHint
import com.hellogerman.app.data.dictionary.SearchConfidence
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
    private val unifiedRepository = UnifiedDictionaryRepository(application, onlineRepository)
    private val helloGermanRepository = HelloGermanRepository(application)
    private val ttsHelper = TTSHelper(application)
    
    init {
        // Initialize unified dictionary on startup
        viewModelScope.launch {
            try {
                unifiedRepository.initialize()
                android.util.Log.d("DictionaryViewModel", "Unified dictionary initialized successfully")
            } catch (e: Exception) {
                android.util.Log.e("DictionaryViewModel", "Failed to initialize unified dictionary", e)
            }
        }
    }
    
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()
    
    private val _searchResult = MutableStateFlow<DictionarySearchResult?>(null)
    val searchResult: StateFlow<DictionarySearchResult?> = _searchResult.asStateFlow()
    
    private val _unifiedSearchResult = MutableStateFlow<UnifiedSearchResult?>(null)
    val unifiedSearchResult: StateFlow<UnifiedSearchResult?> = _unifiedSearchResult.asStateFlow()
    
    private val _detectedLanguage = MutableStateFlow<LanguageHint>(LanguageHint.UNKNOWN)
    val detectedLanguage: StateFlow<LanguageHint> = _detectedLanguage.asStateFlow()
    
    private val _searchConfidence = MutableStateFlow<SearchConfidence>(SearchConfidence.LOW)
    val searchConfidence: StateFlow<SearchConfidence> = _searchConfidence.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()
    
    // Legacy language states (kept for backward compatibility)
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
    
    // Vocabulary states
    private val _isWordInVocabulary = MutableStateFlow(false)
    val isWordInVocabulary: StateFlow<Boolean> = _isWordInVocabulary.asStateFlow()
    
    private val _vocabularyMessage = MutableStateFlow<String?>(null)
    val vocabularyMessage: StateFlow<String?> = _vocabularyMessage.asStateFlow()
    
    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
        _errorMessage.value = null
    }

    // Function to set initial search query and perform search automatically
    fun setInitialSearchQuery(query: String) {
        if (query.isNotBlank()) {
            _searchQuery.value = query.trim()
            searchWord() // Automatically perform the search
        }
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
        
        android.util.Log.d("DictionaryViewModel", "Starting unified search for: $query")
        
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            // Use unified repository for intelligent search
            unifiedRepository.searchWord(query).fold(
                onSuccess = { unifiedResult ->
                    android.util.Log.d("DictionaryViewModel", "Unified search successful for: $query, hasResults: ${unifiedResult.hasResults}")
                    
                    _unifiedSearchResult.value = unifiedResult
                    _detectedLanguage.value = unifiedResult.detectedLanguage
                    _searchConfidence.value = unifiedResult.confidence
                    
                    // Update legacy states for backward compatibility
                    updateLegacyStates(unifiedResult)
                    
                    if (unifiedResult.hasResults) {
                        addToSearchHistory(query)
                        _selectedTab.value = 0
                        checkWordInVocabulary()
                    } else {
                        _errorMessage.value = "No information found for '$query'. Try a different word or check spelling."
                    }
                },
                onFailure = { exception ->
                    android.util.Log.e("DictionaryViewModel", "Unified search failed for: $query", exception)
                    _errorMessage.value = exception.message ?: "An error occurred while searching"
                }
            )
            
            _isLoading.value = false
        }
    }
    
    /**
     * Update legacy states for backward compatibility
     */
    private fun updateLegacyStates(unifiedResult: UnifiedSearchResult) {
        // Update legacy search result with primary translation
        val primaryTranslation = unifiedResult.primaryTranslation
        if (primaryTranslation != null) {
            val legacyResult = DictionarySearchResult(
                originalWord = unifiedResult.originalWord,
                translations = primaryTranslation.englishTranslations,
                fromLanguage = if (unifiedResult.detectedLanguage == LanguageHint.GERMAN) "de" else "en",
                toLanguage = if (unifiedResult.detectedLanguage == LanguageHint.GERMAN) "en" else "de",
                hasResults = true,
                definitions = primaryTranslation.englishTranslations.map { Definition(meaning = it) },
                gender = primaryTranslation.gender,
                wordType = primaryTranslation.wordType
            )
            _searchResult.value = legacyResult
        }
        
        // Update language states based on detection
        when (unifiedResult.detectedLanguage) {
            LanguageHint.GERMAN -> {
                _fromLanguage.value = "de"
                _toLanguage.value = "en"
            }
            LanguageHint.ENGLISH -> {
                _fromLanguage.value = "en"
                _toLanguage.value = "de"
            }
            else -> {
                // Keep current language settings for ambiguous cases
            }
        }
    }
    
    /**
     * Speak the searched word using TTS
     */
    fun speakWord() {
        val word = _searchQuery.value.trim()
        if (word.isNotEmpty()) {
            when (_fromLanguage.value.lowercase()) {
                "de", "german" -> ttsHelper.speakGerman(word)
                "en", "english" -> ttsHelper.speakEnglish(word)
                else -> ttsHelper.speakGerman(word) // Default to German
            }
        }
    }

    /**
     * Speak word slowly for pronunciation learning
     */
    fun speakWordSlowly() {
        val word = _searchQuery.value.trim()
        if (word.isNotEmpty()) {
            when (_fromLanguage.value.lowercase()) {
                "de", "german" -> ttsHelper.speakWordSlowly(word)
                "en", "english" -> ttsHelper.speakEnglishSlowly(word)
                else -> ttsHelper.speakWordSlowly(word) // Default to German
            }
        }
    }

    /**
     * Speak an example sentence
     */
    fun speakExample(text: String) {
        when (_fromLanguage.value.lowercase()) {
            "de", "german" -> ttsHelper.speakGerman(text)
            "en", "english" -> ttsHelper.speakEnglish(text)
            else -> ttsHelper.speakGerman(text) // Default to German
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
        _unifiedSearchResult.value = null
        _detectedLanguage.value = LanguageHint.UNKNOWN
        _searchConfidence.value = SearchConfidence.LOW
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
            else -> code.uppercase()
        }
    }
    
    fun getSupportedLanguages(): List<Pair<String, String>> {
        return listOf(
            "de" to "German",
            "en" to "English"
        )
    }
    
    /**
     * Check if the current word is already in user's vocabulary
     */
    fun checkWordInVocabulary() {
        val word = _searchQuery.value.trim()
        if (word.isNotEmpty()) {
            viewModelScope.launch {
                val existing = helloGermanRepository.getVocabularyByWord(word)
                _isWordInVocabulary.value = existing != null
            }
        }
    }
    
    /**
     * Add current word to user's vocabulary
     */
    fun addWordToVocabulary() {
        val word = _searchQuery.value.trim()
        val result = _searchResult.value
        
        if (word.isEmpty()) {
            _vocabularyMessage.value = "No word to add"
            return
        }
        
        if (result == null || !result.hasResults) {
            _vocabularyMessage.value = "Word not found - cannot add to vocabulary"
            return
        }
        
        viewModelScope.launch {
            try {
                val translation = result.translations.firstOrNull() ?: "No translation available"
                val gender = result.gender
                val level = determineWordLevel(result)
                
                val success = helloGermanRepository.addVocabularyToUserList(
                    word = word,
                    translation = translation,
                    gender = gender,
                    level = level,
                    category = "General",
                    source = "dictionary"
                )
                
                if (success) {
                    _isWordInVocabulary.value = true
                    _vocabularyMessage.value = "'$word' added to vocabulary!"
                } else {
                    _vocabularyMessage.value = "'$word' is already in your vocabulary"
                }
            } catch (e: Exception) {
                _vocabularyMessage.value = "Failed to add word: ${e.message}"
            }
        }
    }
    
    /**
     * Remove current word from user's vocabulary
     */
    fun removeWordFromVocabulary() {
        val word = _searchQuery.value.trim()
        
        if (word.isEmpty()) {
            _vocabularyMessage.value = "No word to remove"
            return
        }
        
        viewModelScope.launch {
            try {
                helloGermanRepository.deleteVocabularyByWord(word)
                _isWordInVocabulary.value = false
                _vocabularyMessage.value = "'$word' removed from vocabulary"
            } catch (e: Exception) {
                _vocabularyMessage.value = "Failed to remove word: ${e.message}"
            }
        }
    }
    
    /**
     * Clear vocabulary message
     */
    fun clearVocabularyMessage() {
        _vocabularyMessage.value = null
    }
    
    /**
     * Determine word level based on search result complexity
     */
    private fun determineWordLevel(result: DictionarySearchResult): String? {
        // Simple heuristic based on available information
        return when {
            result.conjugations != null -> "B1" // Verbs with conjugations
            result.examples.size > 2 -> "A2" // Words with many examples
            result.translations.size > 1 -> "A1" // Basic words with multiple translations
            else -> null // Unknown level
        }
    }
    
    override fun onCleared() {
        super.onCleared()
        ttsHelper.release()
    }
}
