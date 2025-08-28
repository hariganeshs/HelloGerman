package com.hellogerman.app.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.hellogerman.app.data.models.DictionarySearchRequest
import com.hellogerman.app.data.models.DictionarySearchResult
import com.hellogerman.app.data.repository.DictionaryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for dictionary functionality
 */
class DictionaryViewModel(application: Application) : AndroidViewModel(application) {
    
    private val repository = DictionaryRepository(application)
    
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
    
    fun searchWord() {
        val query = _searchQuery.value.trim()
        if (query.isEmpty()) {
            _errorMessage.value = "Please enter a word to search"
            return
        }
        
        if (!repository.isInternetAvailable()) {
            _errorMessage.value = "No internet connection. Please check your network settings."
            return
        }
        
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
                    _searchResult.value = result
                    if (result.hasResults) {
                        addToSearchHistory(query)
                    } else {
                        _errorMessage.value = "No translations found for '$query'"
                    }
                },
                onFailure = { exception ->
                    _errorMessage.value = exception.message ?: "An error occurred while searching"
                }
            )
            
            _isLoading.value = false
        }
    }
    
    fun clearError() {
        _errorMessage.value = null
    }
    
    fun clearResults() {
        _searchResult.value = null
        _searchQuery.value = ""
        _errorMessage.value = null
    }
    
    private fun addToSearchHistory(query: String) {
        val currentHistory = _searchHistory.value.toMutableList()
        if (!currentHistory.contains(query)) {
            currentHistory.add(0, query)
            if (currentHistory.size > 10) { // Keep only last 10 searches
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
}
