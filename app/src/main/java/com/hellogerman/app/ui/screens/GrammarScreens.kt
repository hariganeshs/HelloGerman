package com.hellogerman.app.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.hellogerman.app.data.LessonContentGenerator
import com.hellogerman.app.data.entities.Lesson
import com.hellogerman.app.ui.navigation.Screen
import com.hellogerman.app.ui.theme.GrammarColor
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hellogerman.app.ui.viewmodel.GrammarViewModel

@Composable
fun GrammarDashboard(navController: NavController, grammarViewModel: GrammarViewModel = viewModel()) {
	val levels = listOf("A1","A2","B1","B2","C1","C2")
	Column(
		modifier = Modifier
			.fillMaxSize()
			.padding(16.dp),
		verticalArrangement = Arrangement.spacedBy(16.dp)
	) {
		Text(text = "Grammar", fontSize = 28.sp, fontWeight = FontWeight.Bold)
		Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
			levels.forEach { level ->
				AssistChip(
					label = { Text(level) },
					onClick = { navController.navigate(Screen.GrammarTopicList.createRoute(level)) }
				)
			}
		}
		Card(
			colors = CardDefaults.cardColors(containerColor = GrammarColor.copy(alpha = 0.08f)),
			elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
		) {
			Column(Modifier.padding(16.dp)) {
				Text("Daily Challenge", fontWeight = FontWeight.Bold)
				Spacer(Modifier.height(8.dp))
				Button(onClick = {
					// Navigate to a suggested quiz (placeholder lesson id 0)
					navController.navigate(Screen.GrammarQuiz.createRoute(0))
				}) { Text("Start") }
			}
		}
	}
}

@Composable
fun GrammarTopicListScreen(navController: NavController, level: String) {
	val lessons = remember(level) { LessonContentGenerator.run { 
		// Generate grammar lessons for level only
		val field = LessonContentGenerator::class.java.getDeclaredMethod("generateAllLessons")
		// We will filter by skill and level after generation (reuse existing)
	} }
	// Simpler: use generator for grammar-only by level
	val grammarLessons = remember(level) {
		LessonContentGenerator.run {
			// generateAllLessons already includes grammar; filter now
			generateAllLessons().filter { it.skill == "grammar" && it.level == level }
		}
	}
	LazyColumn(
		modifier = Modifier
			.fillMaxSize()
			.padding(16.dp),
		verticalArrangement = Arrangement.spacedBy(12.dp)
	) {
		item {
			Text("Level $level Topics", fontSize = 22.sp, fontWeight = FontWeight.Bold)
		}
		items(grammarLessons) { lesson ->
			Card(
				modifier = Modifier
					.fillMaxWidth()
					.clickable { navController.navigate(Screen.GrammarLesson.createRoute(lesson.id)) },
				elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
			) {
				Column(Modifier.padding(16.dp)) {
					Text(lesson.title, fontWeight = FontWeight.Bold)
					Spacer(Modifier.height(4.dp))
					Text(lesson.description, color = Color.Gray)
				}
			}
		}
	}
}

@Composable
fun GrammarLessonScreen(navController: NavController, lessonId: Int) {
	// For simplicity, show placeholder and navigate to quiz
	Column(
		Modifier
			.fillMaxSize()
			.padding(16.dp),
		verticalArrangement = Arrangement.spacedBy(12.dp)
	) {
		Text("Lesson", fontSize = 22.sp, fontWeight = FontWeight.Bold)
		Text("Interactive explanations and examples")
		Button(onClick = { navController.navigate(Screen.GrammarQuiz.createRoute(lessonId)) }) {
			Text("Start Quiz")
		}
	}
}

@Composable
fun GrammarQuizScreen(navController: NavController, lessonId: Int, grammarViewModel: GrammarViewModel = viewModel()) {
	// Placeholder quiz UI with scoring
	var score by remember { mutableStateOf(0) }
	var finished by remember { mutableStateOf(false) }
	Column(
		Modifier
			.fillMaxSize()
			.padding(16.dp),
		verticalArrangement = Arrangement.spacedBy(16.dp),
		horizontalAlignment = Alignment.CenterHorizontally
	) {
		Text("Quiz", fontSize = 22.sp, fontWeight = FontWeight.Bold)
		Text("Answer questions to earn points")
		Button(onClick = { score += 10 }) { Text("Answer Correct (+10)") }
		Button(onClick = { finished = true }) { Text("Finish") }
		AnimatedVisibility(finished) {
			Column(horizontalAlignment = Alignment.CenterHorizontally) {
				Text("Total: $score")
				Spacer(Modifier.height(8.dp))
				Button(onClick = {
					grammarViewModel.addPoints("lesson_$lessonId", score)
					navController.popBackStack()
				}) { Text("Save & Back") }
			}
		}
	}
}


