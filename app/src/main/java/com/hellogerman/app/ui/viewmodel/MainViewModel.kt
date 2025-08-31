package com.hellogerman.app.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.hellogerman.app.data.entities.UserProgress
import com.hellogerman.app.data.repository.HelloGermanRepository
import com.hellogerman.app.data.repository.LevelUnlockStatus
import com.hellogerman.app.data.repository.LevelCompletionInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {
    
    private val repository = HelloGermanRepository(application)
    
    private val _userProgress = MutableStateFlow<UserProgress?>(null)
    val userProgress: StateFlow<UserProgress?> = _userProgress.asStateFlow()
    
    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _currentLevel = MutableStateFlow("A1")
    val currentLevel: StateFlow<String> = _currentLevel.asStateFlow()

    private val _grammarTotalPoints = MutableStateFlow(0)
    val grammarTotalPoints: StateFlow<Int> = _grammarTotalPoints.asStateFlow()
    private val _grammarBadgesCount = MutableStateFlow(0)
    val grammarBadgesCount: StateFlow<Int> = _grammarBadgesCount.asStateFlow()
    val weakGrammarTopics = repository.getWeakGrammarTopics()

    // Adaptive progress system
    private val _levelUnlockStatus = MutableStateFlow<LevelUnlockStatus?>(null)
    val levelUnlockStatus: StateFlow<LevelUnlockStatus?> = _levelUnlockStatus.asStateFlow()

    private val _levelCompletionInfo = MutableStateFlow<Map<String, LevelCompletionInfo>>(emptyMap())
    val levelCompletionInfo: StateFlow<Map<String, LevelCompletionInfo>> = _levelCompletionInfo.asStateFlow()

    init {
        loadUserProgress()
        observeGrammarStats()
        loadAdaptiveProgress()
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
    
    private fun observeGrammarStats() {
        viewModelScope.launch {
            repository.getTotalGrammarPoints().collectLatest { points ->
                _grammarTotalPoints.value = points
            }
        }
        viewModelScope.launch {
            repository.getAllGrammarProgress().collectLatest { list ->
                var count = 0
                list.forEach { gp ->
                    val badgesJson = gp.badgesJson
                    if (badgesJson.isNotBlank() && badgesJson != "[]") count += 1
                }
                _grammarBadgesCount.value = count
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

    // Adaptive progress system methods
    private fun loadAdaptiveProgress() {
        viewModelScope.launch {
            userProgress.collectLatest { progress ->
                progress?.let { userProgress ->
                    // Load level unlock status
                    val unlockStatus = repository.checkLevelUnlock(userProgress.currentLevel)
                    _levelUnlockStatus.value = unlockStatus

                    // Load completion info for all skills
                    val completionInfo = repository.getLevelCompletionStatus(userProgress.currentLevel)
                    _levelCompletionInfo.value = completionInfo
                }
            }
        }
    }

    fun refreshAdaptiveProgress() {
        viewModelScope.launch {
            userProgress.value?.let { userProgress ->
                val unlockStatus = repository.checkLevelUnlock(userProgress.currentLevel)
                _levelUnlockStatus.value = unlockStatus

                val completionInfo = repository.getLevelCompletionStatus(userProgress.currentLevel)
                _levelCompletionInfo.value = completionInfo
            }
        }
    }

    suspend fun checkAndAdvanceLevel(): Boolean {
        return repository.autoAdvanceLevelIfReady().also { advanced ->
            if (advanced) {
                refreshAdaptiveProgress()
            }
        }
    }

    suspend fun getLevelProgressText(): String {
        val unlockStatus = _levelUnlockStatus.value ?: return "Loading progress..."
        val progressPercent = unlockStatus.overallProgress

        return if (unlockStatus.canUnlock) {
            "🎉 Congratulations! You've completed ${progressPercent.toInt()}% of ${unlockStatus.currentLevel}. Ready to unlock ${unlockStatus.nextLevel}!"
        } else {
            "${progressPercent.toInt()}% of ${unlockStatus.currentLevel} completed. ${80 - progressPercent.toInt()}% more to unlock ${unlockStatus.nextLevel ?: "next level"}."
        }
    }

    suspend fun getSkillProgressDetails(skill: String): LevelCompletionInfo? {
        return _levelCompletionInfo.value[skill]
    }


}
