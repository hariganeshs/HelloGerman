package com.hellogerman.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
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
import com.hellogerman.app.ui.viewmodel.LessonViewModel
import com.hellogerman.app.ui.theme.SchreibenColor
import com.google.gson.Gson
import com.hellogerman.app.data.entities.*
import java.util.regex.Pattern

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
    var score by remember { mutableStateOf(0) }
    var feedback by remember { mutableStateOf("") }
    
    val context = LocalContext.current
    val gson = remember { Gson() }
    
    LaunchedEffect(lessonId) {
        lessonViewModel.loadLessonById(lessonId)
    }
    
    // Update word count when text changes
    LaunchedEffect(userText.text) {
        wordCount = userText.text.split("\\s+".toRegex()).filter { it.isNotEmpty() }.size
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
                                            Text(
                                                text = "Writing Prompt",
                                                fontSize = 18.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                            Spacer(modifier = Modifier.height(8.dp))
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
                                                         contentDescription = "Time",
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
                                                        contentDescription = "Words",
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
                                        },
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = SchreibenColor
                                        )
                                    ) {
                                        Text("Start Writing")
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
                                                 imageVector = Icons.Default.Info,
                                                 contentDescription = "Time",
                                                 tint = SchreibenColor,
                                                 modifier = Modifier.size(20.dp)
                                             )
                                             Spacer(modifier = Modifier.width(4.dp))
                                             Text(
                                                 text = "${timeRemaining / 60}:${String.format("%02d", timeRemaining % 60)}",
                                                 fontSize = 16.sp,
                                                 fontWeight = FontWeight.Medium,
                                                 color = SchreibenColor
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
                                            // Generate feedback and score
                                            val feedbackResult = generateWritingFeedback(userText.text, content)
                                            feedback = feedbackResult.first
                                            score = feedbackResult.second
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

private fun generateWritingFeedback(text: String, content: SchreibenContent): Pair<String, Int> {
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
