package com.hellogerman.app.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.hellogerman.app.ui.navigation.Screen
import com.hellogerman.app.ui.viewmodel.LessonViewModel

import com.hellogerman.app.ui.components.*
import com.hellogerman.app.ui.animations.enhancedPressAnimation
import com.hellogerman.app.ui.animations.entranceAnimation
import com.hellogerman.app.ui.animations.pulseAnimation
import com.google.gson.Gson
import com.hellogerman.app.data.entities.*
import android.util.Log
import androidx.compose.ui.platform.LocalContext
import com.hellogerman.app.ads.AdMobManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LessonDetailScreen(
    navController: NavController,
    lessonId: Int,
    lessonViewModel: LessonViewModel = viewModel(),

) {
    val currentLesson by lessonViewModel.currentLesson.collectAsState()
    val isLoading by lessonViewModel.isLoading.collectAsState()

    var currentStep by remember { mutableStateOf(0) } // 0: content, 1: quiz, 2: results
    var userAnswers by remember { mutableStateOf(mutableMapOf<String, String>()) }
    var quizCompleted by remember { mutableStateOf(false) }
    var timeSpentInSeconds by remember { mutableStateOf(0) }

    // Dictionary lookup state
    var showDictionaryDialog by remember { mutableStateOf(false) }
    var wordToLookup by remember { mutableStateOf("") }
    


    val gson = remember { Gson() }
    val context = LocalContext.current
    
    // Timer for tracking lesson time
    LaunchedEffect(lessonId) {
        val startTime = System.currentTimeMillis()
        
        while (true) {
            kotlinx.coroutines.delay(1000) // Update every second
            timeSpentInSeconds = ((System.currentTimeMillis() - startTime) / 1000).toInt()
        }
    }

    // Helper function to check if answer is correct for different question types
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
                // Check if all gaps are correctly filled
                question.gaps?.let { gaps ->
                    gaps.indices.all { gapIndex ->
                        val gapAnswer = userAnswers["${question.id}_gap_$gapIndex"]
                        gapAnswer?.trim()?.lowercase() == gaps[gapIndex].trim().lowercase()
                    }
                } ?: false
            }
            QuestionType.TEXT_MATCHING -> {
                // Check if all matching pairs are correct
                question.matchingItems?.let { matchingItems ->
                    matchingItems.all { (left, right) ->
                        val userMatch = userAnswers["${question.id}_match_$left"]
                        userMatch?.trim()?.lowercase() == right.trim().lowercase()
                    }
                } ?: false
            }
            QuestionType.OPEN_ENDED -> {
                // Case-insensitive and trimmed comparison for open-ended text answers
                userAnswer?.trim()?.lowercase() == question.correctAnswer.trim().lowercase()
            }
            else -> {
                // Default case-insensitive comparison for other text questions
                userAnswer?.trim()?.lowercase() == question.correctAnswer.trim().lowercase()
            }
        }
    }

    LaunchedEffect(lessonId) {
        lessonViewModel.loadLessonById(lessonId)
        // Load interstitial ad when lesson is loaded
        AdMobManager.loadInterstitialAd(context)
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = currentLesson?.title ?: "Lesson",
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
                actions = {
                    IconButton(
                        onClick = {
                            navController.popBackStack(Screen.Dashboard.route, inclusive = false)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Home,
                            contentDescription = "Go to Dashboard",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        floatingActionButton = {
            if (!isLoading) {
                currentLesson?.let { lesson ->
                    if (currentStep == 0 && (lesson.skill == "lesen" || lesson.skill == "hoeren")) {
                        ExtendedFloatingActionButton(
                            onClick = { currentStep = 1 },
                            icon = { Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null) },
                            text = { Text("Start Quiz") },
                            modifier = Modifier
                                .navigationBarsPadding()
                                .padding(bottom = 72.dp)
                        )
                    }
                }
            }
        }
    ) { padding ->
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            currentLesson?.let { lesson ->
                val lessonContent = try {
                    when (lesson.skill) {
                        "lesen" -> gson.fromJson(lesson.content, LesenContent::class.java)
                        "hoeren" -> gson.fromJson(lesson.content, HoerenContent::class.java)
                        else -> null
                    }
                } catch (e: Exception) {
                    null
                }
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(bottom = 120.dp)
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
                            
                            item {
                                Text(
                                    text = lesson.description,
                                    fontSize = 16.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            
                            // Display content based on lesson type
                            when (lesson.skill) {
                                "lesen" -> {
                                    val lesenContent = lessonContent as? LesenContent
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
                                                        text = "Reading Text",
                                                        fontSize = 18.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = MaterialTheme.colorScheme.onSurface
                                                    )
                                                    Text(
                                                        text = "Long-press words to look them up",
                                                        fontSize = 12.sp,
                                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                                    )
                                                }
                                                Spacer(modifier = Modifier.height(12.dp))
                                                DictionaryEnabledText(
                                                    text = lesenContent?.text ?: "",
                                                    fontSize = 16.sp,
                                                    onDictionaryLookup = { showDictionaryDialog = true }
                                                )
                                            }
                                        }
                                    }
                                }
                                "hoeren" -> {
                                    val hoerenContent = lessonContent as? HoerenContent
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
                                                        text = "Listening Script",
                                                        fontSize = 18.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = MaterialTheme.colorScheme.onSurface
                                                    )
                                                    Text(
                                                        text = "Long-press words to look them up",
                                                        fontSize = 12.sp,
                                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                                    )
                                                }
                                                Spacer(modifier = Modifier.height(12.dp))
                                                DictionaryEnabledText(
                                                    text = hoerenContent?.script ?: "",
                                                    fontSize = 16.sp,
                                                    onDictionaryLookup = { showDictionaryDialog = true }
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                            
                            // Vocabulary section (only for lesen content)
                            if (lesson.skill == "lesen") {
                                val lesenContent = lessonContent as? LesenContent
                                lesenContent?.vocabulary?.let { vocabulary ->
                                    item {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "Vocabulary",
                                                fontSize = 20.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                            Text(
                                                text = "Long-press words to look them up",
                                                fontSize = 12.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                    
                                    items(vocabulary) { vocabItem ->
                                        DictionaryEnabledVocabularyItem(
                                            word = vocabItem.word,
                                            translation = vocabItem.translation,
                                            example = vocabItem.example,
                                            onDictionaryLookup = { word ->
                                                wordToLookup = word
                                                showDictionaryDialog = true
                                            }
                                        )
                                    }
                                }
                            }
                            
                            item {
                                Button(
                                    onClick = { currentStep = 1 },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .enhancedPressAnimation()
                                        .entranceAnimation(delay = 300)
                                        .pulseAnimation()
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.PlayArrow,
                                        contentDescription = null
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Start Quiz")
                                }
                            }
                        }
                        
                        1 -> {
                            // Quiz (for lesen and hoeren content)
                            val questions = when (lesson.skill) {
                                "lesen" -> (lessonContent as? LesenContent)?.questions
                                "hoeren" -> (lessonContent as? HoerenContent)?.questions
                                else -> null
                            }
                            Log.d("Quiz", "Lesson skill: ${lesson.skill}, Questions found: ${questions?.size ?: 0}")
                            questions?.let { questionsList ->
                                item {
                                    Text(
                                        text = "Quiz",
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                                
                                items(questionsList) { question ->
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
                                                fontSize = 16.sp,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                            // Show English translation for A1 and A2 levels
                                            if (lesson.level in listOf("A1", "A2") && question.questionEnglish != null) {
                                                Text(
                                                    text = question.questionEnglish,
                                                    fontSize = 14.sp,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                    modifier = Modifier.padding(top = 4.dp)
                                                )
                                            }
                                            Spacer(modifier = Modifier.height(12.dp))
                                            
                                            when (question.type) {
                                                QuestionType.MULTIPLE_CHOICE -> {
                                                    question.options?.forEachIndexed { index, option ->
                                                        Card(
                                                            modifier = Modifier
                                                                .fillMaxWidth()
                                                                .padding(vertical = 2.dp)
                                                                .clickable {
                                                                    userAnswers = userAnswers.toMutableMap().apply {
                                                                        put(question.id.toString(), option)
                                                                    }
                                                                    Log.d("Quiz", "Selected option for Q${question.id}: $option")
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
                                                                        Log.d("Quiz", "Selected option for Q${question.id}: $option (via RadioButton)")
                                                                    },
                                                                    colors = RadioButtonDefaults.colors(
                                                                        selectedColor = MaterialTheme.colorScheme.primary
                                                                    )
                                                                )
                                                                Spacer(modifier = Modifier.width(12.dp))
                                                                Column(modifier = Modifier.weight(1f)) {
                                                                    Text(
                                                                        text = option,
                                                                        fontSize = 16.sp,
                                                                        color = MaterialTheme.colorScheme.onSurface,
                                                                        fontWeight = if (userAnswers[question.id.toString()] == option)
                                                                            FontWeight.Bold else FontWeight.Normal
                                                                    )
                                                                    // Show English translation for A1 and A2 levels
                                                                    if (lesson.level in listOf("A1", "A2") &&
                                                                        question.optionsEnglish != null &&
                                                                        index < question.optionsEnglish.size) {
                                                                        Text(
                                                                            text = question.optionsEnglish[index],
                                                                            fontSize = 14.sp,
                                                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                                                        )
                                                                    }
                                                                }
                                                                if (userAnswers[question.id.toString()] == option) {
                                                                    Icon(
                                                                        imageVector = Icons.Default.CheckCircle,
                                                                        contentDescription = "Selected",
                                                                        tint = MaterialTheme.colorScheme.primary,
                                                                        modifier = Modifier.size(20.dp)
                                                                    )
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                                QuestionType.FILL_BLANK -> {
                                                    OutlinedTextField(
                                                        value = userAnswers[question.id.toString()] ?: "",
                                                        onValueChange = { newValue ->
                                                            userAnswers = userAnswers.toMutableMap().apply {
                                                                put(question.id.toString(), newValue)
                                                            }
                                                        },
                                                        label = { Text("Your answer") },
                                                        placeholder = { Text("Enter your answer here...") },
                                                        modifier = Modifier.fillMaxWidth(),
                                                        colors = OutlinedTextFieldDefaults.colors(
                                                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                                                            unfocusedBorderColor = MaterialTheme.colorScheme.outline
                                                        )
                                                    )
                                                }
                                                QuestionType.MULTIPLE_CORRECT_ANSWERS -> {
                                                    // Handle multiple selection
                                                    val selectedAnswers = userAnswers[question.id.toString()]?.split(",")?.filter { it.isNotEmpty() } ?: emptyList()
                                                    question.options?.forEachIndexed { index, option ->
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
                                                                Column(modifier = Modifier.weight(1f)) {
                                                                    Text(
                                                                        text = option,
                                                                        fontSize = 16.sp,
                                                                        color = MaterialTheme.colorScheme.onSurface,
                                                                        fontWeight = if (selectedAnswers.contains(option))
                                                                            FontWeight.Bold else FontWeight.Normal
                                                                    )
                                                                    // Show English translation for A1 and A2 levels
                                                                    if (lesson.level in listOf("A1", "A2") &&
                                                                        question.optionsEnglish != null &&
                                                                        index < question.optionsEnglish.size) {
                                                                        Text(
                                                                            text = question.optionsEnglish[index],
                                                                            fontSize = 14.sp,
                                                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                                                        )
                                                                    }
                                                                }
                                                                if (selectedAnswers.contains(option)) {
                                                                    Icon(
                                                                        imageVector = Icons.Default.CheckCircle,
                                                                        contentDescription = "Selected",
                                                                        tint = MaterialTheme.colorScheme.primary,
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
                                                                        selectedColor = MaterialTheme.colorScheme.primary
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
                                                                        tint = MaterialTheme.colorScheme.primary,
                                                                        modifier = Modifier.size(20.dp)
                                                                    )
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                                QuestionType.GAP_FILL -> {
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
                                                                        onValueChange = { newValue ->
                                                                            userAnswers = userAnswers.toMutableMap().apply {
                                                                                put("${question.id}_gap_$gapIndex", newValue)
                                                                            }
                                                                        },
                                                                        label = { Text("Lücke ${gapIndex + 1}") },
                                                                        placeholder = { Text("Fill gap...") },
                                                                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                                                        colors = OutlinedTextFieldDefaults.colors(
                                                                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                                                                            unfocusedBorderColor = MaterialTheme.colorScheme.outline
                                                                        )
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
                                                                        onValueChange = { newValue ->
                                                                            userAnswers = userAnswers.toMutableMap().apply {
                                                                                put("${question.id}_match_$left", newValue)
                                                                            }
                                                                        },
                                                                        label = { Text("Antwort") },
                                                                        placeholder = { Text("Enter match...") },
                                                                        modifier = Modifier.weight(1f),
                                                                        colors = OutlinedTextFieldDefaults.colors(
                                                                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                                                                            unfocusedBorderColor = MaterialTheme.colorScheme.outline
                                                                        )
                                                                    )
                                                                }
                                                                Spacer(modifier = Modifier.height(8.dp))
                                                            }
                                                        }
                                                    } ?: Text("Matching items not available", color = MaterialTheme.colorScheme.error)
                                                }
                                                QuestionType.OPEN_ENDED -> {
                                                    OutlinedTextField(
                                                        value = userAnswers[question.id.toString()] ?: "",
                                                        onValueChange = { newValue ->
                                                            userAnswers = userAnswers.toMutableMap().apply {
                                                                put(question.id.toString(), newValue)
                                                            }
                                                        },
                                                        label = { Text("Your answer") },
                                                        placeholder = { Text("Enter your answer here...") },
                                                        modifier = Modifier.fillMaxWidth(),
                                                        colors = OutlinedTextFieldDefaults.colors(
                                                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                                                            unfocusedBorderColor = MaterialTheme.colorScheme.outline
                                                        )
                                                    )
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
                                            quizCompleted = true
                                            currentStep = 2
                                        },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .enhancedPressAnimation()
                                            .entranceAnimation(delay = 100),
                                        enabled = userAnswers.size == questionsList.size
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = null
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Submit Quiz")
                                    }
                                }
                            }
                        }
                        
                        2 -> {
                            // Results
                            item {
                                Text(
                                    text = "Quiz Results",
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            
                            item {
                                val questions = when (lesson.skill) {
                                    "lesen" -> (lessonContent as? LesenContent)?.questions
                                    "hoeren" -> (lessonContent as? HoerenContent)?.questions
                                    else -> null
                                }


                                val correctAnswers = questions?.count { question ->
                                    isAnswerCorrect(question, userAnswers[question.id.toString()])
                                } ?: 0
                                val totalQuestions = questions?.size ?: 0
                                val score = if (totalQuestions > 0) (correctAnswers * 100) / totalQuestions else 0
                                
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
                                        Text(
                                            text = "Your Score",
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            text = "$score%",
                                            fontSize = 48.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        Text(
                                            text = "$correctAnswers out of $totalQuestions correct",
                                            fontSize = 16.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                            
                            item {
                                Button(
                                    onClick = {
                                        // Calculate quiz results
                                        val questionsForScore = when (lesson.skill) {
                                            "lesen" -> (lessonContent as? LesenContent)?.questions
                                            "hoeren" -> (lessonContent as? HoerenContent)?.questions
                                            else -> null
                                        }
                                        
                                        val correctAnswers = questionsForScore?.count { question ->
                                            isAnswerCorrect(question, userAnswers[question.id.toString()])
                                        } ?: 0
                                        
                                        val totalQuestions = questionsForScore?.size ?: 0
                                        val finalScore = if (totalQuestions > 0) (correctAnswers * 100) / totalQuestions else 0
                                        
                                        // Update traditional lesson progress
                                        lessonViewModel.updateLessonProgress(
                                            lessonId = lesson.id,
                                            completed = true,
                                            score = finalScore,
                                            timeSpent = timeSpentInSeconds
                                        )

                                        
                                        // Show interstitial ad before navigating back
                                        if (context is android.app.Activity) {
                                            AdMobManager.showInterstitialAd(context)
                                        }
                                        
                                        navController.navigateUp()
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .enhancedPressAnimation()
                                        .entranceAnimation(delay = 200)
                                ) {
                                    Text("Complete Lesson")
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Dictionary Lookup Dialog
    if (showDictionaryDialog) {
        AlertDialog(
            onDismissRequest = { showDictionaryDialog = false },
            title = {
                Text(
                    text = "Look up word in dictionary",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column {
                    Text(
                        text = "Enter the German word you want to look up:",
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    OutlinedTextField(
                        value = wordToLookup,
                        onValueChange = { wordToLookup = it },
                        label = { Text("German word") },
                        placeholder = { Text("e.g., Haus, gehen, schön") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (wordToLookup.isNotBlank()) {
                            // Navigate to dictionary with the word as parameter
                            navController.navigate(Screen.DictionaryWithWord.createRoute(wordToLookup.trim())) {
                                launchSingleTop = true
                            }
                            showDictionaryDialog = false
                            wordToLookup = ""
                        }
                    },
                    enabled = wordToLookup.isNotBlank()
                ) {
                    Text("Look up")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showDictionaryDialog = false
                    wordToLookup = ""
                }) {
                    Text("Cancel")
                }
            }
        )
    }

}

// Composable for lesson text with dictionary lookup via long-press
@Composable
fun DictionaryEnabledText(
    text: String,
    fontSize: androidx.compose.ui.unit.TextUnit = 16.sp,
    onDictionaryLookup: () -> Unit
) {
    Text(
        text = text,
        fontSize = fontSize,
        color = MaterialTheme.colorScheme.onSurface,
        lineHeight = 24.sp,
        modifier = Modifier
            .fillMaxWidth()
            .pointerInput(Unit) {
                detectTapGestures(
                    onLongPress = { onDictionaryLookup() }
                )
            }
    )
}

// Alternative composable for vocabulary items
@Composable
fun DictionaryEnabledVocabularyItem(
    word: String,
    translation: String,
    example: String,
    onDictionaryLookup: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .pointerInput(Unit) {
                detectTapGestures(
                    onLongPress = { onDictionaryLookup(word) }
                )
            },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = word,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = translation,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (example.isNotBlank()) {
                Text(
                    text = example,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}
