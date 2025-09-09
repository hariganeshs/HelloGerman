package com.hellogerman.app.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.hellogerman.app.data.repository.HelloGermanRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import com.hellogerman.app.data.entities.UserProgress

class ThemeViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = HelloGermanRepository(application)

    private val _isDarkMode = MutableStateFlow(false)
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    private val _selectedTheme = MutableStateFlow("default")
    val selectedTheme: StateFlow<String> = _selectedTheme.asStateFlow()

    private val _textSize = MutableStateFlow(1.0f)
    val textSize: StateFlow<Float> = _textSize.asStateFlow()

    private val _userProgress = MutableStateFlow<UserProgress?>(null)
    
    init {
        loadUserProgress()
    }
    
    private fun loadUserProgress() {
        viewModelScope.launch {
            repository.getUserProgress().collect { progress ->
                _userProgress.value = progress
                if (progress != null) {
                    _isDarkMode.value = progress.isDarkMode
                    _selectedTheme.value = progress.selectedTheme
                    _textSize.value = progress.textSize
                }
            }
        }
    }
    
    fun toggleDarkMode() {
        val newValue = !_isDarkMode.value
        _isDarkMode.value = newValue
        viewModelScope.launch {
            _userProgress.value?.let { progress ->
                val updatedProgress = progress.copy(isDarkMode = newValue)
                repository.updateUserProgress(updatedProgress)
            }
        }
    }
    
    fun setDarkMode(enabled: Boolean) {
        _isDarkMode.value = enabled
        viewModelScope.launch {
            _userProgress.value?.let { progress ->
                val updatedProgress = progress.copy(isDarkMode = enabled)
                repository.updateUserProgress(updatedProgress)
            }
        }
    }

    fun setSelectedTheme(theme: String) {
        _selectedTheme.value = theme
        viewModelScope.launch {
            _userProgress.value?.let { progress ->
                val updatedProgress = progress.copy(selectedTheme = theme)
                repository.updateUserProgress(updatedProgress)
            }
        }
    }
}
