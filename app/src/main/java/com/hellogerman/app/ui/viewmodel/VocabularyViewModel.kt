package com.hellogerman.app.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.hellogerman.app.data.entities.UserVocabulary
import com.hellogerman.app.data.repository.HelloGermanRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class VocabularyViewModel(application: Application) : AndroidViewModel(application) {
    
    private val repository = HelloGermanRepository(application)
    
    private val _vocabulary = MutableStateFlow<List<UserVocabulary>>(emptyList())
    val vocabulary: StateFlow<List<UserVocabulary>> = _vocabulary.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _selectedFilter = MutableStateFlow("all")
    val selectedFilter: StateFlow<String> = _selectedFilter.asStateFlow()
    
    fun loadVocabulary() {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                repository.getAllUserVocabulary().collect { vocabList ->
                    _vocabulary.value = applyFilter(vocabList)
                    _isLoading.value = false
                }
            } catch (e: Exception) {
                _isLoading.value = false
            }
        }
    }
    
    fun setFilter(filter: String) {
        _selectedFilter.value = filter
        val currentVocab = repository.getAllUserVocabulary()
        viewModelScope.launch {
            currentVocab.collect { vocabList ->
                _vocabulary.value = applyFilter(vocabList)
            }
        }
    }
    
    fun toggleFavoriteFilter() {
        val newFilter = if (_selectedFilter.value == "favorites") "all" else "favorites"
        setFilter(newFilter)
    }
    
    private fun applyFilter(vocabList: List<UserVocabulary>): List<UserVocabulary> {
        return when (_selectedFilter.value) {
            "favorites" -> vocabList.filter { it.isFavorite }
            "recent" -> vocabList.sortedByDescending { it.addedAt }.take(20)
            else -> vocabList
        }
    }
    
    fun toggleFavorite(word: String) {
        viewModelScope.launch {
            repository.toggleFavoriteStatus(word)
            // Reload vocabulary to update the list
            loadVocabulary()
        }
    }
    
    fun deleteVocabulary(word: String) {
        viewModelScope.launch {
            repository.deleteVocabularyByWord(word)
            // Reload vocabulary to update the list
            loadVocabulary()
        }
    }
}
