package com.hellogerman.app.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.hellogerman.app.data.entities.GrammarProgress
import com.hellogerman.app.data.entities.Lesson
import com.hellogerman.app.data.repository.HelloGermanRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class GrammarViewModel(application: Application) : AndroidViewModel(application) {

	private val repository = HelloGermanRepository(application)

	private val _level = MutableStateFlow("A1")
	val level: StateFlow<String> = _level.asStateFlow()

	val totalPoints = repository.getTotalGrammarPoints()

	fun lessonsByLevel(level: String) = repository.getLessonsBySkillAndLevel("grammar", level)

	suspend fun getLessonById(lessonId: Int): Lesson? = repository.getLessonById(lessonId)

	fun buildTopicKey(level: String, title: String): String =
		("${'$'}level_" + title.lowercase().replace(" ", "_")).take(64)

	fun setLevel(newLevel: String) {
		_level.value = newLevel
	}

	fun addPoints(topicKey: String, points: Int) {
		viewModelScope.launch {
			repository.addGrammarPoints(topicKey, points)
		}
	}

	fun markLessonCompleted(topicKey: String) {
		viewModelScope.launch {
			repository.incrementGrammarCompleted(topicKey)
		}
	}

	fun updateStreak(topicKey: String, streak: Int) {
		viewModelScope.launch {
			repository.updateGrammarStreak(topicKey, streak)
		}
	}

	fun awardBadge(topicKey: String, badgeId: String) {
		viewModelScope.launch {
			repository.awardBadge(topicKey, badgeId)
		}
	}
}


