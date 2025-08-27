package com.hellogerman.app.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.hellogerman.app.data.entities.Lesson
import com.hellogerman.app.data.repository.HelloGermanRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LessonViewModel(application: Application) : AndroidViewModel(application) {
    
    private val repository = HelloGermanRepository(application)
    
    private val _lessons = MutableStateFlow<List<Lesson>>(emptyList())
    val lessons: StateFlow<List<Lesson>> = _lessons.asStateFlow()
    
    private val _currentLesson = MutableStateFlow<Lesson?>(null)
    val currentLesson: StateFlow<Lesson?> = _currentLesson.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _currentSkill = MutableStateFlow("")
    val currentSkill: StateFlow<String> = _currentSkill.asStateFlow()
    
    private val _currentLevel = MutableStateFlow("A1")
    val currentLevel: StateFlow<String> = _currentLevel.asStateFlow()
    
    fun loadLessons(skill: String, level: String) {
        _isLoading.value = true
        _currentSkill.value = skill
        _currentLevel.value = level
        
        viewModelScope.launch {
            repository.getLessonsBySkillAndLevel(skill, level).collect { lessonList ->
                _lessons.value = lessonList
                _isLoading.value = false
            }
        }
    }
    
    fun loadLessonById(lessonId: Int) {
        viewModelScope.launch {
            val lesson = repository.getLessonById(lessonId)
            _currentLesson.value = lesson
        }
    }
    
    fun updateLessonProgress(lessonId: Int, completed: Boolean, score: Int, timeSpent: Int) {
        viewModelScope.launch {
            repository.updateLessonProgress(lessonId, completed, score, timeSpent)
            // Reload current lesson to get updated data
            loadLessonById(lessonId)
        }
    }
    
    fun completeLesson(lessonId: Int, score: Int) {
        viewModelScope.launch {
            repository.updateLessonProgress(lessonId, true, score, 0)
            // Reload current lesson to get updated data
            loadLessonById(lessonId)
        }
    }
    
    suspend fun getCompletedLessonsCount(skill: String, level: String): Int {
        return repository.getCompletedLessonsCount(skill, level)
    }
    
    suspend fun getTotalLessonsCount(skill: String, level: String): Int {
        return repository.getTotalLessonsCount(skill, level)
    }
    
    suspend fun getAverageScore(skill: String, level: String): Double {
        return repository.getAverageScore(skill, level) ?: 0.0
    }
    
    suspend fun shouldAdvanceLevel(skill: String, level: String): Boolean {
        return repository.shouldAdvanceLevel(skill, level)
    }
    
    suspend fun getNextLevel(currentLevel: String): String? {
        return repository.getNextLevel(currentLevel)
    }
}
