package com.hellogerman.app.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.hellogerman.app.data.entities.UserProgress
import com.hellogerman.app.data.repository.HelloGermanRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    
    private val repository = HelloGermanRepository(application)
    
    private val _userProgress = MutableStateFlow<UserProgress?>(null)
    val userProgress: StateFlow<UserProgress?> = _userProgress.asStateFlow()
    
    private val _isDarkMode = MutableStateFlow(false)
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()
    
    private val _textSize = MutableStateFlow(1.0f)
    val textSize: StateFlow<Float> = _textSize.asStateFlow()
    
    private val _dailyGoal = MutableStateFlow(3)
    val dailyGoal: StateFlow<Int> = _dailyGoal.asStateFlow()
    
    init {
        loadUserProgress()
    }
    
    private fun loadUserProgress() {
        viewModelScope.launch {
            repository.getUserProgress().collect { progress ->
                _userProgress.value = progress
                if (progress != null) {
                    _isDarkMode.value = progress.isDarkMode
                    _textSize.value = progress.textSize
                    _dailyGoal.value = progress.dailyGoal
                }
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
    
    fun setTextSize(size: Float) {
        _textSize.value = size
        viewModelScope.launch {
            _userProgress.value?.let { progress ->
                val updatedProgress = progress.copy(textSize = size)
                repository.updateUserProgress(updatedProgress)
            }
        }
    }
    
    fun setDailyGoal(goal: Int) {
        _dailyGoal.value = goal
        viewModelScope.launch {
            _userProgress.value?.let { progress ->
                val updatedProgress = progress.copy(dailyGoal = goal)
                repository.updateUserProgress(updatedProgress)
            }
        }
    }
    
    fun resetProgress() {
        viewModelScope.launch {
            val resetProgress = UserProgress()
            repository.updateUserProgress(resetProgress)
        }
    }
    
    fun exportData(): String {
        return _userProgress.value?.let { progress ->
            """
            User Progress Export:
            Current Level: ${progress.currentLevel}
            Total Lessons Completed: ${progress.totalLessonsCompleted}
            Current Streak: ${progress.currentStreak}
            Longest Streak: ${progress.longestStreak}
            
            Skill Scores:
            - Lesen (Reading): ${progress.lesenScore}%
            - Hören (Listening): ${progress.hoerenScore}%
            - Schreiben (Writing): ${progress.schreibenScore}%
            - Sprechen (Speaking): ${progress.sprechenScore}%
            
            Settings:
            - Daily Goal: ${progress.dailyGoal} lessons
            - Dark Mode: ${if (progress.isDarkMode) "Enabled" else "Disabled"}
            - Text Size: ${(progress.textSize * 100).toInt()}%
            """.trimIndent()
        } ?: "No data available"
    }
}
