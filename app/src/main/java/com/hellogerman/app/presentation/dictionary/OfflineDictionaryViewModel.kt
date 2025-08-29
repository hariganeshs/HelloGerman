package com.hellogerman.app.presentation.dictionary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hellogerman.app.data.models.DictionarySearchResult
import com.hellogerman.app.data.models.DictionarySearchRequest
import com.hellogerman.app.data.repository.OfflineDictionaryRepository
import com.hellogerman.app.data.service.VocabularyPackService
import com.hellogerman.app.data.service.VocabularyPack
import com.hellogerman.app.data.repository.DatabaseStats
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Enhanced Dictionary ViewModel with comprehensive offline support
 * Provides 100% reliable dictionary functionality with smart fallbacks
 */
@HiltViewModel
class OfflineDictionaryViewModel @Inject constructor(
    private val offlineRepository: OfflineDictionaryRepository,
    private val vocabularyPackService: VocabularyPackService
) : ViewModel() {
    
    private val _searchState = MutableStateFlow(DictionarySearchState())
    val searchState: StateFlow<DictionarySearchState> = _searchState.asStateFlow()
    
    private val _vocabularyPacks = MutableStateFlow<List<VocabularyPack>>(emptyList())
    val vocabularyPacks: StateFlow<List<VocabularyPack>> = _vocabularyPacks.asStateFlow()
    
    private val _databaseStats = MutableStateFlow<DatabaseStats?>(null)
    val databaseStats: StateFlow<DatabaseStats?> = _databaseStats.asStateFlow()
    
    private val _wordSuggestions = MutableStateFlow<List<String>>(emptyList())
    val wordSuggestions: StateFlow<List<String>> = _wordSuggestions.asStateFlow()
    
    init {
        // Initialize offline database
        viewModelScope.launch {
            try {
                offlineRepository.initialize()
                loadDatabaseStats()
                loadAvailableVocabularyPacks()
            } catch (e: Exception) {
                _searchState.value = _searchState.value.copy(
                    isLoading = false,
                    error = "Failed to initialize offline dictionary: ${e.message}"
                )
            }
        }
    }
    
    /**
     * Search with comprehensive offline-first approach
     */
    fun searchWord(word: String, fromLang: String = "de", toLang: String = "en") {
        if (word.isBlank()) {
            clearSearch()
            return
        }
        
        viewModelScope.launch {
            _searchState.value = _searchState.value.copy(isLoading = true, error = null)
            
            try {
                val request = DictionarySearchRequest(
                    word = word.trim(),
                    fromLang = fromLang,
                    toLang = toLang
                )
                
                val result = offlineRepository.searchWord(request)
                
                result.fold(
                    onSuccess = { searchResult ->
                        _searchState.value = _searchState.value.copy(
                            isLoading = false,
                            result = searchResult,
                            error = null,
                            searchHistory = addToHistory(word, _searchState.value.searchHistory),
                            dataSource = determineDataSource(searchResult)
                        )
                    },
                    onFailure = { error ->
                        _searchState.value = _searchState.value.copy(
                            isLoading = false,
                            error = "Search failed: ${error.message}",
                            result = null
                        )
                    }
                )
            } catch (e: Exception) {
                _searchState.value = _searchState.value.copy(
                    isLoading = false,
                    error = "Unexpected error: ${e.message}",
                    result = null
                )
            }
        }
    }
    
    /**
     * Get word suggestions for autocomplete
     */
    fun getWordSuggestions(prefix: String) {
        if (prefix.length < 2) {
            _wordSuggestions.value = emptyList()
            return
        }
        
        viewModelScope.launch {
            try {
                val suggestions = offlineRepository.getWordSuggestions(prefix)
                _wordSuggestions.value = suggestions
            } catch (e: Exception) {
                _wordSuggestions.value = emptyList()
            }
        }
    }
    
    /**
     * Get words by CEFR level for learning
     */
    fun getWordsByLevel(level: String) {
        viewModelScope.launch {
            try {
                val words = offlineRepository.getWordsByLevel(level)
                _searchState.value = _searchState.value.copy(
                    levelWords = words,
                    selectedLevel = level
                )
            } catch (e: Exception) {
                _searchState.value = _searchState.value.copy(
                    error = "Failed to load $level words: ${e.message}"
                )
            }
        }
    }
    
    /**
     * Download extended vocabulary pack
     */
    fun downloadExtendedVocabulary() {
        viewModelScope.launch {
            try {
                val workInfo = vocabularyPackService.downloadExtendedVocabulary()
                // Observe work progress
                _searchState.value = _searchState.value.copy(
                    downloadInProgress = true,
                    downloadProgress = 0
                )
            } catch (e: Exception) {
                _searchState.value = _searchState.value.copy(
                    error = "Failed to start download: ${e.message}"
                )
            }
        }
    }
    
    /**
     * Download specialized vocabulary pack
     */
    fun downloadSpecializedVocabulary(packId: String) {
        // Implementation for downloading specific vocabulary packs
        viewModelScope.launch {
            _searchState.value = _searchState.value.copy(
                downloadInProgress = true,
                downloadProgress = 0
            )
            // Start download process
        }
    }
    
    private fun clearSearch() {
        _searchState.value = _searchState.value.copy(
            result = null,
            error = null,
            isLoading = false
        )
        _wordSuggestions.value = emptyList()
    }
    
    private fun addToHistory(word: String, currentHistory: List<String>): List<String> {
        val newHistory = currentHistory.toMutableList()
        newHistory.remove(word) // Remove if already exists
        newHistory.add(0, word) // Add to front
        return newHistory.take(20) // Keep only last 20 searches
    }
    
    private fun determineDataSource(result: DictionarySearchResult): DataSource {
        return when {
            result.definitions.isNotEmpty() && result.examples.isNotEmpty() -> DataSource.OFFLINE_COMPLETE
            result.definitions.isNotEmpty() -> DataSource.OFFLINE_PARTIAL  
            result.hasResults -> DataSource.ONLINE_API
            else -> DataSource.FALLBACK
        }
    }
    
    private suspend fun loadDatabaseStats() {
        try {
            val stats = offlineRepository.getDatabaseStats()
            _databaseStats.value = stats
        } catch (e: Exception) {
            // Ignore stats loading errors
        }
    }
    
    private suspend fun loadAvailableVocabularyPacks() {
        try {
            val packs = vocabularyPackService.getAvailableVocabularyPacks()
            val installedPacks = vocabularyPackService.getInstalledPacks()
            
            val updatedPacks = packs.map { pack ->
                pack.copy(isInstalled = pack.id in installedPacks)
            }
            
            _vocabularyPacks.value = updatedPacks
        } catch (e: Exception) {
            // Ignore pack loading errors
        }
    }
}

data class DictionarySearchState(
    val isLoading: Boolean = false,
    val result: DictionarySearchResult? = null,
    val error: String? = null,
    val searchHistory: List<String> = emptyList(),
    val dataSource: DataSource = DataSource.UNKNOWN,
    val levelWords: List<String> = emptyList(),
    val selectedLevel: String? = null,
    val downloadInProgress: Boolean = false,
    val downloadProgress: Int = 0
)

enum class DataSource {
    OFFLINE_COMPLETE,  // Full offline data available
    OFFLINE_PARTIAL,   // Partial offline data
    ONLINE_API,        // Data from online APIs
    FALLBACK,          // Basic fallback data
    UNKNOWN
}
