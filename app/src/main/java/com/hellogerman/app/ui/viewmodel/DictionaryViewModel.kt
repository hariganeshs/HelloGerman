package com.hellogerman.app.ui.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.hellogerman.app.audio.AndroidTTSService
import com.hellogerman.app.data.entities.DictionaryEntry
import com.hellogerman.app.data.entities.SearchLanguage
import com.hellogerman.app.data.repository.DictionaryRepository
import com.hellogerman.app.data.dictionary.DictionaryImporter
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for dictionary search and management
 * 
 * Manages:
 * - Search queries and results
 * - Autocomplete suggestions
 * - Language selection
 * - Import progress
 * - Dictionary statistics
 */
class DictionaryViewModel(application: Application) : AndroidViewModel(application) {
    
    private val repository = DictionaryRepository(application)
    private val ttsService = AndroidTTSService(application)
    
    // Search state
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()
    
    private val _searchResults = MutableStateFlow<List<DictionaryEntry>>(emptyList())
    val searchResults: StateFlow<List<DictionaryEntry>> = _searchResults.asStateFlow()
    
    private val _suggestions = MutableStateFlow<List<String>>(emptyList())
    val suggestions: StateFlow<List<String>> = _suggestions.asStateFlow()
    
    private val _searchLanguage = MutableStateFlow(SearchLanguage.ENGLISH)
    val searchLanguage: StateFlow<SearchLanguage> = _searchLanguage.asStateFlow()
    
    private val _isSearching = MutableStateFlow(false)
    val isSearching: StateFlow<Boolean> = _isSearching.asStateFlow()
    
    // Selected entry for detail view
    private val _selectedEntry = MutableStateFlow<DictionaryEntry?>(null)
    val selectedEntry: StateFlow<DictionaryEntry?> = _selectedEntry.asStateFlow()
    
    // Import state
    private val _importProgress = MutableStateFlow<DictionaryImporter.ImportProgress?>(null)
    val importProgress: StateFlow<DictionaryImporter.ImportProgress?> = _importProgress.asStateFlow()
    
    private val _isImporting = MutableStateFlow(false)
    val isImporting: StateFlow<Boolean> = _isImporting.asStateFlow()
    
    private val _importResult = MutableStateFlow<DictionaryImporter.ImportResult?>(null)
    val importResult: StateFlow<DictionaryImporter.ImportResult?> = _importResult.asStateFlow()
    
    // Dictionary status
    private val _isDictionaryImported = MutableStateFlow(false)
    val isDictionaryImported: StateFlow<Boolean> = _isDictionaryImported.asStateFlow()
    
    private val _statistics = MutableStateFlow<DictionaryImporter.DictionaryStatistics?>(null)
    val statistics: StateFlow<DictionaryImporter.DictionaryStatistics?> = _statistics.asStateFlow()
    
    // Error handling
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()
    
    // Semantic search state
    private val _useSemanticSearch = MutableStateFlow(false)
    val useSemanticSearch: StateFlow<Boolean> = _useSemanticSearch.asStateFlow()
    
    private val _isSemanticSearchAvailable = MutableStateFlow(false)
    val isSemanticSearchAvailable: StateFlow<Boolean> = _isSemanticSearchAvailable.asStateFlow()
    
    private val _synonyms = MutableStateFlow<List<Pair<DictionaryEntry, Float>>>(emptyList())
    val synonyms: StateFlow<List<Pair<DictionaryEntry, Float>>> = _synonyms.asStateFlow()
    
    private val _relatedWords = MutableStateFlow<List<Pair<DictionaryEntry, Float>>>(emptyList())
    val relatedWords: StateFlow<List<Pair<DictionaryEntry, Float>>> = _relatedWords.asStateFlow()
    
    // Audio playback state
    private val _isPlayingAudio = MutableStateFlow(false)
    val isPlayingAudio: StateFlow<Boolean> = _isPlayingAudio.asStateFlow()
    
    private val _currentPlayingWord = MutableStateFlow<String?>(null)
    val currentPlayingWord: StateFlow<String?> = _currentPlayingWord.asStateFlow()
    
    // Debounce search
    private var searchJob: Job? = null
    private val SEARCH_DEBOUNCE_MS = 300L
    
    init {
        checkDictionaryStatus()
        initializeSemanticSearch()
        initializeTTS()
    }
    
    /**
     * Initialize TTS service
     */
    private fun initializeTTS() {
        viewModelScope.launch {
            try {
                ttsService.initialize()
            } catch (e: Exception) {
                Log.e("DictionaryViewModel", "Error initializing TTS: ${e.message}", e)
            }
        }
    }
    
    // ==================== Search Functions ====================
    
    /**
     * Update search query with debouncing
     */
    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
        
        // Cancel previous search job
        searchJob?.cancel()
        
        if (query.isBlank()) {
            _searchResults.value = emptyList()
            _suggestions.value = emptyList()
            return
        }
        
        // Start new debounced search
        searchJob = viewModelScope.launch {
            delay(SEARCH_DEBOUNCE_MS)
            performSearch(query)
            getSuggestions(query)
        }
    }
    
    /**
     * Perform immediate search without debouncing
     */
    fun searchImmediately(query: String) {
        _searchQuery.value = query
        viewModelScope.launch {
            performSearch(query)
        }
    }
    
    /**
     * Internal search execution - uses hybrid search when semantic search is enabled
     */
    private suspend fun performSearch(query: String) {
        try {
            _isSearching.value = true
            _errorMessage.value = null
            
            val results = if (_useSemanticSearch.value && _isSemanticSearchAvailable.value) {
                // Use hybrid search (exact + semantic)
                val hybridResults = repository.searchHybrid(
                    query = query,
                    language = _searchLanguage.value,
                    useSemanticSearch = true
                )
                hybridResults.map { it.first } // Extract entries, discard scores for now
            } else {
                // Use regular exact/prefix search
                repository.search(
                    query = query,
                    language = _searchLanguage.value,
                    exactMatch = false
                )
            }
            
            _searchResults.value = results
            
            // If we have results, find synonyms/related words
            if (results.isNotEmpty() && _useSemanticSearch.value) {
                findSynonymsFor(query)
            }
            
        } catch (e: Exception) {
            _errorMessage.value = "Search error: ${e.message}"
            _searchResults.value = emptyList()
        } finally {
            _isSearching.value = false
        }
    }
    
    /**
     * Get autocomplete suggestions
     */
    private suspend fun getSuggestions(prefix: String) {
        try {
            val suggestions = repository.getSuggestions(
                prefix = prefix,
                language = _searchLanguage.value,
                limit = 10
            )
            _suggestions.value = suggestions
        } catch (e: Exception) {
            _suggestions.value = emptyList()
        }
    }
    
    /**
     * Toggle search language
     */
    fun toggleSearchLanguage() {
        _searchLanguage.value = when (_searchLanguage.value) {
            SearchLanguage.ENGLISH -> SearchLanguage.GERMAN
            SearchLanguage.GERMAN -> SearchLanguage.ENGLISH
        }
        
        // Re-search with new language
        val currentQuery = _searchQuery.value
        if (currentQuery.isNotBlank()) {
            searchImmediately(currentQuery)
        }
    }
    
    /**
     * Set search language explicitly
     */
    fun setSearchLanguage(language: SearchLanguage) {
        _searchLanguage.value = language
    }
    
    /**
     * Clear search
     */
    fun clearSearch() {
        _searchQuery.value = ""
        _searchResults.value = emptyList()
        _suggestions.value = emptyList()
    }
    
    /**
     * Select an entry for detail view
     */
    fun selectEntry(entry: DictionaryEntry) {
        _selectedEntry.value = entry
    }
    
    /**
     * Clear selected entry
     */
    fun clearSelection() {
        _selectedEntry.value = null
    }
    
    // ==================== Dictionary Management ====================
    
    /**
     * Check if dictionary is imported
     */
    fun checkDictionaryStatus() {
        viewModelScope.launch {
            try {
                _isDictionaryImported.value = repository.isDictionaryImported()
                if (_isDictionaryImported.value) {
                    loadStatistics()
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error checking dictionary status: ${e.message}"
            }
        }
    }
    
    /**
     * Start dictionary import
     */
    fun startImport(clearExisting: Boolean = true) {
        viewModelScope.launch {
            try {
                _isImporting.value = true
                _importProgress.value = null
                _importResult.value = null
                _errorMessage.value = null
                
                val result = repository.importDictionary(
                    clearExisting = clearExisting,
                    progressListener = object : DictionaryImporter.ProgressListener {
                        override fun onProgressUpdate(progress: DictionaryImporter.ImportProgress) {
                            _importProgress.value = progress
                        }
                        
                        override fun onComplete(result: DictionaryImporter.ImportResult) {
                            _importResult.value = result
                            _importProgress.value = null
                            _isImporting.value = false
                            _isDictionaryImported.value = true
                            loadStatistics()
                        }
                        
                        override fun onError(error: Exception) {
                            _errorMessage.value = "Import failed: ${error.message}"
                            _isImporting.value = false
                            _importProgress.value = null
                        }
                    }
                )
                
            } catch (e: Exception) {
                _errorMessage.value = "Import error: ${e.message}"
                _isImporting.value = false
                _importProgress.value = null
            }
        }
    }
    
    /**
     * Load dictionary statistics
     */
    fun loadStatistics() {
        viewModelScope.launch {
            try {
                val stats = repository.getStatistics()
                _statistics.value = stats
            } catch (e: Exception) {
                _errorMessage.value = "Error loading statistics: ${e.message}"
            }
        }
    }
    
    /**
     * Clear dictionary
     */
    fun clearDictionary() {
        viewModelScope.launch {
            try {
                repository.clearDictionary()
                _isDictionaryImported.value = false
                _statistics.value = null
                _searchResults.value = emptyList()
                _suggestions.value = emptyList()
            } catch (e: Exception) {
                _errorMessage.value = "Error clearing dictionary: ${e.message}"
            }
        }
    }
    
    /**
     * Clear error message
     */
    fun clearError() {
        _errorMessage.value = null
    }
    
    // ==================== Semantic Search Functions ====================
    
    /**
     * Initialize semantic search system
     */
    private fun initializeSemanticSearch() {
        viewModelScope.launch {
            try {
                val available = repository.isSemanticSearchAvailable()
                _isSemanticSearchAvailable.value = available
                
                // Initialize vector search if available
                if (available) {
                    repository.initializeVectorSearch()
                }
            } catch (e: Exception) {
                _isSemanticSearchAvailable.value = false
            }
        }
    }
    
    /**
     * Toggle semantic search on/off
     */
    fun toggleSemanticSearch() {
        _useSemanticSearch.value = !_useSemanticSearch.value
        
        // Re-search with new setting if we have a query
        val currentQuery = _searchQuery.value
        if (currentQuery.isNotBlank()) {
            searchImmediately(currentQuery)
        }
    }
    
    /**
     * Set semantic search explicitly
     */
    fun setSemanticSearch(enabled: Boolean) {
        _useSemanticSearch.value = enabled
    }
    
    /**
     * Find synonyms for a word
     */
    private fun findSynonymsFor(word: String) {
        viewModelScope.launch {
            try {
                val synonymResults = repository.findSynonyms(
                    word = word,
                    language = _searchLanguage.value,
                    limit = 10
                )
                _synonyms.value = synonymResults
                
                // Also get related words
                val relatedResults = repository.findRelatedWords(
                    word = word,
                    language = _searchLanguage.value,
                    limit = 15
                )
                _relatedWords.value = relatedResults
                
            } catch (e: Exception) {
                _synonyms.value = emptyList()
                _relatedWords.value = emptyList()
            }
        }
    }
    
    /**
     * Clear synonyms and related words
     */
    fun clearSynonyms() {
        _synonyms.value = emptyList()
        _relatedWords.value = emptyList()
    }
    
    // ==================== Audio Pronunciation Functions ====================
    
    /**
     * Play pronunciation for a German word
     */
    fun playPronunciation(germanWord: String) {
        viewModelScope.launch {
            try {
                _isPlayingAudio.value = true
                _currentPlayingWord.value = germanWord
                
                val audioPath = ttsService.synthesizeSpeech(germanWord)
                if (audioPath != null) {
                    ttsService.playAudio(audioPath)
                } else {
                    _errorMessage.value = "Could not generate audio for: $germanWord"
                }
                
            } catch (e: Exception) {
                _errorMessage.value = "Audio playback error: ${e.message}"
            } finally {
                _isPlayingAudio.value = false
                _currentPlayingWord.value = null
            }
        }
    }
    
    /**
     * Stop audio playback
     */
    fun stopAudio() {
        ttsService.stopAudio()
        _isPlayingAudio.value = false
        _currentPlayingWord.value = null
    }
    
    /**
     * Get TTS cache statistics
     */
    fun getTTSCacheStats(): AndroidTTSService.CacheStatistics {
        return ttsService.getCacheStatistics()
    }
    
    /**
     * Clear TTS audio cache
     */
    fun clearTTSCache() {
        ttsService.clearCache()
    }
    
    // ==================== Utility Functions ====================
    
    /**
     * Format word with gender for display
     */
    fun formatWordWithGender(entry: DictionaryEntry): String {
        return repository.formatWordWithGender(entry)
    }
    
    /**
     * Cleanup resources when ViewModel is cleared
     */
    override fun onCleared() {
        super.onCleared()
        ttsService.release()
    }
}

