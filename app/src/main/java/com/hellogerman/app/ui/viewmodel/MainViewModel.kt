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

class MainViewModel(application: Application) : AndroidViewModel(application) {
    
    private val repository = HelloGermanRepository(application)
    
    private val _userProgress = MutableStateFlow<UserProgress?>(null)
    val userProgress: StateFlow<UserProgress?> = _userProgress.asStateFlow()
    
    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _currentLevel = MutableStateFlow("A1")
    val currentLevel: StateFlow<String> = _currentLevel.asStateFlow()
    
    init {
        loadUserProgress()
    }
    
    private fun loadUserProgress() {
        viewModelScope.launch {
            repository.getUserProgress().collect { progress ->
                _userProgress.value = progress
                if (progress != null) {
                    _currentLevel.value = progress.currentLevel
                } else {
                    // Initialize user progress if it doesn't exist
                    initializeUserProgress()
                }
                _isLoading.value = false
            }
        }
    }
    
    private suspend fun initializeUserProgress() {
        val initialProgress = UserProgress()
        repository.insertUserProgress(initialProgress)
        _userProgress.value = initialProgress
    }
    
    fun updateCurrentLevel(level: String) {
        viewModelScope.launch {
            repository.updateCurrentLevel(level)
            _currentLevel.value = level
        }
    }
    
    fun updateSkillScore(skill: String, score: Int) {
        viewModelScope.launch {
            repository.updateSkillScore(skill, score)
        }
    }
    
    fun incrementLessonsCompleted() {
        viewModelScope.launch {
            repository.incrementLessonsCompleted()
        }
    }
    
    fun updateStreak(streak: Int) {
        viewModelScope.launch {
            repository.updateStreak(streak)
        }
    }
    
    fun updateLastStudyDate() {
        viewModelScope.launch {
            repository.updateLastStudyDate(System.currentTimeMillis())
        }
    }
    
    suspend fun getProgressPercentage(skill: String, level: String): Double {
        return repository.getProgressPercentage(skill, level)
    }
    
    suspend fun shouldAdvanceLevel(skill: String, level: String): Boolean {
        return repository.shouldAdvanceLevel(skill, level)
    }
    
    fun forceReloadLessons() {
        viewModelScope.launch {
            repository.clearAllLessons()
            val lessons = com.hellogerman.app.data.LessonContentGenerator.generateAllLessons()
            repository.insertLessons(lessons)
        }
    }
}
