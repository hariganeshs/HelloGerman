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
import androidx.compose.ui.graphics.SolidColor
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
	val points: Int = 10,
	val questionEn: String? = null
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
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("EN", fontSize = 12.sp, modifier = Modifier.semantics { contentDescription = "English toggle" })
                        Switch(checked = showEnglish, onCheckedChange = { showEnglish = it })
                    }
                }
                
                if (!grammarContent.explanations.isNullOrEmpty()) {
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = GrammarColor.copy(alpha = 0.05f))
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    "Explanations:",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = GrammarColor
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                grammarContent.explanations.forEach { expl ->
                                    Text("• $expl", modifier = Modifier.padding(vertical = 2.dp))
                                }
                                if (showEnglish && !grammarContent.explanationsEn.isNullOrEmpty()) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text("English:", fontWeight = FontWeight.Medium, color = Color.Gray)
                                    grammarContent.explanationsEn.forEach { expl ->
                                        Text("• $expl", color = Color.Gray, modifier = Modifier.padding(vertical = 2.dp))
                                    }
                                }
                            }
                        }
                    }
                }
                
                if (!grammarContent.examples.isNullOrEmpty()) {
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    "Examples:",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                grammarContent.examples.forEach { ex ->
                                    Text("• $ex", modifier = Modifier.padding(vertical = 2.dp))
                                }
                            }
                        }
                    }
                }
                
                item {
                    Button(
                        onClick = { navController.navigate(Screen.GrammarQuiz.createRoute(lessonId)) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(stringResource(id = R.string.start_quiz))
                    }
                }
            }
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
		lesson?.content?.let { 
			try {
				val parsed = gson.fromJson(it, GrammarContentLite::class.java)
				android.util.Log.d("GrammarQuiz", "Parsed content: topicKey=${parsed.topicKey}, quiz size=${parsed.quiz?.size ?: 0}")
				if (parsed.quiz?.isNotEmpty() == true) {
					parsed.quiz.forEach { q ->
						android.util.Log.d("GrammarQuiz", "Question: ${q.question}, Options: ${q.options}")
					}
				}
				parsed
			} catch (e: Exception) {
				android.util.Log.e("GrammarQuiz", "Error parsing lesson content", e)
				null
			}
		}
	}
	var score by remember { mutableStateOf(0) }
	var finished by remember { mutableStateOf(false) }
	var idx by remember { mutableStateOf(0) }
	var selected by remember { mutableStateOf<String?>(null) }
	var showFeedback by remember { mutableStateOf(false) }
	var isCorrect by remember { mutableStateOf(false) }
	var correctAnswers by remember { mutableStateOf(0) }
	val questions = content?.quiz ?: emptyList()
	
	android.util.Log.d("GrammarQuiz", "Lesson ID: $lessonId, Questions found: ${questions.size}")

	Column(
		Modifier
			.fillMaxSize()
			.padding(16.dp),
		verticalArrangement = Arrangement.spacedBy(16.dp)
	) {
		Text(stringResource(id = R.string.quiz), fontSize = 22.sp, fontWeight = FontWeight.Bold)
		if (questions.isEmpty()) {
			Text("No questions found in this lesson.")
			Button(onClick = { navController.popBackStack() }) { Text(stringResource(id = R.string.save_and_back)) }
		} else if (!finished) {
			val q = questions[idx]
			
			// Progress indicator
			Text("Question ${idx + 1} of ${questions.size}", fontSize = 14.sp, color = Color.Gray)
			LinearProgressIndicator(
				progress = (idx + 1).toFloat() / questions.size.toFloat(),
				modifier = Modifier.fillMaxWidth()
			)
			Spacer(modifier = Modifier.height(8.dp))
			
			// English toggle
			var showEnglish by remember { mutableStateOf(false) }
			Row(verticalAlignment = Alignment.CenterVertically) {
				Text("EN", fontSize = 12.sp)
				Switch(checked = showEnglish, onCheckedChange = { showEnglish = it })
			}
			
			// Question text
			Text(q.question, fontSize = 18.sp, fontWeight = FontWeight.Medium)
			if (showEnglish && !q.questionEn.isNullOrBlank()) {
				Text(q.questionEn, fontSize = 14.sp, color = Color.Gray, style = MaterialTheme.typography.bodyMedium)
			}
			Spacer(modifier = Modifier.height(12.dp))
			
			// Answer options
			Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
				q.options.forEach { option ->
					val selectedThis = selected == option
					OutlinedButton(
						onClick = { selected = option },
						modifier = Modifier.fillMaxWidth(),
						colors = ButtonDefaults.outlinedButtonColors(
							containerColor = if (selectedThis) MaterialTheme.colorScheme.primary.copy(alpha = 0.12f) else Color.Transparent,
							contentColor = if (selectedThis) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
						),
						border = if (selectedThis) ButtonDefaults.outlinedButtonBorder.copy(
							width = 2.dp,
							brush = SolidColor(MaterialTheme.colorScheme.primary)
						) else ButtonDefaults.outlinedButtonBorder
					) { 
						Text(option, modifier = Modifier.fillMaxWidth()) 
					}
				}
			}
			// Feedback display
			if (showFeedback) {
				Card(
					colors = CardDefaults.cardColors(
						containerColor = if (isCorrect) Color.Green.copy(alpha = 0.1f) else Color.Red.copy(alpha = 0.1f)
					)
				) {
					Column(modifier = Modifier.padding(16.dp)) {
						Text(
							if (isCorrect) "✓ Correct!" else "✗ Incorrect",
							fontWeight = FontWeight.Bold,
							color = if (isCorrect) Color.Green else Color.Red
						)
						if (!isCorrect) {
							Text("Correct answer: ${q.correct}", color = Color.Gray)
						}
						if (isCorrect) {
							Text("+${q.points} points", color = Color.Green)
						}
					}
				}
			}
			
			Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
				if (!showFeedback) {
					Button(
						onClick = {
							if (selected != null) {
								isCorrect = selected.equals(q.correct, ignoreCase = true)
								if (isCorrect) {
									score += q.points
									correctAnswers++
								}
								showFeedback = true
							}
						},
						enabled = selected != null
					) { Text("Submit") }
				} else {
					Button(onClick = {
						showFeedback = false
						selected = null
						if (idx < questions.lastIndex) {
							idx++
						} else {
							finished = true
						}
					}) { 
						Text(if (idx < questions.lastIndex) "Next" else stringResource(id = R.string.finish))
					}
				}
				Spacer(Modifier.width(8.dp))
				Button(
					onClick = { finished = true },
					colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
				) { Text("Skip Quiz") }
			}
		} else {
			AnimatedVisibility(finished) {
				// Calculate percentage once outside the Card scope
				val percentage = if (questions.isNotEmpty()) (correctAnswers.toFloat() / questions.size * 100).toInt() else 0
				
				Column(horizontalAlignment = Alignment.Start) {
					Card(
						colors = CardDefaults.cardColors(containerColor = GrammarColor.copy(alpha = 0.1f))
					) {
						Column(modifier = Modifier.padding(16.dp)) {
							Text("Quiz Completed!", fontSize = 20.sp, fontWeight = FontWeight.Bold)
							Spacer(modifier = Modifier.height(8.dp))
							Text("Final Score: $score points", fontSize = 16.sp)
							Text("Correct Answers: $correctAnswers/${questions.size}", fontSize = 14.sp, color = Color.Gray)
							Text("Accuracy: $percentage%", fontSize = 14.sp, color = Color.Gray)
							
							Spacer(modifier = Modifier.height(8.dp))
							
							// Performance feedback
							val performanceText = when {
								percentage >= 90 -> "🌟 Excellent work!"
								percentage >= 75 -> "✅ Good job!"
								percentage >= 60 -> "👍 Not bad!"
								else -> "📚 Keep practicing!"
							}
							Text(performanceText, fontSize = 14.sp, fontWeight = FontWeight.Medium)
						}
					}
					
					Spacer(Modifier.height(16.dp))
					
					Button(onClick = {
						// Use actual lesson level instead of hardcoded "A1"
						val actualLevel = lesson?.level ?: "A1"
						val topicKey = content?.topicKey ?: grammarViewModel.buildTopicKey(actualLevel, "lesson_$lessonId")
						
						// Save score and progress
						grammarViewModel.addPoints(topicKey, score)
						
						// Award badges based on performance
						when {
							percentage >= 90 -> grammarViewModel.awardBadge(topicKey, "perfectionist")
							percentage >= 75 -> grammarViewModel.awardBadge(topicKey, "grammar_master")
							correctAnswers >= 3 -> grammarViewModel.awardBadge(topicKey, "quick_learner")
						}
						
						// Save lesson completion
						if (lesson != null) {
							// TODO: Update lesson progress in database
							android.util.Log.d("GrammarQuiz", "Lesson completed: ${lesson.id}, Score: $score, Accuracy: $percentage%")
						}
						
						navController.popBackStack()
					}) { Text(stringResource(id = R.string.save_and_back)) }
				}
			}
		}
	}
}


