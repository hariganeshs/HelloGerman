package com.hellogerman.app.ui.screens

import android.speech.tts.TextToSpeech
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.hellogerman.app.ui.viewmodel.LessonViewModel
import com.hellogerman.app.ui.theme.HoerenColor
import com.google.gson.Gson
import com.hellogerman.app.data.entities.*
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HoerenLessonDetailScreen(
    navController: NavController,
    lessonId: Int,
    lessonViewModel: LessonViewModel = viewModel()
) {
    val currentLesson by lessonViewModel.currentLesson.collectAsState()
    val isLoading by lessonViewModel.isLoading.collectAsState()
    
    var currentStep by remember { mutableStateOf(0) } // 0: content, 1: quiz, 2: results
    var userAnswers by remember { mutableStateOf(mutableMapOf<String, String>()) }
    var quizCompleted by remember { mutableStateOf(false) }
    var score by remember { mutableStateOf(0) }
    var showScript by remember { mutableStateOf(false) } // Toggle for showing/hiding script
    
    // TTS and Audio state
    var tts by remember { mutableStateOf<TextToSpeech?>(null) }
    var isPlaying by remember { mutableStateOf(false) }
    var playbackSpeed by remember { mutableStateOf(1.0f) }
    var timeRemaining by remember { mutableStateOf(0) }
    
    val context = LocalContext.current
    val gson = remember { Gson() }
    
    LaunchedEffect(lessonId) {
        lessonViewModel.loadLessonById(lessonId)
    }
    
    // Initialize TTS
    LaunchedEffect(Unit) {
        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.language = Locale.GERMAN
                tts?.setSpeechRate(playbackSpeed)
            }
        }
    }
    
    // Cleanup TTS
    DisposableEffect(Unit) {
        onDispose {
            tts?.stop()
            tts?.shutdown()
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = currentLesson?.title ?: "Listening Lesson",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = HoerenColor)
            }
        } else {
            currentLesson?.let { lesson ->
                val lessonContent = try {
                    gson.fromJson(lesson.content, HoerenContent::class.java)
                } catch (e: Exception) {
                    null
                }
                
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    when (currentStep) {
                        0 -> {
                            // Lesson Content
                            item {
                                Text(
                                    text = lesson.title,
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            
                            lessonContent?.let { content ->
                                item {
                                    Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = CardDefaults.cardColors(
                                            containerColor = MaterialTheme.colorScheme.surface
                                        )
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(16.dp)
                                        ) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    text = "Audio Script",
                                                    fontSize = 18.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = MaterialTheme.colorScheme.onSurface
                                                )
                                                IconButton(onClick = { showScript = !showScript }) {
                                                    Icon(
                                                        imageVector = if (showScript) Icons.Default.Close else Icons.Default.Info,
                                                        contentDescription = if (showScript) "Hide Script" else "Show Script",
                                                        tint = HoerenColor
                                                    )
                                                }
                                            }
                                            if (showScript) {
                                                Spacer(modifier = Modifier.height(8.dp))
                                                Text(
                                                    text = content.script,
                                                    fontSize = 16.sp,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                        }
                                    }
                                }
                                
                                item {
                                    Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = CardDefaults.cardColors(
                                            containerColor = MaterialTheme.colorScheme.surface
                                        )
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(16.dp)
                                        ) {
                                            Text(
                                                text = "Audio Player",
                                                fontSize = 18.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                            Spacer(modifier = Modifier.height(12.dp))
                                            
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceEvenly,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                IconButton(
                                                    onClick = {
                                                        if (isPlaying) {
                                                            tts?.stop()
                                                            isPlaying = false
                                                        } else {
                                                            tts?.speak(content.script, TextToSpeech.QUEUE_FLUSH, null, null)
                                                            isPlaying = true
                                                        }
                                                    }
                                                ) {
                                                                                                         Icon(
                                                         imageVector = if (isPlaying) Icons.Default.Close else Icons.Default.PlayArrow,
                                                         contentDescription = if (isPlaying) "Stop" else "Play",
                                                         tint = HoerenColor,
                                                         modifier = Modifier.size(32.dp)
                                                     )
                                                }
                                                
                                                Column {
                                                    Text(
                                                        text = "Speed",
                                                        fontSize = 12.sp,
                                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                                    )
                                                    Slider(
                                                        value = playbackSpeed,
                                                        onValueChange = { 
                                                            playbackSpeed = it
                                                            tts?.setSpeechRate(it)
                                                        },
                                                        valueRange = 0.5f..1.5f,
                                                        steps = 4,
                                                        colors = SliderDefaults.colors(
                                                            thumbColor = HoerenColor,
                                                            activeTrackColor = HoerenColor
                                                        )
                                                    )
                                                    Text(
                                                        text = "${playbackSpeed}x",
                                                        fontSize = 12.sp,
                                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                                
                                item {
                                    Button(
                                        onClick = { currentStep = 1 },
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = HoerenColor
                                        )
                                    ) {
                                        Text("Start Quiz")
                                    }
                                }
                            }
                        }
                        
                        1 -> {
                            // Quiz
                            lessonContent?.let { content ->
                                item {
                                    Text(
                                        text = "Quiz",
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                                
                                items(content.questions) { question ->
                                    Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = CardDefaults.cardColors(
                                            containerColor = MaterialTheme.colorScheme.surface
                                        )
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(16.dp)
                                        ) {
                                            Text(
                                                text = "Question ${question.id}",
                                                fontSize = 16.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                            Spacer(modifier = Modifier.height(8.dp))
                                            Text(
                                                text = question.question,
                                                fontSize = 14.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                            Spacer(modifier = Modifier.height(12.dp))
                                            
                                            when (question.type) {
                                                QuestionType.MULTIPLE_CHOICE -> {
                                                    question.options?.forEach { option ->
                                                        Card(
                                                            modifier = Modifier
                                                                .fillMaxWidth()
                                                                .padding(vertical = 2.dp)
                                                                .clickable {
                                                                    userAnswers = userAnswers.toMutableMap().apply {
                                                                        put(question.id.toString(), option)
                                                                    }
                                                                },
                                                            colors = CardDefaults.cardColors(
                                                                containerColor = if (userAnswers[question.id.toString()] == option)
                                                                    MaterialTheme.colorScheme.primaryContainer
                                                                else
                                                                    MaterialTheme.colorScheme.surface
                                                            ),
                                                            elevation = CardDefaults.cardElevation(
                                                                defaultElevation = if (userAnswers[question.id.toString()] == option) 8.dp else 2.dp
                                                            )
                                                        ) {
                                                            Row(
                                                                modifier = Modifier
                                                                    .fillMaxWidth()
                                                                    .padding(16.dp),
                                                                verticalAlignment = Alignment.CenterVertically
                                                            ) {
                                                                RadioButton(
                                                                    selected = userAnswers[question.id.toString()] == option,
                                                                    onClick = {
                                                                        userAnswers = userAnswers.toMutableMap().apply {
                                                                            put(question.id.toString(), option)
                                                                        }
                                                                    },
                                                                    colors = RadioButtonDefaults.colors(
                                                                        selectedColor = HoerenColor
                                                                    )
                                                                )
                                                                Spacer(modifier = Modifier.width(12.dp))
                                                                Text(
                                                                    text = option,
                                                                    fontSize = 16.sp,
                                                                    color = MaterialTheme.colorScheme.onSurface,
                                                                    fontWeight = if (userAnswers[question.id.toString()] == option)
                                                                        FontWeight.Bold else FontWeight.Normal,
                                                                    modifier = Modifier.weight(1f)
                                                                )
                                                                if (userAnswers[question.id.toString()] == option) {
                                                                    Icon(
                                                                        imageVector = Icons.Default.CheckCircle,
                                                                        contentDescription = "Selected",
                                                                        tint = HoerenColor,
                                                                        modifier = Modifier.size(20.dp)
                                                                    )
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                                QuestionType.MULTIPLE_CORRECT_ANSWERS -> {
                                                    val selectedAnswers = userAnswers[question.id.toString()]?.split(",")?.filter { it.isNotEmpty() } ?: emptyList()
                                                    question.options?.forEach { option ->
                                                        Card(
                                                            modifier = Modifier
                                                                .fillMaxWidth()
                                                                .padding(vertical = 2.dp)
                                                                .clickable {
                                                                    val currentSelection = userAnswers[question.id.toString()]?.split(",")?.filter { it.isNotEmpty() }?.toMutableList() ?: mutableListOf()
                                                                    if (currentSelection.contains(option)) {
                                                                        currentSelection.remove(option)
                                                                    } else {
                                                                        currentSelection.add(option)
                                                                    }
                                                                    userAnswers = userAnswers.toMutableMap().apply {
                                                                        put(question.id.toString(), currentSelection.joinToString(","))
                                                                    }
                                                                },
                                                            colors = CardDefaults.cardColors(
                                                                containerColor = if (selectedAnswers.contains(option))
                                                                    MaterialTheme.colorScheme.primaryContainer
                                                                else
                                                                    MaterialTheme.colorScheme.surface
                                                            ),
                                                            elevation = CardDefaults.cardElevation(
                                                                defaultElevation = if (selectedAnswers.contains(option)) 8.dp else 2.dp
                                                            )
                                                        ) {
                                                            Row(
                                                                modifier = Modifier
                                                                    .fillMaxWidth()
                                                                    .padding(16.dp),
                                                                verticalAlignment = Alignment.CenterVertically
                                                            ) {
                                                                Checkbox(
                                                                    checked = selectedAnswers.contains(option),
                                                                    onCheckedChange = { checked ->
                                                                        val currentSelection = userAnswers[question.id.toString()]?.split(",")?.filter { it.isNotEmpty() }?.toMutableList() ?: mutableListOf()
                                                                        if (checked) {
                                                                            currentSelection.add(option)
                                                                        } else {
                                                                            currentSelection.remove(option)
                                                                        }
                                                                        userAnswers = userAnswers.toMutableMap().apply {
                                                                            put(question.id.toString(), currentSelection.joinToString(","))
                                                                        }
                                                                    }
                                                                )
                                                                Spacer(modifier = Modifier.width(12.dp))
                                                                Text(
                                                                    text = option,
                                                                    fontSize = 16.sp,
                                                                    color = MaterialTheme.colorScheme.onSurface,
                                                                    fontWeight = if (selectedAnswers.contains(option))
                                                                        FontWeight.Bold else FontWeight.Normal,
                                                                    modifier = Modifier.weight(1f)
                                                                )
                                                                if (selectedAnswers.contains(option)) {
                                                                    Icon(
                                                                        imageVector = Icons.Default.CheckCircle,
                                                                        contentDescription = "Selected",
                                                                        tint = HoerenColor,
                                                                        modifier = Modifier.size(20.dp)
                                                                    )
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                                QuestionType.TRUE_FALSE -> {
                                                    val options = listOf("Richtig", "Falsch")
                                                    options.forEach { option ->
                                                        Card(
                                                            modifier = Modifier
                                                                .fillMaxWidth()
                                                                .padding(vertical = 2.dp)
                                                                .clickable {
                                                                    userAnswers = userAnswers.toMutableMap().apply {
                                                                        put(question.id.toString(), option)
                                                                    }
                                                                },
                                                            colors = CardDefaults.cardColors(
                                                                containerColor = if (userAnswers[question.id.toString()] == option)
                                                                    MaterialTheme.colorScheme.primaryContainer
                                                                else
                                                                    MaterialTheme.colorScheme.surface
                                                            ),
                                                            elevation = CardDefaults.cardElevation(
                                                                defaultElevation = if (userAnswers[question.id.toString()] == option) 8.dp else 2.dp
                                                            )
                                                        ) {
                                                            Row(
                                                                modifier = Modifier
                                                                    .fillMaxWidth()
                                                                    .padding(16.dp),
                                                                verticalAlignment = Alignment.CenterVertically
                                                            ) {
                                                                RadioButton(
                                                                    selected = userAnswers[question.id.toString()] == option,
                                                                    onClick = {
                                                                        userAnswers = userAnswers.toMutableMap().apply {
                                                                            put(question.id.toString(), option)
                                                                        }
                                                                    },
                                                                    colors = RadioButtonDefaults.colors(
                                                                        selectedColor = HoerenColor
                                                                    )
                                                                )
                                                                Spacer(modifier = Modifier.width(12.dp))
                                                                Text(
                                                                    text = option,
                                                                    fontSize = 16.sp,
                                                                    color = MaterialTheme.colorScheme.onSurface,
                                                                    fontWeight = if (userAnswers[question.id.toString()] == option)
                                                                        FontWeight.Bold else FontWeight.Normal,
                                                                    modifier = Modifier.weight(1f)
                                                                )
                                                                if (userAnswers[question.id.toString()] == option) {
                                                                    Icon(
                                                                        imageVector = Icons.Default.CheckCircle,
                                                                        contentDescription = "Selected",
                                                                        tint = HoerenColor,
                                                                        modifier = Modifier.size(20.dp)
                                                                    )
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                                QuestionType.FILL_BLANK,                                                     QuestionType.GAP_FILL -> {
                                                        question.textForGaps?.let { textWithGaps ->
                                                            Column {
                                                                Text(
                                                                    text = "Füllen Sie die Lücken:",
                                                                    fontSize = 16.sp,
                                                                    fontWeight = FontWeight.Bold,
                                                                    color = MaterialTheme.colorScheme.onSurface
                                                                )
                                                                Spacer(modifier = Modifier.height(8.dp))

                                                                // Split text by gaps and create input fields
                                                                val parts = textWithGaps.split("___")
                                                                parts.forEachIndexed { index, part ->
                                                                    if (index > 0) {
                                                                        val gapIndex = index - 1
                                                                        OutlinedTextField(
                                                                            value = userAnswers["${question.id}_gap_$gapIndex"] ?: "",
                                                                            onValueChange = { userAnswers = userAnswers.toMutableMap().apply {
                                                                                put("${question.id}_gap_$gapIndex", it)
                                                                            }},
                                                                            label = { Text("Lücke ${gapIndex + 1}") },
                                                                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                                                                        )
                                                                    }
                                                                    if (part.isNotEmpty()) {
                                                                        Text(
                                                                            text = part,
                                                                            fontSize = 16.sp,
                                                                            color = MaterialTheme.colorScheme.onSurface
                                                                        )
                                                                    }
                                                                }
                                                            }
                                                        } ?: Text("Gap fill text not available", color = MaterialTheme.colorScheme.error)
                                                    }
                                                    QuestionType.TEXT_MATCHING -> {
                                                        question.matchingItems?.let { matchingItems ->
                                                            Column {
                                                                Text(
                                                                    text = "Verbinden Sie die Begriffe:",
                                                                    fontSize = 16.sp,
                                                                    fontWeight = FontWeight.Bold,
                                                                    color = MaterialTheme.colorScheme.onSurface
                                                                )
                                                                Spacer(modifier = Modifier.height(12.dp))

                                                                matchingItems.forEach { (left, right) ->
                                                                    Row(
                                                                        modifier = Modifier.fillMaxWidth(),
                                                                        horizontalArrangement = Arrangement.SpaceBetween,
                                                                        verticalAlignment = Alignment.CenterVertically
                                                                    ) {
                                                                        Text(
                                                                            text = left,
                                                                            fontSize = 16.sp,
                                                                            color = MaterialTheme.colorScheme.onSurface,
                                                                            modifier = Modifier.weight(1f)
                                                                        )
                                                                        Icon(
                                                                            imageVector = Icons.Default.ArrowForward,
                                                                            contentDescription = "to",
                                                                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                                                                        )
                                                                        OutlinedTextField(
                                                                            value = userAnswers["${question.id}_match_$left"] ?: "",
                                                                            onValueChange = { userAnswers = userAnswers.toMutableMap().apply {
                                                                                put("${question.id}_match_$left", it)
                                                                            }},
                                                                            label = { Text("Antwort") },
                                                                            modifier = Modifier.weight(1f)
                                                                        )
                                                                    }
                                                                    Spacer(modifier = Modifier.height(8.dp))
                                                                }
                                                            }
                                                        } ?: Text("Matching items not available", color = MaterialTheme.colorScheme.error)
                                                    }
                                                else -> {
                                                    Text(
                                                        text = "Question type not supported",
                                                        color = MaterialTheme.colorScheme.error
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                                
                                item {
                                    Button(
                                        onClick = {
                                            // Calculate score with support for new question types
                                            fun isAnswerCorrect(question: Question, userAnswer: String?): Boolean {
                                                return when (question.type) {
                                                    QuestionType.MULTIPLE_CHOICE, QuestionType.TRUE_FALSE -> {
                                                        userAnswer == question.correctAnswer
                                                    }
                                                    QuestionType.FILL_BLANK -> {
                                                        // Case-insensitive and trimmed comparison for text answers
                                                        userAnswer?.trim()?.lowercase() == question.correctAnswer.trim().lowercase()
                                                    }
                                                    QuestionType.MULTIPLE_CORRECT_ANSWERS -> {
                                                        val userSelections = userAnswer?.split(",")?.filter { it.isNotEmpty() }?.toSet() ?: emptySet()
                                                        val correctSelections = question.correctAnswers?.toSet() ?: emptySet()
                                                        userSelections == correctSelections
                                                    }
                                                    QuestionType.GAP_FILL -> {
                                                        question.gaps?.let { gaps ->
                                                            gaps.indices.all { gapIndex ->
                                                                val gapAnswer = userAnswers["${question.id}_gap_$gapIndex"]
                                                                gapAnswer?.trim()?.lowercase() == gaps[gapIndex].trim().lowercase()
                                                            }
                                                        } ?: false
                                                    }
                                                    QuestionType.TEXT_MATCHING -> {
                                                        question.matchingItems?.let { matchingItems ->
                                                            matchingItems.all { (left, right) ->
                                                                val userMatch = userAnswers["${question.id}_match_$left"]
                                                                userMatch?.trim()?.lowercase() == right.trim().lowercase()
                                                            }
                                                        } ?: false
                                                    }
                                                    else -> {
                                                        // Default case-insensitive comparison for open text questions
                                                        userAnswer?.trim()?.lowercase() == question.correctAnswer.trim().lowercase()
                                                    }
                                                }
                                            }

                                            var correctAnswers = 0
                                            content.questions.forEach { question ->
                                                if (isAnswerCorrect(question, userAnswers[question.id.toString()])) {
                                                    correctAnswers++
                                                }
                                            }
                                            score = (correctAnswers * 100) / content.questions.size
                                            quizCompleted = true
                                            currentStep = 2

                                            // Update lesson completion
                                            lessonViewModel.completeLesson(lessonId, score)
                                        },
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = HoerenColor
                                        )
                                    ) {
                                        Text("Submit Quiz")
                                    }
                                }
                            }
                        }
                        
                        2 -> {
                            // Results
                            item {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.surface
                                    )
                                ) {
                                    Column(
                                        modifier = Modifier.padding(16.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = "Completed",
                                            tint = HoerenColor,
                                            modifier = Modifier.size(64.dp)
                                        )
                                        Spacer(modifier = Modifier.height(16.dp))
                                        Text(
                                            text = "Quiz Completed!",
                                            fontSize = 24.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            text = "Your Score: $score%",
                                            fontSize = 20.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = HoerenColor
                                        )
                                        Spacer(modifier = Modifier.height(16.dp))
                                        Button(
                                            onClick = { navController.navigateUp() },
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = HoerenColor
                                            )
                                        ) {
                                            Text("Back to Lessons")
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
