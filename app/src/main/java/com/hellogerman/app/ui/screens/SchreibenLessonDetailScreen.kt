package com.hellogerman.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.hellogerman.app.ui.navigation.Screen
import com.hellogerman.app.ui.viewmodel.LessonViewModel
import com.hellogerman.app.ui.theme.SchreibenColor
import com.google.gson.Gson
import com.hellogerman.app.data.entities.*
import java.util.regex.Pattern
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import androidx.compose.ui.graphics.Color
import com.hellogerman.app.ui.components.LessonIllustration
import com.hellogerman.app.ui.components.CharacterDisplay
import com.hellogerman.app.ui.components.AnimatedProgressBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SchreibenLessonDetailScreen(
    navController: NavController,
    lessonId: Int,
    lessonViewModel: LessonViewModel = viewModel()
) {
    val currentLesson by lessonViewModel.currentLesson.collectAsState()
    val isLoading by lessonViewModel.isLoading.collectAsState()
    
    var currentStep by remember { mutableStateOf(0) } // 0: prompt, 1: writing, 2: feedback
    var userText by remember { mutableStateOf(TextFieldValue("")) }
    var wordCount by remember { mutableStateOf(0) }
    var timeRemaining by remember { mutableStateOf(0) }
    var isTimerRunning by remember { mutableStateOf(false) }
    var score by remember { mutableStateOf(0) }
    var feedback by remember { mutableStateOf("") }
    var grammarIssues by remember { mutableStateOf(listOf<String>()) }
    var spellingSuggestions by remember { mutableStateOf(listOf<String>()) }

    val context = LocalContext.current
    val gson = remember { Gson() }

    LaunchedEffect(lessonId) {
        lessonViewModel.loadLessonById(lessonId)
    }

    // Update word count when text changes
    LaunchedEffect(userText.text) {
        wordCount = userText.text.split("\\s+".toRegex()).filter { it.isNotEmpty() }.size
    }

    // Timer functionality
    LaunchedEffect(isTimerRunning) {
        if (isTimerRunning && timeRemaining > 0) {
            while (isActive && timeRemaining > 0) {
                delay(1000)
                timeRemaining--
                if (timeRemaining == 0) {
                    isTimerRunning = false
                    // Auto-submit when time runs out
                    currentLesson?.let { lesson ->
                        try {
                            val content = gson.fromJson(lesson.content, SchreibenContent::class.java)
                            val feedbackResult = generateWritingFeedback(userText.text, content)
                            feedback = feedbackResult.first
                            score = feedbackResult.second
                            grammarIssues = analyzeGrammar(userText.text)
                            spellingSuggestions = checkSpelling(userText.text)
                            currentStep = 2
                            lessonViewModel.completeLesson(lessonId, score)
                        } catch (e: Exception) {
                            // Handle parsing error
                        }
                    }
                }
            }
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = currentLesson?.title ?: "Writing Lesson",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Go back to writing lessons list"
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
        }
    ) { padding ->
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = SchreibenColor)
            }
        } else {
            currentLesson?.let { lesson ->
                val lessonContent = try {
                    gson.fromJson(lesson.content, SchreibenContent::class.java)
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
                            // Writing Prompt
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
                                            // Lesson illustration and character
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    text = "Writing Prompt",
                                                    fontSize = 18.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = MaterialTheme.colorScheme.onSurface
                                                )

                                                // Lesson illustration
                                                LessonIllustration(
                                                    illustrationResId = lesson.illustrationResId,
                                                    contentDescription = "Writing prompt illustration"
                                                )
                                            }

                                            Spacer(modifier = Modifier.height(8.dp))

                                            // Character display
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.Start,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                CharacterDisplay(
                                                    characterResId = lesson.characterResId,
                                                    animationType = lesson.animationType,
                                                    contentDescription = "Writing guide character"
                                                )
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text(
                                                    text = "Let's write something great!",
                                                    fontSize = 14.sp,
                                                    color = SchreibenColor,
                                                    fontWeight = FontWeight.Medium
                                                )
                                            }

                                            Spacer(modifier = Modifier.height(12.dp))

                                            Text(
                                                text = content.prompt,
                                                fontSize = 16.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                            Spacer(modifier = Modifier.height(16.dp))
                                            
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                                                                 Row(
                                                     verticalAlignment = Alignment.CenterVertically
                                                 ) {
                                                     Icon(
                                                         imageVector = Icons.Default.Info,
                                                         contentDescription = "Time limit indicator",
                                                         tint = SchreibenColor,
                                                         modifier = Modifier.size(20.dp)
                                                     )
                                                     Spacer(modifier = Modifier.width(4.dp))
                                                     Text(
                                                         text = "${content.timeLimit} minutes",
                                                         fontSize = 14.sp,
                                                         color = MaterialTheme.colorScheme.onSurfaceVariant
                                                     )
                                                 }
                                                
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.Edit,
                                                        contentDescription = "Word count indicator",
                                                        tint = SchreibenColor,
                                                        modifier = Modifier.size(20.dp)
                                                    )
                                                    Spacer(modifier = Modifier.width(4.dp))
                                                    Text(
                                                        text = "${content.minWords}-${content.maxWords} words",
                                                        fontSize = 14.sp,
                                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                                
                                item {
                                    Button(
                                        onClick = {
                                            currentStep = 1
                                            timeRemaining = content.timeLimit * 60 // Convert to seconds
                                            isTimerRunning = true
                                        },
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = SchreibenColor
                                        )
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Timer,
                                                contentDescription = "Writing timer icon",
                                                modifier = Modifier.size(20.dp)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text("Start Writing (${content.timeLimit} min)")
                                        }
                                    }
                                }
                            }
                        }
                        
                        1 -> {
                            // Writing Editor
                            lessonContent?.let { content ->
                                item {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "Writing Editor",
                                            fontSize = 20.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        
                                                                                 Row(
                                             verticalAlignment = Alignment.CenterVertically
                                         ) {
                                             Icon(
                                                 imageVector = if (timeRemaining < 300) Icons.Default.Warning else Icons.Default.Timer,
                                                 contentDescription = "Time",
                                                 tint = if (timeRemaining < 300) Color.Red else SchreibenColor,
                                                 modifier = Modifier.size(20.dp)
                                             )
                                             Spacer(modifier = Modifier.width(4.dp))
                                             Text(
                                                 text = "${timeRemaining / 60}:${String.format("%02d", timeRemaining % 60)}",
                                                 fontSize = 16.sp,
                                                 fontWeight = FontWeight.Medium,
                                                 color = if (timeRemaining < 300) Color.Red else SchreibenColor
                                             )
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
                                                text = "Your Response",
                                                fontSize = 16.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                            Spacer(modifier = Modifier.height(8.dp))
                                            
                                            OutlinedTextField(
                                                value = userText,
                                                onValueChange = { userText = it },
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(200.dp),
                                                label = {
                                                    Text("Your German writing response")
                                                },
                                                placeholder = {
                                                    Text("Start writing your response here...")
                                                },
                                                colors = OutlinedTextFieldDefaults.colors(
                                                    focusedBorderColor = SchreibenColor,
                                                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                                                ),
                                                textStyle = TextStyle(
                                                    fontSize = 16.sp,
                                                    lineHeight = 24.sp
                                                )
                                            )
                                            
                                            Spacer(modifier = Modifier.height(8.dp))
                                            
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Text(
                                                    text = "Words: $wordCount",
                                                    fontSize = 14.sp,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                                
                                                Text(
                                                    text = "Target: ${content.minWords}-${content.maxWords}",
                                                    fontSize = 14.sp,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                        }
                                    }
                                }
                                
                                item {
                                    Button(
                                        onClick = {
                                            isTimerRunning = false
                                            // Generate feedback and score
                                            val feedbackResult = generateWritingFeedback(userText.text, content)
                                            feedback = feedbackResult.first
                                            score = feedbackResult.second
                                            grammarIssues = analyzeGrammar(userText.text)
                                            spellingSuggestions = checkSpelling(userText.text)
                                            currentStep = 2

                                            // Update lesson completion
                                            lessonViewModel.completeLesson(lessonId, score)
                                        },
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = SchreibenColor
                                        ),
                                        enabled = wordCount >= (lessonContent?.minWords ?: 0)
                                    ) {
                                        Text("Submit Writing")
                                    }
                                }
                            }
                        }
                        
                        2 -> {
                            // Feedback
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
                                            tint = SchreibenColor,
                                            modifier = Modifier.size(64.dp)
                                        )
                                        Spacer(modifier = Modifier.height(16.dp))
                                        Text(
                                            text = "Writing Submitted!",
                                            fontSize = 24.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            text = "Your Score: $score%",
                                            fontSize = 20.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = SchreibenColor
                                        )
                                        Spacer(modifier = Modifier.height(16.dp))
                                        
                                        Card(
                                            modifier = Modifier.fillMaxWidth(),
                                            colors = CardDefaults.cardColors(
                                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                                            )
                                        ) {
                                            Column(
                                                modifier = Modifier.padding(16.dp)
                                            ) {
                                                Text(
                                                    text = "Feedback",
                                                    fontSize = 18.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = MaterialTheme.colorScheme.onSurface
                                                )
                                                Spacer(modifier = Modifier.height(8.dp))
                                                Text(
                                                    text = feedback,
                                                    fontSize = 14.sp,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )

                                                // Grammar Issues
                                                if (grammarIssues.isNotEmpty()) {
                                                    Spacer(modifier = Modifier.height(12.dp))
                                                    Text(
                                                        text = "Grammatik",
                                                        fontSize = 16.sp,
                                                        fontWeight = FontWeight.Medium,
                                                        color = Color(0xFF1976D2)
                                                    )
                                                    grammarIssues.forEach { issue ->
                                                        Text(
                                                            text = "• $issue",
                                                            fontSize = 14.sp,
                                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                            modifier = Modifier.padding(start = 8.dp, top = 4.dp)
                                                        )
                                                    }
                                                }

                                                // Spelling Suggestions
                                                if (spellingSuggestions.isNotEmpty()) {
                                                    Spacer(modifier = Modifier.height(12.dp))
                                                    Text(
                                                        text = "Rechtschreibung",
                                                        fontSize = 16.sp,
                                                        fontWeight = FontWeight.Medium,
                                                        color = Color(0xFFD32F2F)
                                                    )
                                                    spellingSuggestions.forEach { suggestion ->
                                                        Text(
                                                            text = "• $suggestion",
                                                            fontSize = 14.sp,
                                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                            modifier = Modifier.padding(start = 8.dp, top = 4.dp)
                                                        )
                                                    }
                                                }

                                                // Detailed feedback
                                                lessonContent?.let { content ->
                                                    val detailedFeedback = generateDetailedFeedback(userText.text, content)
                                                    if (detailedFeedback.isNotEmpty()) {
                                                        Spacer(modifier = Modifier.height(12.dp))
                                                        Text(
                                                            text = "Detailliertes Feedback",
                                                            fontSize = 16.sp,
                                                            fontWeight = FontWeight.Medium,
                                                            color = Color(0xFF388E3C)
                                                        )
                                                        Text(
                                                            text = detailedFeedback,
                                                            fontSize = 14.sp,
                                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                            modifier = Modifier.padding(start = 8.dp, top = 4.dp)
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                        
                                        Spacer(modifier = Modifier.height(16.dp))
                                        Button(
                                            onClick = { navController.navigateUp() },
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = SchreibenColor
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

internal fun generateWritingFeedback(text: String, content: SchreibenContent): Pair<String, Int> {
    var score = 0
    val feedback = mutableListOf<String>()
    
    // Word count check
    val wordCount = text.split("\\s+".toRegex()).filter { it.isNotEmpty() }.size
    if (wordCount >= content.minWords && wordCount <= content.maxWords) {
        score += 20
        feedback.add("✓ Good word count ($wordCount words)")
    } else {
        feedback.add("✗ Word count should be between ${content.minWords}-${content.maxWords} words (you wrote $wordCount)")
    }
    
    // Basic grammar check (simple regex patterns)
    val sentences = text.split("[.!?]+".toRegex()).filter { it.trim().isNotEmpty() }
    if (sentences.isNotEmpty()) {
        score += 20
        feedback.add("✓ Good sentence structure")
    } else {
        feedback.add("✗ Improve sentence structure")
    }
    
    // Check for common German words/phrases
    val germanWords = listOf("ich", "du", "er", "sie", "es", "wir", "ihr", "Sie", "und", "oder", "aber", "dass", "wenn", "weil")
    val foundGermanWords = germanWords.count { text.contains(it, ignoreCase = true) }
    if (foundGermanWords >= 3) {
        score += 20
        feedback.add("✓ Good use of German vocabulary")
    } else {
        feedback.add("✗ Try to use more German vocabulary")
    }
    
    // Length and coherence
    if (text.length > 100) {
        score += 20
        feedback.add("✓ Good response length")
    } else {
        feedback.add("✗ Response could be longer")
    }
    
    // Overall coherence
    if (text.contains(".") || text.contains("!") || text.contains("?")) {
        score += 20
        feedback.add("✓ Good coherence and flow")
    } else {
        feedback.add("✗ Improve coherence and punctuation")
    }
    
    return Pair(feedback.joinToString("\n"), score)
}

internal fun analyzeGrammar(text: String): List<String> {
    val issues = mutableListOf<String>()

    // Check for common grammar issues
    val sentences = text.split("[.!?]+".toRegex()).filter { it.trim().isNotEmpty() }

    sentences.forEach { sentence ->
        // Check for capitalization
        if (sentence.trim().isNotEmpty() && !sentence.trim()[0].isUpperCase()) {
            issues.add("Satz sollte mit Großbuchstaben beginnen: '$sentence'")
        }

        // Check for double spaces
        if (sentence.contains("  ")) {
            issues.add("Vermeide doppelte Leerzeichen")
        }

        // Check for common preposition errors
        val prepositionErrors = mapOf(
            "in der" to "im",
            "in dem" to "im",
            "an der" to "an der",
            "an dem" to "am"
        )

        prepositionErrors.forEach { (wrong, correct) ->
            if (sentence.contains(wrong, ignoreCase = true)) {
                issues.add("Möglicherweise '$correct' statt '$wrong'")
            }
        }
    }

    // Check for missing punctuation
    val words = text.split("\\s+".toRegex()).filter { it.isNotEmpty() }
    if (words.size > 10 && !text.contains(".") && !text.contains("!") && !text.contains("?")) {
        issues.add("Füge Interpunktion hinzu")
    }

    return issues.distinct()
}

internal fun checkSpelling(text: String): List<String> {
    val suggestions = mutableListOf<String>()

    // Common German spelling issues for A2 level
    val commonErrors = mapOf(
        "das" to listOf("daß", "dass"),
        "daß" to listOf("das", "dass"),
        "dass" to listOf("das", "daß"),
        "machen" to listOf("machan", "machen"),
        "gehen" to listOf("gehn", "gehen"),
        "kommen" to listOf("kommn", "kommen"),
        "essen" to listOf("esn", "essen"),
        "trinken" to listOf("trinkn", "trinken")
    )

    val words = text.lowercase().split("\\s+".toRegex()).filter { it.isNotEmpty() }

    words.forEach { word ->
        commonErrors.forEach { (correct, alternatives) ->
            if (alternatives.contains(word)) {
                suggestions.add("'$word' → '$correct'")
            }
        }
    }

    // Check for repeated words
    val repeatedWords = words.groupBy { it }.filter { it.value.size > 1 }.keys
    repeatedWords.forEach { word ->
        if (word.length > 3) { // Only flag longer words
            suggestions.add("Wort '$word' wird wiederholt - Variation verwenden?")
        }
    }

    return suggestions.distinct()
}

internal fun generateDetailedFeedback(text: String, content: SchreibenContent): String {
    val feedback = mutableListOf<String>()

    // Word variety analysis
    val words = text.split("\\s+".toRegex()).filter { it.isNotEmpty() }
    val uniqueWords = words.distinct().size
    val varietyRatio = if (words.isNotEmpty()) uniqueWords.toFloat() / words.size else 0f

    if (varietyRatio < 0.6f) {
        feedback.add("Versuche mehr verschiedene Wörter zu verwenden")
    } else if (varietyRatio > 0.8f) {
        feedback.add("✓ Gute Wortvielfalt")
    }

    // Sentence structure analysis
    val sentences = text.split("[.!?]+".toRegex()).filter { it.trim().isNotEmpty() }
    val avgSentenceLength = if (sentences.isNotEmpty()) words.size.toFloat() / sentences.size else 0f

    if (avgSentenceLength < 8) {
        feedback.add("Sätze könnten etwas länger sein")
    } else if (avgSentenceLength > 20) {
        feedback.add("Einige Sätze sind sehr lang - kürzen oder aufteilen?")
    } else {
        feedback.add("✓ Ausgewogene Satzlänge")
    }

    // Content relevance check
    val relevantKeywords = content.prompt.split("\\s+".toRegex())
        .filter { it.length > 3 }
        .take(5) // Take first 5 significant words from prompt

    var keywordMatches = 0
    relevantKeywords.forEach { keyword ->
        if (text.contains(keyword, ignoreCase = true)) {
            keywordMatches++
        }
    }

    if (keywordMatches < relevantKeywords.size / 2) {
        feedback.add("Versuche mehr auf das Thema einzugehen")
    } else {
        feedback.add("✓ Gute thematische Relevanz")
    }

    return feedback.joinToString("\n")
}
