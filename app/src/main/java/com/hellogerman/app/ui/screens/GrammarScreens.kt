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
import com.hellogerman.app.ui.navigation.Screen
import com.hellogerman.app.ui.theme.GrammarColor
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hellogerman.app.ui.viewmodel.GrammarViewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext
import com.hellogerman.app.data.DatabaseInitializer
import androidx.compose.ui.res.stringResource
import com.hellogerman.app.R
import com.google.gson.Gson

// Lightweight DTO for grammar content to avoid abstract/sealed fields during JSON parsing
private data class GrammarContentLite(
	val topicKey: String? = null,
	val explanations: List<String>? = null,
	val explanationsEn: List<String>? = null,
	val examples: List<String>? = null,
	val quiz: List<GrammarQuestionLite>? = null
)

private data class GrammarQuestionLite(
	val question: String = "",
	val options: List<String> = emptyList(),
	val correct: String = "",
	val points: Int = 10
)

@Composable
fun GrammarDashboard(navController: NavController, grammarViewModel: GrammarViewModel = viewModel()) {
	val levels = listOf("A1","A2","B1","B2","C1","C2")
	Column(
		modifier = Modifier
			.fillMaxSize()
			.padding(16.dp),
		verticalArrangement = Arrangement.spacedBy(16.dp)
	) {
		Text(text = stringResource(id = R.string.grammar), fontSize = 28.sp, fontWeight = FontWeight.Bold)
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
				Text(stringResource(id = R.string.daily_challenge), fontWeight = FontWeight.Bold)
				Spacer(Modifier.height(8.dp))
				Button(onClick = {
					// Navigate to a suggested quiz (placeholder lesson id 0)
					navController.navigate(Screen.GrammarQuiz.createRoute(0))
				}) { Text(stringResource(id = R.string.start)) }
			}
		}
	}
}

@Composable
fun GrammarTopicListScreen(navController: NavController, level: String, grammarViewModel: GrammarViewModel = viewModel()) {
	val grammarLessons by grammarViewModel.lessonsByLevel(level).collectAsState(initial = emptyList())
	val context = LocalContext.current
	LazyColumn(
		modifier = Modifier
			.fillMaxSize()
			.padding(16.dp),
		verticalArrangement = Arrangement.spacedBy(12.dp)
	) {
		item {
			Text(stringResource(id = R.string.level_topics, level), fontSize = 22.sp, fontWeight = FontWeight.Bold)
		}
		if (grammarLessons.isEmpty()) {
			item {
				Text(stringResource(id = R.string.no_grammar_lessons, level))
				Spacer(Modifier.height(8.dp))
				Button(onClick = { DatabaseInitializer.forceReloadLessons(context) }) {
					Text(stringResource(id = R.string.load_grammar_lessons))
				}
			}
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
fun GrammarLessonScreen(navController: NavController, lessonId: Int, grammarViewModel: GrammarViewModel = viewModel()) {
    val gson = remember { Gson() }
    val lessonState = produceState<com.hellogerman.app.data.entities.Lesson?>(initialValue = null, lessonId) {
        value = grammarViewModel.getLessonById(lessonId)
    }
    val lesson = lessonState.value
    val grammarContent = remember(lesson?.content) {
        lesson?.content?.let {
            gson.fromJson(it, GrammarContentLite::class.java)
        }
    }
    var showEnglish by remember { mutableStateOf(true) }

    Column(
        Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(lesson?.title ?: stringResource(id = R.string.grammar), fontSize = 22.sp, fontWeight = FontWeight.Bold)
        if (grammarContent == null) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        } else {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("EN", modifier = Modifier.semantics { contentDescription = "English toggle" })
                Switch(checked = showEnglish, onCheckedChange = { showEnglish = it })
            }
            Text("Explanations:")
            (grammarContent.explanations ?: emptyList()).forEach { expl ->
                Text("- " + expl)
            }
            if (showEnglish && (grammarContent.explanationsEn?.isNotEmpty() == true)) {
                grammarContent.explanationsEn?.forEach { expl ->
                    Text("- " + expl, color = Color.Gray)
                }
            }
            Text("Examples:")
            (grammarContent.examples ?: emptyList()).forEach { ex ->
                Text("- " + ex)
            }
        }
        Button(onClick = { navController.navigate(Screen.GrammarQuiz.createRoute(lessonId)) }) {
            Text(stringResource(id = R.string.start))
        }
    }
}

@Composable
fun GrammarQuizScreen(navController: NavController, lessonId: Int, grammarViewModel: GrammarViewModel = viewModel()) {
	val gson = remember { Gson() }
	val lessonState = produceState<com.hellogerman.app.data.entities.Lesson?>(initialValue = null, lessonId) {
		value = grammarViewModel.getLessonById(lessonId)
	}
	val lesson = lessonState.value
	val content = remember(lesson?.content) {
		lesson?.content?.let { gson.fromJson(it, GrammarContentLite::class.java) }
	}
	var score by remember { mutableStateOf(0) }
	var finished by remember { mutableStateOf(false) }
	var idx by remember { mutableStateOf(0) }
	var selected by remember { mutableStateOf<String?>(null) }
	val questions = content?.quiz ?: emptyList()

	Column(
		Modifier
			.fillMaxSize()
			.padding(16.dp),
		verticalArrangement = Arrangement.spacedBy(16.dp)
	) {
		Text(stringResource(id = R.string.quiz), fontSize = 22.sp, fontWeight = FontWeight.Bold)
		if (questions.isEmpty()) {
			Text("No questions found.")
			Button(onClick = { navController.popBackStack() }) { Text(stringResource(id = R.string.save_and_back)) }
		} else if (!finished) {
			val q = questions[idx]
			Text(q.question)
			Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
				q.options.forEach { option ->
					val selectedThis = selected == option
					OutlinedButton(
						onClick = { selected = option },
						colors = ButtonDefaults.outlinedButtonColors(
							containerColor = if (selectedThis) MaterialTheme.colorScheme.primary.copy(alpha = 0.08f) else Color.Transparent
						)
					) { Text(option) }
				}
			}
			Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
				Button(onClick = {
					if (selected != null) {
						if (selected.equals(q.correct, ignoreCase = true)) score += q.points
						selected = null
						if (idx < questions.lastIndex) idx++ else finished = true
					}
				}) { Text("Submit") }
				Spacer(Modifier.width(8.dp))
				Button(onClick = { finished = true }) { Text(stringResource(id = R.string.finish)) }
			}
		} else {
			AnimatedVisibility(finished) {
				Column(horizontalAlignment = Alignment.Start) {
					Text(stringResource(id = R.string.total, score))
					Spacer(Modifier.height(8.dp))
					Button(onClick = {
						val topicKey = grammarViewModel.buildTopicKey("A1", "lesson_$lessonId")
						grammarViewModel.addPoints(topicKey, score)
						if (score >= 15) grammarViewModel.awardBadge(topicKey, "fast_starter")
						navController.popBackStack()
					}) { Text(stringResource(id = R.string.save_and_back)) }
				}
			}
		}
	}
}


